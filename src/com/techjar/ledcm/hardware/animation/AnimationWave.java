package com.techjar.ledcm.hardware.animation;

import com.techjar.ledcm.LEDCubeManager;
import com.techjar.ledcm.util.Timer;
import org.lwjgl.util.Color;

public class AnimationWave extends Animation {

    private Timer timer = new Timer();
    private boolean fill = false;

    @Override
    public String getName() {
        return "Wave";
    }

    @Override
    public void refresh() {
        float centerX = ((dimension.getX() - 1) / 2);
        float centerY = ((dimension.getY() - 1) / 2);
        float centerZ = ((dimension.getZ() - 1) / 2);
        for (int x = 0; x < dimension.getX(); x++)
            for (int y = 0; y < dimension.getY(); y++) {
                float amplitude = (float) (Math.sin((timer.getSeconds() * 5) + Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2))) * 3.5 + centerZ);

                for (int z = 0; z < dimension.getZ(); z++) {
                    boolean paint = fill ? z <= Math.round(amplitude) : Math.round(amplitude) == z;
                    ledManager.setLEDColor(x, z, y, !paint ? new Color(0, 0, 0) : LEDCubeManager.getPaintColor());
                }
            }
    }

    @Override
    public void reset() {
        timer.restart();
    }
}
