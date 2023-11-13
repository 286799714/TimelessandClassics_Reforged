package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.init.ModSyncedDataKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageAimingState extends PlayMessage<MessageAimingState> {
    private float dist = 0;

    public MessageAimingState() {}

    public MessageAimingState(float dist)
    {
        this.dist = dist;
    }

    @Override
    public void encode(MessageAimingState message, FriendlyByteBuf buffer)
    {
        buffer.writeFloat(message.dist);
    }

    @Override
    public MessageAimingState decode(FriendlyByteBuf buffer)
    {
        return new MessageAimingState(buffer.readFloat());
    }

    @Override
    public void handle(MessageAimingState messageAimingState, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null)
            {
                ModSyncedDataKeys.AIMING_STATE.setValue(player, messageAimingState.dist);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}