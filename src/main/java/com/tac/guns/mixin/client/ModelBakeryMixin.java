package com.tac.guns.mixin.client;

import com.tac.guns.client.event.ModelBakeryProcessLoadingEvent;
import com.tac.guns.client.resource.gunskin.GunSkinLoader;
import com.tac.guns.client.resource.gunskin.SkinManager;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin({ModelBakery.class})
public abstract class ModelBakeryMixin{
    @Shadow
    public abstract UnbakedModel getModel(ResourceLocation modelLocation);
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> topLevelModels;
    @Shadow @Final public static ModelResourceLocation MISSING_MODEL_LOCATION;

    @Inject(method = "processLoading",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 0
            ),
            remap = true)
    public void onBakeryLoading(ProfilerFiller p_119249_, int p_119250_, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ModelBakeryProcessLoadingEvent(unbakedCache, topLevelModels, getModel(MISSING_MODEL_LOCATION)));
        //reload
        SkinManager.reload();
        GunSkinLoader.load();
    }
}