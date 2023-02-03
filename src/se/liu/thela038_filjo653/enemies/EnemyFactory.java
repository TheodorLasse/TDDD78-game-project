package se.liu.thela038_filjo653.enemies;

import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.resources.JsonReader;
import se.liu.thela038_filjo653.sprites.Enemy;
import se.liu.thela038_filjo653.sprites.Entity;
import se.liu.thela038_filjo653.weapons.WeaponType;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating new enemies of different types.
 */
public class EnemyFactory
{
    /**
     * The types of enemies that the factory can create.
     */
    public enum EnemyType
    {
	COP, WALKER, BOSS
    }


    private final ImageLoader imageLoader;
    private final AudioLoader audioLoader;
    private final EntityHandler entityHandler;
    private final SpriteHandler spriteHandler;
    private final Entity target;
    private final EnumMap<EnemyType, Map<?, ?>> enemyData = new EnumMap<>(EnemyType.class);

    /**
     * Which weapon an enemy is equipped with depends on which wave it currently is.
     */
    private final Map<WeaponType, Integer> weaponTypeWaveMap = Map.of(WeaponType.PISTOL, 0,
								      WeaponType.UZI, 15,
								      WeaponType.RIFLE, 25,
								      WeaponType.RPG, 35);

    public EnemyFactory(final ImageLoader imageLoader, final AudioLoader audioLoader, final EntityHandler entityHandler,
			final SpriteHandler spriteHandler, final Entity target)
    {
	this.imageLoader = imageLoader;
	this.audioLoader = audioLoader;
	this.entityHandler = entityHandler;
	this.spriteHandler = spriteHandler;
	this.target = target;

	loadJson();
    }

    private void loadJson() {
	// Uses readJsonCritical to terminate the program if it fails to load the enemy json data,
	// because the game should not run without enemies.
	enemyData.put(EnemyType.COP, JsonReader.readJsonCritical(JsonReader.VALUES.COP));
	enemyData.put(EnemyType.WALKER, JsonReader.readJsonCritical(JsonReader.VALUES.WALKER));
	enemyData.put(EnemyType.BOSS, JsonReader.readJsonCritical(JsonReader.VALUES.BOSS));
    }

    /**
     * Creates a new Enemy of a given type.
     *
     * @param enemyType The type that will be created.
     * @param position  The position for the new Enemy.
     *
     * @return A new Enemy.
     */
    public Enemy createEnemy(EnemyType enemyType, Vector2D position, int wave) {
	Map<?, ?> data = enemyData.get(enemyType);

	final int scaling = (int) (double) data.get("scaling");
	final int rangeScalingMultiplier = 3;
	final int rangeScaling = scaling * rangeScalingMultiplier;

	final int health = (int) (double) data.get("health") + scaling * wave;
	final int speed = (int) (double) data.get("speed") + scaling * wave;
	final int attackRange = (int) (double) data.get("attackRange") + rangeScaling * wave;
	final int scoreWorth = (int) (double) data.get("score");
	WeaponType weaponType = getWaveWeapon(wave);


	List<BufferedImage> baseTexture;
	List<BufferedImage> walkingTexture;
	List<BufferedImage> idleTexture;

	switch (enemyType) {
	    case BOSS:
		weaponType = WeaponType.MELEE_BOSS;
		baseTexture = imageLoader.getZombieBossBase();
		walkingTexture = imageLoader.getZombieBossWalking();
		idleTexture = imageLoader.getZombieBossWalking();
		break;
	    case COP:
		baseTexture = imageLoader.getZombieCopBase();
		walkingTexture = imageLoader.getZombieCopWalking();
		idleTexture = imageLoader.getZombieCopWalking();
		break;
	    case WALKER:
	    default:
		weaponType = WeaponType.MELEE;
		baseTexture = imageLoader.getZombieWalkerBase();
		walkingTexture = imageLoader.getZombieWalkerWalking();
		idleTexture = imageLoader.getZombieWalkerWalking();
		break;
	}

	Enemy enemy = new Enemy(position, baseTexture.get(0), health, speed, target, weaponType, attackRange,  scoreWorth,
				entityHandler, spriteHandler, imageLoader, audioLoader);

	enemy.setBaseTexture(baseTexture);
	enemy.setWalkingTexture(walkingTexture);
	enemy.setIdleTexture(idleTexture);

	return enemy;
    }

    private WeaponType getWaveWeapon(final int wave) {
        if (wave >= weaponTypeWaveMap.get(WeaponType.RPG)) return WeaponType.RPG;
        else if (wave >= weaponTypeWaveMap.get(WeaponType.RIFLE)) return WeaponType.RIFLE;
	else if (wave >= weaponTypeWaveMap.get(WeaponType.UZI)) return WeaponType.UZI;
	else return WeaponType.PISTOL;
    }
}
