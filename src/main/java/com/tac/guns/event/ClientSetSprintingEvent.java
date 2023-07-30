package com.tac.guns.event;

import net.minecraftforge.eventbus.api.Event;

/**
 * @author Arcomit
 * @updateDate 2023/7/27
 * @Note: Triggered when the player sets the sprint state (client)
 */
public class ClientSetSprintingEvent extends Event {
    private boolean sprinting;
    public ClientSetSprintingEvent(boolean sprinting){
        this.sprinting = sprinting;
    }

    public void setSprinting(boolean sprinting){
        this.sprinting = sprinting;
    }

    public boolean getSprinting(){
        return sprinting;
    }
}
