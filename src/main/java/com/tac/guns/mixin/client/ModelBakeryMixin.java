package com.tac.guns.mixin.client;

import com.mojang.logging.LogUtils;
import com.tac.guns.client.render.gunskin.GunSkinLoader;
import com.tac.guns.client.render.gunskin.SkinLoader;
import com.tac.guns.client.render.gunskin.SkinManager;

import com.tac.guns.duck.CacheableModelBakery;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin({ModelBakery.class})
public abstract class ModelBakeryMixin implements CacheableModelBakery {
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

        //init
        SkinLoader.missingModel = getModel(MISSING_MODEL_LOCATION);
        SkinLoader.unbakedModels = unbakedCache;
        SkinLoader.topUnbakedModels = topLevelModels;
        GunSkinLoader.missingModel = getModel(MISSING_MODEL_LOCATION);
        GunSkinLoader.unbakedModels = unbakedCache;
        GunSkinLoader.topUnbakedModels = topLevelModels;

        //reload
        SkinManager.reload();
    }
}