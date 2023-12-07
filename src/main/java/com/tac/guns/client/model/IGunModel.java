package com.tac.guns.client.model;

import com.mojang.math.Vector3f;
import com.tac.guns.client.util.Pair;

import javax.annotation.Nullable;

public interface IGunModel extends IModel{
    void setScope(IScopeModel scope);
    IScopeModel getScope();
    void setMuzzle(IMuzzleModel muzzle);
    IMuzzleModel getMuzzleModel();
    /**
     * @return the transform of gun model when aiming with iron sight.
     *         former is the translation,
     *         latter is the rotation.
     * */
    @Nullable Pair<Vector3f, Vector3f> getIronSightTransform();
    /**
     * @return former is the position of shell ejector.
     *         latter is the motion of shell to eject.
     * */
    @Nullable Pair<Vector3f, Vector3f> getShellEjectorTransform();
}
