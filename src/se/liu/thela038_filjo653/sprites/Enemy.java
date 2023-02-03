package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.collectables.CAmmo;
import se.liu.thela038_filjo653.sprites.collectables.CCash;
import se.liu.thela038_filjo653.sprites.collectables.CHealth;
import se.liu.thela038_filjo653.time.DeltaTimer;
import se.liu.thela038_filjo653.weapons.WeaponFactory;
import se.liu.thela038_filjo653.weapons.WeaponType;

import java.awt.image.BufferedImage;

/**
 * The Enemy class contains an AI that tracks and attacks a given target, and should be extended by all enemies.
 */
public class Enemy extends LivingEntity
{
    protected Entity target; /** The enemy will try to attack this target. */
    protected int scoreWorth;
    protected double attackRange;
    protected DeltaTimer staggerTimer = new DeltaTimer();
    protected static final double ZOMBIE_ANIMATION_TICK = 0.7;

    private final static double DROP_PROBABILITY = 0.8;


    public Enemy(final Vector2D position, final BufferedImage startTexture, final int health, final int speed, Entity target, WeaponType weaponType, double attackRange,
		  int scoreWorth, final EntityHandler entityHandler, final SpriteHandler spriteHandler,
		 ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, new Vector2D(startTexture.getWidth(), startTexture.getHeight()), 0, startTexture, entityHandler, spriteHandler, health, speed,
	      imageLoader, audioLoader);

	this.scoreWorth = scoreWorth;
	this.attackRange = attackRange;
	this.target = target;

	setAnimationTick(ZOMBIE_ANIMATION_TICK);
	setWeapon(WeaponFactory.createWeapon(weaponType, this, imageLoader, audioLoader));
	setCollisionType(CollisionType.ENEMY);
    }

    public int getScoreWorth() {
	return scoreWorth;
    }

    @Override public void update(final DeltaTime deltaTime) {
        staggerTimer.update(deltaTime);

	ai();

	//every enemy has a 1/10000 chance to play a growl sound every game tick
	final double growlProbability = 0.0001;
	if (RND.nextDouble() <= growlProbability) {
	    audioLoader.playGrowlSound();
	}
	super.update(deltaTime);
    }

    @Override public void collideWall(final Wall entity) {
	if (!entity.isBarrier()) {
	    super.collideWall(entity);
	}
    }

    @Override public void collideBullet(final Bullet entity) {
	super.collideBullet(entity);
	staggerTimer.setTimer(1);
    }

    @Override protected void onDeath(LivingEntity killer) {
	killer.onKill(scoreWorth);
	super.onDeath(killer);

	audioLoader.playDeathSound();

	if (RND.nextDouble() <= DROP_PROBABILITY) {
	    entityHandler.add(getDrop());
	}
    }

    @Override public boolean isEnemy() {
	return true;
    }

    /**
     * Returns a new vector pointing at the target.
     *
     * @return A new Vector.
     */
    protected Vector2D getTargetDirection() {
	return Vector2D.pointAt(getCollisionCenter(), target.getCollisionCenter());
    }

    /**
     * Returns the angle between this enemy's weapon and the target.
     *
     * @return The rotation.
     */
    @Override public double getAttackRotation() {
	// The position of the "tip" of the weapon.
	Vector2D weaponOrigin = Vector2D.getSum(getPosition(), weapon.getCorrectAttackOffset());

	// Vector pointing from the weapon to the target
	Vector2D direction = Vector2D.pointAt(weaponOrigin, target.getCollisionCenter());

	return direction.getAngle().getRadians();
    }

    /**
     * Returns the distance to the target.
     *
     * @return Distance as double.
     */
    protected double getTargetDistance() {
	return getTargetDirection().getLength();
    }

    /**
     * Updates the enemy AI.
     */
    protected void ai() {
	// Vector pointing to target
	Vector2D targetDir = getTargetDirection();

	// Look at the target
	rotation = targetDir.getAngle();

	// Updates the velocity to move towards the target
	velocity.setTo(targetDir);
	double currentSpeed;
	if (staggerTimer.isComplete()) {
	    currentSpeed = speed;
	}
	else {
	    final double knockBackMultiplier = 0.2;
	    currentSpeed = speed * knockBackMultiplier;
	}
	velocity.setLength(currentSpeed);

	// Attack
	if (getTargetDistance() <= attackRange && staggerTimer.isComplete()) {
	    attack();
	}

	// Reload if no ammo left
	if (weapon.getAmmo() == 0) {
	    weapon.reload();
	}
    }

    /**
     * Returns an entity that should be dropped when this entity dies.
     *
     * @return An entity.
     */
    private Entity getDrop() {
        final int amountOfDrops = 3;
	switch (RND.nextInt(amountOfDrops)) {
	    case 0 -> {
		final int cashAmount = 15;
		return new CCash(position, entityHandler, spriteHandler, cashAmount, imageLoader, audioLoader);
	    }
	    case 1 -> {
		final int healthAmount = 10;
		return new CHealth(position, entityHandler, spriteHandler, healthAmount, imageLoader, audioLoader);
	    }
	    default -> {
		final int ammoAmount = 3;
		return new CAmmo(position, entityHandler, spriteHandler, ammoAmount, imageLoader, audioLoader);
	    }
	}
    }
}
