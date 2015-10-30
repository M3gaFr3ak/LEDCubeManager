package com.techjar.ledcm.hardware;

import com.techjar.ledcm.util.Dimension3D;
import com.techjar.ledcm.util.Vector3;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

/**
 * @author Leonelf
 */
public class STP16Monochrome16 implements LEDManager {

    private boolean gammaCorrection;
    private byte[] red = new byte[4096];
    private byte[] green = new byte[4096];
    private byte[] blue = new byte[4096];
    private LEDArray ledArray;

    @Override
    public int getResolution() {
        return 255;
    }

    @Override
    public float getFactor() {
        return 1;
    }

    @Override
    public Dimension3D getDimensions() {
        return new Dimension3D(16, 16, 16);
    }

    @Override
    public int getLEDCount() {
        return 4096;
    }

    @Override
    public boolean isMonochrome() {
        return true;
    }

    @Override
    public Color getMonochromeColor() {
        return (Color) Color.WHITE;
    }

    @Override
    public boolean getGammaCorrection() {
        return gammaCorrection;
    }

    @Override
    public void setGammaCorrection(boolean gammaCorrection) {
        this.gammaCorrection = gammaCorrection;
    }

    @Override
    public byte[] getCommData() {
        synchronized (this) {
            return new byte[1];
        }
    }

    @Override
    public LEDArray getLEDArray() {
        return ledArray;
    }

    @Override
    public void updateLEDArray() {
        ledArray = new LEDArray(this, red, green, blue);
    }

    @Override
    public Color getLEDColorReal(int x, int y, int z) {
        return getLEDColor(x, y, z);
    }

    @Override
    public Color getLEDColor(int x, int y, int z) {
        if (x < 0 || x > 15)
            throw new IllegalArgumentException("Invalid X coordinate: " + x);
        if (y < 0 || y > 15)
            throw new IllegalArgumentException("Invalid Y coordinate: " + y);
        if (z < 0 || z > 15)
            throw new IllegalArgumentException("Invalid Z coordinate: " + z);
        int i = (y << 8) | (x << 4) | z;
        return new Color(red[i], green[i], blue[i]);
    }

    @Override
    public void setLEDColorReal(int x, int y, int z, ReadableColor color) {
        setLEDColor(x, y, z, color);
    }

    @Override
    public void setLEDColor(int x, int y, int z, ReadableColor color) {
        if (x < 0 || x > 15)
            throw new IllegalArgumentException("Invalid X coordinate: " + x);
        if (y < 0 || y > 15)
            throw new IllegalArgumentException("Invalid Y coordinate: " + y);
        if (z < 0 || z > 15)
            throw new IllegalArgumentException("Invalid Z coordinate: " + z);

        int i = (y << 8) | (x << 4) | z;
        red[i] = color.getRedByte();
        green[i] = color.getGreenByte();
        blue[i] = color.getBlueByte();
    }

    @Override
    public int encodeVector(Vector3 vector) {
        return encodeVector((int) vector.getY(), (int) vector.getX(), (int) vector.getZ());
    }

    @Override
    public int encodeVector(int x, int y, int z) {
        return (y << 8) | (x << 4) | z;
    }

    @Override
    public Vector3 decodeVector(int value) {
        return new Vector3((value >> 4) & 15, (value >> 8) & 15, value & 15);
    }

    @Override
    public int getBaudRate() {
        return 921600;
    }
}
