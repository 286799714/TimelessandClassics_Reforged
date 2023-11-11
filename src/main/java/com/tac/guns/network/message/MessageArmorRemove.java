package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.duck.PlayerWithSynData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageArmorRemove extends PlayMessage<MessageArmorRemove> {
    @Override
    public void encode(MessageArmorRemove messageArmorRemove, FriendlyByteBuf friendlyByteBuf) {}

    @Override
    public MessageArmorRemove decode(FriendlyByteBuf friendlyByteBuf) {return new MessageArmorRemove();}

    @Override
    public void handle(MessageArmorRemove messageArmorRemove, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null && !player.isSpectator())
            {
                ItemStack rig = ((PlayerWithSynData) player).getRig();
                if(!rig.isEmpty()){
                    if(player.getInventory().getFreeSlot() == -1){
                        player.drop(rig, true);
                    }
                    else
                        player.addItem(rig);
                }
                ((PlayerWithSynData) player).setRig(ItemStack.EMPTY);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
