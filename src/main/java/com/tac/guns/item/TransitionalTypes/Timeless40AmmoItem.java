package com.tac.guns.item.TransitionalTypes;

import com.tac.guns.GunMod;
import com.tac.guns.item.AmmoItem;
import com.tac.guns.util.Process;
import net.minecraft.item.Item;

public class Timeless40AmmoItem extends AmmoItem {
    public Timeless40AmmoItem() {
        this(properties -> properties);
    }

    public Timeless40AmmoItem(Process<Properties> properties) {
        super(properties.process(new Item.Properties().maxStackSize(6).group(GunMod.AMMO)));
    }
}