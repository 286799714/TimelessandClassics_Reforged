package com.tac.guns.common;

import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import javafx.geometry.Side;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Dictionary;
import java.util.Hashtable;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class DiscardOffhand {
    public static int TICK_THRESHOLD = 10;
    public static Dictionary<LivingEntity, Integer> mapping = new Hashtable<>();

    @SubscribeEvent
    public static void onChange(LivingEquipmentChangeEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof PlayerEntity && !entity.getEntityWorld().isRemote)
        {
            if (event.getSlot() == EquipmentSlotType.MAINHAND)
            {
                //  Whenever the player changes his main hand item,
                //he enters a short safe-time where offhand item won't be discarded.
                mapping.put(entity, TICK_THRESHOLD);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        LivingEntity entity = event.player;
        if (event.phase == TickEvent.Phase.END && !entity.getEntityWorld().isRemote)
        {
            Integer safeTime = mapping.get(entity);
            if (safeTime != null && safeTime != 0)
            {
                //clamp it to prevent negative ticks somehow.
                mapping.put(entity, Math.max(mapping.get(entity)-1, 0));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        //probably unnecessary, just in case.
        mapping.put(event.getPlayer(), TICK_THRESHOLD);
    }

    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        //stop the server from maintaining uncessary lists.
        //probably needs to handled killed players too. A death-respawn group.
        LivingEntity entity = event.getPlayer();
        if (mapping.get(entity) != null)
        {
            mapping.remove(entity);
        }
    }

    public static boolean isSafeTime(PlayerEntity entity)
    {
        Integer safeTime = mapping.get(entity);
        if (safeTime == null)
        {
            return true;
        }
        return safeTime != 0;
    }
}
