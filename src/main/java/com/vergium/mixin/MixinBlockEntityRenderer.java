package com.vergium.mixin;

import com.vergium.Vergium;
import com.vergium.optimization.RenderOptimizer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderer {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void onRender(E blockEntity, float partialTick,
                                                    PoseStack poseStack,
                                                    MultiBufferSource bufferSource,
                                                    CallbackInfo ci) {
        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        RenderOptimizer renderOpt = instance.getRenderOptimizer();
        if (renderOpt != null && !renderOpt.shouldRenderBlockEntity(blockEntity)) {
            ci.cancel();
        }
    }
}
