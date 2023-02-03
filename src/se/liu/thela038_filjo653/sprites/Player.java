package se.liu.thela038_filjo653.sprites;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.WeaponInventory;
import se.liu.thela038_filjo653.input.GameKeyListener;
import se.liu.thela038_filjo653.input.Key;
import se.liu.thela038_filjo653.input.KeyEvent;
import se.liu.thela038_filjo653.input.KeyState;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.weapons.ProjectileWeapon;
import se.liu.thela038_filjo653.weapons.Weapon;
import se.liu.thela038_filjo653.weapons.WeaponFactory;
import se.liu.thela038_filjo653.weapons.WeaponType;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Map;

import static se.liu.thela038_filjo653.weapons.WeaponFactory.createWeapon;

/**
 * Class representing a player that is controlled by the user.
 */
public class Player extends LivingEntity implements GameKeyListener
{
    private int score;
    private int cash;
    private boolean shooting = false;
    private boolean alive = true;
    private final WeaponInventory inventory = new WeaponInventory();
    private final static int SPEED = 100;
    private final static int MAX_HEALTH = 75;
    protected static final CollisionType COLLISION_TYPE = CollisionType.PLAYER;
    private Point screenLocation = null;
    private SpriteTexture reloadBar = null;

    public Player(final Vector2D position, final double rotation, final SpriteHandler spriteHandler, final EntityHandler entityHandler,
		  final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	super(position, new Vector2D(imageLoader.getCharacterBase().get(0).getWidth(), imageLoader.getCharacterBase().get(0).getHeight()),
	      rotation, null, entityHandler, spriteHandler, MAX_HEALTH, SPEED, imageLoader, audioLoader);
	addWeapon(createWeapon(WeaponType.PISTOL, this, imageLoader, audioLoader));

	final int startMagazines = 30;
	inventory.addMagazinesToSelected(startMagazines);

	setBaseTexture(imageLoader.getCharacterBase());
	setWalkingTexture(imageLoader.getCharacterWalking());
	setIdleTexture(imageLoader.getCharacterIdle());
	setCollisionType(COLLISION_TYPE);
	this.score = 0;
	this.cash = 0;
    }

    public void setScreenLocation(final Point screenLocation) {
	this.screenLocation = screenLocation;
    }

    public void onKeyEvent(KeyEvent e, AbstractMap<Key, KeyState> keyStates) {
	switch (e.getKey()) {
	    case BUY -> {
		if (e.getKeyState() == KeyState.PRESSED) {
		    WeaponType latestWeapon = inventory.getLatestWeapon().getWeaponType();
		    WeaponFactory.buyWeapon(this, getCash(), latestWeapon, imageLoader, audioLoader);
		}
	    }
	    case RELOAD -> {
		if (e.getKeyState() == KeyState.PRESSED) reload();
	    }
	    case SHOOT -> {
		shooting = e.getKeyState() == KeyState.PRESSED;
	    }
	    case SWITCH -> {
		if (e.getKeyState() == KeyState.PRESSED) switchWeapon();
	    }
	    case UP, RIGHT, DOWN, LEFT -> {
		handleMovementEvent(keyStates);
	    }
	}
    }

    @Override public void update(final DeltaTime deltaTime) {
	super.update(deltaTime);
	if (shooting) {
	    attack();
	}
	updateReloadBar();
    }

    /**
     * Basically translates mouse position into player rotation
     */
    @Override protected void updateCharRotation() {
	rotation.setRadians(getMouseRotation(new Vector2D())); //no offset when updating player rotation
	updateDirection();
    }

    public int getScore() {
	return score;
    }

    public void onKill(int scoreAdd){
        score += scoreAdd;

	// 1/20 chance to play player sound effect on kill
	final double soundProbability = 0.05d;
	if (RND.nextDouble() <= soundProbability) audioLoader.playPlayerSound();
    }

    public int getCash() {
	return cash;
    }

    public void addWeapon(Weapon weapon) {
	inventory.addWeapon(weapon);
	inventory.selectNewestWeapon();
	setWeapon(inventory.getSelectedWeapon());
    }

    public void addCash(int cash) {
	this.cash += cash;
    }

    public int getAmmunitionMagazines() {
	return inventory.getSelectedMagazinesCount();
    }

