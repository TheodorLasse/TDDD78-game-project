package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.GameComponent;
import se.liu.thela038_filjo653.Rotation;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.SpriteLayer;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.weapons.Weapon;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * Class representing an Entity that is living, i.e moves, has health and a weapon etc.
 */
public abstract class LivingEntity extends Entity
{
    protected int health;
    protected int maxHealth;
    protected int speed;
    protected Weapon weapon = null;
    protected int direction;
    protected List<BufferedImage> baseTexture = null;
    protected List<BufferedImage> walkingTexture = null;
    protected List<BufferedImage> idleTexture = null;
    protected SpriteTexture healthbarMissing = null;
    protected SpriteTexture healthbarMax = null;
    protected double timer = 0;
    protected double animationTick;
    protected final static CollisionType COLLISION_TYPE = CollisionType.LIVING_ENTITY;
    protected final static Random RND = new Random();

    private Vector2D previousPosition;
    private final static int CHAR_DIRS = 8;

    protected LivingEntity(final Vector2D position, final Vector2D size, final double rotation, final BufferedImage texture,
			   final EntityHandler entityHandler, final SpriteHandler spriteHandler, final int maxHealth,
			   final int speed, final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, size, rotation, texture, imageLoader, entityHandler, spriteHandler, audioLoader);
	this.maxHealth = maxHealth;
	this.health = maxHealth;
	this.speed = speed;
	this.direction = getCharacterDirection();
	setCollisionType(COLLISION_TYPE);
	final double defaultAnimationTick = 0.25;
	setAnimationTick(defaultAnimationTick);

