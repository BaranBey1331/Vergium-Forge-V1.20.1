package com.vergium.optimization;

import com.vergium.Vergium;
import com.vergium.config.VergiumConfig;
import com.vergium.util.SystemMonitor;
import com.vergium.util.SystemMonitor.ThermalState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HeatOptimizer {

    private final VergiumConfig config;
    private final SystemMonitor systemMonitor;
    private int tickCounter = 0;

    private double throttleLevel = 0.0;

    private long lastFrameEnd = 0;
    private long targetFrameTimeNs;
    private long additionalDelayNs = 0;

    public HeatOptimizer(VergiumConfig config, SystemMonitor systemMonitor) {
        this.config = config;
        this.systemMonitor = systemMonitor;
        updateTargetFrameTime();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!config.isHeatManagementEnabled()) return;
        if (event.phase != TickEvent.Phase.START) return;

        tickCounter++;

        systemMonitor.update();

        if (tickCounter % 20 == 0) {
            updateThrottleLevel();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (!config.isHeatManagementEnabled()) return;

        if (event.phase == TickEvent.Phase.END) {
            if (config.isFramePacing() && additionalDelayNs > 0) {
                long now = System.nanoTime();
                long elapsed = now - lastFrameEnd;
                long sleepNs = additionalDelayNs - elapsed;

                if (sleepNs > 1_000_000) {
                    try {
                        Thread.sleep(sleepNs / 1_000_000, (int) (sleepNs % 1_000_000));
                    } catch (InterruptedException ignored) {}
                }
            }
            lastFrameEnd = System.nanoTime();
        }
    }

    private void updateThrottleLevel() {
        double cpuUsage = systemMonitor.getCpuUsage();
        ThermalState thermalState = systemMonitor.getThermalState();
        int cpuLimit = config.getCpuUsageLimit();

        double cpuThrottle = 0;
        if (cpuUsage > cpuLimit) {
            cpuThrottle = Math.min(1.0, (cpuUsage - cpuLimit) / (100.0 - cpuLimit));
        }

        double thermalThrottle = 0;
        if (config.isThermalThrottle()) {
            switch (thermalState) {
                case COOL: thermalThrottle = 0; break;
                case WARM: thermalThrottle = 0.15; break;
                case HOT: thermalThrottle = 0.4; break;
                case THROTTLING: thermalThrottle = 0.7; break;
            }
        }

        double memoryThrottle = 0;
        MemoryOptimizer memOpt = Vergium.getInstance().getMemoryOptimizer();
        if (memOpt != null) {
            if (memOpt.isCriticalMemory()) {
                memoryThrottle = 0.5;
            } else if (memOpt.isHighMemory()) {
                memoryThrottle = 0.2;
            }
        }

        double targetThrottle = Math.max(cpuThrottle, Math.max(thermalThrottle, memoryThrottle));
        throttleLevel = throttleLevel * 0.8 + targetThrottle * 0.2;

        updateTargetFrameTime();
        if (throttleLevel > 0.1) {
            long baseDelay = targetFrameTimeNs / 10;
            additionalDelayNs = (long) (baseDelay * throttleLevel);
        } else {
            additionalDelayNs = 0;
        }

        if (throttleLevel > 0.3) {
            Vergium.LOGGER.debug("Heat throttle active: {}% (CPU: {}%, Thermal: {})",
                String.format("%.1f", throttleLevel * 100),
                String.format("%.1f", cpuUsage),
                thermalState);
        }
    }

    private void updateTargetFrameTime() {
        targetFrameTimeNs = 1_000_000_000L / config.getTargetFps();
    }

    public double getThrottleLevel() {
        return throttleLevel;
    }

    public boolean shouldReduceWorkload() {
        return throttleLevel > 0.3;
    }

    public boolean shouldHeavilyReduce() {
        return throttleLevel > 0.6;
    }

    public float getPerformanceMultiplier() {
        return (float) Math.max(0.25, 1.0 - throttleLevel * 0.75);
    }

    public int getAdjustedMaxParticles() {
        return (int) (config.getMaxParticles() * getPerformanceMultiplier());
    }

    public int getAdjustedChunkUpdateLimit() {
        return Math.max(1, (int) (config.getChunkUpdateLimit() * getPerformanceMultiplier()));
    }

    public float getAdjustedEntityRenderDistance() {
        return config.getEntityRenderDistance() * getPerformanceMultiplier();
    }
}
