package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.client.network.ClientPlayHandler;
import com.tac.guns.common.Gun;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRigInvToClient extends PlayMessage<MessageRigInvToClient>
{
	public MessageRigInvToClient() {}

	public MessageRigInvToClient(int count) {
		this.count = count;
	}

	private int count;
	public int getCount()
	{
		return this.count;
	}

	private boolean onlyResetRigCount = false;
	public boolean getOnlyResetRigCount()
	{
		return this.onlyResetRigCount;
	}
	public MessageRigInvToClient(boolean reset) {this.onlyResetRigCount = reset;}
	public MessageRigInvToClient(ItemStack rig, ResourceLocation id)
	{
		this.count = Gun.ammoCountInRig(rig, id);
	}

	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeInt(this.count);
	}

	@Override
	public void encode(MessageRigInvToClient messageRigInvToClient, FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeInt(messageRigInvToClient.count);
	}

	public MessageRigInvToClient decode(FriendlyByteBuf buffer)
	{
		return new MessageRigInvToClient(buffer.readInt());
	}

	@Override
	public void handle(MessageRigInvToClient messageRigInvToClient, Supplier<NetworkEvent.Context> supplier) {

	}


	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() ->
		{
				ClientPlayHandler.updateRigInv(this);
		});
		ctx.setPacketHandled(true);
	}
}
