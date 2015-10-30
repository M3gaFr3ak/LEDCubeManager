package com.techjar.ledcm.hardware.animation;

import com.techjar.ledcm.util.MathHelper;
import com.techjar.ledcm.util.Vector2;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import org.lwjgl.util.Color;

public class AnimationSpectrumBars16 extends AnimationSpectrumAnalyzer {

    private float[] amplitudes = new float[256];
    private int bandIncrement = 1;

    @Override
    public String getName() {
        return "Spectrum Bars 16";
    }

    @Override
    public synchronized void refresh() {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                int i = x + y * 16;
                float amplitude = amplitudes[i] - 2;
                for (int j = 0; j < 16; j++) {
                    float increment = (5.0F * (j + 1)) * (1 - (i / 270F));
                    ledManager.setLEDColorReal(x, j, y, amplitude > 0 ? colorAtY(j, MathHelper.clamp(amplitude / increment, 0, 1)) : new Color());
                    amplitude -= increment;
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
                float band = fft.getBand(i * bandIncrement + j);
                if (band > amplitude)
                    amplitude = band;
            }
            if (amplitude > amplitudes[i])
                amplitudes[i] = amplitude;
            else if (amplitudes[i] > 0)
                amplitudes[i] -= Math.max(amplitudes[i] / 15, 1F);
        }
    }

    @Override
    public void processBeatDetect(BeatDetect bt) {
    }

    private Color colorAtY(int y, float brightness) {
        int res = ledManager.getResolution();
        if (y > 12)
            return new Color(Math.round(res * brightness), 0, 0);
        if (y > 8)
            return new Color(Math.round(res * brightness), Math.round(res * brightness), 0);
        if (y > 1)
            return new Color(0, Math.round(res * brightness), 0);
        return new Color(0, 0, Math.round(res * brightness));
    }

    private Vector2 spiralPosition(int index) {
        // (di, dj) is a vector - direction in which we move right now
        int di = 1;
        int dj = 0;
        // length of current segment
        int segment_length = 1;

        // current position (i, j) and how much of current segment we passed
        int i = 0;
        int j = 0;
        int segment_passed = 0;
        for (int k = 0; k < index; ++k) {
            // make a step, add 'direction' vector (di, dj) to current position (i, j)
            i += di;
            j += dj;
            ++segment_passed;

            if (segment_passed == segment_length) {
                // done with current segment
                segment_passed = 0;

                // 'rotate' directions
                int buffer = di;
                di = -dj;
                dj = buffer;

                // increase segment length if necessary
                if (dj == 0) {
                    ++segment_length;
                }
            }
        }

        return new Vector2(i, j);
    }
}