	previousPosition = position.copy();
    }

    /**
     * Adds the specified amount of health this object.
     *
     * @param health Health to add.
     */
    public void addHealth(final int health) {
	this.health = Math.min(this.health + health, maxHealth);
    }

    /**
     * Subtracts the specified amount of health this object.
     *
     * @param health Health to add.
     */
    public void subtractHealth(final int health, final LivingEntity source) {
	this.health -= health;
	addBlood();
	audioLoader.playMeatSound();
	if(this.health <= 0){
	    onDeath(source);
	}
    }

    @Override public void update(DeltaTime deltaTime) {
        // Save the position before this entity is moved
        previousPosition = position.copy();

	super.update(deltaTime);

	timer += deltaTime.getSeconds();
	weapon.update(deltaTime);

	updateHealthbars();
	updateCharRotation();
	updateTexture();
    }

    @Override public Area getCollisionArea(){
	return new Area(new Ellipse2D.Double(getPosition().getX(), getPosition().getY(), getSize().getX(), getSize().getY()));
    }

    @Override public void collideWall(final Wall wall){
	Vector2D originalPos = position.copy();
	// Move back to previous position
        position.setTo(previousPosition);

        // Test move Y
	position.setY(originalPos.getY());

	// If collides, move back
	Area collisionArea = getCollisionArea();
	collisionArea.intersect(wall.getCollisionArea());
	if (!collisionArea.isEmpty()) {
	    position.setY(previousPosition.getY());
	}

	// Test move X
	position.setX(originalPos.getX());

	// If collides, move back
	collisionArea = getCollisionArea();
	collisionArea.intersect(wall.getCollisionArea());
	if (!collisionArea.isEmpty()) {
	    position.setX(previousPosition.getX());
	}
    }


    /**
     * Updates the texture of the LivingEntity dependent on direction and isMoving
     */
    protected void updateTexture() {
	List<BufferedImage> alternateImages;
	if (isMoving()) {
	    alternateImages = walkingTexture;
	} else {
	    alternateImages = idleTexture;
	}

	if (timer % (animationTick * 2) <= animationTick) {
	    setTexture(baseTexture.get(direction));
	} else {
	    setTexture(alternateImages.get(direction));
	}
    }

    /**
     * Updates the different fields impacted by changes in rotation
     */
    protected void updateCharRotation(){
	updateDirection();
    }

    /**
     * Updates the direction of the LivingEntity
     */
    protected void updateDirection(){
	direction = getCharacterDirection(); // Gets a number between 0-7 used for getting the correct images
    }

    /**
     * Updates the healthbar of the LivingEntity
     */
    protected void updateHealthbars(){
	final int barHeight = 4;
	Vector2D healthbarSize = new Vector2D(size.getX(), barHeight);
	Vector2D healthBarRemainingSize = new Vector2D(healthbarSize.getX() * health/maxHealth, barHeight);

	//First add a red bar, then the green bar of remaining health on top
	healthbarMissing = updateIndividualBar(healthbarMissing, healthbarSize, Color.RED);
	healthbarMax = updateIndividualBar(healthbarMax, healthBarRemainingSize, Color.green);
    }

    /**
     * Updates one of the healthbar layers of the LivingEntity
     */
    protected SpriteTexture updateIndividualBar(SpriteTexture oldHealthbar, Vector2D healthbarSize, Color color){
        return updateIndividualBar(oldHealthbar, healthbarSize, color, 0);
    }

    /**
     * Updates one of the healthbar layers of the LivingEntity
     */
    protected SpriteTexture updateIndividualBar(SpriteTexture oldHealthbar, Vector2D healthbarSize, Color color, double heightOffset){
        Vector2D pos = new Vector2D(position.getX(), position.getY() + heightOffset);
	SpriteTexture updatedHealthbar = new SpriteTexture(pos, healthbarSize, 0, color, SpriteType.RECTANGLE);
	spriteHandler.renew(updatedHealthbar, oldHealthbar, SpriteLayer.LAST);
	return updatedHealthbar;
    }

    public int getDirection() {
	return direction;
    }

    /**
     * Gets the rotation value for an attack
     */
    public double getAttackRotation(){
        return this.getRotation();
    }

    public void setWeapon(final Weapon weapon) {
	this.weapon = weapon;
    }

    protected void setAnimationTick(double tick){
        this.animationTick = tick;
    }

    public void setBaseTexture(final List<BufferedImage> baseTexture) {
	this.baseTexture = baseTexture;
    }

    public void setWalkingTexture(final List<BufferedImage> walkingTexture) {
	this.walkingTexture = walkingTexture;
    }

    public void setIdleTexture(final List<BufferedImage> idleTexture) {
	this.idleTexture = idleTexture;
    }

    /**
     * Character model rotates in 8 directions, this gets the closest one
     */
    protected int getCharacterDirection() {
	final double radian = Math.PI;

	// Convert Rotation object to match character models (they don't have the same start point in rotation)
	Rotation charRotation = new Rotation(-getRotation());
	charRotation.addRadians(radian / 2); // this offsets the difference in start point of rotation

	final double sliceOffset = radian/8; // Offset centres the "slices" of the circle to the character model
				   // causing transitions between images to feel a lot more intuitive.

	for (int i = 0; i < CHAR_DIRS; i++) {
	    if (charRotation.getRadians() < (i + 1) * radian / 4 - sliceOffset) { //goes from 0 to 1.75*PI in 8 steps
		return i;
	    }
	}
	return 0; //Due to the offset the last 1/16th part of the circle will end up here
    }

    /**
     * Causes the LivingEntity to attack, i.e shoot a bullet or melee
     */
    protected void attack() {
	weapon.onWeaponAttack(entityHandler, spriteHandler);
    }

    /**
     * Causes the LivingEntity to die
     */
    protected void onDeath(LivingEntity killer){
        spriteHandler.remove(healthbarMissing);
        spriteHandler.remove(healthbarMax);
	entityHandler.remove(this);
    }

    /**
     * Is this entity an enemy
     */
    public boolean isEnemy(){
	return false;
    }

    /**
     * When this entity kills another entity. Base implementation is to do nothing.
     */
    public void onKill(int scoreAdd){}

    @Override public void draw(final Graphics g, final GameComponent gc) {
	super.draw(g, gc);
	if (weapon != null) {
	    weapon.draw(g, gc, direction, (int) position.getX(), (int) position.getY(),
				  baseTexture.get(0).getWidth());
	}
    }

    /**
     * Adds blood to the background from this LivingEntity.
     */
    private void addBlood() {
        List<BufferedImage> bloodImages = imageLoader.getBackgroundBlood();
        BufferedImage bloodImage = bloodImages.get(RND.nextInt(bloodImages.size()));

        int posX = (int) (getCollisionCenter().getX() - bloodImage.getWidth() / 2);
	int posY = (int) (getCollisionCenter().getY() - bloodImage.getHeight() / 2);

	spriteHandler.addToBackground(bloodImage, posX, posY);
    }
}
