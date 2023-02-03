package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.GameComponent;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class representing an immovable wall that blocks the player and if it's not a barrier, it blocks
 * the enemies too. Barriers blocks the player from exiting the map but lets enemies in.
 */
public class Wall extends Entity
{
    private boolean visible;
    private boolean barrier;
    protected static final CollisionType COLLISION_TYPE = CollisionType.WALL;

    public Wall(final Vector2D position, final double rotation, final EntityHandler entityHandler, final SpriteHandler spriteHandler,
                boolean visible, final AffineTransform at, final boolean barrier, final ImageLoader imageLoader, final AudioLoader audioLoader){
        super(position, new Vector2D(), rotation, null, imageLoader, entityHandler, spriteHandler, audioLoader);
        setCollisionType(COLLISION_TYPE);

        List<BufferedImage> cars = new ArrayList<>();
        cars.add(imageLoader.getImage(ImageLoader.ImageName.CAR_1));
        cars.add(imageLoader.getImage(ImageLoader.ImageName.CAR_2));

        this.visible = visible;
        this.barrier = barrier;
        if (visible){
            Random rnd = new Random();
            setTexture(ImageLoader.rotateImage(rotation, cars.get(rnd.nextInt(cars.size())), at));
            setSize(new Vector2D(texture.getWidth(), texture.getHeight()));
        }
    }

    /**
     * For creating invisble walls, needs no texture param but needs a size Vector instead
     */
    public Wall(final Vector2D position, final double rotation, final EntityHandler entityHandler, final SpriteHandler spriteHandler,
                final Vector2D size, final boolean barrier, final ImageLoader imageLoader, final AudioLoader audioLoader){
        super(position, size, rotation, null, imageLoader, entityHandler, spriteHandler, audioLoader);
        setCollisionType(COLLISION_TYPE);
        this.visible = false;
        this.barrier = barrier;
    }

    public boolean isBarrier() {
        return barrier;
    }

    @Override public Area getCollisionArea() {
        if (visible){ //the "car/obstacle wall" hitbox is different from invisible wall hitboxes
            final int adjustXPos = 20;
            final int adjustWidth = 40;
            final int adjustHeight = 50;
            Rectangle collision =
                    new Rectangle((int)getPosition().getX() + adjustXPos, (int)getPosition().getY(),
                                         (int)getSize().getX() - adjustWidth, (int)getSize().getY() - adjustHeight);
            return new Area(collision);
        }
        else {return super.getCollisionArea();}
    }


    @Override public void draw(final Graphics g, final GameComponent gc) {
        if (visible) {
            super.draw(g, gc);
        }
    }
}
