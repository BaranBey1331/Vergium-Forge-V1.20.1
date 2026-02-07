package com.vergium.mixin;

import com.vergium.Vergium;
import com.vergium.optimization.EntityOptimizer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(T entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                 double camX, double camY, double camZ,
                                 CallbackInfoReturnable<Boolean> cir) {
        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        EntityOptimizer entityOpt = instance.getEntityOptimizer();
        if (entityOpt != null && !entityOpt.shouldRenderEntity(entity)) {
            cir.setReturnValue(false);
        }
    }
}
