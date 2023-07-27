package com.tac.guns.mixin.client;

import com.tac.guns.event.ClientSetSprintingEvent;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Arcomit
 * @updateDate 2023/7/27
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @ModifyVariable(at = @At("HEAD"),method = "setSprinting")
    public boolean setSprinting(boolean sprinting){
        ClientSetSprintingEvent event = new ClientSetSprintingEvent(sprinting);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);//当玩家疾跑状态发生改变时发送事件
        return event.getSprinting();
    }
}
