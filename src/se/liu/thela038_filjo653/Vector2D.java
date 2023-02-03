package se.liu.thela038_filjo653;

import java.util.Comparator;
import java.util.Objects;

/**
 * The class Vector2D is for representing quantities that have both a magnitude, and a direction in 2D space. It contains functions for
 * performing basic vector calculations or modifications. The vector is represented as a coordinate (x, y), and is always pointing from (0,
 * 0) to (x, y).
 */
public class Vector2D
{
    private double x;
    private double y;

    public Vector2D() {
	x = 0;
	y = 0;
    }

    public Vector2D(final double x, final double y) {
	this.x = x;
	this.y = y;
    }

    public Vector2D(final Rotation direction, final double length) {
	this.x = Math.cos(direction.getRadians()) * length;
	this.y = Math.sin(direction.getRadians()) * length;
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    /**
     * Returns the angle of this vector.
     *
     * @return A new Rotation object.
     */
    public Rotation getAngle() {
	return new Rotation(Math.atan2(y, x));
    }

    /**
     * Returns the length of this vector.
     *
     * @return length
     */
    public double getLength() {
	return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * Sets the value of this vector to equal a given vector.
     *
     * @param vector
     */
    public void setTo(final Vector2D vector) {
	x = vector.x;
	y = vector.y;
    }

    public void setX(final double x) {
	this.x = x;
    }

    public void setY(final double y) {
	this.y = y;
    }

    //These functions are unused but since Vector2D is a basic data type within this game, removing
    //it's basic functions doesn't seem like a good idea, even though it's unused.
    public void addX(final double x) {
	this.x += x;
    }

    public void addY(final double y) {
	this.y += y;
    }

    /**
     * Sets the length of this vector. The angle will not be changed.
     *
     * @param length New vector length.
     */
    public void setLength(double length) {
	Rotation angle = getAngle();

	// Don't update if already 0. To avoid floating-point rounding errors.
	if (x != 0) {
	    x = Math.cos(angle.getRadians()) * length;
	}
	if (y != 0) {
	    y = Math.sin(angle.getRadians()) * length;
	}
    }

    /**
     * Adds the value of a vector to this vector.
     *
     * @param vector
     */
    public void add(Vector2D vector) {
	x += vector.x;
	y += vector.y;
    }


    /**
     * Returns a new vector with identical values.
     *
     * @return A new vector.
     */
    public Vector2D copy() {
	return new Vector2D(x, y);
    }

    /**
     * Returns a new vector with the sum from the two given vectors.
     *
     * @param vector1
     * @param vector2
     *
     * @return A new vector.
     */
    public static Vector2D getSum(Vector2D vector1, Vector2D vector2) {
	return new Vector2D(vector1.x + vector2.x, vector1.y + vector2.y);
    }

    /**
     * Returns a new vector from a multiplication.
     *
     * @param vector
     * @param factor
     *
     * @return A new vector.
     */
    public static Vector2D getProduct(Vector2D vector, double factor) {
	return new Vector2D(vector.x * factor, vector.y * factor);
    }

    /**
     * Returns a new vector that points from one vector to another.
     *
     * @param from Vector to point from.
     * @param to   Vector to point to.
     *
     * @return A new vector.
     */
    public static Vector2D pointAt(Vector2D from, Vector2D to) {
	return new Vector2D(to.x - from.x, to.y - from.y);
    }

    @Override public boolean equals(final Object o) {
	if (this == o) {
	    return true;
	}
	if (o == null || getClass() != o.getClass()) {
	    return false;
	}
	final Vector2D vector = (Vector2D) o;
	return Double.compare(vector.x, x) == 0 && Double.compare(vector.y, y) == 0;
    }

    @Override public int hashCode() {
	return Objects.hash(x, y);
    }

    @Override public String toString() {
	return "Vector{" + "x=" + x + ", y=" + y + '}';
    }

    public static class Vector2DComparatorHeight implements Comparator<Vector2D>
    {
	@Override public int compare(final Vector2D o1, final Vector2D o2) {
	    return Integer.compare((int)o1.getY(), (int)o2.getY());
	}
    }


    /**
     * Tests for Vector class
     *
     * @param args
     */
    public static void main(String[] args) {
	final int testLength = 100;
        // Precision to use when comparing floating point numbers.
        final double comparisonPrecision = 0.0001;

	// Test equals
	Vector2D v1 = new Vector2D(2, 5);
	assert v1.equals(new Vector2D(2, 5));

	Vector2D v2 = new Vector2D(3, 4);
	assert !v1.equals(v2);

	// Test getAngle
	final double fourth = 0.25d;
	final double radians1 = Math.PI * fourth; // 45 degrees
	v1 = new Vector2D(3, 3);
	assert v1.getAngle().equals(new Rotation(radians1));

	// Test setLength
	v1 = new Vector2D(3, 4);
	Rotation r = v1.getAngle();
	v1.setLength(testLength);
	assert v1.getAngle().equals(r);
	assert Math.abs(v1.getLength() - testLength) < comparisonPrecision;

	// Test pointAt
	v1 = new Vector2D(10, 15);
	v2 = new Vector2D(12, 20);
	assert Vector2D.pointAt(v1, v2).equals(new Vector2D(2, 5));

	// Test create from rotation
	v1 = new Vector2D(new Rotation(0), testLength);
	assert Math.abs(v1.getX() - testLength) < comparisonPrecision;
	assert Math.abs(v1.getY()) < comparisonPrecision;

	final double radians2 = 4;
	Rotation angle = new Rotation(radians2);
	v2 = new Vector2D(angle, testLength);
	assert v2.getX() < 0;
	assert v2.getY() < 0;
	assert Math.abs(v2.getAngle().getRadians() - angle.getRadians()) < comparisonPrecision;
	assert Math.abs(v2.getLength() - testLength) < comparisonPrecision;
    }
}
