
package com.techjar.ledcm.util;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

/**
 *
 * @author Techjar
 */
public class Vector3 {
    protected float x;
    protected float y;
    protected float z;

    public Vector3() {
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3(org.lwjgl.util.vector.Vector3f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3 copy() {
        return new Vector3(this);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3 other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 add(float number) {
        return new Vector3(this.x + number, this.y + number, this.z + number);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3 subtract(float number) {
        return new Vector3(this.x - number, this.y - number, this.z - number);
    }

    public Vector3 multiply(float number) {
        return new Vector3(this.x * number, this.y * number, this.z * number);
    }

    public Vector3 multiply(Matrix3f matrix) {
        float newX = matrix.m00 * x + matrix.m01 * y + matrix.m02 * z;
        float newY = matrix.m10 * x + matrix.m11 * y + matrix.m12 * z;
        float newZ = matrix.m20 * x + matrix.m21 * y + matrix.m22 * z;
        return new Vector3(newX, newY, newZ);
    }

    public Vector3 multiply(Matrix4f matrix) {
        float newX = matrix.m00 * x + matrix.m01 * y + matrix.m02 * z + matrix.m03;
        float newY = matrix.m10 * x + matrix.m11 * y + matrix.m12 * z + matrix.m13;
        float newZ = matrix.m20 * x + matrix.m21 * y + matrix.m22 * z + matrix.m23;
        return new Vector3(newX, newY, newZ);
    }

    public Vector3 divide(float number) {
        return new Vector3(this.x / number, this.y / number, this.z / number);
    }

    public Vector3 negate() {
        return new Vector3(-this.x, -this.y, -this.z);
    }

    public Angle angle(Vector3 other) {
        float deltaX = other.x - x;
        float deltaZ = other.z - z;
        float pitch = (float)Math.toDegrees(Math.atan2(other.y - y, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));
        float yaw = (float)Math.toDegrees(Math.atan2(-deltaX, -deltaZ));
        /*float pitch = (float)Math.toDegrees(Math.asin(other.y - y));
        float yaw = (float)Math.toDegrees(Math.atan2(-(other.x - x), -(other.z - z)));*/
        return new Angle(pitch, yaw);
    }

    public float length() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float distance(Vector3 other) {
        float xx = other.x - x;
        float yy = other.y - y;
        float zz = other.z - z;
        return (float)Math.sqrt(xx * xx + yy * yy + zz * zz);
    }

    public float distanceSquared(Vector3 other) {
        float xx = other.x - x;
        float yy = other.y - y;
        float zz = other.z - z;
        return xx * xx + yy * yy + zz * zz;
    }

    public float distance2D(Vector2 other) {
        return other.subtract(new Vector2(this.x, this.z)).length();
    }

    public float distanceSquared2D(Vector2 other) {
        return other.subtract(new Vector2(this.x, this.z)).lengthSquared();
    }

    public float distance2D(Vector3 other) {
        return this.distance2D(new Vector2(other.x, other.z));
    }

    public float distanceSquared2D(Vector3 other) {
        return this.distanceSquared2D(new Vector2(other.x, other.z));
    }

    public void normalize() {
        set(divide(length()));
    }

    public Vector3 normalized() {
        return divide(length());
    }

    public float dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public Vector3 project(Vector3 other) {
        return other.multiply(other.dot(this) / other.dot(other));
    }

    public static Vector3 slerp(Vector3 start, Vector3 end, float fraction) {
        float dot = start.dot(end);
        float theta = (float)Math.acos(dot) * fraction;
        Vector3 relative = end.subtract(start.multiply(dot));
        relative.normalize();
        return start.multiply((float)Math.cos(theta)).add(relative.multiply((float)Math.sin(theta)));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Float.floatToIntBits(this.x);
        hash = 53 * hash + Float.floatToIntBits(this.y);
        hash = 53 * hash + Float.floatToIntBits(this.z);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector3 other = (Vector3)obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Vector3{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
