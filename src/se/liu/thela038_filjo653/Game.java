package se.liu.thela038_filjo653;

import se.liu.thela038_filjo653.enemies.EnemySpawner;
import se.liu.thela038_filjo653.input.KeyHandler;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.Player;
import se.liu.thela038_filjo653.sprites.Sprite;
import se.liu.thela038_filjo653.sprites.SpriteTexture;
import se.liu.thela038_filjo653.sprites.Wall;
import se.liu.thela038_filjo653.time.DeltaTime;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Class where the entire game is implemented. Controlls the central update function which controls deltaTime and calls upon all other
 * updates etc. Also initializes the game, shows the window and the end screen.
 */
public class Game
{
    private static final double MAX_FPS = 144;
    private static final Random RND = new Random();
    private EntityHandler entityHandler = null;
    private SpriteHandler spriteHandler = null;
    private ImageLoader imageLoader;
    private AudioLoader audioLoader;
    private EnemySpawner enemySpawner = null;
    private Player player = null;
    private volatile boolean stopGame;
    private int width;
    private int height;
    private Point screenLocation = null;
    private SpriteTexture cashText = null;
    private SpriteTexture scoreText = null;
    private SpriteTexture ammoText = null;
    private int wave;
    private GameComponent gameComponent;

    private final Logger logger = Logger.getLogger("");

    public Game() {
	setUpLogger();

	imageLoader = new ImageLoader();
	audioLoader = new AudioLoader();

	// Try to load all images and audio.
	// If the loading fails, then exit the program because there is no point in running the game without these resources.
	try {
	    imageLoader.loadImages();
	    audioLoader.loadAudio();
	} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
	    logger.log(Level.SEVERE, e.toString(), e);
	    // This triggers a CatchWithExit warning in the automatic code inspection. It can be ignored because the game should not run
	    // without resources.
	    System.exit(1);
	}

	BufferedImage background = imageLoader.getImage(ImageLoader.ImageName.BACKGROUND);
	width = background.getWidth();
	height = background.getHeight();

	gameComponent = new GameComponent(this);
	show();
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    private void updateScreenLocation() {
	screenLocation.setLocation(gameComponent.getLocationOnScreen());
    }

    private void addWalls() {
	AffineTransform at = new AffineTransform();

	List<Vector2D> carPositions = new ArrayList<>();
	final int cars = 5;
	for (int i = 0; i < cars; i++) {
	    final int minWidth = (int) (width * (double) (i) / (cars + 1));
	    final int maxWidth = (int) (width * (double) (i + 1) / (cars + 1));
	    final double minHeightPercent = 0.3, maxHeightPercent = 0.5;
	    final int minHeight = (int) (height * minHeightPercent);
	    final int maxHeight = (int) (height * maxHeightPercent);

	    // Random position between min/max values
	    final int posX = RND.nextInt(maxWidth - minWidth) + minWidth;
	    final int posY = RND.nextInt(maxHeight - minHeight) + minHeight;
	    carPositions.add(new Vector2D(posX, posY));
	}
	carPositions.sort(new Vector2D.Vector2DComparatorHeight()); //sorts cars in drawing order

	//obstacles (i.e cars):
	for (Vector2D carPos : carPositions) {
	    entityHandler.add(new Wall(carPos, 0, entityHandler, spriteHandler, true, at, false, imageLoader, audioLoader));
	}
    }

