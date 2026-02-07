package com.vergium.optimization;

import com.vergium.config.VergiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class ChunkOptimizer {

    private final VergiumConfig config;
    private int tickCounter = 0;
    private int chunkUpdatesThisFrame = 0;
    private final Set<Long> recentlyUpdatedChunks = new HashSet<>();

    public ChunkOptimizer(VergiumConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!config.isChunkOptimization()) return;
        if (event.phase != TickEvent.Phase.START) return;

        tickCounter++;
        chunkUpdatesThisFrame = 0;

        // Clear recently updated chunks periodically
        if (tickCounter % 100 == 0) {
            recentlyUpdatedChunks.clear();
        }
    }

    public boolean shouldUpdateChunk(int chunkX, int chunkZ) {
        if (!config.isChunkOptimization()) return true;

        // Check update limit
        if (chunkUpdatesThisFrame >= config.getChunkUpdateLimit()) {
            return false;
        }

        // Lazy chunk loading - spread updates across frames
        if (config.isLazyChunkLoading()) {
            long key = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
            if (recentlyUpdatedChunks.contains(key)) {
                return false;
            }
        }

        // Priority based on distance
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            double playerChunkX = mc.player.getX() / 16.0;
            double playerChunkZ = mc.player.getZ() / 16.0;
            double dist = Math.sqrt(
                Math.pow(chunkX - playerChunkX, 2) +
                Math.pow(chunkZ - playerChunkZ, 2)
            );

            // Skip distant chunks on odd frames for lazy loading
            if (config.isLazyChunkLoading() && dist > 8 && tickCounter % 2 == 0) {
                return false;
            }
        }

        chunkUpdatesThisFrame++;
        return true;
    }

    public void markChunkUpdated(int chunkX, int chunkZ) {
        long key = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
        recentlyUpdatedChunks.add(key);
    }

    public boolean shouldRenderChunk(int chunkX, int chunkY, int chunkZ) {
        if (!config.isChunkOptimization()) return true;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;

        Vec3 playerPos = mc.player.position();
        double dx = (chunkX * 16 + 8) - playerPos.x;
        double dy = (chunkY * 16 + 8) - playerPos.y;
        double dz = (chunkZ * 16 + 8) - playerPos.z;
        double distSq = dx * dx + dy * dy + dz * dz;
        double cullDist = config.getCullDistance();

        return distSq <= cullDist * cullDist;
    }

    public int getChunkUpdatesThisFrame() {
        return chunkUpdatesThisFrame;
    }
}
