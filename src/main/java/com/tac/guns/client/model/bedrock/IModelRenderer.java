package com.tac.guns.client.model.bedrock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public interface IModelRenderer {
    void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay);
}
