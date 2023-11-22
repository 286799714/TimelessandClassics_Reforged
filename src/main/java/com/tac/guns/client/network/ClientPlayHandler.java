package com.tac.guns.client.network;

import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import com.tac.guns.Config;
import com.tac.guns.client.BulletTrail;
import com.tac.guns.client.CustomGunManager;
import com.tac.guns.client.CustomRigManager;
import com.tac.guns.client.audio.GunShotSound;
import com.tac.guns.client.handler.BulletTrailRenderingHandler;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.HUDRenderingHandler;
import com.tac.guns.client.animation.module.AnimationMeta;
import com.tac.guns.client.animation.module.AnimationSoundManager;
import com.tac.guns.client.animation.module.AnimationSoundMeta;
import com.tac.guns.common.NetworkGunManager;
import com.tac.guns.common.NetworkRigManager;
import com.tac.guns.init.ModParticleTypes;
import com.tac.guns.network.message.*;
import com.tac.guns.particles.BulletHoleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ClientPlayHandler
{
    public static void handleMessageGunSound(MessageGunSound message)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        if(message.showMuzzleFlash())
        {
            GunRenderingHandler.get().showMuzzleFlashForPlayer(message.getShooterId());
        }

        if(message.getShooterId() == mc.player.getId())
        {
            Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(message.getId(), SoundSource.PLAYERS, (float) (message.getVolume()*Config.CLIENT.sounds.weaponsVolume.get()), message.getPitch(), false, 0, SoundInstance.Attenuation.LINEAR, 0, 0, 0,
                    true));
        }
        else
        {
            Minecraft.getInstance().getSoundManager().play(new GunShotSound(message.getId(), SoundSource.PLAYERS, message.getX(), message.getY(), message.getZ(), message.getVolume(), message.getPitch(), message.isReload()));
        }
    }

    public static void handleMessageAnimationSound(UUID fromWho, ResourceLocation animationResource, ResourceLocation soundResource, boolean play){
        Level world = Minecraft.getInstance().level;
        if(world == null) return;
        Player player = world.getPlayerByUUID(fromWho);
        if (player == null) return;
        if (animationResource == null || soundResource == null) return;
        AnimationMeta animationMeta = new AnimationMeta(animationResource);
        AnimationSoundMeta soundMeta = new AnimationSoundMeta(soundResource);
        if (play) AnimationSoundManager.INSTANCE.playerSound(player, animationMeta, soundMeta);
        else AnimationSoundManager.INSTANCE.interruptSound(player, animationMeta);
    }

    public static void handleMessageBlood(MessageBlood message)
    {
        if(!Config.CLIENT.particle.enableBlood.get())
        {
            return;
        }
        Level world = Minecraft.getInstance().level;
        if(world != null)
        {
            for(int i = 0; i < message.getAmount(); i++)
            {
                Vec3 motion = message.getMotion();
                world.addParticle(ModParticleTypes.BLOOD.get(), true, message.getX(), message.getY(), message.getZ(), -motion.x*0.1, -motion.y*0.1, -motion.z*0.1);
                motion = motion.multiply(1 - 0.7 * Math.random(), 1 - 0.7 * Math.random(), 1 - 0.7 * Math.random());
                if(message.isPenetrate())
                    world.addParticle(ModParticleTypes.BLOOD.get(), true, message.getX(), message.getY(), message.getZ(), motion.x, motion.y, motion.z);
            }
        }
    }

    public static void handleMessageBulletTrail(MessageBulletTrail message)
    {
        Level world = Minecraft.getInstance().level;
        if(world != null)
        {

            int[] entityIds = message.getEntityIds();
            Vec3[] positions = message.getPositions();
            Vec3[] motions = message.getMotions();
            float[] shooterYaws = message.getShooterYaws();
            float[] shooterPitch = message.getShooterPitches();
            ItemStack item = message.getItem();
            int trailColor = message.getTrailColor();
            double trailLengthMultiplier = message.getTrailLengthMultiplier();
            int life = message.getLife();
            double gravity = message.getGravity();
            int shooterId = message.getShooterId();
            for(int i = 0; i < message.getCount(); i++)
            {
                BulletTrailRenderingHandler.get().add(new BulletTrail(entityIds[i], positions[i], motions[i], shooterYaws[i], shooterPitch[i], item, trailColor, trailLengthMultiplier, life, gravity, shooterId, message.getCount()));
            }
        }
    }

    public static void handleExplosionStunGrenade(MessageStunGrenade message)
    {
        Minecraft mc = Minecraft.getInstance();
        ParticleEngine particleManager = mc.particleEngine;
        Level world = mc.level;
        double x = message.getX();
        double y = message.getY();
        double z = message.getZ();

        /* Spawn lingering smoke particles */
        for(int i = 0; i < 30; i++)
        {
            spawnParticle(particleManager, ParticleTypes.CLOUD, x, y, z, world.random, 0.2);
        }

        /* Spawn fast moving smoke/spark particles */
        for(int i = 0; i < 30; i++)
        {
            Particle smoke = spawnParticle(particleManager, ParticleTypes.SMOKE, x, y, z, world.random, 4.0);
            smoke.setLifetime((int) ((8 / (Math.random() * 0.1 + 0.4)) * 0.5));
            spawnParticle(particleManager, ParticleTypes.CRIT, x, y, z, world.random, 4.0);
        }
    }

    private static Particle spawnParticle(ParticleEngine manager, ParticleOptions data, double x, double y, double z, Random rand, double velocityMultiplier)
    {
        //if(GunMod.cabLoaded)
            //deleteBitOnHit();
        return manager.createParticle(data, x, y, z, (rand.nextDouble() - 0.5) * velocityMultiplier, (rand.nextDouble() - 0.5) * velocityMultiplier, (rand.nextDouble() - 0.5) * velocityMultiplier);
    }
  /*  private static boolean deleteBitOnHit(BlockPos blockPos, BlockState blockState, double x, double y, double z)//(IParticleData data, double x, double y, double z, Random rand, double velocityMultiplier)
    {
        Minecraft mc = Minecraft.getInstance();
        ChiselAdaptingWorldMutator chiselAdaptingWorldMutator = new ChiselAdaptingWorldMutator(mc.world, blockPos);
        float bitSize = ChiselsAndBitsAPI.getInstance().getStateEntrySize().getSizePerBit();
        ChiselsAndBitsAPI.getInstance().getMutatorFactory().in(mc.world, blockPos).overrideInAreaTarget(Blocks.AIR.getDefaultState(), new Vector3d(bitSize*Math.abs(x),bitSize*Math.abs(y),bitSize*Math.abs(z)));
        return true;
    }
*/
    public static void handleProjectileHitBlock(MessageProjectileHitBlock message)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;
        Level world = mc.level;
        if (world != null) {
            if (message.isBlast())
                return;

            BlockState state = world.getBlockState(message.getPos());
            double holeX = message.getX() + 0.005 * message.getFace().getStepX();
            double holeY = message.getY() + 0.005 * message.getFace().getStepY();
            double holeZ = message.getZ() + 0.005 * message.getFace().getStepZ();
            double distance = Math.sqrt(mc.player.distanceToSqr(message.getX(), message.getY(), message.getZ()));
            if(message.isHaveHole())
                world.addParticle(new BulletHoleData(message.getFace(), message.getPos()), false, holeX, holeY, holeZ, 0, 0, 0);
            for (int i = 0; i < 5; i++) {
                Vec3i normal = message.getFace().getNormal();
                Vec3 motion = new Vec3(0,0,0);
                Vec3 reflection = message.getDirection().subtract(message.getDirection().multiply(normal.getX() * 2, normal.getY() * 2, normal.getZ() * 2));
                motion.add(reflection.multiply(0.5f, 0.5f, 0.5f));
                world.addParticle(ModParticleTypes.HIT_BLOCK_SMOKE.get(), false, message.getX(), message.getY(), message.getZ(), motion.x , motion.y, motion.z);
            }
            if (distance < 32.0) {
                world.playLocalSound(message.getX(), message.getY(), message.getZ(), state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.75F, 2.0F, false);
            }
        }
    }

    private static double getRandomDir(Random random) {
        return -0.25 + random.nextDouble() * 0.5;
    }

    public static void handleProjectileHitEntity(MessageProjectileHitEntity message)
    {
        Minecraft mc = Minecraft.getInstance();
        Level world = mc.level;
        if(world == null)
            return;

        HUDRenderingHandler.get().hitMarkerTracker = (int) HUDRenderingHandler.hitMarkerRatio;
        HUDRenderingHandler.get().hitMarkerHeadshot = message.isHeadshot();

        SoundEvent event = getHitSound(message.isCritical(), message.isHeadshot(), message.isPlayer()); // Hit marker sound, after sound set HuD renderder hitmarker ticker to 3 fade in and out quick, use textured crosshair as a base
        if(event == null)
            return;

        mc.getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F, 1.0F + world.random.nextFloat() * 0.2F));
    }

    @Nullable
    private static SoundEvent getHitSound(boolean critical, boolean headshot, boolean player)
    {
        if(critical)
        {
            if(Config.CLIENT.sounds.playSoundWhenCritical.get())
            {
                SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Config.CLIENT.sounds.criticalSound.get()));
                return event != null ? event : SoundEvents.PLAYER_ATTACK_CRIT;
            }
        }
        else if(headshot)
        {
            if(Config.CLIENT.sounds.playSoundWhenHeadshot.get())
            {
                //SoundEvent event = ModSounds.HEADSHOT_EXTENDED_PLAYFUL.get();//ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Config.CLIENT.sounds.headshotSound.get()));
                SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Config.CLIENT.sounds.headshotSound.get()));
                return event != null ? event : SoundEvents.PLAYER_ATTACK_KNOCKBACK;
            }
        }
        else if(player)
        {
            return SoundEvents.PLAYER_HURT;
        }
        else
        {
            return SoundEvents.PLAYER_ATTACK_WEAK; // Hitmarker
        }

        return null;
    }

    public static void handleRemoveProjectile(MessageRemoveProjectile message)
    {
        BulletTrailRenderingHandler.get().remove(message.getEntityId());
    }

    /*public static void handleDevelopingGuns(MessageUpdateGuns message)
    {
        NetworkGunManager.updateRegisteredGuns(message);
        CustomGunManager.updateCustomGuns(message);
    }*/

    public static void handleUpdateGuns(MessageUpdateGuns message)
    {
        NetworkGunManager.updateRegisteredGuns(message);
        CustomGunManager.updateCustomGuns(message);
    }
    public static void handleUpdateRigs(MessageUpdateRigs message)
    {
        NetworkRigManager.updateRegisteredRigs(message);
        CustomRigManager.updateCustomRigs(message);
    }
}
