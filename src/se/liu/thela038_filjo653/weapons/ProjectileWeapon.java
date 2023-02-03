package se.liu.thela038_filjo653.weapons;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.GameComponent;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.Bullet;
import se.liu.thela038_filjo653.sprites.ExplosiveBullet;
import se.liu.thela038_filjo653.sprites.LivingEntity;
import se.liu.thela038_filjo653.sprites.PiercingBullet;
import se.liu.thela038_filjo653.time.DeltaTimer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Class representing a weapon that shoots bullets. Also contains all information about the weapon, from ammo to recharge time left.
 */
public class ProjectileWeapon implements Weapon
{
    protected LivingEntity owner;
    protected int speed;
    protected int damage;
    protected double reloadTime;
    protected double cooldownTime;
    protected int ammo;
    protected int maxAmmo;
    protected DeltaTimer rechargeTimer = new DeltaTimer();
    protected List<BufferedImage> texture;
    protected BulletType bulletType;
    protected List<Vector2D> bulletOffset;
    protected ImageLoader imageLoader;
    protected AudioLoader audioLoader;
    private WeaponType weaponType;

    private boolean reloading;

    protected ProjectileWeapon(final LivingEntity owner, final int speed, final int damage, final double reloadTime,
			       final double cooldownTime, final int maxAmmo, final List<BufferedImage> texture,
			       final List<Vector2D> bulletOffset, final BulletType bulletType, final WeaponType weaponType,
			       final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	this.owner = owner;
	this.speed = speed;
	this.damage = damage;
	this.reloadTime = reloadTime;
	this.cooldownTime = cooldownTime;
	this.ammo = maxAmmo;
	this.maxAmmo = maxAmmo;
	this.texture = texture;
	this.bulletType = bulletType;
	this.bulletOffset = bulletOffset;
	this.imageLoader = imageLoader;
	this.audioLoader = audioLoader;
	this.weaponType = weaponType;
    }

    public int getAmmo() {
	return ammo;
    }

    public int getMaxAmmo() {
	return maxAmmo;
    }

    public boolean isReloading() {
	return reloading;
    }

    public double getReloadTime() {
	return reloadTime;
    }

    public double getReloadTimeLeft() {
	return reloading ? rechargeTimer.getElapsedSeconds() : 0;
    }

    /**
     * Updates the weapon internally
     */
    @Override public void update(DeltaTime deltaTime) {
	rechargeTimer.update(deltaTime);

	if (rechargeTimer.isComplete()) {
	    reloading = false;
	}
    }

    private void playAttackSound() {
	switch (weaponType) {
	    case UZI -> audioLoader.playSound(AudioLoader.AudioEffect.UZI);
	    case RIFLE -> audioLoader.playSound(AudioLoader.AudioEffect.RIFLE);
	    case RPG -> audioLoader.playSound(AudioLoader.AudioEffect.RPG);
	    default -> audioLoader.playSound(AudioLoader.AudioEffect.PISTOL);
	}
    }

    private void playReloadSound() {
	switch (weaponType) {
	    case RPG -> audioLoader.playSound(AudioLoader.AudioEffect.RPG_RELOAD);
	    default -> audioLoader.playSound(AudioLoader.AudioEffect.RELOAD);
	}
    }

    @Override public WeaponType getWeaponType() {
	return weaponType;
    }

    public List<BufferedImage> getTexture() {
	return texture;
    }

    /**
     * Gives the correct offset given the owner's direction which is one of 8 directions
     */
    public Vector2D getCorrectAttackOffset() {
	return bulletOffset.get(owner.getDirection());
    }

    /**
     * Refills this weapon with ammo.
     */
    public void reload() {
	reloading = true;
	rechargeTimer.setTimer(reloadTime);
	ammo = maxAmmo;
	playReloadSound();
    }

    /**
     * Adds a bullet entity to the entityHandler and updates the ammo logic
     */
    public void onWeaponAttack(EntityHandler entityHandler, SpriteHandler spriteHandler) {
	if (ammo > 0 && rechargeTimer.isComplete()) {
	    Bullet bullet;
	    Vector2D bulletPosition = Vector2D.getSum(owner.getPosition(), getCorrectAttackOffset());
	    double bulletRotation = owner.getAttackRotation();
	    switch (this.bulletType) {
		case PIERCING -> {
		    bullet = new PiercingBullet(bulletPosition, bulletRotation, spriteHandler, entityHandler, speed, damage, owner,
						imageLoader, audioLoader);
		}
		case EXPLOSIVE -> {
		    bullet = new ExplosiveBullet(bulletPosition, bulletRotation, spriteHandler, entityHandler, speed, damage, owner,
						 imageLoader, audioLoader);
		}
		default -> {
		    bullet = new Bullet(bulletPosition, bulletRotation, spriteHandler, entityHandler, speed, damage, owner, imageLoader,
					audioLoader);
		}
	    }

	    playAttackSound();
	    entityHandler.add(bullet);

	    ammo -= 1;
	    rechargeTimer.setTimer(cooldownTime);
	}
    }

    @Override public void draw(Graphics g, GameComponent gc, int direction, int x, int y, int textureSideLength) {
	//offset needed since weapon sprite sheets have different dimensions from char sheets
	int offset = (getTexture().get(0).getWidth() - textureSideLength) / 2;
	g.drawImage(texture.get(direction), x - offset, y - offset, gc);
    }
}
