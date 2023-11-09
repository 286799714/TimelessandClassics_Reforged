package com.tac.guns.client.render.animation;

import com.tac.guns.GunMod;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.AnimationSoundMeta;
import com.tac.guns.client.render.animation.module.Animations;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.init.ModItems;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class UDP9AnimationController extends GunAnimationController {
    public static int INDEX_BODY = 6;
    public static int INDEX_MAG = 5;
    public static int INDEX_LEFT_HAND = 8;
    public static int INDEX_RIGHT_HAND = 0;
    public static int INDEX_BOLT = 3;
    public static int INDEX_HANDLE = 2;
    public static int INDEX_BULLET = 4;

    public static final AnimationMeta RELOAD_NORM = new AnimationMeta(new ResourceLocation("tac","animations/udp_9_reload_norm.gltf"));
    public static final AnimationMeta DRAW = new AnimationMeta(new ResourceLocation("tac","animations/udp_9_draw.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(new ResourceLocation("tac","animations/udp_9_reload_empty.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(new ResourceLocation("tac","animations/udp_9_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(new ResourceLocation("tac","animations/udp_9_inspect.gltf"));
    public static final AnimationMeta STATIC = new AnimationMeta(new ResourceLocation("tac","animations/udp_9_static.gltf"));
    private static final UDP9AnimationController instance = new UDP9AnimationController();

    private UDP9AnimationController() {
        try {
            Animations.load(RELOAD_NORM);
            Animations.load(RELOAD_EMPTY);
            Animations.load(DRAW);
            Animations.load(INSPECT);
            Animations.load(INSPECT_EMPTY);
            Animations.load(STATIC);
        } catch (IOException e) {
            GunMod.LOGGER.fatal(e.getStackTrace());
        }
        enableStaticState();
        GunAnimationController.setAnimationControllerMap(ModItems.UDP_9.getId(),this);
    }

    @Override
    public AnimationSoundMeta getSoundFromLabel(AnimationLabel label){
        return super.getSoundFromLabel(ModItems.UDP_9.get(), label);
    }

    public static UDP9AnimationController getInstance(){ return instance; }

    @Override
    public AnimationMeta getAnimationFromLabel(AnimationLabel label) {
        switch (label){
            case RELOAD_NORMAL: return RELOAD_NORM;
            case RELOAD_EMPTY: return RELOAD_EMPTY;
            case DRAW: return DRAW;
            case INSPECT: return INSPECT;
            case INSPECT_EMPTY: return INSPECT_EMPTY;
            case STATIC: return STATIC;
            default: return null;
        }
    }

    @Override
    protected int getAttachmentsNodeIndex() {
        return INDEX_BODY;
    }

    @Override
    protected int getRightHandNodeIndex() {
        return INDEX_RIGHT_HAND;
    }

    @Override
    protected int getLeftHandNodeIndex() {
        return INDEX_LEFT_HAND;
    }
}