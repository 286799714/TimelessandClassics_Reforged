package com.tac.guns.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.tac.guns.client.handler.BulletTrailRenderingHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mixin(LevelRenderer.class)
public class WorldRendererMixin
{
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0))
    private void renderBullets(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_109607_, CallbackInfo ci) {
        //TODO: FIX TRAILS AND ADJUST FOR BULLET SPEED
        if(BulletTrailRenderingHandler.get() != null)
            BulletTrailRenderingHandler.get().render(p_109600_, p_109601_);
    }
}
