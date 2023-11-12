package com.tac.guns.item.attachment;

import com.tac.guns.item.attachment.impl.GunSkin;

public interface IGunSkin extends IAttachment<GunSkin> {
    @Override
    default Type getType() {
        return Type.GUN_SKIN;
    }
}