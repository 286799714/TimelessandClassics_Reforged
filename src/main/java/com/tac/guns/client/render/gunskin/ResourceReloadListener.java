package com.tac.guns.client.render.gunskin;

import com.tac.guns.client.render.model.MyCachedModels;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ResourceReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {
        MyCachedModels.cleanCache();
        SkinManager.cleanCache();
    }
}
