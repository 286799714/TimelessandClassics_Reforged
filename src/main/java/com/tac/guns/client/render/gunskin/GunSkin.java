package com.tac.guns.client.render.gunskin;

import com.tac.guns.client.render.model.CacheableModel;

import com.tac.guns.client.render.model.GunComponent;
import net.minecraft.resources.ResourceLocation;


import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class GunSkin {
    protected final Map<GunComponent, CacheableModel> models = new HashMap<>();
    public final ResourceLocation skinName;
    public final ResourceLocation gunItemRegistryName;
    protected ResourceLocation icon;
    protected ResourceLocation miniIcon;

    public GunSkin(ResourceLocation skinName, ResourceLocation gunItemRegistryName){
        this.skinName = skinName;
        this.gunItemRegistryName = gunItemRegistryName;
    }

    @Nullable
    public CacheableModel getModel(GunComponent component){
        return models.get(component);
    }
    protected void addComponent(GunComponent component, CacheableModel model){
        this.models.put(component, model);
    }

    public Map<GunComponent, CacheableModel> getModels(){
        return this.models;
    }

    @Nullable
    public ResourceLocation getIcon() {
        return icon;
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }
    @Nullable
    public ResourceLocation getMiniIcon() {
        return miniIcon;
    }
    public void setMiniIcon(ResourceLocation miniIcon) {
        this.miniIcon = miniIcon;
    }

    public boolean isDefaultSkin(){
        return skinName == null;
    }

    public boolean isMissingSkin(){
        return gunItemRegistryName == null;
    }

    public void cleanCache(){
        for(CacheableModel model : models.values()){
            model.cleanCache();
        }
    }
}