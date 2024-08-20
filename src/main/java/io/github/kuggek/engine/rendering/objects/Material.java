package io.github.kuggek.engine.rendering.objects;

import org.joml.Vector4f;

public class Material {
    public static final int NO_ID = -1;

    private int ID;
    private String name;
    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f specular;
    private float shininess;

    public Material(int ID, float[] ambient, float[] diffuse, float[] specular, float shininess) {
        this.ID = ID;
        this.name = "Material" + ID;
        this.ambient = new Vector4f(ambient[0], ambient[1], ambient[2], ambient[3]);
        this.diffuse = new Vector4f(diffuse[0], diffuse[1], diffuse[2], diffuse[3]);
        this.specular = new Vector4f(specular[0], specular[1], specular[2], specular[3]);
        this.shininess = shininess;
    }

    public Material(int ID, Vector4f ambient, Vector4f diffuse, Vector4f specular, float shininess) {
        this.ID = ID;
        this.name = "Material" + ID;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    public Material(int ID, String name, Vector4f ambient, Vector4f diffuse, Vector4f specular, float shininess) {
        this.ID = ID;
        this.name = name;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public Vector4f getAmbient() {
        return ambient;
    }

    public void setAmbient(Vector4f ambient) {
        this.ambient = ambient;
    }

    public Vector4f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector4f diffuse) {
        this.diffuse = diffuse;
    }

    public Vector4f getSpecular() {
        return specular;
    }

    public void setSpecular(Vector4f specular) {
        this.specular = specular;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
}