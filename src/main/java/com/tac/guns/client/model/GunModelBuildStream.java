package com.tac.guns.client.model;

import com.mojang.math.Vector3f;
import com.tac.guns.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.HumanoidArm;

public class GunModelBuildStream {
    protected BedrockAnimatedModel model;

    public GunModelBuildStream(BedrockAnimatedModel model){
        this.model = model;
    }

    public BedrockAnimatedModel getModel(){
        return model;
    }

    public GunModelBuildStream replaceHandsRendering(){
        model.setFunctionalRenderer("LeftHand", bedrockPart -> (poseStack, consumer, light, overlay) -> {
            //do it because transform data from bedrock model is upside down
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180f));
            RenderUtil.renderFirstPersonArm(Minecraft.getInstance().player, HumanoidArm.LEFT, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), light);
        });
        model.setFunctionalRenderer("RightHand", bedrockPart -> (poseStack, consumer, light, overlay) -> {
            //do it because transform data from bedrock model is upside down
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180f));
            RenderUtil.renderFirstPersonArm(Minecraft.getInstance().player, HumanoidArm.RIGHT, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), light);
        });
        return this;
    }
}
