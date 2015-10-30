package com.techjar.ledcm.hardware.animation;

import com.techjar.ledcm.LEDCubeManager;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import org.lwjgl.util.Color;

/**
 * @author Techjar
 */
public class AnimationSpectrumShooters16 extends AnimationSpectrumAnalyzer {

    private float[] amplitudes = new float[256];
    private int bandIncrement = 1;
    private boolean rainbow = false;
    private float sensitivity = 20.0F;

    @Override
    public String getName() {
        return "Spectrum Shooters 16";
    }

    @Override
    public synchronized void refresh() {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 14; z >= 0; z--) {
                    ledManager.setLEDColor(x, y, z + 1, ledManager.getLEDColor(x, y, z));
                    ledManager.setLEDColor(x, y, z, new Color());
                }
            }
        }
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                int i = x + y * 16;
                float amplitude = amplitudes[i] - 2;
                if (amplitude > sensitivity * (1 - (i / 270F))) {
                    Color color = new Color();
                    if (rainbow)
                        color.fromHSB(i / 255F, 1, 1);
                    else
                        color = LEDCubeManager.getPaintColor();
                    ledManager.setLEDColor(x, y, 0, color);
                }
            }
        }
    }

    @Override
    public void reset() {
        amplitudes = new float[256];
    }

    @Override
    public boolean isFFT() {
        return true;
    }

    @Override
    public boolean isBeatDetect() {
        return false;
    }

    @Override
    public int getBeatDetectMode() {
        return BeatDetect.FREQ_ENERGY;
    }

    @Override
    public synchronized void processFFT(FFT fft) {
        for (int i = 0; i < 256; i++) {
            float amplitude = 0;
            for (int j = 0; j < bandIncrement; j++) {
                float band = fft.getBand(i);
                if (band > amplitude)
                    amplitude = band;
            }
            if (amplitude > amplitudes[i])
                amplitudes[i] = amplitude;
            else if (amplitudes[i] > 0)
                amplitudes[i] -= Math.max(amplitudes[i] / 7, 1F);
        }
    }

    @Override
    public void processBeatDetect(BeatDetect bt) {
    }
}
