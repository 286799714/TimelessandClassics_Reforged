package com.tac.guns.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mixin(LevelRenderer.class)
public class WorldRendererMixin
{
    /*@Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;checkMatrixStack(Lcom/mojang/blaze3d/matrix/MatrixStack;)V", ordinal = 0))
    private void renderBullets(PoseStack stack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera info, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo ci) {
        //BulletTrailRenderingHandler.get().render(stack, partialTicks);
    }*/
}