    public void addAmmunitionMagazines(final int ammunitionMagazines) {
	inventory.addMagazinesToSelected(ammunitionMagazines);
    }

    public int getWeaponAmmo() {
	return weapon.getAmmo();
    }

    public void subtractCash(int cash) {
	this.cash -= cash;
    }

    private double getMouseRotation(Vector2D offset) {
	int x = (int) screenLocation.getX() + (int) position.getX() +
		(int) offset.getX(); //adjusts for the players position in the JComponent window
	int y = (int) screenLocation.getY() + (int) position.getY() + (int) offset.getY();
	Point playerScreenPosition = new Point(x, y);
	Point mouseScreenPosition = MouseInfo.getPointerInfo().getLocation();
	return getAngleBetweenPoints(playerScreenPosition, mouseScreenPosition);
    }

    public boolean isAlive() {
	return alive;
    }

    /**
     * Returns mouse angle but adjusted for bullet offset from top left corner since offset changes angle
     */
    @Override public double getAttackRotation() {
	Vector2D bulletOffset = weapon.getCorrectAttackOffset();
	return getMouseRotation(bulletOffset);
    }

    @Override public Area getCollisionArea() {
	final double adjustment = 0.3; //makes the hitbox smaller than the texture of the character
	int adjustedY = (int) (getPosition().getY() + getSize().getY() * adjustment);
	int adjustedHeight = (int) (getSize().getY() - getSize().getY() * adjustment);
	return new Area(new Ellipse2D.Double(getPosition().getX(), adjustedY, getSize().getX(), adjustedHeight));
    }

    private double getAngleBetweenPoints(Point p1, Point p2)
    {
	double xDiff = p2.x - p1.x;
	double yDiff = p2.y - p1.y;
	return Math.atan2(yDiff, xDiff);
    }

    /**
     * Updates the players movement based on the currently pressed keys.
     *
     * @param keyStates Currently pressed keys.
     */
    private void handleMovementEvent(AbstractMap<Key, KeyState> keyStates) {
	// Vector for storing input direction
	Vector2D inputDir = new Vector2D();

	// Map the Enum directions to Vector2D directions
	Map<Key, Vector2D> directions = new EnumMap<>(Key.class);
	final double dirLength = 1;
	directions.put(Key.UP, new Vector2D(0, -dirLength));
	directions.put(Key.RIGHT, new Vector2D(dirLength, 0));
	directions.put(Key.DOWN, new Vector2D(0, dirLength));
	directions.put(Key.LEFT, new Vector2D(-dirLength, 0));

	// Add pressed keys to input direction
	for (final Map.Entry<Key, Vector2D> entry : directions.entrySet()) {
	    if (keyStates.get(entry.getKey()) == KeyState.PRESSED) {
	        inputDir.add(entry.getValue());
	    }
	}

	// Set to the correct speed
	inputDir.setLength(SPEED);

	// Update velocity
	velocity.setTo(inputDir);
    }

    private void switchWeapon() {
	inventory.selectNextWeapon();
	audioLoader.playSound(AudioLoader.AudioEffect.CHANGE_WEAPON);
	setWeapon(inventory.getSelectedWeapon());
    }

    private void reload() {
	ProjectileWeapon pWeapon = (ProjectileWeapon) inventory.getSelectedWeapon();
	// If allowed to reload
	if (inventory.hasMagazines() && !pWeapon.isReloading() && pWeapon.getAmmo() != pWeapon.getMaxAmmo()) {
	    inventory.removeMagazine();
	    weapon.reload();
	}
    }


    @Override protected void onDeath(final LivingEntity killer) {
	alive = false;
    }

    /**
     * Updates the reload-bar for the player's weapon.
     */
    private void updateReloadBar() {
	ProjectileWeapon pWeapon = (ProjectileWeapon) this.weapon;

	final int barHeight = 4;
	Vector2D ammoBarSize = new Vector2D(size.getX(), barHeight);
	Vector2D ammoBarRemainingSize = new Vector2D(ammoBarSize.getX() * pWeapon.getReloadTimeLeft() / pWeapon.getReloadTime(), barHeight);

	final double reloadBarOffset = -10;
	reloadBar = updateIndividualBar(reloadBar, ammoBarRemainingSize, Color.CYAN, reloadBarOffset);
    }
}
