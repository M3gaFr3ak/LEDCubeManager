package com.techjar.ledcm.hardware.manager;

import com.techjar.ledcm.hardware.LEDArray;
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
    private byte[] greyscale = new byte[4096];
    private LEDArray ledArray;


    public STP16Monochrome16(String[] args) {
    }

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
            byte[] data = new byte[16 * 16 * 2];
            int[] dataBuffer = new int[16 * 16];
            for (int layer = 0; layer < 16; layer++) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        if (greyscale[layer * 256 + x * 16 + y] != 0)
                            dataBuffer[layer * 16 + x] |= 1 << y;
                    }
                }
            }

            for (int i = 0, j = 0; i < 256; i++, j += 2) {
                data[j + 1] = (byte) dataBuffer[i];
                data[j] = (byte) (dataBuffer[i] >> 8);
            }
            return data;
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
        greyscale[i] = (byte) ((0.2125 * color.getRed()) + (0.7154 * color.getGreen()) + (0.0721 * color.getBlue()));
        if (greyscale[i] != 0)
            greyscale[i] = (byte) 0xFF;
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
        return 921600;//TODO 2000000;
    }
}
