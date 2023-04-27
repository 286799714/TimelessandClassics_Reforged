package com.tac.guns.network.message;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.framework.api.network.PlayMessage;
import com.tac.guns.client.network.ClientPlayHandler;
import com.tac.guns.common.CustomRig;
import com.tac.guns.common.CustomRigLoader;
import com.tac.guns.common.NetworkRigManager;
import com.tac.guns.common.Rig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class MessageUpdateRigs extends PlayMessage<MessageUpdateRigs> implements NetworkRigManager.IRigProvider
{
    private ImmutableMap<ResourceLocation, Rig> registeredRigs;
    private ImmutableMap<ResourceLocation, CustomRig> customRigs;

    public MessageUpdateRigs() {}

    public MessageUpdateRigs(ImmutableMap<ResourceLocation, Rig> registeredRigs, ImmutableMap<ResourceLocation, CustomRig> customRigs) {
        this.registeredRigs = registeredRigs;
        this.customRigs = customRigs;
    }

    @Override
    public void encode(MessageUpdateRigs messageUpdateRigs, FriendlyByteBuf buffer) {
        Validate.notNull(NetworkRigManager.get());
        Validate.notNull(CustomRigLoader.get());
        NetworkRigManager.get().writeRegisteredRigs(buffer);
        CustomRigLoader.get().writeCustomRigs(buffer);
    }

    @Override
    public MessageUpdateRigs decode(FriendlyByteBuf buffer)
    {
        ImmutableMap<ResourceLocation, Rig> registeredRigs = NetworkRigManager.readRegisteredRigs(buffer);
        ImmutableMap<ResourceLocation, CustomRig> customrig = CustomRigLoader.readCustomRigs(buffer);
        return new MessageUpdateRigs(registeredRigs, customrig);
    }

    @Override
    public void handle(MessageUpdateRigs messageUpdateRigs, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleUpdateRigs(this));
        supplier.get().setPacketHandled(true);
    }
    @Override
    public ImmutableMap<ResourceLocation, Rig> getRegisteredRigs()
    {
        return this.registeredRigs;
    }

    public ImmutableMap<ResourceLocation, CustomRig> getCustomRigs()
    {
        return this.customRigs;
    }
}
