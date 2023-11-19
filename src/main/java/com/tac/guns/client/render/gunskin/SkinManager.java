package com.tac.guns.client.render.gunskin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class SkinManager {
    private static Map<ResourceLocation, Map<ResourceLocation, GunSkin>> skins = new HashMap<>();  //gunItemRegistryName -> (skinName -> GunSkin)
    private static Map<ResourceLocation, DefaultSkin> defaultSkins = new HashMap<>();        //gunItemRegistryName -> DefaultSkin

    static {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        ((ReloadableResourceManager) manager).registerReloadListener((ResourceManagerReloadListener) resourceManager -> SkinManager.cleanCache());
    }

    public static void reload() {
        skins = new HashMap<>();
        defaultSkins = new HashMap<>();
        init();
    }

    public static void cleanCache() {
        for (GunSkin skin : defaultSkins.values()) {
            skin.cleanCache();
        }
        for (Map<ResourceLocation, GunSkin> map : skins.values()) {
            for (GunSkin skin : map.values()) {
                skin.cleanCache();
            }
        }
    }

    private static void init() {
        //get skin configs from all namespace
        Set<String> nameSpaces = Minecraft.getInstance().getResourceManager().getNamespaces();
        for (String nameSpace : nameSpaces) {
            ResourceLocation loc = new ResourceLocation(nameSpace, "models/gunskin/skin.json");
            try {
                List<Resource> all = Minecraft.getInstance().getResourceManager().getResources(loc);
                for (Resource resource : all) {
                    loadSkinList(resource);
                }
            } catch (IOException e) {
                GunMod.LOGGER.warn("Failed to load skins from {} {}", loc, e);
            }
        }
        loadDefaultSkins();
    }

    private static void loadSkinList(Resource resource) throws IOException {
        JsonObject json;
        InputStream stream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        json = JsonParser.parseReader(reader).getAsJsonObject();
        String nameSpace = resource.getLocation().getNamespace();

        for (Map.Entry<String, JsonElement> e : json.entrySet()) {
            //gun
            String gun = e.getKey();
            SkinLoader loader = SkinLoader.getSkinLoader(gun);
            if (loader == null) continue;
            JsonObject skins = e.getValue().getAsJsonObject();

            for (Map.Entry<String, JsonElement> s : skins.entrySet()) {
                // skin
                try {
                    String skinName = s.getKey();
                    JsonObject skinObject = s.getValue().getAsJsonObject();

                    String skinType = skinObject.get("type").getAsString();
                    ResourceLocation skinLoc = ResourceLocation.tryParse(nameSpace + ":" + skinName);

                    if (skinLoc == null) {
                        GunMod.LOGGER.warn("Failed to load skins of {} named {}: invalid name.", gun, skinName);
                        continue;
                    } else if (!defaultSkins.containsKey(loader.getGunRegistryName())) {
                        GunMod.LOGGER.warn("Failed to load skins of {} named {}: default skin no loaded.", gun, skinName);
                        continue;
                    }

                    if ("custom".equals(skinType)) {
                        JsonObject modelObject = skinObject.get("models").getAsJsonObject();

                        Map<String, String> components = new HashMap<>();

                        for (Map.Entry<String, JsonElement> c : modelObject.entrySet()) {
                            components.put(c.getKey(), c.getValue().getAsString());
                        }

                        if (registerCustomSkin(loader, skinLoc, components)) {
                            GunMod.LOGGER.info("Loaded custom gun skin of {} named {}", gun, skinName);
                        }

                    } else if ("texture".equals(skinType)) {
                        JsonObject modelObject = skinObject.get("textures").getAsJsonObject();

                        List<Pair<String, ResourceLocation>> textures = new ArrayList<>();

                        for (Map.Entry<String, JsonElement> c : modelObject.entrySet()) {
                            ResourceLocation tl = ResourceLocation.tryParse(c.getValue().getAsString());
                            if (tl != null) {
                                textures.add(new Pair<>(c.getKey(), tl));
                            }
                        }

                        if (registerTextureOnlySkin(loader, skinLoc, textures)) {
                            GunMod.LOGGER.info("Loaded texture-only gun skin of {} named {}", gun, skinName);
                        }
                    }else if("common_texture".equals(skinType)){
                        JsonObject modelObject = skinObject.get("textures").getAsJsonObject();

                        List<Pair<String, ResourceLocation>> textures = new ArrayList<>();

                        for (Map.Entry<String, JsonElement> c : modelObject.entrySet()) {
                            ResourceLocation tl = ResourceLocation.tryParse(c.getValue().getAsString());
                            if (tl != null) {
                                textures.add(new Pair<>(c.getKey(), tl));
                            }
                        }
                        ResourceLocation icon = null;
                        if(skinObject.get("icon")!=null){
                            icon = ResourceLocation.tryParse(skinObject.get("icon").getAsString());
                        }
                        ResourceLocation miniIcon = null;
                        if(skinObject.get("mini_icon")!=null){
                            miniIcon = ResourceLocation.tryParse(skinObject.get("mini_icon").getAsString());
                        }

                        int cnt = registerCommonSkins(loader,textures,icon,miniIcon);
                        GunMod.LOGGER.info("Loaded common gun skins of {}, total: {}", gun, cnt);
                        continue;
                    }else {
                        GunMod.LOGGER.warn("Failed to load skins of {} named {}: unknown type.", gun, skinName);
                        continue;
                    }

                    if(skinObject.get("icon")!=null){
                        ResourceLocation rl = ResourceLocation.tryParse(skinObject.get("icon").getAsString());
                        GunSkin skin = getSkin(loader.getGunRegistryName(),skinLoc);
                        if(skin!=null && rl!=null){
                            loader.loadSkinIcon(skin,rl);
                        }
                    }

                    if(skinObject.get("mini_icon")!=null){
                        ResourceLocation rl = ResourceLocation.tryParse(skinObject.get("mini_icon").getAsString());
                        GunSkin skin = getSkin(loader.getGunRegistryName(),skinLoc);
                        if(skin!=null && rl!=null){
                            loader.loadSkinMiniIcon(skin,rl);
                        }
                    }

                } catch (Exception e2) {
                    GunMod.LOGGER.warn("Failed to load skins from {} {}.", resource.getLocation(), e2);
                }
            }
        }
        reader.close();
        stream.close();
    }
    public static void loadDefaultSkins() {
        SkinLoaders.init();
        for (SkinLoader loader : SkinLoader.skinLoaders.values()) {
            loadDefaultSkin(loader.getGunRegistryName(), loader.loadDefaultSkin());
        }
    }

    public static void loadDefaultSkin(ResourceLocation gunItemRegistryName, DefaultSkin skin){
        defaultSkins.put(gunItemRegistryName, skin);
    }

    /**
     * Try to load preset dyed skins for a gun.
     */
    private static int registerCommonSkins(SkinLoader loader, List<Pair<String, ResourceLocation>> textures,
                                           ResourceLocation icon, ResourceLocation mini_icon){
        String[] skinList = {
                "black", "blue", "brown", "dark_blue", "dark_green",
                "gray", "green", "jade", "light_gray", "magenta",
                "orange", "pink", "purple", "red", "sand", "white"
        };
        int cnt = 0;
        for(String color : skinList){
            ResourceLocation rl = new ResourceLocation("tac:"+color);
            List<Pair<String, ResourceLocation>> skinTextures =
                    textures.stream().map(
                            (p)-> new Pair<>(p.getFirst(),ResourceLocation.tryParse(p.getSecond()+"_"+color))
                    ).collect(Collectors.toList());
            if(registerTextureOnlySkin(loader,rl,skinTextures)){
                cnt++;
                GunSkin gunSkin = getSkin(loader.getGunRegistryName(),rl);
                if(gunSkin!=null){
                    if(icon!=null){
                        loader.loadSkinIcon(gunSkin,icon);
                    }
                    if(mini_icon!=null){
                        loader.loadSkinMiniIcon(gunSkin,mini_icon);
                    }
                }

            }
        }
        return cnt;
    }

    private static boolean registerCustomSkin(SkinLoader loader, ResourceLocation skinLocation, Map<String, String> models) {
        GunSkin skin = loader.loadCustomSkin(skinLocation, models);

        if (skin != null) {
            skins.putIfAbsent(loader.getGunRegistryName(), new HashMap<>());
            skins.get(loader.getGunRegistryName()).put(skinLocation, skin);
            return true;
        } else return false;
    }

    private static boolean registerTextureOnlySkin(SkinLoader loader, ResourceLocation skinLocation, List<Pair<String, ResourceLocation>> textures) {
        for(Pair<String, ResourceLocation> p : textures){
            ResourceLocation tl = ResourceLocation.tryParse(p.getSecond().getNamespace()+":textures/"+p.getSecond().getPath()+".png");
            if(tl == null || !Minecraft.getInstance().getResourceManager().hasResource(tl)) {
                return false;
            }
        }
        GunSkin skin = loader.loadTextureOnlySkin(skinLocation, textures);

        if (skin != null) {
            skins.putIfAbsent(loader.getGunRegistryName(), new HashMap<>());
            skins.get(loader.getGunRegistryName()).put(skinLocation, skin);
            return true;
        } else return false;
    }

    public static @Nullable GunSkin getSkin(ResourceLocation gunItemRegistryName, ResourceLocation skinLocation) {
        if (skinLocation != null && skins.containsKey(gunItemRegistryName)) return skins.get(gunItemRegistryName).get(skinLocation);
        else return null;
    }

    public static @Nonnull GunSkin getSkin(ItemStack stack) {
        ResourceLocation gun = stack.getItem().getRegistryName();
        if (stack.getTag() != null) {
            if (stack.getTag().contains("Skin", Tag.TAG_STRING)) {
                String skinLoc = stack.getTag().getString("Skin");
                ResourceLocation loc = ResourceLocation.tryParse(skinLoc);
                GunSkin skin = getSkin(gun, loc);
                return skin == null ? MissingSkin.INSTANCE : skin;
            }
        }
        GunSkin skin = getDefaultSkin(gun);
        return skin == null ? MissingSkin.INSTANCE : skin;
    }

    public static DefaultSkin getDefaultSkin(ResourceLocation gunItemRegistryName) {
        if(gunItemRegistryName==null)return null;
        return defaultSkins.get(gunItemRegistryName);
    }
}
