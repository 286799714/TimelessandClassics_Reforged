package com.tac.guns.client.render.animation.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public enum AnimationSoundManager {
    INSTANCE;
    private final Map<UUID, Map<ResourceLocation, ISound> > soundsMap = new HashMap<>();

    public void playerSound(PlayerEntity player, AnimationMeta animationMeta, AnimationSoundMeta soundMeta){
        Map<ResourceLocation, ISound> map = soundsMap.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
        ISound sound = map.get(animationMeta.getResourceLocation());
        if(sound == null) {
            SoundEvent soundEvent = new SoundEvent(soundMeta.getResourceLocation());
            sound = new EntityTickableSound(soundEvent, SoundCategory.PLAYERS, player);
        }
        if(sound instanceof EntityTickableSound){
            EntityTickableSound entityTickableSound = (EntityTickableSound) sound;
            if(entityTickableSound.isStopped()){
                SoundEvent soundEvent = new SoundEvent(soundMeta.getResourceLocation());
                sound = new EntityTickableSound(soundEvent, SoundCategory.PLAYERS, player);
            }
        }
        if(Minecraft.getInstance().getSoundManager().isActive(sound)) return;
        Minecraft.getInstance().getSoundManager().play(sound);
        map.put(animationMeta.getResourceLocation(), sound);
    }

    public void onPlayerDeath(PlayerEntity player){
        Map<ResourceLocation, ISound> map = soundsMap.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
        for(ISound sound : map.values()){
            Minecraft.getInstance().getSoundManager().stop(sound);
        }
        soundsMap.remove(player.getUUID());
    }

    public void interruptSound(PlayerEntity player, AnimationMeta animationMeta){
        Map<ResourceLocation, ISound> map = soundsMap.get(player.getUUID());
        if(map != null){
            ISound sound = map.get(animationMeta.getResourceLocation());
            if(sound != null){
                Minecraft.getInstance().getSoundManager().stop(sound);
            }
            map.remove(animationMeta.getResourceLocation());
        }
    }
}
