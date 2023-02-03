package se.liu.thela038_filjo653.input;

import java.util.AbstractMap;

/**
 * Interface that needs to be implemented by all classes that wants to get KeyEvents from a KeyHandler.
 */
public interface GameKeyListener
{
    public void onKeyEvent(KeyEvent e, AbstractMap<Key, KeyState> keyStates);
}
