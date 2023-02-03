package se.liu.thela038_filjo653;

import se.liu.thela038_filjo653.sprites.Sprite;

import javax.swing.*;
import java.awt.*;

/**
 * Class for drawing on the JFrame created in the Game class.
 */
public class GameComponent extends JComponent
{
    private Game game;

    public GameComponent(Game game){
        this.game = game;

    }

    @Override
    public Dimension getPreferredSize(){
	return new Dimension(game.getWidth(), game.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        // Draw all sprites
        for (Sprite sprite : game.getSpriteIterator()) {
            sprite.draw(g, this);
        }
    }
}