    private void addBarriers() {
	final double houseWidthPercent = 0.33; //percent of the image's width/height covered by "unwalkable" house
	final double houseHeightPercent = 0.09;
	final int margin = 100;
	Vector2D houseSize = new Vector2D(width * houseWidthPercent, height * houseHeightPercent);
	List<Vector2D> corners = new ArrayList<>();
	corners.add(new Vector2D(0, 0));
	corners.add(new Vector2D(0, height - houseSize.getY()));
	corners.add(new Vector2D(width - houseSize.getX(), 0));
	corners.add(new Vector2D(width - houseSize.getX(), height - houseSize.getY()));

	//map barriers:
	final int doubleMargin = margin * 2;
	boolean isMapBarrier = true;
	entityHandler
		.add(new Wall(new Vector2D(-margin, -margin), 0, entityHandler, spriteHandler, new Vector2D(width + doubleMargin, margin),
			      isMapBarrier, imageLoader, audioLoader));
	entityHandler
		.add(new Wall(new Vector2D(-margin, height), 0, entityHandler, spriteHandler, new Vector2D(width + doubleMargin, margin),
			      isMapBarrier, imageLoader, audioLoader));
	entityHandler
		.add(new Wall(new Vector2D(-margin, -margin), 0, entityHandler, spriteHandler, new Vector2D(margin, height + doubleMargin),
			      isMapBarrier, imageLoader, audioLoader));
	entityHandler
		.add(new Wall(new Vector2D(width, -margin), 0, entityHandler, spriteHandler, new Vector2D(width, height + doubleMargin),
			      isMapBarrier, imageLoader, audioLoader));
	//house barriers
	for (Vector2D corner : corners) {
	    entityHandler.add(new Wall(corner, 0, entityHandler, spriteHandler, houseSize.copy(), false, imageLoader, audioLoader));
	}
    }


    private void init() {
	spriteHandler = new SpriteHandler();
	entityHandler = new EntityHandler();
	stopGame = false;
	wave = 0;

	BufferedImage foregroundImg = imageLoader.getImage(ImageLoader.ImageName.FOREGROUND);
	Vector2D foregroundPos = new Vector2D(width - foregroundImg.getWidth(), height - foregroundImg.getHeight());
	SpriteTexture foreground = new SpriteTexture(foregroundPos, 0, foregroundImg);

	spriteHandler.setBackground(imageLoader.getImage(ImageLoader.ImageName.BACKGROUND));
	spriteHandler.add(foreground, SpriteLayer.LAST);

	final double playerXPosPercent = 0.5, playerYPosPercent = 0.8;
	final Vector2D playerStartingPos = new Vector2D(width * playerXPosPercent, height * playerYPosPercent);
	this.player = new Player(playerStartingPos, 0, spriteHandler, entityHandler, imageLoader, audioLoader);

	addWalls();
	addBarriers();

	enemySpawner = new EnemySpawner(this, entityHandler, spriteHandler, player, imageLoader, audioLoader);

	entityHandler.add(player);

	audioLoader.playMusic();

	player.setScreenLocation(screenLocation); //necessary for updating mouse position relative to JComponent
	final KeyHandler keyHandler = new KeyHandler(gameComponent);
	keyHandler.addKeyListener(player);
    }

    private void onGameOver(long gameOverTime) {
	BufferedImage image = imageLoader.getImage(ImageLoader.ImageName.DEATH);

	final double endPosXPercent = 0.5, endPosYPercent = 0.2;
	final double endPosX = (getWidth() - image.getWidth()) * endPosXPercent;
	final double endPosY = (getHeight() - image.getHeight()) * endPosYPercent;
	Vector2D endPos = new Vector2D(endPosX, endPosY);

	SpriteTexture endScreen = new SpriteTexture(endPos, 0, image);
	spriteHandler.add(endScreen, SpriteLayer.LAST);
	spriteHandler.update(new DeltaTime(0));
	gameComponent.repaint();

	while (stopGame) {
	    final int secondsUntilRestart = 7;
	    if (System.nanoTime() - gameOverTime > TimeUnit.SECONDS.toNanos(secondsUntilRestart)) start();
	}
    }


    /**
     * Starts the game.
     */
    public void start() {
	init();

	long lastUpdate = System.nanoTime();

	// Minimum number of nanoseconds for each game loop
	final double minFrameTime = DeltaTime.NANO_SECONDS_IN_SECOND / MAX_FPS;

	while (!stopGame) {
	    long startTime = System.nanoTime();
	    long deltaTime = startTime - lastUpdate;
	    lastUpdate = startTime;

	    update(new DeltaTime(deltaTime));
	    gameComponent.repaint();

	    long totalTime = System.nanoTime() - startTime;
	    // To avoid using 100% of cpu core when not needed, sleep until the minimum frame time is met
	    if (totalTime < minFrameTime) {
		try {
		    TimeUnit.NANOSECONDS.sleep((long) minFrameTime - totalTime);
		} catch (InterruptedException e) {
		    logger.log(Level.WARNING, e.toString(), e);
		}
	    }
	}

	onGameOver(lastUpdate);
    }


