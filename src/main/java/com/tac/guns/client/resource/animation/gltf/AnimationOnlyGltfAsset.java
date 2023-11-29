package com.tac.guns.client.resource.animation.gltf;

import java.util.List;

public class AnimationOnlyGltfAsset {
    private List<Accessor> accessors;
    private List<Animation> animations;
    private List<Buffer> buffers;
    private List<BufferView> bufferViews;
    private List<Node> nodes;

    public List<Accessor> getAccessors() {
        return this.accessors;
    }

    public List<Animation> getAnimations() {
        return animations;
    }

    public List<Buffer> getBuffers() {
        return buffers;
    }

    public List<BufferView> getBufferViews() {
        return bufferViews;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setAccessors(List<Accessor> accessors) {
        this.accessors = accessors;
    }

    public void setAnimations(List<Animation> animations) {
        this.animations = animations;
    }

    public void setBuffers(List<Buffer> buffers) {
        this.buffers = buffers;
    }

    public void setBufferViews(List<BufferView> bufferViews) {
        this.bufferViews = bufferViews;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
