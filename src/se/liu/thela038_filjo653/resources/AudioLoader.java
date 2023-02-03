package se.liu.thela038_filjo653.resources;


import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * Class that loads all sound effects and sorts them into functions that play a random sound effect from a category. Also has playMusic()
 * which starts the background music and playSound() which can play any sound from the loaded sounds.
 */
public class AudioLoader
{
    public enum AudioEffect
    {
	PISTOL, UZI, RIFLE, RPG, RELOAD, RPG_RELOAD, EXPLOSION, DEATH_1, DEATH_2, DEATH_3, DEATH_4, DEATH_5, DEATH_6, DEATH_7, DEATH_8, BUY_GUN, CHANGE_WEAPON, MEAT_1, MEAT_2, MEAT_3, MEAT_4, MEAT_5, MEAT_6, MEAT_7, GROWL_1, GROWL_2, GROWL_3, GROWL_4, GROWL_5, PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4, PLAYER_5, MUSIC, PICKUP
    }

    /**
     * From enum to actual file name
     */
    final private static Map<AudioEffect, String> AUDIO_NAME_MAP =
	    Map.ofEntries(Map.entry(AudioEffect.PISTOL, "pistol"), Map.entry(AudioEffect.UZI, "uzi"), Map.entry(AudioEffect.RIFLE, "ak"),
			  Map.entry(AudioEffect.RPG, "rpg"), Map.entry(AudioEffect.RELOAD, "reload"),
			  Map.entry(AudioEffect.RPG_RELOAD, "rpgReload"), Map.entry(AudioEffect.EXPLOSION, "explosion"),
			  Map.entry(AudioEffect.DEATH_1, "death1"), Map.entry(AudioEffect.DEATH_2, "death2"),
			  Map.entry(AudioEffect.DEATH_3, "death3"), Map.entry(AudioEffect.DEATH_4, "death4"),
			  Map.entry(AudioEffect.DEATH_5, "death5"), Map.entry(AudioEffect.DEATH_6, "death6"),
			  Map.entry(AudioEffect.DEATH_7, "death7"), Map.entry(AudioEffect.DEATH_8, "death8"),
			  Map.entry(AudioEffect.BUY_GUN, "buyGun"), Map.entry(AudioEffect.CHANGE_WEAPON, "changeWeapon"),
			  Map.entry(AudioEffect.MEAT_1, "meat1"), Map.entry(AudioEffect.MEAT_2, "meat2"),
			  Map.entry(AudioEffect.MEAT_3, "meat3"), Map.entry(AudioEffect.MEAT_4, "meat4"),
			  Map.entry(AudioEffect.MEAT_5, "meat5"), Map.entry(AudioEffect.MEAT_6, "meat6"),
			  Map.entry(AudioEffect.MEAT_7, "meat7"), Map.entry(AudioEffect.GROWL_1, "morr1"),
			  Map.entry(AudioEffect.GROWL_2, "morr2"), Map.entry(AudioEffect.GROWL_3, "morr3"),
			  Map.entry(AudioEffect.GROWL_4, "morr4"), Map.entry(AudioEffect.GROWL_5, "morr5"),
			  Map.entry(AudioEffect.PLAYER_1, "player1"), Map.entry(AudioEffect.PLAYER_2, "player2"),
			  Map.entry(AudioEffect.PLAYER_3, "player3"), Map.entry(AudioEffect.PLAYER_4, "player4"),
			  Map.entry(AudioEffect.PLAYER_5, "player5"),
			  Map.entry(AudioEffect.MUSIC, "music"), Map.entry(AudioEffect.PICKUP, "pickup"));

    private final Map<AudioEffect, Clip> audioEffects;
    private static final Random RND = new Random();

    public AudioLoader() {
	audioEffects = new EnumMap<>(AudioEffect.class);
    }

    public void playSound(AudioEffect audioName) {
	audioEffects.get(audioName).setFramePosition(0);
	audioEffects.get(audioName).start();
    }

    public void playRandomSound(AudioEffect[] audioEffects) {
	playSound(audioEffects[RND.nextInt(audioEffects.length)]);
    }

    public void playMusic() {
	final int loopCount = 100;
	Clip music = audioEffects.get(AudioEffect.MUSIC);
	music.loop(loopCount);
    }

    public void playPlayerSound() {
	AudioEffect[] playerSounds =
		new AudioEffect[] { AudioEffect.PLAYER_1, AudioEffect.PLAYER_2, AudioEffect.PLAYER_3, AudioEffect.PLAYER_4,
			AudioEffect.PLAYER_5};

	playRandomSound(playerSounds);
    }

    public void playDeathSound() {
	AudioEffect[] deathSounds =
		new AudioEffect[] { AudioEffect.DEATH_1, AudioEffect.DEATH_2, AudioEffect.DEATH_3, AudioEffect.DEATH_4, AudioEffect.DEATH_5,
			AudioEffect.DEATH_6, AudioEffect.DEATH_7, AudioEffect.DEATH_8 };

	playRandomSound(deathSounds);
    }

    public void playMeatSound() {
	AudioEffect[] meatSounds =
		new AudioEffect[] { AudioEffect.MEAT_1, AudioEffect.MEAT_2, AudioEffect.MEAT_3, AudioEffect.MEAT_4, AudioEffect.MEAT_5,
			AudioEffect.MEAT_6, AudioEffect.MEAT_7 };

	playRandomSound(meatSounds);
    }

    public void playGrowlSound() {
	AudioEffect[] growlSounds = new AudioEffect[] { AudioEffect.GROWL_1, AudioEffect.GROWL_2, AudioEffect.GROWL_3, AudioEffect.GROWL_4,
		AudioEffect.GROWL_5 };

	playRandomSound(growlSounds);
    }

    public void loadAudio() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
	for (AudioEffect iterAudioName : AudioEffect.values()) {
	    final URL audioURL = ClassLoader.getSystemResource("audio/" + AUDIO_NAME_MAP.get(iterAudioName) + ".wav");

	    Clip soundEffect = AudioSystem.getClip();
	    soundEffect.open(AudioSystem.getAudioInputStream(audioURL));

	    audioEffects.put(iterAudioName, soundEffect);
	}
    }
}
