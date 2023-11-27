package com.tac.guns.client.resource.gunskin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tac.guns.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class GunSkinManager{
    private static GunSkinManager instance;
    private final Map<ResourceLocation, GunSkin> skins = new HashMap<>();                            //skinName -> GunSkin
    private final Map<ResourceLocation, ResourceLocation> defaultSkins = new HashMap<>();            //gunItemRegistryName -> skinName

    public static final String skinIndex = "gunskins.json";
    private GunSkinManager(){}

    public static GunSkinManager getInstance(){
        if (instance == null) {
            instance = new GunSkinManager();
        }
        return instance;
    }

    protected @NotNull List<GunSkinResource> getGunSkinResources() {
        List<GunSkinResource> list = new ArrayList<>();
        Set<String> nameSpaces = Minecraft.getInstance().getResourceManager().getNamespaces();
        for (String nameSpace : nameSpaces) {
            ResourceLocation location = new ResourceLocation(nameSpace, GunSkinManager.skinIndex);
            try {
                List<Resource> resources = Minecraft.getInstance().getResourceManager().getResources(location);
                for (Resource resource : resources) {
                    InputStream stream = resource.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    for (Map.Entry<String, JsonElement> elementEntry : json.entrySet()) {
                        JsonObject valueJson = elementEntry.getValue().getAsJsonObject();
                        String loaderName = valueJson.get("loader").getAsString();
                        if("vanilla".equals(loaderName)) continue;

                        ResourceLocation metaLocation = new ResourceLocation(nameSpace, elementEntry.getKey());

                        GunSkinResource preparing = new GunSkinResource();
                        preparing.loaderName = loaderName;
                        preparing.metaLocation = metaLocation;
                        list.add(preparing);
                    }
                }
            } catch (IOException ignore) {}
        }
        return list;
    }

    public void registerGunSkin(GunSkin skin){
        skins.put(skin.skinName, skin);
    }

    public void registerDefaultGunSkin(ResourceLocation gunItemRegistryName, ResourceLocation skinName){
        defaultSkins.put(gunItemRegistryName, skinName);
    }

    public @Nullable GunSkin getGunSkin(ResourceLocation skinName){
        return skins.get(skinName);
    }

    public @Nullable GunSkin getDefaultGunSkin(ResourceLocation gunItemRegistryName){
        ResourceLocation skinName = defaultSkins.get(gunItemRegistryName);
        if(skinName == null) return null;
        return skins.get(skinName);
    }

    public void removeGunSkin(ResourceLocation skinName){
        skins.remove(skinName);
    }

    public void removeDefaultGunSkin(ResourceLocation gunItemRegistryName){
        defaultSkins.remove(gunItemRegistryName);
    }

    public void clearGunSkin(){
        skins.clear();
    }

    public void cleanCache(){
        for(GunSkin gunSkin : skins.values()){
            gunSkin.cleanCache();
        }
    }

    public @Nonnull GunSkin getSkinFromTag(ItemStack stack) {
        ResourceLocation gun = stack.getItem().getRegistryName();
        if (stack.getTag() != null) {
            if (stack.getTag().contains("Skin", Tag.TAG_STRING)) {
                String skinLoc = stack.getTag().getString("Skin");
                ResourceLocation loc = ResourceLocation.tryParse(skinLoc);
                GunSkin skin = getGunSkin(loc);
                if(skin == null)
                    skin = getDefaultGunSkin(gun);
                return skin == null ? GunSkin.MISSING_SKIN : skin;
            }
        }
        GunSkin skin = getDefaultGunSkin(gun);
        return skin == null ? GunSkin.MISSING_SKIN : skin;
    }

    public static class GunSkinResource {
        public ResourceLocation metaLocation;
        public String loaderName;
    }
}
