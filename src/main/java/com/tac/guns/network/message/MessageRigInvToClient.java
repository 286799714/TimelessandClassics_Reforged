package com.tac.guns.network.message;

import com.tac.guns.client.network.ClientPlayHandler;
import com.tac.guns.common.Gun;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRigInvToClient implements IMessage
{
	public MessageRigInvToClient() {}

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

	public void decode(FriendlyByteBuf buffer)
	{
		this.count = buffer.readInt();
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
