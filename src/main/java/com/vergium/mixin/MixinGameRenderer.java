package com.vergium.mixin;

import com.vergium.Vergium;
import com.vergium.optimization.HeatOptimizer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderStart(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        // Coordinate all optimizers before frame render
        var heatOpt = instance.getHeatOptimizer();
        if (heatOpt != null) {
            float multiplier = heatOpt.getPerformanceMultiplier();
            // This multiplier is used by individual optimizers via getters
        }
    }
}
