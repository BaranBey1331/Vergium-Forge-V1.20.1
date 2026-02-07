package com.vergium.mixin;

import com.mojang.blaze3d.platform.Window;
import com.vergium.Vergium;
import com.vergium.optimization.HeatOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class MixinWindow {

    @Inject(method = "updateVsync", at = @At("HEAD"))
    private void onUpdateVsync(boolean vsync, CallbackInfo ci) {
        // Track VSync state for frame pacing decisions
        Vergium instance = Vergium.getInstance();
        if (instance != null) {
            Vergium.LOGGER.debug("VSync state updated: {}", vsync);
        }
    }
}
