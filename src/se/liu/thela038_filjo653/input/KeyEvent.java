package se.liu.thela038_filjo653.input;

/**
 * Class for representing a single key press or release.
 */
public class KeyEvent
{
    private Key key;
    private KeyState keyState;

    public KeyEvent(final Key key, final KeyState keyState) {
        this.key = key;
        this.keyState = keyState;
    }

    public Key getKey() {
        return key;
    }

    public KeyState getKeyState() {
        return keyState;
    }
}
