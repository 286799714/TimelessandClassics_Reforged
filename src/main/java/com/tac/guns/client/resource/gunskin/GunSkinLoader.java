package com.tac.guns.client.resource.gunskin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.resource.model.CacheableModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class GunSkinLoader {
    protected String extension = ".meta.json";
    protected HashSet<String> componentsNamespaces = new HashSet<>();

    public void usingComponentsNamespace(String namespace){
        componentsNamespaces.add(namespace);
    }

    public void disableComponentsNamespace(String namespace){
        componentsNamespaces.remove(namespace);
    }
    /**
     * When loading custom skin,
     * The loader will load the model file named "{skin_id}_{components}.json" in the same directory based on the components specified in meta json.
     * When loading texture only skin,
     * The loader will simply load the textures specified in meta json.
     *
     * @param metaLocation the ResourceLocation of gun skin meta json file.
     *                     Filename must end with ".meta.json".
     *                     For example, "ak47_skin1.meta.json".
     *                     The file name before ".meta.json" will be used as the skin's id.
     */
    public GunSkin loadGunSkin(ResourceLocation metaLocation) {
        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(metaLocation);

            String path = metaLocation.getPath();
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            if(!fileName.endsWith(extension)) return null;

            String namespace = metaLocation.getNamespace();
            String skinId = fileName.substring(0, fileName.length() - extension.length());

            InputStream stream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            SkinType skinType =
                    SkinType.valueOf(
                            json.get("type").getAsString().toUpperCase()
                    );
            ResourceLocation gunRegistryName =
                    ResourceLocation.tryParse(
                            json.get("gun_registry_name").getAsString()
                    );
            ResourceLocation icon =
                    ResourceLocation.tryParse(
                            json.get("icon").getAsString()
                    );
            ResourceLocation miniIcon =
                    ResourceLocation.tryParse(
                            json.get("mini_icon").getAsString()
                    );
            GunSkin gunSkin = new GunSkin(new ResourceLocation(namespace, skinId), gunRegistryName);
            gunSkin.setIcon(icon);
            gunSkin.setMiniIcon(miniIcon);

            switch (skinType) {
                case CUSTOM -> {
                    //using component namespace
                    JsonArray namespaces = json.getAsJsonArray("component_namespace");
                    if(namespaces != null) namespaces.forEach((element) -> usingComponentsNamespace(element.getAsString()));

                    //traverse and load all components
                    String mainPath = namespace + ":" + path.substring(0, path.length() - extension.length());
                    JsonObject componentsJson = json.getAsJsonObject("components");
                    for (Map.Entry<String, JsonElement> entry : componentsJson.entrySet()) {
                        String componentKey = entry.getKey();
                        String group = entry.getValue().getAsString();
                        GunComponent component = getComponent(componentKey);

                        //try to load the component model from file
                        ResourceLocation modelRL = component.getModelLocation(mainPath);
                        if (modelRL != null) {
                            ForgeModelBakery.addSpecialModel(modelRL);
                            CacheableModel componentModel = new CacheableModel(modelRL);
                            gunSkin.putComponentModel(component, componentModel);
                        }else {
                            GunMod.LOGGER.info("    Missing component model {} while loading {}", componentKey, skinId);
                            gunSkin.putComponentModel(component, CacheableModel.MISSING_MODEL);
                        }
                        gunSkin.mapComponentGroup(component, group);
                    }
                }
                case TEXTURE_ONLY -> {
                    //todo
                }
            }
            return gunSkin;

        } catch (Exception e) {
            GunMod.LOGGER.warn("Failed to load skin {}\n{}", metaLocation, e);
        }

        return null;
    }

    public @Nonnull GunComponent getComponent(String componentKey){
        for(String namespace : componentsNamespaces){
            GunComponent component = GunComponent.getComponent(namespace, componentKey);
            if(component != null) return component;
        }
        //Registered component not found, return a no registration required default GunComponent.
        return new GunComponent(null, componentKey);
    }

    /**
     * This method can only be called during ModelBakery execution processLoading
     * */
    public static void load(){
        //remove all skins from cache because they need to reload.
        GunSkinManager.cleanCache();

        //get all declared gun skins in gunskins.json.
        Minecraft.getInstance().getResourceManager().getNamespaces();
        Set<String> nameSpaces = Minecraft.getInstance().getResourceManager().getNamespaces();
        for (String nameSpace : nameSpaces) {
            LogUtils.getLogger().info(nameSpace);
            ResourceLocation location = new ResourceLocation(nameSpace, "gunskins.json");
            try {
                List<Resource> resources = Minecraft.getInstance().getResourceManager().getResources(location);
                GunMod.LOGGER.info("loading skins from {}", location);
                for (Resource resource : resources) {
                    InputStream stream = resource.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    for (Map.Entry<String, JsonElement> elementEntry : json.entrySet()) {
                        ResourceLocation gunSkinLocation = new ResourceLocation(nameSpace, elementEntry.getKey());
                        //try to load and register skin
                        GunSkinLoader loader = new GunSkinLoader();
                        GunSkin skin = loader.loadGunSkin(gunSkinLocation);
                        if(skin != null) {
                            GunMod.LOGGER.info("    successfully loaded skin {}", skin.skinName);
                            GunSkinManager.registerGunSkin(skin);
                        }
                    }
                }
            } catch (IOException ignore) {}
        }
    }

    public enum SkinType {
        CUSTOM,
        TEXTURE_ONLY;
    }
}
