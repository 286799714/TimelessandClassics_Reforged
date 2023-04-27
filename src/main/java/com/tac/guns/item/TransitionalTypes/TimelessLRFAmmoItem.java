package com.tac.guns.item.TransitionalTypes;

import com.tac.guns.GunMod;
import com.tac.guns.item.AmmoItem;
import com.tac.guns.util.Process;


public class TimelessLRFAmmoItem extends AmmoItem {
	public TimelessLRFAmmoItem() {
		this(properties -> properties);
	}

	public TimelessLRFAmmoItem(Process<Properties> properties) {
		super(properties.process(new Properties().maxStackSize(36).group(GunMod.AMMO)));
	}
}
