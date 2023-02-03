package se.liu.thela038_filjo653.sprites.collectables;

import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.Player;

/**
 * Collectable that gives the player ammunition when collected.
 */
public class CAmmo extends Collectable
{
    private final int ammoAmount;

    public CAmmo(final Vector2D position, final EntityHandler entityHandler, final SpriteHandler spriteHandler,
		 final int ammoAmount, final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, imageLoader.getImage(ImageLoader.ImageName.AMMO), entityHandler, spriteHandler, imageLoader, audioLoader);

	this.ammoAmount = ammoAmount;
    }

    @Override protected void onCollect(final Player player) {
        player.addAmmunitionMagazines(ammoAmount);
    }
}
