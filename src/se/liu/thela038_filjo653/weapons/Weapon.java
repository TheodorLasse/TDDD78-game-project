package se.liu.thela038_filjo653.weapons;

import se.liu.thela038_filjo653.time.DeltaTime;
import se.liu.thela038_filjo653.EntityHandler;
import se.liu.thela038_filjo653.GameComponent;
import se.liu.thela038_filjo653.SpriteHandler;
import se.liu.thela038_filjo653.Vector2D;

import java.awt.*;


/**
 * Defines some basic behaviour for weapons. All weapons need to be able to update, draw themselves, return offests, attack etc.
 */
public interface Weapon
{
    public void update(DeltaTime deltaTime);
    public void draw(Graphics g, GameComponent gc, int direction, int x, int y, int textureSideLength);
    public Vector2D getCorrectAttackOffset();
    public WeaponType getWeaponType();
    public int getAmmo();
    public void onWeaponAttack(EntityHandler entityHandler, SpriteHandler spriteHandler);
    public void reload();
}
