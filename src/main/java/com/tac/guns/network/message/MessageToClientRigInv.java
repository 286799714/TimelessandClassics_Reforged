package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.common.network.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageToClientRigInv extends PlayMessage<MessageToClientRigInv>
{

	private ResourceLocation id;
	public ResourceLocation getId()
	{
		return this.id;
	}

	public MessageToClientRigInv(ResourceLocation id)
	{
		this.id = id;
	}
	@Override
	public void encode(MessageToClientRigInv messageToClientRigInv, FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(messageToClientRigInv.id);
	}

	public MessageToClientRigInv decode(FriendlyByteBuf buffer)
	{
		return new MessageToClientRigInv(buffer.readResourceLocation());
	}

	@Override
	public void handle(MessageToClientRigInv messageToClientRigInv, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() ->
		{
			ServerPlayer player = context.get().getSender();
			if(player != null && !player.isSpectator())
			{
				ServerPlayHandler.handleRigAmmoCount(player, messageToClientRigInv.id);
			}
		});
		context.get().setPacketHandled(true);
	}
}
