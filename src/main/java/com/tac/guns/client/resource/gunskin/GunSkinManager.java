package com.tac.guns.client.resource.gunskin;

import com.tac.guns.Reference;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class GunSkinManager {
    private static Map<ResourceLocation, GunSkin> skins = new HashMap<>();                            //skinName -> GunSkin
    private final static Map<ResourceLocation, ResourceLocation> defaultSkins = new HashMap<>();      //gunItemRegistryName -> skinName

    public static void registerGunSkin(GunSkin skin){
        skins.put(skin.skinName, skin);
    }

    public static void registerDefaultGunSkin(ResourceLocation gunItemRegistryName, ResourceLocation skinName){
        defaultSkins.put(gunItemRegistryName, skinName);
    }

    public static @Nullable GunSkin getGunSkin(ResourceLocation skinName){
        return skins.get(skinName);
    }

    public static @Nullable GunSkin getDefaultGunSkin(ResourceLocation gunItemRegistryName){
        ResourceLocation skinName = defaultSkins.get(gunItemRegistryName);
        if(skinName == null) return null;
        return skins.get(skinName);
    }

    public static void removeGunSkin(ResourceLocation skinName){
        skins.remove(skinName);
    }

    public static void removeDefaultGunSkin(ResourceLocation gunItemRegistryName){
        defaultSkins.remove(gunItemRegistryName);
    }

    public static void cleanCache(){
        for(GunSkin gunSkin : skins.values()){
            gunSkin.cleanCache();
        }
        skins = new HashMap<>();
    }

    public static @Nonnull GunSkin getSkinFromTag(ItemStack stack) {
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
}
