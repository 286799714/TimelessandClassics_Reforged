package com.tac.guns.client.render.animation;

import com.tac.guns.GunMod;
import com.tac.guns.client.render.animation.module.*;
import com.tac.guns.init.ModItems;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class MK23AnimationController extends PistalAnimationController {
    public static int INDEX_BODY = 3;
    public static int INDEX_SLIDE = 2;
    public static int INDEX_MAG = 5;
    public static int INDEX_BULLET = 4;
    public static int INDEX_LEFT_HAND = 0;
    public static int INDEX_RIGHT_HAND = 7;

    public static final AnimationMeta RELOAD_NORM = new AnimationMeta(new ResourceLocation("tac","animations/mk23_reload_norm.gltf"));
    public static final AnimationMeta DRAW = new AnimationMeta(new ResourceLocation("tac","animations/mk23_draw.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(new ResourceLocation("tac","animations/mk23_reload_empty.gltf"));
    public static final AnimationMeta STATIC = new AnimationMeta(new ResourceLocation("tac","animations/mk23_static.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(new ResourceLocation("tac","animations/mk23_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(new ResourceLocation("tac","animations/mk23_inspect.gltf"));
    private static final MK23AnimationController instance = new MK23AnimationController();

    @Override
    public AnimationMeta getAnimationFromLabel(GunAnimationController.AnimationLabel label) {
        switch (label){
            case RELOAD_NORMAL: return RELOAD_NORM;
            case RELOAD_EMPTY: return RELOAD_EMPTY;
            case DRAW: return DRAW;
            case STATIC: return STATIC;
            case INSPECT: return INSPECT;
            case INSPECT_EMPTY: return INSPECT_EMPTY;
            default: return null;
        }
    }

    private MK23AnimationController() {
        try {
            Animations.load(RELOAD_NORM);
            Animations.load(DRAW);
            Animations.load(RELOAD_EMPTY);
            Animations.load(STATIC);
            Animations.load(INSPECT);
            Animations.load(INSPECT_EMPTY);
        } catch (IOException e) {
            GunMod.LOGGER.fatal(e.getStackTrace());
        }
        enableStaticState();
        GunAnimationController.setAnimationControllerMap(ModItems.MK23.getId(),this);
    }

    @Override
    public AnimationSoundMeta getSoundFromLabel(AnimationLabel label){
        return super.getSoundFromLabel(ModItems.MK23.get(), label);
    }


    public static MK23AnimationController getInstance() { return instance; }

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

    @Override
    public int getSlideNodeIndex() {
        return INDEX_SLIDE;
    }

    @Override
    public int getMagazineNodeIndex() {
        return INDEX_MAG;
    }
}