package com.tac.guns.item.TransitionalTypes;

import com.tac.guns.GunMod;
import com.tac.guns.item.AmmoItem;
import com.tac.guns.util.Process;


public class TimelessRFAmmoItem extends AmmoItem {
	public TimelessRFAmmoItem() {
		this(properties -> properties);
	}

	public TimelessRFAmmoItem(Process<Properties> properties) {
		super(properties.process(new Properties().maxStackSize(30).group(GunMod.AMMO)));
	}
}
