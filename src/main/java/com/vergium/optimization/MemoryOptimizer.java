package com.vergium.optimization;

import com.vergium.Vergium;
import com.vergium.config.VergiumConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MemoryOptimizer {

    private final VergiumConfig config;
    private int tickCounter = 0;
    private long lastGCTime = 0;
    private static final long MIN_GC_INTERVAL_MS = 30_000;

    private static final double HIGH_MEMORY_THRESHOLD = 0.85;
    private static final double CRITICAL_MEMORY_THRESHOLD = 0.95;

    private boolean highMemory = false;
    private boolean criticalMemory = false;

    public MemoryOptimizer(VergiumConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!config.isMemoryOptimization()) return;
        if (event.phase != TickEvent.Phase.START) return;

        tickCounter++;

        if (tickCounter % 100 == 0) {
            checkMemory();
        }
    }

    private void checkMemory() {
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory();
        long usedMem = runtime.totalMemory() - runtime.freeMemory();
        double usage = (double) usedMem / maxMem;

        boolean wasHighMemory = highMemory;
        highMemory = usage > HIGH_MEMORY_THRESHOLD;
        criticalMemory = usage > CRITICAL_MEMORY_THRESHOLD;

        if (criticalMemory) {
            long now = System.currentTimeMillis();
            if (now - lastGCTime > MIN_GC_INTERVAL_MS) {
                Vergium.LOGGER.warn("Memory critically high ({}%), suggesting GC",
                    String.format("%.1f", usage * 100));
                System.gc();
                lastGCTime = now;
            }
        } else if (highMemory && !wasHighMemory) {
            Vergium.LOGGER.info("Memory usage high ({}%), enabling aggressive optimizations",
                String.format("%.1f", usage * 100));
        }
    }

    public boolean isHighMemory() {
        return highMemory;
    }

    public boolean isCriticalMemory() {
        return criticalMemory;
    }

    public double getMemoryUsageRatio() {
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory();
        long usedMem = runtime.totalMemory() - runtime.freeMemory();
        return (double) usedMem / maxMem;
    }

    public boolean shouldReduceCache() {
        return highMemory;
    }

    public boolean shouldSkipNonEssentialAllocations() {
        return criticalMemory;
    }
}
