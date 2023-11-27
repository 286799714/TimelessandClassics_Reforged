package com.tac.guns.client.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class HitBlockSmokeParticle  extends ExplodeParticle {
    protected HitBlockSmokeParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet spriteSet) {
        super(world, x, y, z, motionX, motionY, motionZ, spriteSet);

        this.setColor(0.56f,0.56f,0.56f);
        this.quadSize = 0.3f + 0.15f * this.random.nextFloat();
        this.lifetime = (int)(6D / ((double)this.random.nextFloat() * 0.9 + 0.1D)) + 2;
        this.gravity = 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_106588_) {
            this.sprites = p_106588_;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType p_106599_, @NotNull ClientLevel p_106600_, double p_106601_, double p_106602_, double p_106603_, double p_106604_, double p_106605_, double p_106606_) {
            return new HitBlockSmokeParticle(p_106600_, p_106601_, p_106602_, p_106603_, p_106604_, p_106605_, p_106606_, this.sprites);
        }
    }
}
