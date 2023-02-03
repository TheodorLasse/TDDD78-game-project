package se.liu.thela038_filjo653.sprites.collectables;

import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.Player;

/**
 * Collectable that gives the player cash when collected.
 */
public class CCash extends Collectable
{
    private final int cashAmount;

    public CCash(final Vector2D position, final EntityHandler entityHandler, final SpriteHandler spriteHandler, final int cashAmount,
		 final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, imageLoader.getImage(ImageLoader.ImageName.MONEY), entityHandler, spriteHandler, imageLoader, audioLoader);

	this.cashAmount = cashAmount;
    }

    @Override protected void onCollect(final Player player) {
	player.addCash(cashAmount);
    }
}
