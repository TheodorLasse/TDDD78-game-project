package se.liu.thela038_filjo653;

import java.util.Objects;

/**
 * The Rotation class is for representing a rotation. A rotation will always be between 0 and 2*pi radians, and will always be converted to
 * fit that format.
 */
public class Rotation
{
    private double radians;

    public Rotation(double radians) {
	setRadians(radians);
    }

    /**
     * Sets rotation in radians.
     *
     * @param radians
     */
    public void setRadians(double radians) {
	double newRadians = radians % (2 * Math.PI);

	// To avoid negative rotations
	if (newRadians < 0) {
	    this.radians = newRadians + 2 * Math.PI;
	} else {
	    this.radians = newRadians;
	}
    }

    /**
     * Returns the current rotation in radians.
     *
     * @return Radians.
     */
    public double getRadians() {
	return radians;
    }

    /**
     * Adds rotation in radians.
     *
     * @param radians
     */
    public void addRadians(final double radians) {
	setRadians(this.radians + radians);
    }

    @Override public boolean equals(final Object o) {
	if (this == o) {
	    return true;
	}
	if (o == null || getClass() != o.getClass()) {
	    return false;
	}
	final Rotation rotation = (Rotation) o;
	return Double.compare(rotation.radians, radians) == 0;
    }

    @Override public int hashCode() {
	return Objects.hash(radians);
    }
}
