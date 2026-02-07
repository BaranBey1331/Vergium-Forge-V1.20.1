package com.vergium.optimization;

import com.vergium.Vergium;
import com.vergium.config.VergiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class EntityOptimizer {

    private final VergiumConfig config;
    private int frameCounter = 0;
    private final Map<Integer, Integer> entitySkipFrames = new HashMap<>();

    public EntityOptimizer(VergiumConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (!config.isEntityOptimization()) return;
        if (event.phase != TickEvent.Phase.START) return;
        frameCounter++;

        // Clean up skip frame map periodically
        if (frameCounter % 600 == 0) {
            entitySkipFrames.clear();
        }
    }

    public boolean shouldRenderEntity(Entity entity) {
        if (!config.isEntityOptimization()) return true;
        if (entity instanceof Player) return true; // Always render players

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;

        Vec3 playerPos = mc.player.position();
        Vec3 entityPos = entity.position();
        double distSq = playerPos.distanceToSqr(entityPos);

        float renderDist = config.getEntityRenderDistance();
        FpsOptimizer fpsOpt = Vergium.getInstance().getFpsOptimizer();
        if (fpsOpt != null) {
            renderDist = fpsOpt.getAdjustedEntityRenderDistance();
        }

        // Hard cull
        if (distSq > renderDist * renderDist) {
            return false;
        }

        // LOD-based frame skipping for distant entities
        double dist = Math.sqrt(distSq);
        int skipInterval = calculateSkipInterval(entity, dist);

        if (skipInterval > 1) {
            int entityId = entity.getId();
            int lastRendered = entitySkipFrames.getOrDefault(entityId, 0);
            if (frameCounter - lastRendered < skipInterval) {
                return false;
            }
            entitySkipFrames.put(entityId, frameCounter);
        }

        return true;
    }

    private int calculateSkipInterval(Entity entity, double distance) {
        float lodBias = config.getLodBias();

        // Base skip interval on distance
        int baseSkip;
        if (distance < 16) {
            baseSkip = 1; // Render every frame
        } else if (distance < 32) {
            baseSkip = 2;
        } else if (distance < 48) {
            baseSkip = 3;
        } else {
            baseSkip = 4;
        }

        // Adjust for entity type - less important entities can be skipped more
        if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) {
            baseSkip *= 2;
        } else if (entity instanceof ArmorStand) {
            baseSkip *= 3;
        }

        // Apply LOD bias (lower = more aggressive culling)
        baseSkip = (int) (baseSkip / lodBias);

        // Aggressive culling mode
        if (config.isAggressiveCulling()) {
            baseSkip = (int) (baseSkip * 1.5);
        }

        return Math.max(1, baseSkip);
    }

    public boolean shouldTickEntity(Entity entity) {
        if (!config.isEntityOptimization() || !config.isTickOptimization()) return true;
        if (entity instanceof Player) return true;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;

        double distSq = mc.player.position().distanceToSqr(entity.position());

        // Don't tick very distant entities as often
        if (distSq > 64 * 64) {
            return frameCounter % 4 == (entity.getId() % 4);
        } else if (distSq > 32 * 32) {
            return frameCounter % 2 == (entity.getId() % 2);
        }

        return true;
    }
}
