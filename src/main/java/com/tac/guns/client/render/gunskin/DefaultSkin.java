package com.tac.guns.client.render.gunskin;

import com.tac.guns.client.SpecialModel;
import com.tac.guns.client.render.model.GunComponent;
import net.minecraft.resources.ResourceLocation;


public class DefaultSkin extends GunSkin{
    public DefaultSkin(String gun) {
        super("default",gun,null);
    }

    public DefaultSkin(ResourceLocation gun) {
        super(new ResourceLocation(gun.getNamespace()+":default"),gun,null);
    }

    @Override
    public SpecialModel getModel(GunComponent component){
        return models.get(component);
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon;
    }
}
