package com.tac.guns.client.model;

import com.mojang.math.Vector3f;
import com.tac.guns.client.util.Pair;

import javax.annotation.Nullable;
import java.util.List;

public interface IMuzzleModel extends IModel{
    /**
     * @return list of muzzle smoke release window.
     *         former is the position of muzzle smoke release window,
     *         latter is the motion of muzzle smoke.
     * */
    @Nullable
    List<Pair<Vector3f, Vector3f>> getMuzzleSmokeTransform();
}
