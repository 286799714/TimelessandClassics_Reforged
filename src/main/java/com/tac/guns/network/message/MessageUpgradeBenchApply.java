package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.common.network.ServerPlayHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class MessageUpgradeBenchApply extends PlayMessage<MessageUpgradeBenchApply>
{
    // Ew public
   public BlockPos pos;
   public String reqKey;
    public MessageUpgradeBenchApply() {}

    public MessageUpgradeBenchApply(BlockPos pos, String reqKey)
    {
        this.pos = pos;
        this.reqKey = reqKey;
    }

    @Override
    public void encode(MessageUpgradeBenchApply messageUpgradeBenchApply, FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(messageUpgradeBenchApply.pos);
        buffer.writeUtf(messageUpgradeBenchApply.reqKey);


    }

    @Override
    public MessageUpgradeBenchApply decode(FriendlyByteBuf buffer)
    {
        return new MessageUpgradeBenchApply(buffer.readBlockPos(), buffer.readUtf());
    }

    @Override
    public void handle(MessageUpgradeBenchApply messageUpgradeBenchApply, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleUpgradeBenchApply(messageUpgradeBenchApply, player);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
