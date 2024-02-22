package com.tac.guns.compat.kubejs.custom;

import com.tac.guns.item.transition.TimelessGunItem;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class GunItemBuilder extends ItemBuilder {
    public GunItemBuilder(ResourceLocation i) {
        super(i);
        this.unstackable();
    }

    @Override
    public Item createObject() {
        return new TimelessGunItem(properties -> properties.tab(this.group));
    }
}
