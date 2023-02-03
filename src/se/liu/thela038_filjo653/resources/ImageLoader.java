package se.liu.thela038_filjo653.resources;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Class that loads all images and sorts them into appropriate lists etc.
 */
public class ImageLoader
{
    public enum ImageName
    {
	BACKGROUND, FOREGROUND, DEATH, CHAR_SHEET, ZOMBIE_COP_SHEET, ZOMBIE_WALKER_SHEET, ZOMBIE_BOSS_SHEET, PISTOL_SHEET, UZI_SHEET, RIFLE_SHEET, RPG_SHEET,
	EXPLOSION_SHEET, BLOOD_SHEET, BULLET, RPG_BULLET, CAR_1, CAR_2, AMMO, MONEY, HEALTH_KIT,
    }

    /**
     * From enum to actual file name
     */
    final private static Map<ImageName, String> IMAGE_NAME_MAP = Map.ofEntries(
	    Map.entry(ImageName.BACKGROUND, "background"), Map.entry(ImageName.FOREGROUND, "foreground"),
	    Map.entry(ImageName.DEATH, "death"), Map.entry(ImageName.CHAR_SHEET, "char_sheet"),
	    Map.entry(ImageName.ZOMBIE_COP_SHEET, "zombiecop_sheet"), Map.entry(ImageName.ZOMBIE_WALKER_SHEET, "zombiewalker_sheet"),
	    Map.entry(ImageName.ZOMBIE_BOSS_SHEET, "zombieboss_sheet"),
	    Map.entry(ImageName.PISTOL_SHEET, "pistol_sheet"), Map.entry(ImageName.UZI_SHEET, "uzi_sheet"),
	    Map.entry(ImageName.RIFLE_SHEET, "ak_sheet"), Map.entry(ImageName.RPG_SHEET, "rpg_sheet"),
	    Map.entry(ImageName.EXPLOSION_SHEET, "explosion_sheet"), Map.entry(ImageName.BLOOD_SHEET, "blood_sheet"),
	    Map.entry(ImageName.BULLET, "bullet"), Map.entry(ImageName.RPG_BULLET, "rpg_bullet"),
	    Map.entry(ImageName.CAR_1, "car1"), Map.entry(ImageName.CAR_2, "car2"),
	    Map.entry(ImageName.AMMO, "ammo"), Map.entry(ImageName.MONEY, "money"),
	    Map.entry(ImageName.HEALTH_KIT, "healthkit"));

    final private static int TILES_PER_ROW = 8;

    private List<BufferedImage> characterBase = null;
    private List<BufferedImage> characterIdle = null;
    private List<BufferedImage> characterWalking = null;
    private List<BufferedImage> zombieCopBase = null;
    private List<BufferedImage> zombieCopWalking = null;
    private List<BufferedImage> zombieWalkerBase = null;
    private List<BufferedImage> zombieWalkerWalking = null;
    private List<BufferedImage> zombieBossBase = null;
    private List<BufferedImage> zombieBossWalking = null;
    private List<BufferedImage> explosion = null;
    private List<BufferedImage> backgroundBlood = null;
    private List<BufferedImage> pistol = null;
    private List<BufferedImage> uzi = null;
    private List<BufferedImage> rifle = null;
    private List<BufferedImage> rpg = null;

    private enum CharRow
    {
	BASE, IDLE, WALKING
    }

    private static final Map<CharRow, Integer> CHAR_ROW_MAP = Map.of(CharRow.BASE, 0,
								     CharRow.IDLE, 1,
								     CharRow.WALKING, 2);

    public List<BufferedImage> getCharacterBase() {
	return characterBase;
    }

    public List<BufferedImage> getCharacterIdle() {
	return characterIdle;
    }

    public List<BufferedImage> getCharacterWalking() {
	return characterWalking;
    }

    public List<BufferedImage> getZombieCopBase() {
	return zombieCopBase;
    }

    public List<BufferedImage> getZombieCopWalking() {
	return zombieCopWalking;
    }

    public List<BufferedImage> getZombieWalkerBase() {
	return zombieWalkerBase;
    }

    public List<BufferedImage> getZombieWalkerWalking() {
	return zombieWalkerWalking;
    }

    public List<BufferedImage> getZombieBossBase() {return zombieBossBase; }

    public List<BufferedImage> getZombieBossWalking() {return zombieBossWalking; }

    public List<BufferedImage> getExplosion() {
	return explosion;
    }

    public List<BufferedImage> getBackgroundBlood() {
	return backgroundBlood;
    }

    public List<BufferedImage> getPistol() {
	return pistol;
    }

    public List<BufferedImage> getUzi() {
	return uzi;
    }

    public List<BufferedImage> getRifle() {
	return rifle;
    }

    public List<BufferedImage> getRpg() {
	return rpg;
    }

    private final Map<ImageName, BufferedImage> images;

