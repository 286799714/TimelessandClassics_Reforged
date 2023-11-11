package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.duck.PlayerWithSynData;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageArmorEquip extends PlayMessage<MessageArmorEquip> {
    @Override
    public void encode(MessageArmorEquip messageArmorEquip, FriendlyByteBuf friendlyByteBuf) {}

    @Override
    public MessageArmorEquip decode(FriendlyByteBuf friendlyByteBuf) {
        return new MessageArmorEquip();
    }

    @Override
    public void handle(MessageArmorEquip messageArmorEquip, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null && !player.isSpectator())
            {
                ItemStack newRig = player.getMainHandItem();
                if(newRig.getItem() instanceof ArmorRigItem) {
                    ItemStack oldRig = ((PlayerWithSynData) player).getRig();
                    ((PlayerWithSynData) player).setRig(newRig);
                    player.setItemInHand(InteractionHand.MAIN_HAND, oldRig);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
