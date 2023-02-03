package se.liu.thela038_filjo653;

/**
 * The entrypoint for the game. Creates a game object and calls upon it's start function to initilize the entire game.
 */
public class Main
{
    public static void main(String[] args) {
        // Create and start the game
        Game game = new Game();
        game.start();
    }
}
