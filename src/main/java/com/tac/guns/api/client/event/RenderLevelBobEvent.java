package com.tac.guns.api.client.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class RenderLevelBobEvent extends Event {
    @Cancelable
    public static class BobHurt extends RenderLevelBobEvent{ }
    @Cancelable
    public static class BobView extends RenderLevelBobEvent{ }
}
