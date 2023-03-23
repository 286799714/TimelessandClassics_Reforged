package com.tac.guns.client.render.gun.model.scope.scopeUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ScopeGlobal extends GameRenderer {
    private boolean shouldLoadRenderers = true;

    public ScopeGlobal(Minecraft mcIn) {
        super(mcIn, mcIn.getResourceManager(), mcIn.renderBuffers());
    }

    /*public ScopeGlobal(Minecraft mcIn) {
        super(mcIn, mcIn.getResourceManager(), mcIn.getRenderTypeBuffers());
    }*/
   /* @Override
    public void setWorldAndLoadRenderers(@Nullable ClientWorld worldClientIn) {
        shouldLoadRenderers = false;
        super.setWorldAndLoadRenderers(worldClientIn);
        super.setDisplayListEntitiesDirty();
    }

    *//*@Override
    public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
        shouldLoadRenderers = false;
        super.(viewEntity, partialTicks, camera, frameCount, playerSpectator);
    }*//*

    @Override
    public void loadRenderers() {
        super.loadRenderers();
        shouldLoadRenderers = true;
    }

    @Override
    public void renderEntityOutlineFramebuffer() {
        if(shouldLoadRenderers)
            super.renderEntityOutlineFramebuffer();
    }

    //renderEntityOutlineFramebuffer

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {
    }

    // TODO: RESERVERED FOR THERMAL SCOPES
    *//*@Nullable
    @Override
    public Framebuffer getEntityOutlineFramebuffer() {
        return super.getEntityOutlineFramebuffer();
    }*//*


    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {
    }

    @Override
    public void playEvent(PlayerEntity player, int type, BlockPos blockPosIn, int data) {
    }*/
}