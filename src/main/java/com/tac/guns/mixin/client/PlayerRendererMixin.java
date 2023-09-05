package com.tac.guns.mixin.client;

import com.tac.guns.client.render.armor.vestlayer.VestLayerRender;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
        super(p_174289_, p_174290_, p_174291_);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V", at = @At("RETURN"))
    public void addArmorLayer(EntityRendererProvider.Context p_174557_, boolean p_174558_, CallbackInfo ci){
        this.addLayer(new VestLayerRender<>(this));
    }
}
