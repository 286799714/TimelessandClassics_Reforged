package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.duck.PlayerWithSynData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageArmorUpdate extends PlayMessage<MessageArmorUpdate> {
    @Override
    public void encode(MessageArmorUpdate messageArmorUpdate, FriendlyByteBuf friendlyByteBuf) { }

    @Override
    public MessageArmorUpdate decode(FriendlyByteBuf friendlyByteBuf) {return new MessageArmorUpdate();}

    @Override
    public void handle(MessageArmorUpdate messageArmorUpdate, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if(player != null && !player.isSpectator())
            {
                ((PlayerWithSynData) player).updateRig();
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
