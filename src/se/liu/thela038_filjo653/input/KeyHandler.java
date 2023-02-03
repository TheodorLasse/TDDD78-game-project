package se.liu.thela038_filjo653.input;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Class for handling key presses from the user. It contains a list of KeyListeners that will be notified on every key event that are
 * handled by this KeyHandler.
 */
public class KeyHandler
{
    final private JComponent pane;
    final private List<GameKeyListener> keyListeners;
    final private EnumMap<Key, KeyState> keyStates;

    public KeyHandler(final JComponent pane) {
	this.pane = pane;

	keyListeners = new ArrayList<>();
	keyStates = new EnumMap<>(Key.class);

	// Set all keys to released
	for (Key key : Key.values()) {
	    keyStates.put(key, KeyState.RELEASED);
	}

	addInputAction(java.awt.event.KeyEvent.VK_W, Key.UP);
	addInputAction(java.awt.event.KeyEvent.VK_A, Key.LEFT);
	addInputAction(java.awt.event.KeyEvent.VK_S, Key.DOWN);
	addInputAction(java.awt.event.KeyEvent.VK_D, Key.RIGHT);
	addInputAction(java.awt.event.KeyEvent.VK_SPACE, Key.SHOOT);
	addInputAction(java.awt.event.KeyEvent.VK_R, Key.RELOAD);
	addInputAction(java.awt.event.KeyEvent.VK_E, Key.BUY);
	addInputAction(java.awt.event.KeyEvent.VK_Q, Key.SWITCH);
    }

    /**
     * Adds a new key listener that will be called every time a key updates.
     *
     * @param keyListener
     */
    public void addKeyListener(GameKeyListener keyListener) {
	keyListeners.add(keyListener);
    }

    /**
     * Notifies all key listeners about a key event.
     */
    private void notifyListeners(KeyEvent e) {
	for (GameKeyListener keyListener : keyListeners) {
	    keyListener.onKeyEvent(e, new EnumMap<>(keyStates));
	}
    }

    /**
     * Handles a key event and updates the key state. If the key state changed, the listeners will be notified.
     *
     * @param e Event to handle.
     */
    private void handleEvent(KeyEvent e) {
	final KeyState oldState = keyStates.get(e.getKey());

	final Key key = e.getKey();
	final KeyState newState = e.getKeyState();

	// Only update if the key state actually changed
	if (oldState != newState) {
	    keyStates.put(key, newState);
	    notifyListeners(e);
	}
    }


    /**
     * Adds a keyboard key and its Key representation to the input and action maps.
     *
     * @param keyCode Keyboard key.
     * @param key     Key representation.
     */
    private void addInputAction(int keyCode, Key key) {
	final InputMap inputMap = pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	final ActionMap actionMap = pane.getActionMap();

	// Add to input map
	inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, false), "press_" + key);
	inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, true), "release_" + key);

	// Add to action map
	actionMap.put("press_" + key, new KeyAction(new KeyEvent(key, KeyState.PRESSED)));
	actionMap.put("release_" + key, new KeyAction(new KeyEvent(key, KeyState.RELEASED)));
    }

    private class KeyAction extends AbstractAction
    {
	private final KeyEvent keyEvent;

	private KeyAction(final KeyEvent keyEvent) {
	    this.keyEvent = keyEvent;
	}

	@Override public void actionPerformed(final ActionEvent e) {
	    handleEvent(keyEvent);
	}
    }
}
