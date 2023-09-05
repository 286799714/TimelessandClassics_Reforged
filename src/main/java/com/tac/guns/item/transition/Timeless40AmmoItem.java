package com.tac.guns.item.transition;

import com.tac.guns.GunMod;
import com.tac.guns.item.AmmoItem;
import com.tac.guns.util.Process;
import net.minecraft.world.item.Item;

public class Timeless40AmmoItem extends AmmoItem {
    public Timeless40AmmoItem() {
        this(properties -> properties);
    }

    public Timeless40AmmoItem(Process<Properties> properties) {
        super(properties.process(new Item.Properties().stacksTo(6).tab(GunMod.AMMO)));
    }
}