package se.liu.thela038_filjo653.weapons;

import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.resources.JsonReader;
import se.liu.thela038_filjo653.sprites.LivingEntity;
import se.liu.thela038_filjo653.sprites.Player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for creating all weapons. The only difference between different weapons is the values of the parameters to the two
 * weapon classes which is taken care of here.
 */
public class WeaponFactory
{
    private static Iterable<WeaponType> getPlayerWeapons() {
	List<WeaponType> playerWeapons = new ArrayList<>();
	playerWeapons.add(WeaponType.PISTOL);
	playerWeapons.add(WeaponType.UZI);
	playerWeapons.add(WeaponType.RIFLE);
	playerWeapons.add(WeaponType.RPG);
	return playerWeapons;
    }

    private static final Map<WeaponType, Integer> WEAPON_COST_MAP = Map.of(WeaponType.PISTOL, 0,
									   WeaponType.UZI, 50,
									   WeaponType.RIFLE, 150,
									   WeaponType.RPG, 250);

    private static int getWeaponCost(WeaponType type){
        return WEAPON_COST_MAP.get(type);
    }

    public static void buyWeapon(Player player, int cash, WeaponType latestWeapon, ImageLoader imageLoader, AudioLoader audioLoader) {
	for (WeaponType iterWeapon : getPlayerWeapons()) {
	    int iterWeaponCost = getWeaponCost(iterWeapon);
	    if (getWeaponCost(latestWeapon) >= iterWeaponCost) {
		continue;
	    }
	    if (iterWeaponCost <= cash){
	        player.subtractCash(iterWeaponCost);
	        player.addWeapon(createWeapon(iterWeapon, player, imageLoader, audioLoader));
	        audioLoader.playSound(AudioLoader.AudioEffect.BUY_GUN);
	        break;
	    }
	}
    }

    /**
     * Returns a weapon with different values depending on which weaponType is sent in
     *
     * @param weaponType
     * @param owner
     * @param imageLoader
     * @param audioLoader
     */
    public static Weapon createWeapon(final WeaponType weaponType, final LivingEntity owner, final ImageLoader imageLoader,
				      final AudioLoader audioLoader)
    {
	Map<?, ?> jsonMap;
	List<BufferedImage> texture;
	BulletType bulletType;

	switch (weaponType) {
	    case MELEE -> {
	        jsonMap = JsonReader.readJsonCritical(JsonReader.VALUES.MELEE);
	        return createMelee(owner, audioLoader, jsonMap);
	    }
	    case MELEE_BOSS -> {
		jsonMap = JsonReader.readJsonCritical(JsonReader.VALUES.MELEE_BOSS);
		return createMelee(owner, audioLoader, jsonMap);
	    }
	    case PISTOL -> {
		jsonMap = JsonReader.readJsonCritical(JsonReader.VALUES.PISTOL);
		texture = imageLoader.getPistol();
		bulletType = BulletType.REGULAR;
	    }
	    case UZI -> {
		jsonMap = JsonReader.readJsonCritical(JsonReader.VALUES.UZI);
		texture = imageLoader.getUzi();
		bulletType = BulletType.REGULAR;
	    }
	    case RIFLE -> {
		jsonMap = JsonReader.readJsonCritical(JsonReader.VALUES.RIFLE);
		texture = imageLoader.getRifle();
		bulletType = BulletType.PIERCING;
	    }
	    case RPG -> {
		jsonMap = JsonReader.readJsonCritical(JsonReader.VALUES.RPG);
		texture = imageLoader.getRpg();
		bulletType = BulletType.EXPLOSIVE;
	    }
	    default -> {
		return null;
	    }
	}
	return createProjectileWeapon(owner, imageLoader, audioLoader, jsonMap, texture, bulletType, weaponType);
    }

    private static ProjectileWeapon createProjectileWeapon(final LivingEntity owner, final ImageLoader imageLoader,
							   final AudioLoader audioLoader, final Map<?, ?> jsonMap,
							   final List<BufferedImage> texture, final BulletType bulletType,
							   final WeaponType weaponType)
    {

	int speed = (int) (double) jsonMap.get("speed");
	int damage = (int) (double) (jsonMap.get("damage"));
	double reloadTime = (double) jsonMap.get("reloadTime");
	double cooldownTime = (double) jsonMap.get("cooldownTime");
	int maxAmmo = (int) (double) jsonMap.get("maxAmmo");
	List<?> bulletOffsetX = (List<?>) jsonMap.get("bulletOffsetX");
	List<?> bulletOffsetY = (List<?>) jsonMap.get("bulletOffsetY");

	final List<Vector2D> bulletOffset = new ArrayList<>();
	for (int i = 0; i < bulletOffsetX.size(); i++) {
	    bulletOffset.add(new Vector2D((double) bulletOffsetX.get(i),(double) bulletOffsetY.get(i)));
	}

	return new ProjectileWeapon(owner, speed, damage, reloadTime, cooldownTime, maxAmmo, texture, bulletOffset, bulletType, weaponType,
				    imageLoader, audioLoader);
    }

    private static MeleeWeapon createMelee(final LivingEntity owner, final AudioLoader audioLoader, final Map<?, ?> jsonMap) {
	final int damage = (int) (double) (jsonMap.get("damage"));
	final double cooldownTime = (double) jsonMap.get("cooldownTime");
	final int range = (int) (double) jsonMap.get("range");
	final WeaponType weaponType = WeaponType.MELEE;
	return new MeleeWeapon(owner, damage, cooldownTime, range, weaponType, audioLoader);
    }
}
