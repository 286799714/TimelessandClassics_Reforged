package com.tac.guns.client.gunskin;

import com.tac.guns.client.SpecialModels;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ResourceReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {
        SpecialModels.cleanCache();
        SkinManager.cleanCache();
    }
}
