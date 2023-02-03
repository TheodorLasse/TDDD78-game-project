package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.GameComponent;
import se.liu.thela038_filjo653.Rotation;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.collectables.Collectable;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * The Entity class is for every object in the game that needs collision-logic, and is updated on every frame.
 */
public abstract class Entity implements Sprite
{
    protected CollisionType collisionType = null;
    protected Vector2D position;
    protected Vector2D velocity;
    protected Vector2D size;
    protected Rotation rotation;
    protected BufferedImage texture;
    protected ImageLoader imageLoader;
    protected AudioLoader audioLoader;
    protected EntityHandler entityHandler;
    protected SpriteHandler spriteHandler;
    protected static final CollisionType COLLISION_TYPE = CollisionType.ENTITY;

    protected Entity(final Vector2D position, final Vector2D size, final double rotation, final BufferedImage texture,
		     final ImageLoader imageLoader, final EntityHandler entityHandler, final SpriteHandler spriteHandler,
		     final AudioLoader audioLoader)
    {
	this.position = position;
	this.size = size;
	this.rotation = new Rotation(rotation);
	this.texture = texture;
	this.imageLoader = imageLoader;
	this.audioLoader = audioLoader;
	this.entityHandler = entityHandler;
	this.spriteHandler = spriteHandler;
	setCollisionType(COLLISION_TYPE);

	velocity = new Vector2D();
    }

    /**
     * Follows the class hierarchy down to default entity collide unless interrupted
     * e.g collidePlayer -> collideLivingEntity -> baseCollide.
     *
     * Doing this in a more object oriented way is hard since collisions happen in pairs where
     * both entities need to know which type the other is. For example, a bullet needs to know
     * if the entity it hit is a LivingEntity where it will destory itself or if it hit a wall
     * where it ignores the collision.
     */
    public void onCollide(Entity entity){
        switch (entity.getCollisionType()){
	    case LIVING_ENTITY -> collideLivingEntity((LivingEntity) entity);
	    case WALL -> collideWall((Wall) entity);
	    case BULLET -> collideBullet((Bullet) entity);
	    case PLAYER -> collidePlayer((Player) entity);
	    case ENEMY -> collideEnemy((Enemy) entity);
	    case COLLECTABLE -> collideCollectable((Collectable)entity);
	    case ENTITY -> baseCollide(entity);
	}
    }

    /**
     * Follows the class hierarchy down to default entity collide unless interrupted
     * e.g collidePlayer -> collideLivingEntity -> baseCollide
     */
    public void collideLivingEntity(LivingEntity entity){ baseCollide(entity);}

    /**
     * Follows the class hierarchy down to default entity collide unless interrupted
     * e.g collidePlayer -> collideLivingEntity -> baseCollide
     */
    public void collideWall(Wall entity){ baseCollide(entity);}

    /**
     * Follows the class hierarchy down to default entity collide unless interrupted
     * e.g collidePlayer -> collideLivingEntity -> baseCollide
     */
    public void collideBullet(Bullet entity){ baseCollide(entity);}

    /**
     * Follows the class hierarchy down to default entity collide unless interrupted
     * e.g collidePlayer -> collideLivingEntity -> baseCollide
     */
    public void collidePlayer(Player entity){ collideLivingEntity(entity);}

    /**
     * Follows the class hierarchy down to default entity collide unless interrupted
     * e.g collidePlayer -> collideLivingEntity -> baseCollide
     */
    public void collideEnemy(Enemy entity){ collideLivingEntity(entity);}

    /**
     * Follows the class hierarchy down to default entity collide unless interrupted
     * e.g collidePlayer -> collideLivingEntity -> baseCollide
     */
    public void collideCollectable(Collectable entity){ baseCollide(entity);}

    /**
     * The base collide logic. Base implementation is to do nothing.
     * Unless interrupted all collides follow the hierarchy down to this one
     * e.g collidePlayer -> collideLivingEntity -> baseCollide
     */
    public void baseCollide(Entity entity){}

    /**
     * Updates the entity.
     */
    public void update(DeltaTime deltaTime) {
	// Update position from velocity
	position.add(Vector2D.getProduct(velocity, deltaTime.getSeconds()));
    }

    /**
     * Checks if the entitiy is in motion
     */
    public boolean isMoving() {
	return velocity.getLength() > 0;
    }

    @Override public Vector2D getPosition() {
	return position.copy();
    }

    @Override public Vector2D getSize() {
	return size.copy();
    }

    @Override public double getRotation() {
	return rotation.getRadians();
    }

    public CollisionType getCollisionType() {
	return this.collisionType;
    }

    /**
     * Returns the entity's collision area
     */
    public Area getCollisionArea(){
	return new Area(new Rectangle((int)getPosition().getX(), (int)getPosition().getY(), (int)getSize().getX(), (int)getSize().getY()));
    }

    /**
     * Returns the center of the entity's collision area.
     */
    public Vector2D getCollisionCenter() {
	Rectangle2D bounds = getCollisionArea().getBounds2D();
	return new Vector2D(bounds.getCenterX(), bounds.getCenterY());
    }



    protected void setSize(Vector2D size){
        this.size = size;
    }

    protected void setTexture(BufferedImage image){
	this.texture = image;
    }

    protected BufferedImage getTexture() {
	return texture;
    }

    protected void setCollisionType(CollisionType collisionType){
        this.collisionType = collisionType;
    }

    @Override public void draw(final Graphics g, final GameComponent gc) {
	g.drawImage(getTexture(), (int) position.getX(), (int) position.getY(), gc);
    }

}
