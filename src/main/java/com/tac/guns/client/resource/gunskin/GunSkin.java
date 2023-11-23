package com.tac.guns.client.resource.gunskin;

import com.tac.guns.client.resource.model.CacheableModel;
import com.tac.guns.client.resource.model.VanillaBakedModel;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class GunSkin {
    protected Map<GunComponent, CacheableModel> models = new HashMap<>();
    protected Map<GunComponent, String> componentGroupMap = new HashMap<>();
    public final ResourceLocation skinName;
    public final ResourceLocation gunItemRegistryName;
    public ResourceLocation icon;
    public ResourceLocation miniIcon;

    public static final GunSkin MISSING_SKIN = new GunSkin(null, null){
        @Override
        public CacheableModel getModel(GunComponent component){
            return VanillaBakedModel.MISSING_MODEL;
        }
    };

    public GunSkin(ResourceLocation skinName, ResourceLocation gunItemRegistryName){
        this.skinName = skinName;
        this.gunItemRegistryName = gunItemRegistryName;
    }

    public @Nullable CacheableModel getModel(GunComponent component){
        return models.get(component);
    }

    public void setComponentModel(GunComponent component, @Nullable CacheableModel model){
        this.models.put(component, model);
    }

    public @Nullable String getGroup(GunComponent component){
        return componentGroupMap.get(component);
    }

    public void setComponentGroup(GunComponent component, @Nullable String group){
        this.componentGroupMap.put(component, group);
    }

    public void cleanCache(){
        for(CacheableModel model : models.values()){
            model.cleanCache();
        }
    }
}
