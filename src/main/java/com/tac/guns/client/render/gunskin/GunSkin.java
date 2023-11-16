package com.tac.guns.client.render.gunskin;

import com.tac.guns.client.render.model.CachedModel;

import com.tac.guns.client.render.model.GunComponent;
import net.minecraft.resources.ResourceLocation;


import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class GunSkin {
    protected final Map<GunComponent, CachedModel> models = new HashMap<>();
    public final ResourceLocation skinName;
    public final ResourceLocation gunItemRegistryName;
    protected ResourceLocation icon;
    protected ResourceLocation miniIcon;
    private DefaultSkin defaultSkin;

    public GunSkin(ResourceLocation skinName, ResourceLocation gunItemRegistryName, DefaultSkin skin){
        this.skinName = skinName;
        this.gunItemRegistryName = gunItemRegistryName;
        this.defaultSkin=skin;
    }

    protected GunSkin(ResourceLocation skinName, ResourceLocation gunItemRegistryName){
        this.skinName = skinName;
        this.gunItemRegistryName = gunItemRegistryName;
    }

    public void setDefaultSkin(DefaultSkin defaultSkin) {
        this.defaultSkin = defaultSkin;
    }

    @Nullable
    public CachedModel getModel(GunComponent component){
        return models.getOrDefault(component,defaultSkin.getModel(component));
    }
    protected void addComponent(GunComponent component, CachedModel model){
        this.models.put(component, model);
    }

    public Map<GunComponent, CachedModel> getModels(){
        return this.models;
    }

    @Nullable
    public ResourceLocation getIcon() {
        if(icon!=null)return icon;
        else return defaultSkin.getIcon();
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }
    @Nullable
    public ResourceLocation getMiniIcon() {
        if(miniIcon!=null)return miniIcon;
        else return defaultSkin.getMiniIcon();
    }
    public void setMiniIcon(ResourceLocation miniIcon) {
        this.miniIcon = miniIcon;
    }

    public void cleanCache(){
        for(CachedModel model : models.values()){
            model.cleanCache();
        }
    }
}
