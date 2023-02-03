package se.liu.thela038_filjo653.time;

import java.security.InvalidParameterException;

/**
 * Class for measuring time from a series of DeltaTime objects. This can be used by objects inside the game loop to measure time based on
 * the DeltaTime between each update.
 */
public class DeltaTimer
{
    private double timerTargetSeconds;
    private double elapsedSeconds;

    public DeltaTimer() {
	setTimer(0);
    }

    public DeltaTimer(final double seconds) throws InvalidParameterException {
	setTimer(seconds);
    }

    /**
     * Updates the DeltaTimer with a new DeltaTime.
     *
     * @param deltaTime Elapsed time since last update.
     */
    public void update(final DeltaTime deltaTime) {
	elapsedSeconds += deltaTime.getSeconds();
    }

    /**
     * Sets the elapsed time back to 0;
     */
    public void restart() {
        elapsedSeconds = 0;
    }

    /**
     * Sets a new time for the timer, and restarts the timer.
     *
     * @param seconds Seconds to time.
     */
    public void setTimer(final double seconds) throws InvalidParameterException {
	if (seconds < 0) {
	    throw new InvalidParameterException("seconds is less than 0");
	}

	timerTargetSeconds = seconds;
        restart();
    }

    /**
     * Returns the elapsed time as seconds.
     *
     * @return Seconds as double.
     */
    public double getElapsedSeconds() {
	return elapsedSeconds;
    }

    /**
     * Returns true if the elapsed time is more or equal to the time that the timer was set to.
     *
     * @return true if timer is complete.
     */
    public boolean isComplete() {
	return elapsedSeconds >= timerTargetSeconds;
    }
}
