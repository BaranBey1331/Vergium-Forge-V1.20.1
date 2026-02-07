package com.vergium.mixin;

import com.vergium.Vergium;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "runTick", at = @At("HEAD"))
    private void onTickStart(boolean renderLevel, CallbackInfo ci) {
        // Hook into the game tick for global optimization coordination
        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        var heatOpt = instance.getHeatOptimizer();
        if (heatOpt != null && heatOpt.shouldHeavilyReduce()) {
            // When heavily throttling, we can skip some processing
            // The actual skipping is handled by individual optimizers
        }
    }
}
