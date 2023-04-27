package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.common.network.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class MessageShoot extends PlayMessage<MessageShoot>
{
    private float rotationYaw;
    private float rotationPitch;

    private float randP;
    private float randY;

    public MessageShoot() {}

    public MessageShoot(float yaw, float pitch, float randP, float randY)
    {
        this.rotationPitch = pitch;
        this.rotationYaw = yaw;
        this.randP = randP;
        this.randY = randY;
    }

    @Override
    public void encode(MessageShoot messageShoot, FriendlyByteBuf buffer)
    {
        buffer.writeFloat(messageShoot.rotationYaw);
        buffer.writeFloat(messageShoot.rotationPitch);
        buffer.writeFloat(messageShoot.randP);
        buffer.writeFloat(messageShoot.randY);
    }

    @Override
    public MessageShoot decode(FriendlyByteBuf buffer)
    {
        return new MessageShoot(buffer.readFloat(), buffer.readFloat(),buffer.readFloat(), buffer.readFloat());
    }

    @Override
    public void handle(MessageShoot messageShoot, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleShoot(messageShoot, player, messageShoot.randP, messageShoot.randY);
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public float getRotationYaw()
    {
        return this.rotationYaw;
    }

    public float getRotationPitch()
    {
        return this.rotationPitch;
    }
}
