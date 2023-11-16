package com.tac.guns.client.render.gunskin;

import com.tac.guns.client.render.model.CachedModel;
import com.tac.guns.client.render.model.GunComponent;
import net.minecraft.resources.ResourceLocation;


public class DefaultSkin extends GunSkin{
    public DefaultSkin(ResourceLocation gun) {
        super(new ResourceLocation(gun.getNamespace()+":default"),gun,null);
    }

    @Override
    public CachedModel getModel(GunComponent component){
        return models.get(component);
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon;
    }
}
