package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.GameComponent;
import se.liu.thela038_filjo653.Vector2D;

import java.awt.*;

/**
 * The most basic type that can be rendered inside the game.
 */
public interface Sprite
{
    public Vector2D getPosition();
    public Vector2D getSize();
    public double getRotation();

    public void draw(final Graphics g, final GameComponent gc);
}
