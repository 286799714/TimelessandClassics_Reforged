package com.tac.guns.client.audio;

import com.tac.guns.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class ReloadingSound extends EntityBoundSoundInstance {
    public ReloadingSound(SoundEvent sound, SoundSource category, Entity entity) {
        super(sound, category, 1.0F, 1.0F, entity);
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.pitch = 1.0f;
        this.attenuation = Attenuation.NONE;

        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null)
        {
            float distance = Config.SERVER.gunShotMaxDistance.get().floatValue();
            this.volume = volume * (1.0F - Math.min(1.0F, (float) Math.sqrt(player.distanceToSqr(x, y, z)) / distance));
            this.volume *= this.volume;
        }
    }
    @Override
    public void tick(){
        super.tick();
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null)
        {
            float distance = Config.SERVER.gunShotMaxDistance.get().floatValue();
            this.volume = this.volume * (1.0F - Math.min(1.0F, (float) Math.sqrt(player.distanceToSqr(this.x, this.y, this.z)) / distance));
            this.volume *= this.volume;
        }
    }
}
