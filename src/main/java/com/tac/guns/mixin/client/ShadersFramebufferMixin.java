package com.tac.guns.mixin.client;

import com.tac.guns.client.render.ScreenTextureState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO: MIXIN DISABLED!!!!!!!


@Pseudo // OptiFine may not be installed
//@Mixin(targets = "net.optifine.shaders.Shaders")
@Mixin(targets = "net.optifine.shaders.ShadersRender")
public class ShadersFramebufferMixin
{
    @Dynamic("Added by OptiFine")
    @Inject(
            method = "renderHand0",
            at = @At(value = "INVOKE"),
            cancellable = true,
            //require = 0, // Don't fail if OptiFine isn't present
            remap = false // OptiFine added method
    )
    private static void tac_forceUnsetRightHand(CallbackInfo ci) {
        ScreenTextureState.instance().onPreRenderHand0();
    }

    @Dynamic("Added by OptiFine")
    @Inject(
            method = "renderHand1",
            at = @At(value = "INVOKE"),
            cancellable = true,
            //require = 0, // Don't fail if OptiFine isn't present
            remap = false // OptiFine added method
    )
    private static void tac_forceUnsetLeftHand(CallbackInfo ci) {
        ScreenTextureState.instance().onPreRenderHand1();
    }







    //if (Shaders.isRenderingWorld) {

    // relearn how to steal inputs, using int cap of glDisableWrapper can help

    //renderFinal
    /**
     * bindColorImage
     */

    /*@Dynamic("Added by OptiFine")
    @Inject(
            method = "glDisableWrapper",
            at = @At(value = "HEAD"),
            cancellable = false,
            //require = 0, // Don't fail if OptiFine isn't present
            remap = false // OptiFine added method
    )
    private static void tac_getMappedShaderImage(CallbackInfo ci) {
        if(ScreenTextureState.instance() != null && Minecraft.getInstance().player != null && Minecraft.getInstance().world != null) {
            //OptifineHelper.setSkipRenderHands();
            ScreenTextureState.instance().SetImageFromOptifine();
        }
    }*/
}