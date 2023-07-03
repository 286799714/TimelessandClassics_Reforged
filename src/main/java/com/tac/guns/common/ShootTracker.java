package com.tac.guns.common;

import com.google.common.collect.Maps;
import com.tac.guns.Reference;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ShootTracker
{
    private boolean isShooting = false;
    private boolean isTicked = false;
    private static final Map<Player, ShootTracker> SHOOT_TRACKER_MAP = new WeakHashMap<>();

    public static ShootTracker getShootTracker(Player player)
    {
        return SHOOT_TRACKER_MAP.computeIfAbsent(player, player1 -> new ShootTracker());
    }

    @SubscribeEvent
    public static void onGunFire(GunFireEvent.Post event){
        if(event.isClient()) return;
        ShootTracker tracker = getShootTracker(event.getPlayer());
        tracker.isShooting = true;
        tracker.isTicked = false;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        SHOOT_TRACKER_MAP.values().forEach(tracker -> {
            if(tracker.isTicked) tracker.isShooting = false;
            else tracker.isTicked = true;
        });
    }

    public boolean isShooting() {
        return isShooting;
    }
}
