package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.GameComponent;
import se.liu.thela038_filjo653.Rotation;
import se.liu.thela038_filjo653.Vector2D;

import java.awt.*;

/**
 * Class representing a texture that can draw itself but is not an Entity, i.e not an object that interacts physically with other objects.
 * Examples: background, healthbars, score etc.
 */
public class SpriteTexture implements Sprite
{
    private Vector2D position;
    private Vector2D size = new Vector2D();
    private Rotation rotation;
    private SpriteType type;
    private Image image = null;
    private Color color = null;
    private String textString = null;
    private int fontSize = DEFAULT_FONT_SIZE;
    private final static int DEFAULT_FONT_SIZE = 24;

    /**
     * Create SpriteTexture with image.
     */
    public SpriteTexture(Vector2D position, double rotation, Image image) {
	this.type = SpriteType.IMAGE;
	this.image = image;
	this.position = position;
	this.size = new Vector2D(position.getX() + image.getWidth(null), position.getY() + image.getHeight(null));
	this.rotation = new Rotation(rotation);

    }

    /**
     * Create SpriteTexture with size and color.
     */
    public SpriteTexture(Vector2D position, Vector2D size, double rotation, Color color, SpriteType type) {
	this.type = type;
	this.color = color;
	this.position = position;
	this.size = size;
	this.rotation = new Rotation(rotation);

    }

    /**
     * Create SpriteTexture with text.
     */
    public SpriteTexture(Vector2D position, double rotation, Color color, String textString, int fontSize) {
	this.type = SpriteType.TEXT;
	this.color = color;
	this.textString = textString;
	this.position = position;
	this.fontSize = fontSize;
	this.rotation = new Rotation(rotation);

    }

    /**
     * Create SpriteTexture with text and color.
     */
    public SpriteTexture(Vector2D position, double rotation, Color color, String textString) {
	this.type = SpriteType.TEXT;
	this.color = color;
	this.textString = textString;
	this.position = position;
	this.rotation = new Rotation(rotation);
    }

    @Override public double getRotation() {
	return rotation.getRadians();
    }

    @Override public Vector2D getPosition() {
	return position;
    }

    @Override public Vector2D getSize() {
	return size;
    }

    @Override public void draw(final Graphics g, final GameComponent gc) {
	int positionX = (int) position.getX();
	int positionY = (int) position.getY();
	switch (type) {
	    case IMAGE -> g.drawImage(image, positionX, positionY, gc);
	    case LINE -> {
		g.setColor(color);
		g.drawLine(positionX, positionY, (int) size.getX(), (int) size.getY());
	    }
	    case TEXT -> {
		Font font = new Font("Verdana", Font.BOLD, fontSize);
		g.setFont(font);
		g.setColor(color);
		g.drawString(textString, positionX, positionY);
	    }
	    case RECTANGLE -> {
		g.setColor(color);
		g.fillRect(positionX, positionY, (int) size.getX(), (int) size.getY());
	    }
	}

    }
}
