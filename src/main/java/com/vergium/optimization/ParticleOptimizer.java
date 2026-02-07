package com.vergium.optimization;

import com.vergium.config.VergiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class ParticleOptimizer {

    private final VergiumConfig config;
    private final AtomicInteger activeParticleCount = new AtomicInteger(0);
    private int frameCounter = 0;

    public ParticleOptimizer(VergiumConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            frameCounter++;
            // Reset counter periodically (will be re-counted)
            if (frameCounter % 20 == 0) {
                activeParticleCount.set(0);
            }
        }
    }

    public boolean shouldSpawnParticle(double x, double y, double z) {
        if (!config.isParticleOptimization()) return true;

        // Check particle limit
        if (activeParticleCount.get() >= config.getMaxParticles()) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;

        Vec3 playerPos = mc.player.position();
        double distSq = playerPos.distanceToSqr(x, y, z);

        // Cull distant particles
        double cullDist = config.getCullDistance() * 0.75; // Particles cull closer
        if (distSq > cullDist * cullDist) {
            return false;
        }

        // Probabilistic culling based on distance
        if (distSq > 32 * 32) {
            // 50% chance to cull at medium distance
            return frameCounter % 2 == 0;
        }
        if (distSq > 48 * 48) {
            // 75% chance to cull at far distance
            return frameCounter % 4 == 0;
        }

        activeParticleCount.incrementAndGet();
        return true;
    }

    public boolean shouldRenderParticle(double x, double y, double z) {
        if (!config.isParticleOptimization()) return true;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;

        Vec3 playerPos = mc.player.position();
        double distSq = playerPos.distanceToSqr(x, y, z);

        double renderDist = config.getEntityRenderDistance() * 0.5;
        return distSq <= renderDist * renderDist;
    }

    public void setActiveParticleCount(int count) {
        activeParticleCount.set(count);
    }

    public int getActiveParticleCount() {
        return activeParticleCount.get();
    }
}
