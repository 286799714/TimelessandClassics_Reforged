package com.tac.guns.client.event;

import com.mojang.math.Quaternion;
import net.minecraftforge.eventbus.api.Event;

public class BeforeCameraSetupEvent extends Event {
    private Quaternion quaternion = Quaternion.ONE;
    private final float partialTicks;

    public BeforeCameraSetupEvent(float partialTicks){
        this.partialTicks = partialTicks;
    }

    public Quaternion getQuaternion() {
        return quaternion;
    }

    public void setQuaternion(Quaternion quaternion) {
        this.quaternion = quaternion;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
