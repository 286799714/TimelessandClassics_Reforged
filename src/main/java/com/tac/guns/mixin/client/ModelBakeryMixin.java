package com.tac.guns.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tac.guns.GunMod;
import com.tac.guns.client.resource.gunskin.GunSkin;
import com.tac.guns.client.resource.gunskin.VanillaGunSkinLoader;

import com.tac.guns.client.resource.gunskin.GunSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        VanillaGunSkinLoader.missingModel = getModel(MISSING_MODEL_LOCATION);
        VanillaGunSkinLoader.unbakedCache = unbakedCache;
        VanillaGunSkinLoader.topLevelModels = topLevelModels;

        //load all gun skins which use vanilla loader
        Minecraft.getInstance().getResourceManager().getNamespaces();
        Set<String> nameSpaces = Minecraft.getInstance().getResourceManager().getNamespaces();
        for (String nameSpace : nameSpaces) {
            ResourceLocation location = new ResourceLocation(nameSpace, GunSkinManager.skinIndex);
            try {
                List<Resource> resources = Minecraft.getInstance().getResourceManager().getResources(location);
                GunMod.LOGGER.info("loading vanilla skins from {}", location);
                VanillaGunSkinLoader loader = new VanillaGunSkinLoader();
                for (Resource resource : resources) {
                    InputStream stream = resource.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    for (Map.Entry<String, JsonElement> elementEntry : json.entrySet()) {
                        JsonObject valueJson = elementEntry.getValue().getAsJsonObject();
                        if(!"vanilla".equals(valueJson.get("loader").getAsString())) continue;
                        ResourceLocation gunSkinLocation = new ResourceLocation(nameSpace, elementEntry.getKey() + VanillaGunSkinLoader.extension);
                        //try to load and register skin
                        GunSkin skin = loader.loadGunSkin(gunSkinLocation);
                        if(skin != null) {
                            GunSkinManager.getInstance().registerGunSkin(skin);
                        }
                    }
                }
            } catch (IOException ignore) {}
        }
        GunSkinManager.getInstance().cleanCache();
    }
}