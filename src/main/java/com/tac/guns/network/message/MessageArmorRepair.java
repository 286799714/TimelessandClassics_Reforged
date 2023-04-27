package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.common.network.ServerPlayHandler;
import com.tac.guns.init.ModSyncedDataKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageArmorRepair extends PlayMessage<MessageArmorRepair> {
	private boolean repairOrCancel;
	private boolean repairApply;

	public MessageArmorRepair() {}


	/**
	 * @param repairOrCancel IS NOT NEEDED IF MESSAGE BEING USED FOR FIX APPLICATION, true = play sound, started fixing, false = cancelation
	 * @param repairApply
	 */
	public MessageArmorRepair(boolean repairOrCancel, boolean repairApply)
	{
		this.repairOrCancel = repairOrCancel;
		this.repairApply = repairApply;
	}
	@Override
	public void encode(MessageArmorRepair messageArmorRepair, FriendlyByteBuf buffer) {
		buffer.writeBoolean(messageArmorRepair.repairOrCancel);
		buffer.writeBoolean(messageArmorRepair.repairApply);
	}

	public MessageArmorRepair decode(FriendlyByteBuf buffer)
	{
		boolean newrepairOrCancel = buffer.readBoolean();
		boolean newrepairApply = buffer.readBoolean();
		return new MessageArmorRepair(newrepairOrCancel, newrepairApply);
	}

	@Override
	public void handle(MessageArmorRepair messageArmorRepair, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null && !player.isSpectator())
			{
				if(this.repairApply)
				{
					ServerPlayHandler.handleArmorFixApplication(player);
					SyncedEntityData.instance().set(player, ModSyncedDataKeys.QREPAIRING, false);
				}
				else
					SyncedEntityData.instance().set(player, ModSyncedDataKeys.QREPAIRING, this.repairOrCancel);
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
