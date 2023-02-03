package se.liu.thela038_filjo653;

import se.liu.thela038_filjo653.weapons.Weapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that keeps a collection of weapons and their ammunition, and keeps track of which is currently selected.
 */
public class WeaponInventory
{
    private final List<Weapon> weapons;
    private final Map<Weapon, Integer> magazinesMap;
    private int selectedIndex = -1;

    public WeaponInventory() {
	weapons = new ArrayList<>();
	magazinesMap = new HashMap<>();
    }

    /**
     * Returns the weapon at the specified index.
     *
     * @param index Weapon index.
     *
     * @return Weapon.
     */
    public Weapon getWeapon(int index) {
	if (index < 0 || index >= weapons.size()) {
	    throw new IndexOutOfBoundsException(index);
	}
	return weapons.get(index);
    }

    /**
     * Returns the latest weapon added to the inventory
     *
     * @return Weapon.
     */
    public Weapon getLatestWeapon() {
	return getWeapon(weapons.size() - 1);
    }

    /**
     * Returns the weapon that is currently selected.
     *
     * @return Weapon.
     */
    public Weapon getSelectedWeapon() {
	if (weapons.isEmpty()) {
	    return null;
	}
	return getWeapon(selectedIndex);
    }

    /**
     * Returns the current number of magazines for the selected weapon.
     *
     * @return Number of magazines.
     */
    public int getSelectedMagazinesCount() {
	Weapon weapon = getSelectedWeapon();

	if (weapon == null) {
	    return 0;
	}
	return magazinesMap.get(weapon);
    }

    /**
     * Returns true if the selected weapon has any magazines left.
     *
     * @return boolean.
     */
    public boolean hasMagazines() {
	return getSelectedMagazinesCount() > 0;
    }

    /**
     * Adds the specified amount of magazines to the currently selected weapon.
     *
     * @param magazinesCount Number of magazines to add.
     */
    public void addMagazinesToSelected(int magazinesCount) {
	int newCount = getSelectedMagazinesCount() + magazinesCount;
	magazinesMap.put(getSelectedWeapon(), newCount);
    }

    /**
     * Removes one magazine from the currently selected weapon.
     */
    public void removeMagazine() {
	final int toRemove = 1;
	addMagazinesToSelected(-toRemove);
    }

    /**
     * Adds a new weapon to the inventory.
     *
     * @param weapon Weapon to add.
     */
    public void addWeapon(Weapon weapon) {
	addWeapon(weapon, 0);
    }

    /**
     * Adds a new weapon and its magazines to the inventory.
     *
     * @param weapon    Weapon to add.
     * @param magazines Number of magazines.
     */
    public void addWeapon(Weapon weapon, int magazines) {
	weapons.add(weapon);
	magazinesMap.put(weapon, magazines);
    }

    /**
     * Selects the next weapon in the inventory.
     */
    public void selectNextWeapon() {
	selectedIndex++;
	if (selectedIndex >= weapons.size()) {
	    selectedIndex = 0;
	}
    }

    /**
     * Selects the weapons that was last added to the inventory.
     */
    public void selectNewestWeapon() {
	selectedIndex = weapons.size() - 1;
    }
}
