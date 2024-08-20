package io.github.kuggek.engine.ecs.components.rendering;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

import io.github.kuggek.engine.core.Transform;
import io.github.kuggek.engine.ecs.GameComponent;
import io.github.kuggek.engine.ecs.components.EditableComponentField;
import io.github.kuggek.engine.rendering.objects.RenderInstance;

public class RenderInstanceComponent extends GameComponent implements RenderInstance {
    @EditableComponentField
    private int meshID;

    @EditableComponentField
    private int materialID;

    @EditableComponentField
    private boolean texturingEnabled;

    @EditableComponentField
    private int textureID;

    @EditableComponentField
    private boolean normalMapEnabled;
    
    @EditableComponentField
    private int normalMapID;

    @EditableComponentField
    private boolean lit;

    @EditableComponentField
    private boolean castsShadows;

    @EditableComponentField
    private Map<Integer, Integer> textureParameters;

    public RenderInstanceComponent() {
        super();
        this.meshID = -1;
        this.materialID = -1;
        this.texturingEnabled = false;
        this.textureID = -1;
        this.normalMapEnabled = false;
        this.normalMapID = -1;
        this.lit = true;
        this.castsShadows = true;
        this.textureParameters = new HashMap<>();
    }

    public Matrix4f getModelMatrix() {
        return transform.getModelMatrix();
    }

    public int getMeshID() {
        return meshID;
    }

    public void setMeshID(int meshID) {
        this.meshID = meshID;
    }

    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public boolean isTexturingEnabled() {
        return texturingEnabled;
    }

    public void setTexturingEnabled(boolean texturingEnabled) {
        this.texturingEnabled = texturingEnabled;
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public Transform getTransform() {
        return transform;
    }

    public boolean isNormalMapEnabled() {
        return normalMapEnabled;
    }

    public void setNormalMapEnabled(boolean normalMapEnabled) {
        this.normalMapEnabled = normalMapEnabled;
    }

    public int getNormalMapID() {
        return normalMapID;
    }

    public void setNormalMapID(int normalMapID) {
        this.normalMapID = normalMapID;
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    public boolean castsShadows() {
        return castsShadows;
    }

    public void setCastsShadows(boolean castsShadows) {
        this.castsShadows = castsShadows;
    }

    public Map<Integer, Integer> getTextureParameters() {
        return textureParameters;
    }

    public void addTextureParameter(int key, int value) {
        textureParameters.put(key, value);
    }

    public void removeTextureParameter(int key) {
        textureParameters.remove(key);
    }

    public void clearTextureParameters() {
        textureParameters.clear();
    }
}
