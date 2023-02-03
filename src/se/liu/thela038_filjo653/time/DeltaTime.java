package se.liu.thela038_filjo653.time;

/**
 * Class for representing a length of time. It has the time resolution of one nano second. The time length can not be changed
 * after the object is created.
 */
public class DeltaTime
{
    private long nanoSeconds;

    public DeltaTime(final long nanoSeconds) {
	this.nanoSeconds = nanoSeconds;
    }

    /** The number of nano seconds in a second. */
    public static final int NANO_SECONDS_IN_SECOND = 1000000000;

    /**
     * Returns the delta time as seconds.
     *
     * @return Seconds as double.
     */
    public double getSeconds() {
	return nanoSeconds / (double) NANO_SECONDS_IN_SECOND;
    }

    /**
     * Returns the delta time as nano seconds.
     *
     * @return Nano seconds as long.
     */
    public long getNanoSeconds() {
	return nanoSeconds;
    }
}
