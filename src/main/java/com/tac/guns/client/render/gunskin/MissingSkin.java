package com.tac.guns.client.render.gunskin;

import com.tac.guns.client.render.model.CacheableModel;
import com.tac.guns.client.render.model.GunComponent;
import net.minecraft.resources.ResourceLocation;

public class MissingSkin extends GunSkin{
    public static final MissingSkin INSTANCE = new MissingSkin();

    private MissingSkin() {
        super(null, null);
    }

    @Override
    public CacheableModel getModel(GunComponent component){
        return CacheableModel.MISSING_MODEL;
    }

    @Override
    public ResourceLocation getIcon() {
        return null;
    }
}
