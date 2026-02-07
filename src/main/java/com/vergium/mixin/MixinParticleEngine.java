package com.vergium.mixin;

import com.vergium.Vergium;
import com.vergium.optimization.ParticleOptimizer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class MixinParticleEngine {

    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void onCreateParticle(ParticleOptions options, double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed,
                                   CallbackInfoReturnable<Particle> cir) {
        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        ParticleOptimizer particleOpt = instance.getParticleOptimizer();
        if (particleOpt != null && !particleOpt.shouldSpawnParticle(x, y, z)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        // Update particle count tracking
        var heatOpt = instance.getHeatOptimizer();
        if (heatOpt != null && heatOpt.shouldReduceWorkload()) {
            // Heat optimizer will reduce max particles dynamically
        }
    }
}
