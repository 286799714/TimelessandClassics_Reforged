package com.tac.guns.client.event;

import com.tac.guns.GunMod;
import com.tac.guns.client.resource.ClientGunLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ReloadResourceEvent {
    public static final ResourceLocation BLOCK_ATLAS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");

    @SubscribeEvent
    public static void onTextureStitchEventPost(TextureStitchEvent.Post event) {
        if (BLOCK_ATLAS_TEXTURE.equals(event.getAtlas().location())) {
            reloadAllPack();
        }
    }

    public static void reloadAllPack() {
        StopWatch watch = StopWatch.createStarted();
        {
            ClientGunLoader.initAndReload();
        }
        watch.stop();
        GunMod.LOGGER.info("Model loading time: {} ms", watch.getTime(TimeUnit.MICROSECONDS) / 1000.0);
    }
}
