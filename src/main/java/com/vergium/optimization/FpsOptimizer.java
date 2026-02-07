package com.vergium.optimization;

import com.vergium.Vergium;
import com.vergium.config.VergiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FpsOptimizer {

    private final VergiumConfig config;
    private int frameCounter = 0;
    private boolean reducedMode = false;

    public FpsOptimizer(VergiumConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (!config.isFpsBoostEnabled()) return;
        if (event.phase != TickEvent.Phase.START) return;

        frameCounter++;
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        // Dynamically adjust render settings based on current performance
        if (config.isFramePacing()) {
            applyFramePacing(mc);
        }
    }

    private void applyFramePacing(Minecraft mc) {
        var tracker = Vergium.getInstance().getPerformanceTracker();
        if (tracker == null) return;

        int currentFps = tracker.getFps();
        int targetFps = config.getTargetFps();

        // If we're below target FPS, enter reduced mode
        if (currentFps < targetFps * 0.8 && !reducedMode) {
            reducedMode = true;
            Vergium.LOGGER.debug("Entering reduced rendering mode (FPS: {})", currentFps);
        } else if (currentFps > targetFps * 1.1 && reducedMode) {
            reducedMode = false;
            Vergium.LOGGER.debug("Exiting reduced rendering mode (FPS: {})", currentFps);
        }
    }

    public boolean isReducedMode() {
        return reducedMode;
    }

    public int getAdjustedChunkUpdateLimit() {
        if (!config.isFpsBoostEnabled()) return config.getChunkUpdateLimit();
        if (reducedMode) {
            return Math.max(1, config.getChunkUpdateLimit() / 2);
        }
        return config.getChunkUpdateLimit();
    }

    public float getAdjustedEntityRenderDistance() {
        if (!config.isFpsBoostEnabled()) return config.getEntityRenderDistance();
        if (reducedMode) {
            return config.getEntityRenderDistance() * 0.6f;
        }
        return config.getEntityRenderDistance();
    }
}
