package com.tac.guns.client.resource.gunskin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.resource.model.CacheableModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
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
    public static UnbakedModel missingModel;

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

            String skinType = json.get("type").getAsString();
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

            if ("custom".equals(skinType)) {
                //traverse and load all components
                String mainPath = namespace + ":" + path.substring(0, path.length() - extension.length());
                JsonObject componentsJson = json.getAsJsonObject("components");
                for (Map.Entry<String, JsonElement> entry : componentsJson.entrySet()) {
                    //parse gun component
                    String[] spiltComponentName = entry.getKey().split(":");
                    String componentNamespace = spiltComponentName.length < 2 ? null : spiltComponentName[0];
                    String componentKey = spiltComponentName.length < 2 ? spiltComponentName[0] : spiltComponentName[1];
                    GunComponent component = new GunComponent(componentNamespace, componentKey);

                    //mapping components to groups
                    String group = entry.getValue().getAsString();
                    gunSkin.mapComponentGroup(component, group);

                    //try to load the component model from file
                    ResourceLocation modelRL = component.getModelLocation(mainPath);
                    if (modelRL != null) {
                        ForgeModelBakery.addSpecialModel(modelRL);
                        CacheableModel componentModel = new CacheableModel(modelRL);
                        gunSkin.putComponentModel(component, componentModel);
                    } else {
                        GunMod.LOGGER.info("        Missing component model {} while loading {}", componentKey, namespace + ":" + skinId);
                        gunSkin.putComponentModel(component, CacheableModel.MISSING_MODEL);
                    }
                }
            }
            GunMod.LOGGER.info("    loaded skin {}", namespace + ":" +skinId);
            return gunSkin;
        } catch (Exception e) {
            GunMod.LOGGER.warn("    Failed to load skin {}\n{}", metaLocation, e);
        }

        return null;
    }

    public static void loadModelsFromProfile(){
        //remove all skins from cache because they need to reload.
        GunSkinManager.cleanCache();

        //get all declared gun skins in gunskins.json.
        Minecraft.getInstance().getResourceManager().getNamespaces();
        Set<String> nameSpaces = Minecraft.getInstance().getResourceManager().getNamespaces();
        for (String nameSpace : nameSpaces) {
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
                            GunSkinManager.registerGunSkin(skin);
                        }
                    }
                }
            } catch (IOException ignore) {}
        }
    }

    public static class TextureModel {
        public static final ResourceLocation atlasLocation = new ResourceLocation("minecraft:textures/atlas/blocks.png");
        private final BlockModel unbaked;

        private TextureModel(BlockModel model) {
            this.unbaked = model;
        }

        public static GunSkinLoader.TextureModel tryCreateCopy(ResourceLocation parentLocation) {
            GunSkinLoader.TextureModel textureModel = null;
            if (ForgeModelBakery.instance() != null) {
                BlockModel parent = (BlockModel) ForgeModelBakery.instance().getModel(parentLocation);

                if (parent == missingModel) return null;

                List<BlockElement> list = Lists.newArrayList();
                Map<String, Either<Material, String>> map = Maps.newHashMap();

                BlockModel model = new BlockModel(parentLocation, list, map,
                        true, null, parent.getTransforms(), parent.getOverrides());

                textureModel = new GunSkinLoader.TextureModel(model);
            }
            return textureModel;
        }

        public void applyTextures(List<Pair<String, ResourceLocation>> textures) {
            textures.forEach((p) -> applyTexture(p.getFirst(), p.getSecond()));
        }

        public void applyTexture(String key, ResourceLocation textureLocation) {
            this.unbaked.textureMap.put(key, Either.left(new Material(atlasLocation, textureLocation)));
        }

        public BlockModel getModel() {
            return unbaked;
        }
    }
}
