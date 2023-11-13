package com.tac.guns.item.attachment.impl;

import com.tac.guns.interfaces.IGunModifier;
import net.minecraft.resources.ResourceLocation;

public class GunSkin extends Attachment {
    public ResourceLocation getSkin() {
        return skin;
    }

    private ResourceLocation skin;

    private GunSkin(String skin, IGunModifier...  modifier) {
        super(modifier);
        this.skin = ResourceLocation.tryParse("tac:"+skin);
    }

    private GunSkin(ResourceLocation resourceLocation, IGunModifier...  modifier) {
        super(modifier);
        this.skin = resourceLocation;
    }

    public static GunSkin create(ResourceLocation resourceLocation, IGunModifier...  modifier) {
        return new GunSkin(resourceLocation, modifier);
    }

    public static GunSkin create(String skin, IGunModifier...  modifier) {
        return new GunSkin(skin, modifier);
    }
}
