package se.liu.thela038_filjo653;

import se.liu.thela038_filjo653.sprites.Entity;
import se.liu.thela038_filjo653.sprites.Sprite;
import se.liu.thela038_filjo653.time.DeltaTime;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling all the entities in the game. It updates all entities, notifies them about collisions, and contains functions for
 * adding/removing entities to/from the game.
 */
public class EntityHandler
{
    private List<Entity> entities;
    private List<Entity> toRemove;
    private List<Entity> toAdd;


    public EntityHandler() {
	entities = new ArrayList<>();
	toRemove = new ArrayList<>();
	toAdd = new ArrayList<>();
    }

    /**
     * Checks if a given Entity type exists within the entityhandler.
     */
    public <T> boolean isEntityTypeExisting(Class<T> type) {
	for (Sprite s:getIterator()) {
	    //Kodinspektionen markerar detta som fel, men detta går inte att lösa snyggare
	    //med polymorfism. Har rätt liknande funktion som om det användes i en equals() funktion
	    if (type.isAssignableFrom(s.getClass())){
	        return true;
	    }
	}
	return false;
    }

    /**
     * Updates the entity handler and all entities.
     *
     * @param deltaTime
     */
    public void update(DeltaTime deltaTime) {
	// Update all entities
	for (Entity entity : entities) {
	    entity.update(deltaTime);
	}

	internalUpdate();
    }

    /**
     * Updates EntityHandler internally
     */
    public void internalUpdate() {
	// Check collisions
	updateCollisions();
	addEntities();
	clearEntities();
    }

    /**
     * Schedules an entity for addition to the entity handler. It will be added when possible.
     *
     * @param entity
     */
    public void add(Entity entity) {
	toAdd.add(entity);
    }

    /**
     * Schedules an entity for removal from the entity handler. It will be removed when possible.
     *
     * @param entity
     */
    public void remove(Entity entity) {
	// Do not allow duplicates
	if (!toRemove.contains(entity)) {
	    toRemove.add(entity);
	}
    }

    /**
     * Returns an iterator with a sprite for each entity.
     *
     * @return Iterator with sprites
     */
    public Iterable<Sprite> getIterator() {
	List<Sprite> sprites = new ArrayList<>(entities);
	return sprites;
    }

    /**
     * Checks for collisions between entities in this entity handler. Notifies the entities if collision.
     */
    private void updateCollisions() {
        // Loops through all pairs of entities

	// End at entities.size() - 1, to avoid comparing two entities twice. The last index will be covered by e2.
	for (int e1Index = 0; e1Index < entities.size() - 1; e1Index++) {
	    // Start at e1Index + 1, to avoid comparing two entities twice. All entities before will be covered by e1.
	    for (int e2Index = e1Index + 1; e2Index < entities.size(); e2Index++) {

	        Entity e1 = entities.get(e1Index);
		Entity e2 = entities.get(e2Index);
		Area a1 = e1.getCollisionArea();
		Area a2 = e2.getCollisionArea();

		a1.intersect(a2);

		if (!a1.isEmpty()) {
		    handleCollision(e1, e2);
		}
	    }
	}
    }

    /**
     * Handles the collision between two entities.
     *
     * @param entity1
     * @param entity2
     */
    private void handleCollision(Entity entity1, Entity entity2) {
	entity1.onCollide(entity2);
	entity2.onCollide(entity1);
    }


    /**
     * Checks if there are any entities in a given area, returns those entities
     *
     * @param Area
     */
    public List<Entity> entitiesInArea(Area range) {
        List<Entity> entitiesInArea = new ArrayList<>();
	for (int eIndex = 0; eIndex < entities.size() - 1; eIndex++) {
	    Entity entity = entities.get(eIndex);
	    Area entityArea = entity.getCollisionArea();
	    Area rangeCopy = new Area(range);

	    entityArea.intersect(rangeCopy);
	    if (!entityArea.isEmpty()) {
		entitiesInArea.add(entity);
	    }
	}
	return entitiesInArea;
    }

    /**
     * Adds entities that have been scheduled for adding.
     */
    private void addEntities() {
	entities.addAll(toAdd);
	toAdd.clear();
    }

    /**
     * Removes entities that have been scheduled for removal.
     */
    private void clearEntities() {
	entities.removeAll(toRemove);
	toRemove.clear();
    }
}
