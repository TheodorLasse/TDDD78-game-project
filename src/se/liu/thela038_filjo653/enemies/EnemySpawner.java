package se.liu.thela038_filjo653.enemies;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.Game;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.SpriteLayer;
import se.liu.thela038_filjo653.Vector2D;
import se.liu.thela038_filjo653.resources.AudioLoader;
import se.liu.thela038_filjo653.resources.ImageLoader;
import se.liu.thela038_filjo653.sprites.Enemy;
import se.liu.thela038_filjo653.sprites.Entity;
import se.liu.thela038_filjo653.sprites.SpriteTexture;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Class that creates new waves of enemies and spawns them in different locations.
 */
public class EnemySpawner
{
    private Game game;
    private final EntityHandler entityHandler;
    private final SpriteHandler spriteHandler;
    private double timer = 0;
    private double spawnTimer = 0;
    private List<Vector2D> spawnLocations = new ArrayList<>();
    private List<Enemy> spawnEnemies = new ArrayList<>();
    private final EnemyFactory factory;
    private final static Random RND = new Random();

    public EnemySpawner(final Game game, final EntityHandler entityHandler, final SpriteHandler spriteHandler, final Entity target,
			final ImageLoader imageLoader, final AudioLoader audioLoader)
    {
	this.game = game;
	this.entityHandler = entityHandler;
	this.spriteHandler = spriteHandler;
	addSpawnLocations();

	factory = new EnemyFactory(imageLoader, audioLoader, entityHandler, spriteHandler, target);
    }

    private void addSpawnLocations() {
	int width = game.getWidth();
	int height = game.getHeight();
	final double middleWidth = (double) width / 2;
	final double middleHeight = (double) height / 2;
	final int distanceToMap = 100;

	spawnLocations.add(new Vector2D(middleWidth, -distanceToMap));
	spawnLocations.add(new Vector2D(middleWidth, height + distanceToMap));
	spawnLocations.add(new Vector2D(-distanceToMap, middleHeight));
	spawnLocations.add(new Vector2D(width + distanceToMap, middleHeight));
    }


    /**
     * Updates the enemySpawner returning the updated wave number and spawning enemies if enough time has passed or all enemies are dead
     *
     * @param deltaTime
     * @param currentWaveNumber
     */
    public int update(DeltaTime deltaTime, int currentWaveNumber) {
	timer += deltaTime.getSeconds();
	spawnTimer -= deltaTime.getSeconds();
	if (spawnTimer < 0) {
	    spawnTimer = 0;
	}

	int wave = currentWaveNumber;
	final double timerNewWave = 35;

	if (timer > timerNewWave || !entityHandler.isEntityTypeExisting(Enemy.class)) {
	    timer = 0;
	    wave = currentWaveNumber + 1;
	    createWave(wave);
	}
	addEnemies();
	return wave;
    }

    private void addEnemies() {
	if (spawnTimer == 0 && !spawnEnemies.isEmpty()) {
	    final double spawnTick = 0.5;
	    spawnTimer += spawnTick;
	    entityHandler.add(spawnEnemies.get(0));
	    spawnEnemies.remove(0);
	}
    }

    private void createWave(int currentWaveNumber) {
	SpriteTexture waveText = createWaveText(currentWaveNumber);
	final int waveTextDuration = 7;
	spriteHandler.add(waveText, waveTextDuration, SpriteLayer.LAST);
	addEnemiesList(currentWaveNumber);
    }

    private void addEnemiesList(int currentWaveNumber) {
	final int baseEnemyValue = 7;
	List<EnemyFactory.EnemyType> enemyTypes = List.of(EnemyFactory.EnemyType.WALKER, EnemyFactory.EnemyType.COP);

	//total of 100 meaning 80% chance for walker spawn and 20% chance for cop spawn
	final int walkerProbability = 80;
	final int copProbability = 20;
	Map<EnemyFactory.EnemyType, Integer> enemySpawnChance = Map.of(EnemyFactory.EnemyType.WALKER, walkerProbability,
								       EnemyFactory.EnemyType.COP, copProbability);

	int totalSpawnChance = 0;
	for (EnemyFactory.EnemyType type:enemyTypes) {
	    totalSpawnChance += enemySpawnChance.get(type);
	}

	int currentEnemyValue = baseEnemyValue * currentWaveNumber;

	while (currentEnemyValue > 0) {
	    int randomSpawnLocation = RND.nextInt(spawnLocations.size());
	    Vector2D position = spawnLocations.get(randomSpawnLocation).copy();

	    final int bossWaveSpacing = 10;
	    if (currentWaveNumber % bossWaveSpacing == 0){
		currentEnemyValue -= addEnemy(EnemyFactory.EnemyType.BOSS, position.copy(), currentWaveNumber);
	    }

	    final int enemyTypeRoll = RND.nextInt(totalSpawnChance + 1);

	    for (int i = enemyTypes.size() - 1; i >= 0 ; i--) {
		EnemyFactory.EnemyType iterEnemyType = enemyTypes.get(i);
		if (enemySpawnChance.get(iterEnemyType) >= enemyTypeRoll){
		    currentEnemyValue -= addEnemy(iterEnemyType, position.copy(), currentWaveNumber);
		}
	    }
	}
    }

    private int addEnemy(EnemyFactory.EnemyType type, Vector2D position, int currentWaveNumber){
        Enemy enemy = factory.createEnemy(type, position, currentWaveNumber);
        spawnEnemies.add(enemy);
        return enemy.getScoreWorth();
    }

    private SpriteTexture createWaveText(int currentWaveNumber) {
        final double marginPercent = 0.05;
        final double textPosY = game.getHeight() * marginPercent;
	Vector2D waveTextPos = new Vector2D(0, textPosY);
	SpriteTexture waveText = new SpriteTexture(waveTextPos, 0, Color.RED, "WAVE " + currentWaveNumber, 50);
	return waveText;
    }
}
