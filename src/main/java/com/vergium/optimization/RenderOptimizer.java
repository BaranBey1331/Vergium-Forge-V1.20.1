package com.vergium.optimization;

import com.vergium.Vergium;
import com.vergium.config.VergiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderOptimizer {

    private final VergiumConfig config;
    private int frameCounter = 0;

    // Frustum culling cache
    private Vec3 lastCameraPos;
    private float lastCameraYaw;
    private float lastCameraPitch;
    private boolean cameraMovedSignificantly = true;

    public RenderOptimizer(VergiumConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (!config.isRenderOptimization()) return;
        if (event.phase != TickEvent.Phase.START) return;

        frameCounter++;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Vec3 currentPos = mc.player.position();
            float currentYaw = mc.player.getYRot();
            float currentPitch = mc.player.getXRot();

            if (lastCameraPos != null) {
                double posDelta = currentPos.distanceToSqr(lastCameraPos);
                float yawDelta = Math.abs(currentYaw - lastCameraYaw);
                float pitchDelta = Math.abs(currentPitch - lastCameraPitch);

                cameraMovedSignificantly = posDelta > 0.01 || yawDelta > 0.5 || pitchDelta > 0.5;
            }

            lastCameraPos = currentPos;
            lastCameraYaw = currentYaw;
            lastCameraPitch = currentPitch;
        }
    }

    public boolean shouldRenderBlockEntity(BlockEntity blockEntity) {
        if (!config.isRenderOptimization()) return true;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 playerPos = mc.player.position();
        double distSq = playerPos.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

        // Cull distant block entities
        double cullDist = config.getCullDistance() * 0.5;
        if (distSq > cullDist * cullDist) {
            return false;
        }

        // LOD skip for distant block entities
        if (distSq > 32 * 32) {
            return frameCounter % 3 == 0;
        }

        return true;
    }

    public boolean shouldUpdateLighting() {
        if (!config.isRenderOptimization()) return true;

        // Only update lighting every other frame when camera hasn't moved
        if (!cameraMovedSignificantly) {
            return frameCounter % 2 == 0;
        }
        return true;
    }

    public boolean hasCameraMovedSignificantly() {
        return cameraMovedSignificantly;
    }

    public boolean isInFrustum(AABB aabb) {
        // Basic frustum check - the actual frustum culling is done in mixins
        if (!config.isRenderOptimization()) return true;
        if (!config.isAggressiveCulling()) return true;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;

        Vec3 playerPos = mc.player.position();
        double distSq = aabb.distanceToSqr(playerPos);
        double cullDist = config.getCullDistance();

        return distSq <= cullDist * cullDist;
    }
}
