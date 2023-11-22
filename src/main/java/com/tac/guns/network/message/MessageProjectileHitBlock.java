package com.tac.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.client.network.ClientPlayHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class MessageProjectileHitBlock extends PlayMessage<MessageProjectileHitBlock>
{
    private double x;
    private double y;
    private double z;
    private BlockPos pos;
    private Direction face;
    private boolean isBlast;
    private Vec3 direction;
    private boolean haveHole;

    public MessageProjectileHitBlock() {
    }

    public MessageProjectileHitBlock(double x, double y, double z, BlockPos pos, Direction face, boolean isBlast, Vec3 direction, boolean haveHole) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pos = pos;
        this.face = face;
        this.isBlast = isBlast;
        this.direction = direction;
        this.haveHole = haveHole;
    }

    @Override
    public void encode(MessageProjectileHitBlock messageProjectileHitBlock, FriendlyByteBuf buffer) {
        buffer.writeDouble(messageProjectileHitBlock.x);
        buffer.writeDouble(messageProjectileHitBlock.y);
        buffer.writeDouble(messageProjectileHitBlock.z);
        buffer.writeBlockPos(messageProjectileHitBlock.pos);
        buffer.writeEnum(messageProjectileHitBlock.face);
        buffer.writeBoolean(messageProjectileHitBlock.isBlast);
        buffer.writeDouble(messageProjectileHitBlock.direction.x);
        buffer.writeDouble(messageProjectileHitBlock.direction.y);
        buffer.writeDouble(messageProjectileHitBlock.direction.z);
        buffer.writeBoolean(messageProjectileHitBlock.haveHole);
    }

    @Override
    public MessageProjectileHitBlock decode(FriendlyByteBuf buffer) {
        return new MessageProjectileHitBlock(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readBlockPos(),
                buffer.readEnum(Direction.class),
                buffer.readBoolean(),
                new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
                buffer.readBoolean()
        );
    }

    @Override
    public void handle(MessageProjectileHitBlock messageProjectileHitBlock, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleProjectileHitBlock(messageProjectileHitBlock));
        supplier.get().setPacketHandled(true);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getFace() {
        return this.face;
    }

    public boolean isBlast() {
        return this.isBlast;
    }

    public Vec3 getDirection() {return direction;}

    public boolean isHaveHole() {return haveHole;}
}
