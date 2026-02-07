package com.vergium.mixin;

import com.vergium.Vergium;
import com.vergium.optimization.ChunkOptimizer;
import com.vergium.optimization.HeatOptimizer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderDispatcher.class)
public abstract class MixinChunkRenderDispatcher {

    @Inject(method = "uploadAllPendingUploads", at = @At("HEAD"))
    private void onUploadPending(CallbackInfo ci) {
        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        // The chunk optimizer controls update rates
        // Heat optimizer can further reduce chunk update throughput
        HeatOptimizer heatOpt = instance.getHeatOptimizer();
        if (heatOpt != null && heatOpt.shouldReduceWorkload()) {
            // Limit is applied through the config dynamically
        }
    }
}
