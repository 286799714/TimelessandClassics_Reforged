package com.tac.guns.event;

import net.minecraftforge.eventbus.api.Event;

/**
 * @author Arcomit
 * @updateDate 2023/7/27
 * @说明：当玩家设置疾跑状态时触发(客户端)
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
