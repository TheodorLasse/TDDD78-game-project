package se.liu.thela038_filjo653.weapons;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.GameComponent;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.sprites.CollisionType;
import se.liu.thela038_filjo653.sprites.Entity;
import se.liu.thela038_filjo653.sprites.LivingEntity;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 * Class representing a weapon without projectiles. It creates an area that inflicts damage instead of shooting a bullet.
 */
public class MeleeWeapon implements Weapon
{
    private int range;
    protected LivingEntity owner;
    protected int damage;
    protected double cooldownTime;
    protected double recharging = 0;
    private WeaponType weaponType;
    protected AudioLoader audioLoader;
    private boolean isEnemy;


    public MeleeWeapon(final LivingEntity owner, final int damage, final double cooldownTime, final int range, final WeaponType weaponType,
	    final AudioLoader audioLoader)
    {
	this.owner = owner;
	this.damage = damage;
	this.cooldownTime = cooldownTime;
	this.range = range;
	this.weaponType = weaponType;
	this.audioLoader = audioLoader;
	this.isEnemy = owner.isEnemy();
    }

    @Override public WeaponType getWeaponType() {
	return weaponType;
    }

    @Override public Vector2D getCorrectAttackOffset() {
	return new Vector2D();
    }

    @Override public int getAmmo() {
	return 0;
    }

    @Override public void onWeaponAttack(final EntityHandler entityHandler, final SpriteHandler spriteHandler) {
        if (recharging <= 0) {
	    double xPos = owner.getPosition().getX() - range;
	    double yPos = owner.getPosition().getY() - range;
	    double xSize = owner.getSize().getX() + range;
	    double ySize = owner.getSize().getY() + range;
	    Area attackReach = new Area(new Ellipse2D.Double(xPos, yPos, xSize, ySize));

	    List<Entity> entitiesInRange = entityHandler.entitiesInArea(attackReach);

	    for (Entity entity : entitiesInRange) {
	        CollisionType entityType = entity.getCollisionType();
		if ((entityType == CollisionType.PLAYER && isEnemy) || (entityType == CollisionType.ENEMY && !isEnemy)) {
		    ((LivingEntity) entity).subtractHealth(damage, owner);
		    recharging = cooldownTime;
		}
	    }
	    if (isEnemy) audioLoader.playGrowlSound();
	}
    }

    @Override public void update(final DeltaTime deltaTime) {
	if (recharging > 0){
	    recharging -= deltaTime.getSeconds();
	}
    }

    @Override public void draw(final Graphics g, final GameComponent gc, final int direction,
			       final int x, final int y, final int textureSideLength) {
	//base implementation of meleeWeapon is just using the hands which doesnt have an extra texture
    }

    @Override public void reload() {

    }
}
