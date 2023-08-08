package com.tac.guns.item.attachment.impl;

import com.tac.guns.interfaces.IGunModifier;

public class GunSkin extends Attachment {
    private GunSkin(IGunModifier... modifier) {
        super(modifier);
    }

    public static GunSkin create(IGunModifier... modifier) {
        return new GunSkin(modifier);
    }
}
