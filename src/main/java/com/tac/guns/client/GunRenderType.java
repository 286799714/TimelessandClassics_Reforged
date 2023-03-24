package com.tac.guns.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.tac.guns.Reference;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.render.ScreenTextureState;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import static org.lwjgl.opengl.GL11.GL_QUADS;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public final class GunRenderType extends RenderType
{
    private static final RenderType BULLET_TRAIL = RenderType.create(Reference.MOD_ID + ":projectile_trail", DefaultVertexFormat. POSITION_COLOR, GL_QUADS, 256, true, true,
            RenderType.CompositeState.builder().setCullState(NO_CULL).setAlphaState(DEFAULT_ALPHA).setTransparencyState(TRANSLUCENT_TRANSPARENCY)/*.diffuseLighting(RenderState.DIFFUSE_LIGHTING_DISABLED).overlay(OVERLAY_ENABLED)*/.createCompositeState(false));
    private static final RenderType SCREEN =
            RenderType.create(Reference.MOD_ID + ":screen_texture", DefaultVertexFormat.NEW_ENTITY, GL_QUADS, 1024, true, false, RenderType.CompositeState.builder().setTexturingState(ScreenTextureState.instance()).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setCullState(RenderStateShard.CULL).createCompositeState(false)); //256
    private static final RenderType MUZZLE_FLASH = RenderType.create(Reference.MOD_ID + ":muzzle_flash", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, GL_QUADS, 256, true, false, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(GunRenderingHandler.MUZZLE_FLASH_TEXTURE, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).createCompositeState(true));
    private static final RenderType MUZZLE_SMOKE = RenderType.create(Reference.MOD_ID + ":muzzle_smoke", DefaultVertexFormat.POSITION_COLOR_TEX, GL_QUADS, 256, true, false, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(GunRenderingHandler.MUZZLE_SMOKE_TEXTURE, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).createCompositeState(true));

    private static final RenderType SCREEN_BLACK =
            RenderType.create(Reference.MOD_ID + ":screen_black", DefaultVertexFormat.NEW_ENTITY, GL_QUADS, 64, true, false,
                    RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(GunRenderingHandler.MUZZLE_SMOKE_TEXTURE, false, false)).createCompositeState(false)); //256
    private GunRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn)
    {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getBulletTrail()
    {
        return BULLET_TRAIL;
    }

    public static RenderType getScreen()
    {
        return SCREEN;
    } // E : Figuring out how to refactor this = fixing all of Optifine's scope issues
    public static RenderType getScreenBlack()
    {
        return SCREEN_BLACK;
    }
    public static RenderType getMuzzleSmoke() { return MUZZLE_SMOKE; }

    public static RenderType getMuzzleFlash()
    {
        return MUZZLE_FLASH;
    }
}
