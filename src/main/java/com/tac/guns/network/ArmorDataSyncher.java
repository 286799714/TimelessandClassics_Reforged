package com.tac.guns.network;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArmorDataSyncher {
    private static ArmorDataSyncher instance;

    private static final EntityDataAccessor<ItemStack> ARMOR_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.ITEM_STACK);
    protected ArmorDataSyncher(){

    }

    public static ArmorDataSyncher getInstance(){
        if(instance == null) return instance = new ArmorDataSyncher();
        else return instance;
    }
}
