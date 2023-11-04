package com.tac.guns.client.gunskin;

import com.mojang.math.Vector3d;
import com.tac.guns.client.SpecialModel;

import net.minecraft.resources.ResourceLocation;


import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GunSkin {
    protected final Map<ModelComponent, SpecialModel> models = new HashMap<>();
    protected Map<ModelComponent, Vector3d> extraOffset;
    public final ResourceLocation registerName;
    public final ResourceLocation gun;
    protected ResourceLocation icon;
    protected ResourceLocation miniIcon;
    private DefaultSkin defaultSkin;

    public GunSkin(String skinName, String gun, DefaultSkin skin){
        this.registerName = ResourceLocation.tryParse("tac:"+skinName);
        this.gun = ResourceLocation.tryParse("tac:" + gun);
        this.defaultSkin=skin;
    }

    public GunSkin(ResourceLocation registerName, String gun, DefaultSkin skin){
        this.gun = ResourceLocation.tryParse("tac:" + gun);
        this.registerName = registerName;
        this.defaultSkin=skin;
    }

    public GunSkin(ResourceLocation skinName, ResourceLocation gun, DefaultSkin skin){
        this.registerName = skinName;
        this.gun = gun;
        this.defaultSkin=skin;
    }

    protected GunSkin(ResourceLocation registerName, ResourceLocation gun){
        this.gun = gun;
        this.registerName = registerName;
    }

    public void setDefaultSkin(DefaultSkin defaultSkin) {
        this.defaultSkin = defaultSkin;
    }

    @Nullable
    public SpecialModel getModel(ModelComponent component){
        return models.getOrDefault(component,defaultSkin.getModel(component));
    }
    protected void addComponent(ModelComponent component,SpecialModel model){
        this.models.put(component, model);
    }

    public Map<ModelComponent,SpecialModel> getModels(){
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
        for(SpecialModel model : models.values()){
            model.cleanCache();
        }
    }
}
