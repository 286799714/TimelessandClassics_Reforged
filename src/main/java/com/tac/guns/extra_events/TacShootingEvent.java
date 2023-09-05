package com.tac.guns.extra_events;


import com.tac.guns.Config;
import com.tac.guns.Reference;
import com.tac.guns.common.Gun;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.transition.TimelessGunItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Locale;

/**
 * Author: ClumsyAlien
 */


@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TacShootingEvent
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void preShoot(GunFireEvent.Pre event)
    {
        if(!(event.getStack().getItem() instanceof TimelessGunItem))
            return;
        HandleFireMode(event);
    }
    private static void HandleFireMode(GunFireEvent.Pre event)
    {
        ItemStack gunItem = event.getStack();
        int[] gunItemFireModes = gunItem.getTag().getIntArray("supportedFireModes");
        Gun gun = ((GunItem) gunItem.getItem()).getModifiedGun(gunItem); // Quick patch up, will create static method for handling null supported modes

        if(gunItem.getTag().get("CurrentFireMode") == null) // If user has not checked fire modes yet, default to first mode
        {
            if(ArrayUtils.isEmpty(gunItemFireModes) || gunItemFireModes == null)
            {
                gunItemFireModes = gun.getGeneral().getRateSelector();
                gunItem.getTag().putIntArray("supportedFireModes", gunItemFireModes);
            }
            gunItem.getTag().putInt("CurrentFireMode", gunItemFireModes[0]);
        }

        int currentFireMode = gunItem.getTag().getInt("CurrentFireMode");
        if(currentFireMode == 0)
        {
            if(!Config.COMMON.gameplay.safetyExistence.get())
            {
                gunItem.getTag().remove("CurrentFireMode");
                gunItem.getTag().putInt("CurrentFireMode", gunItemFireModes[currentFireMode+1]);
            }
            else // Safety clicks
            {
                event.getPlayer().displayClientMessage(new TranslatableComponent("info." + Reference.MOD_ID + ".gun_safety_lock", new KeybindComponent("key.tac.fireSelect").getString().toUpperCase(Locale.ENGLISH)).withStyle(ChatFormatting.GREEN) ,true);
                event.setCanceled(true);
            }

            ResourceLocation fireModeSound = gun.getSounds().getCock(); // Use cocking sound for now
            if(fireModeSound != null && event.getPlayer().isAlive())
            {
                event.getPlayer().playSound(new SoundEvent(fireModeSound), 1.0F, 1.0F);
            }
        }
    }
}