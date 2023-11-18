package com.tac.guns.client.render.gunskin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tac.guns.GunMod;
import com.tac.guns.client.render.model.CacheableModel;
import com.tac.guns.client.render.model.GunComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.model.ForgeModelBakery;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;

public class GunSkinLoader {
    public static UnbakedModel missingModel;                             //Reference to unbaked missing model
    public static Map<ResourceLocation, UnbakedModel> unbakedModels;     //Reference to unbakedModels in ModelBakery,
                                                                         //which is used to cache and bake texture changed component model
    public static Map<ResourceLocation, UnbakedModel> topUnbakedModels;  //Reference to topUnbakedModels in ModelBakery,
                                                                         //which is used to cache and bake texture changed component model

    private static final String extension = ".meta.json";

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
     *                         Filename must end with ".meta.json".
     *                         For example, "ak47_skin1.meta.json".
     *                         The file name before ".meta.json" will be used as the skin's id.
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
                    String mainPath = namespace + ":" + path.substring(0, path.length() - extension.length());

                    JsonObject componentsJson = json.getAsJsonObject("components");
                    for (Map.Entry<String, JsonElement> entry : componentsJson.entrySet()) {
                        String componentKey = entry.getKey();
                        String group = entry.getValue().getAsString();
                        GunComponent component = getComponent(componentKey);

                        //try to load the component model
                        ResourceLocation modelRL = component.getModelLocation(mainPath);
                        if (modelRL != null && Minecraft.getInstance().getResourceManager().hasResource(modelRL)) {
                            CacheableModel componentModel = new CacheableModel(modelRL);
                            ForgeModelBakery.addSpecialModel(modelRL);
                            gunSkin.putComponentModel(component, componentModel);
                        }

                        gunSkin.mapComponentGroup(component, group);
                    }
                }
                case TEXTURE_ONLY -> {
                    JsonObject componentsJson = json.getAsJsonObject("textures");
                    for (Map.Entry<String, JsonElement> entry : componentsJson.entrySet()) {

                    }
                }
            }
            return gunSkin;

        } catch (Exception e) {
            GunMod.LOGGER.warn("Failed to load skins from {}\n{}", metaLocation, e);
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

    public enum SkinType {
        CUSTOM,
        TEXTURE_ONLY;
    }
}
