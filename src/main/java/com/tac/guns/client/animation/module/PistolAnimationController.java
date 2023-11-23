package com.tac.guns.client.animation.module;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class PistolAnimationController extends GunAnimationController {
    public abstract int getSlideNodeIndex();
    public abstract int getMagazineNodeIndex();
}
