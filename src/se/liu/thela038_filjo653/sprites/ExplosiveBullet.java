package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;

/**
 * Class representing a bullet that will create an explosion on impact. Created by the rpg.
 */
public class ExplosiveBullet extends Bullet
{
    public ExplosiveBullet(final Vector2D position, final double rotation, final SpriteHandler spriteHandler,
			   final EntityHandler entityHandler, final int speed, final int damage,
			   final LivingEntity owner, final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, rotation, spriteHandler, entityHandler, speed, damage, owner, imageLoader, audioLoader);
	setTexture(imageLoader.getImage(ImageLoader.ImageName.BULLET));
    }

    @Override protected void removeThis() {
        int explosionSpeed = 0;
        Entity explosion = new Explosion(getPosition(),  getRotation(), spriteHandler,
					 entityHandler, explosionSpeed, damage, owner, imageLoader, audioLoader);
        audioLoader.playSound(AudioLoader.AudioEffect.EXPLOSION);
        entityHandler.add(explosion);
	super.removeThis();
    }
}
