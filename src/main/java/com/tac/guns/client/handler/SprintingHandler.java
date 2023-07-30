package com.tac.guns.client.handler;

import com.tac.guns.Reference;
import com.tac.guns.client.handler.AimingHandler;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.event.ClientSetSprintingEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Arcomit
 * @updateDate 2023/7/27
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID,value = Dist.CLIENT)
public class SprintingHandler {
    @SubscribeEvent
    public static void playerSetSprinting(ClientSetSprintingEvent event) {
        if (event.getSprinting()){
            if ((AimingHandler.get().isAiming()) || ShootingHandler.get().isShooting()){
                event.setSprinting(false);
                //Prevent the player from entering a Sprinting state (is prevent, not set Sprinting(false) after set Sprinting(true))
                //Clicking Sprinting to enter the sprint will only trigger set Sprinting(true) once, so we can cancel it with set Sprinting(false), but long pressing the sprint key will always trigger set Sprinting(true).
                //The set Sprinting() method will give the player acceleration if it is True, and the time difference between set Sprinting(true) and set Sprinting(false) will prevent the player from slowing down.
            }
        }
    }
}
