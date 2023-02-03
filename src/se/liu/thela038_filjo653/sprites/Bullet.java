package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.Rotation;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.SpriteLayer;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.collectables.Collectable;

import java.awt.*;

/**
 * Class representing the bullet entity that is shot by projectileWeapons.
 */
public class Bullet extends Entity
{
    protected static final CollisionType COLLISION_TYPE = CollisionType.BULLET;
    protected double bulletFlightTimer = 1;
    protected double bulletTrailTimer = 0.1;
    protected int damage;
    protected Vector2D lastPosition;
    protected LivingEntity owner;

    public Bullet(final Vector2D position, final double rotation, final SpriteHandler spriteHandler, final EntityHandler entityHandler,
		  final int speed, final int damage, final LivingEntity owner, final ImageLoader imageLoader,
		  final AudioLoader audioLoader){
	    super(position,
		  new Vector2D(imageLoader.getImage(ImageLoader.ImageName.BULLET).getWidth(), imageLoader.getImage(ImageLoader.ImageName.BULLET).getHeight()),
		  rotation, imageLoader.getImage(ImageLoader.ImageName.BULLET), imageLoader, entityHandler, spriteHandler, audioLoader);
	    this.damage = damage;
	    this.lastPosition = position.copy();
	    this.owner = owner;
	    setCollisionType(COLLISION_TYPE);
	    velocity.setTo(new Vector2D(new Rotation(rotation), speed)); //sets the speed of the bullet, needs a rotation object, not the
									//actual rotation value (which is a double between 0 - 2*PI)
    }


    @Override public void update(final DeltaTime deltaTime) {
	super.update(deltaTime);
	bulletFlightTimer -= deltaTime.getSeconds();
	if (bulletFlightTimer < 0){
	    removeThis();
	}
	if (bulletTrailTimer > 0){
	spriteHandler.add(new SpriteTexture(getPosition(), lastPosition, getRotation(), Color.WHITE, SpriteType.LINE),
			  bulletTrailTimer, SpriteLayer.LAST);
	}
	lastPosition = position.copy();
    }

    @Override public void baseCollide(final Entity entity) {
	if (!entity.equals(owner)){
	    removeThis();
	}
    }

    @Override public void collideWall(final Wall entity) {}

    @Override public void collideCollectable(final Collectable entity) {}

    @Override public void collideLivingEntity(final LivingEntity entity) {
	if (!owner.equals(entity)){
	    entity.subtractHealth(getDamage(), owner);
	}
	super.collideLivingEntity(entity);
    }

    public int getDamage() {
	return damage;
    }

    protected void setBulletTrailTimer(final double bulletTrailTimer) {
	this.bulletTrailTimer = bulletTrailTimer;
    }

    protected void removeThis(){
        entityHandler.remove(this);
    }
}
