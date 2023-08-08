package com.tac.guns.item.attachment;

import com.tac.guns.item.attachment.impl.GunSkin;

public interface IgunSkin extends IAttachment<GunSkin> {
    @Override
    default Type getType() {
        return Type.GUN_SKIN;
    }
}
