package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a bullet that can pierce multiple enemies
 */
public class PiercingBullet extends Bullet
{
    private List<Entity> hitEntities = new ArrayList<>();
    private int amountOfHitEntities = 0;

    public PiercingBullet(final Vector2D position, final double rotation, final SpriteHandler spriteHandler,
			  final EntityHandler entityHandler, final int speed, final int damage, final LivingEntity owner,
			  final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, rotation, spriteHandler, entityHandler, speed, damage, owner, imageLoader, audioLoader);
    }

    @Override public void collideLivingEntity(final LivingEntity entity) {
	hitEntities.removeIf(Objects::isNull);
	if (!(entity.equals(owner) || hitEntities.contains(entity))){
	    hitEntities.add(entity);
	    amountOfHitEntities ++;
	    super.collideLivingEntity(entity);
	}
    }

    @Override public void baseCollide(final Entity entity) {
        final int pierceDepth = 3;
	if (amountOfHitEntities >= pierceDepth) removeThis();
    }
}
