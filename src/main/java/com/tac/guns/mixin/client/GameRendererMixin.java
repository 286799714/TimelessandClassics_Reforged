package com.tac.guns.mixin.client;

import com.tac.guns.client.handler.AimingHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(method = "getFov", at = @At("HEAD"))
    public void getIsRenderHand(Camera p_109142_, float p_109143_, boolean p_109144_, CallbackInfoReturnable<Double> cir){
        AimingHandler.get().isRenderingHand = !p_109144_;
    }
}