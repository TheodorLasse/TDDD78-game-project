package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing an explosion that damages nearby LivingEntities. Created by the explosiveBullet.
 */
public class Explosion extends Bullet
{
    private double timerStop = 1;
    private double timer = 0;
    private List<LivingEntity> hitEntities = new ArrayList<>();

    public Explosion(final Vector2D position, final double rotation, final SpriteHandler spriteHandler, final EntityHandler entityHandler,
		     final int speed, final int damage, final LivingEntity owner, final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
        //getSum function with offset causes centre of explosion to be placed on the bullet's position
	super(Vector2D.getSum(position, new Vector2D((double) -imageLoader.getExplosion().get(0).getWidth() / 2, (double) -imageLoader.getExplosion().get(0).getHeight() / 2)),
	      rotation, spriteHandler, entityHandler, speed, damage, owner, imageLoader, audioLoader);
	setSize(new Vector2D(imageLoader.getExplosion().get(0).getWidth(), imageLoader.getExplosion().get(0).getHeight()));
	setBulletTrailTimer(0);
    }

    @Override public void update(final DeltaTime deltaTime) {
	super.update(deltaTime);
	timer += deltaTime.getSeconds();
	if (timer >= timerStop) {
	    entityHandler.remove(this);
	}
    }

    @Override public void collideLivingEntity(final LivingEntity entity) {
        if (!hitEntities.contains(entity)){
	    hitEntities.add(entity);
	    super.collideLivingEntity(entity);
	}
    }

    @Override public Area getCollisionArea(){
	return new Area(new Ellipse2D.Double(getPosition().getX(), getPosition().getY(), getSize().getX(), getSize().getY()));
    }

    @Override public void baseCollide(final Entity entity) {}

    @Override protected BufferedImage getTexture() {
        int textureIndexSize = imageLoader.getExplosion().size() - 1;
        int index = (int)(timer/timerStop * textureIndexSize);
        if (index > textureIndexSize) index = textureIndexSize;
	return imageLoader.getExplosion().get(index);
    }
}