    public ImageLoader() {
	images = new EnumMap<>(ImageName.class);
    }

    /**
     * Returns the image with a given name.
     *
     * @param imgName Image name to look for.
     *
     * @return An Image
     */
    public BufferedImage getImage(ImageName imgName) {
	return images.get(imgName);
    }

    /**
     * Loads all images and creates sheets.
     */
    public void loadImages() throws IOException, FileNotFoundException {
	for (ImageName iterImageName : ImageName.values()) {
	    // The slash triggers a warning in the automatic code inspection. That can be ignored because this is a resource URL.
	    final String name = "images/" + IMAGE_NAME_MAP.get(iterImageName) + ".png";
	    final URL imgURL = ClassLoader.getSystemResource(name);

	    if (imgURL == null) {
		throw new FileNotFoundException("Could not find resource " + name);
	    }

	    images.put(iterImageName, loadImage(imgURL));
	}

	createSheets();
    }

    private void createSheets() {
        BufferedImage charSheet = getImage(ImageName.CHAR_SHEET);
        BufferedImage zombieCopSheet = getImage(ImageName.ZOMBIE_COP_SHEET);
        BufferedImage zombieWalkerSheet = getImage(ImageName.ZOMBIE_WALKER_SHEET);
	BufferedImage zombieBossSheet = getImage(ImageName.ZOMBIE_BOSS_SHEET);

	characterBase = loadChar(CharRow.BASE, charSheet);
	characterIdle = loadChar(CharRow.IDLE, charSheet);
	characterWalking = loadChar(CharRow.WALKING, charSheet);

	zombieCopBase = loadChar(CharRow.BASE, zombieCopSheet);
	zombieCopWalking = loadChar(CharRow.WALKING, zombieCopSheet);

	zombieWalkerBase = loadChar(CharRow.BASE, zombieWalkerSheet);
	zombieWalkerWalking = loadChar(CharRow.WALKING, zombieWalkerSheet);

	zombieBossBase = loadChar(CharRow.BASE, zombieBossSheet);
	zombieBossWalking = loadChar(CharRow.WALKING, zombieBossSheet);

	final int explosionRows = 5;
	explosion = loadSheet(getImage(ImageName.EXPLOSION_SHEET), 0, explosionRows);
	backgroundBlood = loadSheet(getImage(ImageName.BLOOD_SHEET), 0, 1);
	pistol = loadSheet(getImage(ImageName.PISTOL_SHEET), 0, 1);
	uzi = loadSheet(getImage(ImageName.UZI_SHEET), 0, 1);
	rifle = loadSheet(getImage(ImageName.RIFLE_SHEET), 0, 1);
	rpg = loadSheet(getImage(ImageName.RPG_SHEET), 0, 1);
    }

    /**
     * Loads and returns an image. If the image can't be loaded, a default image will be returned.
     *
     * @param imageName
     *
     * @return image
     * @throws IOException
     */
    private static BufferedImage loadImage(URL url) throws IOException {
	return ImageIO.read(url);
    }


    private static List<BufferedImage> loadChar(CharRow row, BufferedImage sheet) {
	final int charTilesPerColumn = CharRow.values().length;
	final int startRow = CHAR_ROW_MAP.get(row);
	int tileSizeWidth = sheet.getWidth() / TILES_PER_ROW;
	int tileSizeHeight = sheet.getHeight() / charTilesPerColumn;
	final int rows = 1;
	return subImageLoop(sheet, startRow, tileSizeWidth, tileSizeHeight, rows);
    }


    private static List<BufferedImage> loadSheet(BufferedImage sheet, int startRow, int endRow) {
	int tileSizeWidth = sheet.getWidth() / TILES_PER_ROW;
	int tileSizeHeight = sheet.getHeight() / endRow;
	return subImageLoop(sheet, startRow, tileSizeWidth, tileSizeHeight, endRow);
    }

    private static List<BufferedImage> subImageLoop(BufferedImage image, int startRow, int tileSizeWidth, int tileSizeHeight, int rows) {
	List<BufferedImage> images = new ArrayList<>();
	for (int k = 0; k < rows; k++) {
	    for (int i = 0; i < TILES_PER_ROW; i++) {
		images.add(image.getSubimage(i * tileSizeWidth, (startRow + k) * tileSizeHeight, tileSizeWidth, tileSizeHeight));
	    }
	}
	return images;
    }


    public static BufferedImage rotateImage(double rotation, BufferedImage image, AffineTransform at) {
	final double sin = Math.abs(Math.sin(rotation));
	final double cos = Math.abs(Math.cos(rotation));
	final int w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
	final int h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
	final BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
	at.translate((double) w / 2, (double) h / 2);
	at.rotate(rotation, 0, 0);
	at.translate((double) -image.getWidth() / 2, (double) -image.getHeight() / 2);
	final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
	rotateOp.filter(image, rotatedImage);
	return rotatedImage;
    }
}
