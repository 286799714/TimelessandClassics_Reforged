package com.tac.guns.client.animation;

import com.tac.guns.client.util.Pair;

import java.util.List;

public interface AnimationListenerSupplier {
    List<Pair<String, AnimationListener>> supplyListeners();
}
