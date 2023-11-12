package com.tac.guns.network.message;

import com.mojang.logging.LogUtils;
import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.duck.PlayerWithSynData;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.ArmorRigContainerProvider;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class MessageArmorOpenAmmoPack extends PlayMessage<MessageArmorOpenAmmoPack> {
    @Override
    public void encode(MessageArmorOpenAmmoPack messageArmorOpenAmmoPack, FriendlyByteBuf friendlyByteBuf) {}

    @Override
    public MessageArmorOpenAmmoPack decode(FriendlyByteBuf friendlyByteBuf) {return new MessageArmorOpenAmmoPack();}

    @Override
    public void handle(MessageArmorOpenAmmoPack messageArmorOpenAmmoPack, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null && !player.isSpectator())
            {
                ItemStack rig = ((PlayerWithSynData) player).getRig();
                if(!rig.isEmpty()) {
                    ArmorRigContainerProvider containerProvider = new ArmorRigContainerProvider(rig);
                    NetworkHooks.openGui(player, containerProvider);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
