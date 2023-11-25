package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.GunMod;
import com.tac.guns.common.network.ServerPlayHandler;
import com.tac.guns.duck.PlayerWithSynData;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public class MessageArmorRepair extends PlayMessage<MessageArmorRepair> {
	public MessageArmorRepair() {}

	@Override
	public void encode(MessageArmorRepair messageColorBench, FriendlyByteBuf buffer) {}

	@Override
	public MessageArmorRepair decode(FriendlyByteBuf buffer) {
		return new MessageArmorRepair();
	}

	@Override
	public void handle(MessageArmorRepair messageArmorRepair, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null && !player.isSpectator())
			{
				ServerPlayHandler.handleArmorFixApplication(player);
				SyncedEntityData.instance().set(player, ModSyncedDataKeys.QREPAIRING, false);
			}
		});

		supplier.get().setPacketHandled(true);
	}
}
