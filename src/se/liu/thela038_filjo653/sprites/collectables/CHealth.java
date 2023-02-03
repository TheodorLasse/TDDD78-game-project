package se.liu.thela038_filjo653.sprites.collectables;

import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.Player;


/**
 * Collectable that gives the player health when collected.
 */
public class CHealth extends Collectable
{
    private final int healthAmount;

    public CHealth(final Vector2D position, final EntityHandler entityHandler, final SpriteHandler spriteHandler,
		   final int healthAmount, final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, imageLoader.getImage(ImageLoader.ImageName.HEALTH_KIT), entityHandler, spriteHandler, imageLoader, audioLoader);

	this.healthAmount = healthAmount;
    }

    @Override protected void onCollect(final Player player) {
	player.addHealth(healthAmount);
    }
}
