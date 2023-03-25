package com.tac.guns.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public interface IMessage
{
    void encode(FriendlyByteBuf buffer);

    void decode(FriendlyByteBuf buffer);

    void handle(Supplier<NetworkEvent.Context> supplier);
}
