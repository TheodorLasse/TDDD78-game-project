package se.liu.thela038_filjo653.sprites.collectables;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.CollisionType;
import se.liu.thela038_filjo653.sprites.Entity;
import se.liu.thela038_filjo653.sprites.Player;
import se.liu.thela038_filjo653.time.DeltaTimer;

import java.awt.image.BufferedImage;

/**
 * Abstract class for collectables that can be collected by the player. It does not give the player anything when collected, that needs to
 * be handled by sub-classes.
 */
public abstract class Collectable extends Entity
{
    protected static final CollisionType COLLISION_TYPE = CollisionType.COLLECTABLE;
    protected static final int SIDE_LENGTH = 30;
    protected static final double START_ROTATION = 0;

    private static final double BOUNCE_HEIGHT = 5;
    private static final double BOUNCE_FREQUENCY = 1;
    private final DeltaTimer bounceTimer = new DeltaTimer();
    private final Vector2D defaultPosition;

    protected Collectable(final Vector2D position, final BufferedImage texture, final EntityHandler entityHandler,
			  final SpriteHandler spriteHandler, final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, new Vector2D(SIDE_LENGTH, SIDE_LENGTH), START_ROTATION, texture, imageLoader, entityHandler, spriteHandler, audioLoader);
	setCollisionType(COLLISION_TYPE);
	this.defaultPosition = position.copy();
    }

    @Override public void update(final DeltaTime deltaTime) {
	super.update(deltaTime);

	bounceTimer.update(deltaTime);
	bounce();
    }

    @Override public void collidePlayer(final Player player) {
	super.collidePlayer(player);

	onCollect(player);
	audioLoader.playSound(AudioLoader.AudioEffect.PICKUP);
	entityHandler.remove(this);
    }

    /**
     * Is executed when the player collects this collectable. Needs to be implemented by all subclasses.
     *
     * @param player Player that collected this collectable.
     */
    protected abstract void onCollect(Player player);

    /**
     * Makes the collectable bounce up and down.
     *
     * @param deltaTime Time between update.
     */
    protected void bounce() {
        position.setY(defaultPosition.getY() + Math.sin(bounceTimer.getElapsedSeconds() * BOUNCE_FREQUENCY * 2 * Math.PI) * BOUNCE_HEIGHT);
    }
}
