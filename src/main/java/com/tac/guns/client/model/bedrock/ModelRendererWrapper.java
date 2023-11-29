package com.tac.guns.client.model.bedrock;


import com.mojang.math.Quaternion;

public class ModelRendererWrapper implements IModelRenderer {
    private final BedrockPart modelRenderer;

    public ModelRendererWrapper(BedrockPart modelRenderer) {
        this.modelRenderer = modelRenderer;
    }

    public BedrockPart getModelRenderer() {
        return modelRenderer;
    }

    @Override
    public float getRotateAngleX() {
        return modelRenderer.xRot;
    }

    @Override
    public void setRotateAngleX(float xRot) {
        modelRenderer.xRot = xRot;
    }

    @Override
    public float getRotateAngleY() {
        return modelRenderer.yRot;
    }

    @Override
    public void setRotateAngleY(float yRot) {
        modelRenderer.yRot = yRot;
    }

    @Override
    public float getRotateAngleZ() {
        return modelRenderer.zRot;
    }

    @Override
    public void setRotateAngleZ(float zRot) {
        modelRenderer.zRot = zRot;
    }

    @Override
    public float getOffsetX() {
        return modelRenderer.offsetX;
    }

    @Override
    public void setOffsetX(float offsetX) {
        modelRenderer.offsetX = offsetX;
    }

    @Override
    public float getOffsetY() {
        return modelRenderer.offsetY;
    }

    @Override
    public void setOffsetY(float offsetY) {
        modelRenderer.offsetY = offsetY;
    }

    @Override
    public float getOffsetZ() {
        return modelRenderer.offsetZ;
    }

    @Override
    public void setOffsetZ(float offsetZ) {
        modelRenderer.offsetZ = offsetZ;
    }

    @Override
    public float getRotationPointX() {
        return modelRenderer.x;
    }

    @Override
    public float getRotationPointY() {
        return modelRenderer.y;
    }

    @Override
    public float getRotationPointZ() {
        return modelRenderer.z;
    }

    @Override
    public boolean isHidden() {
        return !modelRenderer.visible;
    }

    @Override
    public void setHidden(boolean hidden) {
        modelRenderer.visible = !hidden;
    }

    @Override
    public float getInitRotateAngleX() {
        return modelRenderer.getInitRotX();
    }

    @Override
    public float getInitRotateAngleY() {
        return modelRenderer.getInitRotY();
    }

    @Override
    public float getInitRotateAngleZ() {
        return modelRenderer.getInitRotZ();
    }

    @Override
    public void setAdditionalQuaternion(Quaternion quaternion) {
        modelRenderer.additionalQuaternion = quaternion;
    }

    @Override
    public Quaternion getAdditionalQuaternion() {
        return modelRenderer.additionalQuaternion;
    }

    @Override
    public void setScaleX(float scaleX) {
        modelRenderer.xScale = scaleX;
    }

    @Override
    public float getScaleX() {
        return modelRenderer.xScale;
    }

    @Override
    public void setScaleY(float scaleY) {
        modelRenderer.yScale = scaleY;
    }

    @Override
    public float getScaleY() {
        return modelRenderer.yScale;
    }

    @Override
    public void setScaleZ(float scaleZ) {
        modelRenderer.zScale = scaleZ;
    }

    @Override
    public float getScaleZ() {
        return modelRenderer.zScale;
    }
}