    /**
     * Updates the game.
     */
    private void update(DeltaTime deltaTime) {
	updateScreenLocation();
	updateText();
	wave = enemySpawner.update(deltaTime, wave);
	entityHandler.update(deltaTime);
	spriteHandler.update(deltaTime);
	if (!player.isAlive()) {
	    stopGame = true;
	}
    }

    private void updateText() {
        final int scoreBottomOffset = 5;
        final int scorePosY = height - scoreBottomOffset;
	Vector2D scorePos = new Vector2D(0, scorePosY);
	final int scoreHeight = 25;
	Color scoreColor = Color.BLACK;
	String scoreString = "SCORE: " + player.getScore();

	final int cashPosY = scorePosY - scoreHeight;
	Vector2D cashPos = new Vector2D(0, cashPosY);
	Color cashColor = Color.YELLOW;
	String cashString = "$" + player.getCash();

	final int ammoRightOffset = 200, ammoBottomOffset = 5;
	final int ammoPosX = width - ammoRightOffset;
	final int ammoPosY = height - ammoBottomOffset;
	Vector2D ammoPos = new Vector2D(ammoPosX, ammoPosY);
	Color ammoColor = Color.BLACK;
	String ammoString = "Ammo:" + player.getWeaponAmmo() + " / " + player.getAmmunitionMagazines();

	// removes the old text and adds the updated one
	cashText = updateIndividualText(cashText, cashPos, cashColor, cashString);
	scoreText = updateIndividualText(scoreText, scorePos, scoreColor, scoreString);
	ammoText = updateIndividualText(ammoText, ammoPos, ammoColor, ammoString);
    }

    private SpriteTexture updateIndividualText(SpriteTexture oldText, Vector2D position, Color color, String str) {
	SpriteTexture newText = new SpriteTexture(position, 0, color, str);
	spriteHandler.renew(newText, oldText, SpriteLayer.LAST);
	return newText;
    }

    /**
     * Returns an iterable var of all sprites. Basically merges all sprites into one list for gameComponent. The list's order matters,
     * sprites are drawn before entities, etc
     */
    public Iterable<Sprite> getSpriteIterator() {
	List<Sprite> sprites = new ArrayList<>();

	addIterator(sprites, spriteHandler.getLayerIterator(SpriteLayer.FIRST));
	addIterator(sprites, entityHandler.getIterator());
	addIterator(sprites, spriteHandler.getLayerIterator(SpriteLayer.LAST));

	return sprites;
    }

    public void addIterator(List<Sprite> sprites, Iterable<Sprite> it) {
	for (Sprite s : it) {
	    sprites.add(s);
	}
    }

    /**
     * Creates the game window.
     */
    private void show() {
	JFrame frame = new JFrame("Game");
	frame.setLayout(new BorderLayout());
	frame.getContentPane().add(gameComponent);
	frame.setResizable(false);
	frame.pack();
	frame.setVisible(true);
	screenLocation = gameComponent.getLocationOnScreen();
    }

    /**
     * Adds a new FileHandler to the logger. If the FileHandler fails, a ConsoleHandler is used as backup.
     */
    private void setUpLogger() {
	try {
	    FileHandler fh = new FileHandler("LogFile.log");
	    logger.addHandler(fh);
	    fh.setFormatter(new SimpleFormatter());
	} catch (IOException e) {
	    // This catch clause triggers a "CatchFallthrough" warning in the automatic code inspection. The warning can be ignored because
	    // the exception is handled in the catch clause (In this case by adding a ConsoleHandler instead of the FileHandler).

	    logger.log(Level.SEVERE, e.toString(), e);

	    // If the logger don't have any handlers, add a ConsoleHandler
	    if (logger.getHandlers().length == 0) {
		ConsoleHandler ch = new ConsoleHandler();
		logger.addHandler(ch);
	    }
	}
    }
}
