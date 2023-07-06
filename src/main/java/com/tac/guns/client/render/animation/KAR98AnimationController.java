package com.tac.guns.client.render.animation;

import com.tac.guns.GunMod;
import com.tac.guns.client.render.animation.module.*;
import com.tac.guns.init.ModItems;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class KAR98AnimationController extends PumpShotgunAnimationController {
    public static int INDEX_BODY = 11;
    public static int INDEX_LEFT_HAND = 12;
    public static int INDEX_RIGHT_HAND = 6;
    public static int INDEX_CLIP = 4;
    public static int INDEX_BOLT_FIX = 0;
    public static int INDEX_BOLT_ROTATE = 1;
    public static int INDEX_BULLET2 = 3;
    public static int INDEX_BULLET3 = 5;
    public static int INDEX_BULLETS = 9;

    public static final AnimationMeta STATIC = new AnimationMeta(new ResourceLocation("tac","animations/kar98_static.gltf"));
    public static final AnimationMeta DRAW = new AnimationMeta(new ResourceLocation("tac","animations/kar98_draw.gltf"));
    public static final AnimationMeta INTRO = new AnimationMeta(new ResourceLocation("tac","animations/kar98_reload_intro.gltf"));
    public static final AnimationMeta LOOP = new AnimationMeta(new ResourceLocation("tac","animations/kar98_reload_loop.gltf"));
    public static final AnimationMeta NORMAL_END = new AnimationMeta(new ResourceLocation("tac","animations/kar98_reload_end.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(new ResourceLocation("tac","animations/kar98_reload_empty.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(new ResourceLocation("tac","animations/kar98_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(new ResourceLocation("tac","animations/kar98_inspect.gltf"));
    public static final AnimationMeta BOLT = new AnimationMeta(new ResourceLocation("tac","animations/kar98_bolt.gltf"));
    private static final KAR98AnimationController instance = new KAR98AnimationController();

    private KAR98AnimationController(){
        try {
            Animations.load(NORMAL_END);
            Animations.load(RELOAD_EMPTY);
            Animations.load(DRAW);
            Animations.load(INSPECT);
            Animations.load(INSPECT_EMPTY);
            Animations.load(STATIC);
            Animations.load(BOLT);
            Animations.load(INTRO);
            Animations.load(LOOP);
        } catch (IOException e) {
            GunMod.LOGGER.fatal(e.getStackTrace());
        }
        enableStaticState();
        GunAnimationController.setAnimationControllerMap(ModItems.KAR98.getId(),this);
    }

    public static KAR98AnimationController getInstance(){
        return instance;
    }

    @Override
    public AnimationMeta getAnimationFromLabel(AnimationLabel label) {
        switch (label){
            case RELOAD_EMPTY: return RELOAD_EMPTY;
            case RELOAD_NORMAL: return NORMAL_END;
            case DRAW: return DRAW;
            case INSPECT: return INSPECT;
            case INSPECT_EMPTY: return INSPECT_EMPTY;
            case STATIC: return STATIC;
            case PULL_BOLT: return BOLT;
            case RELOAD_INTRO: return INTRO;
            case RELOAD_LOOP: return LOOP;
            default: return null;
        }
    }

    @Override
    public AnimationSoundMeta getSoundFromLabel(AnimationLabel label){
        return super.getSoundFromLabel(ModItems.KAR98.get(), label);
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