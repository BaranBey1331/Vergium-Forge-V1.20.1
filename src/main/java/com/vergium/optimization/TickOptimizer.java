package com.vergium.optimization;

import com.vergium.config.VergiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TickOptimizer {

    private final VergiumConfig config;
    private int tickCounter = 0;
    private long lastTickDuration = 0;
    private long tickStartTime = 0;
    private boolean tickOverloaded = false;

    private static final long MAX_TICK_TIME_NS = 50_000_000L; // 50ms = 1 tick

    public TickOptimizer(VergiumConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!config.isTickOptimization()) return;

        if (event.phase == TickEvent.Phase.START) {
            tickCounter++;
            tickStartTime = System.nanoTime();
        } else {
            lastTickDuration = System.nanoTime() - tickStartTime;
            tickOverloaded = lastTickDuration > MAX_TICK_TIME_NS;
        }
    }

    public boolean shouldProcessRedstone() {
        if (!config.isTickOptimization()) return true;
        // Skip some redstone processing when tick overloaded
        if (tickOverloaded) {
            return tickCounter % 2 == 0;
        }
        return true;
    }

    public boolean shouldProcessBlockTicks() {
        if (!config.isTickOptimization()) return true;
        if (tickOverloaded) {
            return tickCounter % 3 == 0;
        }
        return true;
    }

    public int getReducedTickRate() {
        if (!config.isTickOptimization()) return 1;
        if (tickOverloaded) return 2;
        return 1;
    }

    public boolean isTickOverloaded() {
        return tickOverloaded;
    }

    public long getLastTickDuration() {
        return lastTickDuration;
    }

    public int getTickCounter() {
        return tickCounter;
    }
}
