package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.client.network.ClientPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class MessageBlood extends PlayMessage<MessageBlood>
{
    private double x;
    private double y;
    private double z;
    private Vec3 motion;
    private boolean penetrate;
    private int amount;

    public MessageBlood() {}

    public MessageBlood(double x, double y, double z, Vec3 motion, boolean penetrate, int amount)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.motion = motion;
        this.penetrate = penetrate;
        this.amount = amount;
    }

    @Override
    public void encode(MessageBlood messageBlood, FriendlyByteBuf buffer)
    {
        buffer.writeDouble(messageBlood.x);
        buffer.writeDouble(messageBlood.y);
        buffer.writeDouble(messageBlood.z);
        buffer.writeDouble(messageBlood.motion.x);
        buffer.writeDouble(messageBlood.motion.y);
        buffer.writeDouble(messageBlood.motion.z);
        buffer.writeBoolean(messageBlood.penetrate);
        buffer.writeInt(messageBlood.amount);
    }

    @Override
    public MessageBlood decode(FriendlyByteBuf buffer)
    {
        return new MessageBlood(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readBoolean(), buffer.readInt());
    }

    @Override
    public void handle(MessageBlood messageBlood, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleMessageBlood(messageBlood));
        supplier.get().setPacketHandled(true);
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public Vec3 getMotion(){return motion;}

    public boolean isPenetrate() {
        return penetrate;
    }

    public int getAmount(){
        return amount;
    }
}
