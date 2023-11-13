package com.tac.guns.network;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkChannelBuilder;
import com.tac.guns.Reference;
import com.tac.guns.common.NetworkGunManager;
import com.tac.guns.common.NetworkRigManager;
import com.tac.guns.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler
{
    public static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel handshakeChannel;
    private static SimpleChannel playChannel = FrameworkChannelBuilder.create(Reference.MOD_ID, "play", 1)


    .registerPlayMessage(MessageAim.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageReload.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageShoot.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageSaveItemUpgradeBench.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageLightChange.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageUnload.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageStunGrenade.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageCraft.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageBulletTrail.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageAttachments.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageInspection.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageColorBench.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageUpdateGuns.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageUpdateRigs.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageBlood.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageShooting.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageGunSound.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageProjectileHitBlock.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageProjectileHitEntity.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageRemoveProjectile.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageFireMode.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageUpdateGunID.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageUpgradeBenchApply.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageUpdateMoveInacc.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageEmptyMag.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageArmorRepair.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageArmorEquip.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageArmorRemove.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageArmorUpdate.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageArmorOpenAmmoPack.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessagePlayerShake.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageUpdatePlayerMovement.class, NetworkDirection.PLAY_TO_SERVER)
    .registerPlayMessage(MessageAnimationSound.class, NetworkDirection.PLAY_TO_CLIENT)
    .registerPlayMessage(MessageAnimationRun.class, NetworkDirection.PLAY_TO_SERVER)
            .build();
    private static int nextMessageId = 0;

    public static void init()
    {
        FrameworkAPI.registerLoginData(new ResourceLocation(Reference.MOD_ID, "network_gun_manager"), NetworkGunManager.LoginData::new);
        FrameworkAPI.registerLoginData(new ResourceLocation(Reference.MOD_ID, "network_rig_manager"), NetworkRigManager.LoginData::new);

    }
    /**
     * Gets the handshake network channel for MrCrayfish's Gun Mod
     */
    public static SimpleChannel getHandshakeChannel()
    {
        return handshakeChannel;
    }

    /**
     * Gets the play network channel for MrCrayfish's Gun Mod
     */
    public static SimpleChannel getPlayChannel()
    {
        return playChannel;
    }
}
