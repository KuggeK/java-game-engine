package io.github.kuggek.engine.core.assets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import io.github.kuggek.engine.rendering.objects.Material;
import io.github.kuggek.engine.rendering.objects.Mesh;
import io.github.kuggek.engine.rendering.objects.Texture;

public class SQLiteAssetManager implements AssetManager {

    private static SQLiteAssetManager instance;
    private static Connection conn;

    private SQLiteAssetManager() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:assets.db");
            initTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initTables() { 
        try {
            String[] tables = ResourceManager.listFilesIn("db/tables/");
            
            for (String table : tables) {
                System.out.println(table);
                String query = ResourceManager.readFile(table);
                conn.createStatement().execute(query);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static SQLiteAssetManager getInstance() {
        if (instance == null) {
            instance = new SQLiteAssetManager();
        }
        return instance;
    }

    @Override
    public void deleteMesh(int ID) throws Exception {
        String query = fetchQuery("deleteMesh.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1, ID);
        statement.executeUpdate();
    }

    @Override
    public void deleteTexture(int ID) throws Exception {
        String query = fetchQuery("deleteTexture.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1, ID);
        statement.executeUpdate();
    }

    @Override
    public Mesh fetchMesh(int ID) throws Exception {
        String query = fetchQuery("fetchMesh.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1, ID);
        ResultSet res = statement.executeQuery();
        if (!res.isBeforeFirst()) {
            throw new SQLException("Mesh not found in database");
        }
        Mesh mesh = resultSetToMesh(res);
        res.close();
        return mesh;
    }

    @Override
    public List<Mesh> fetchAllMeshes() throws Exception {
        String query = fetchQuery("fetchAllMeshes.sql");
        ResultSet res = conn.createStatement().executeQuery(query);
        if (!res.isBeforeFirst()) {
            throw new SQLException("No meshes found in database");
        }
        List<Mesh> meshes = new ArrayList<>();
        while (res.next()) {
            meshes.add(resultSetToMesh(res));
        }
        res.close();
        return meshes;
    }

    @Override
    public Texture fetchTexture(int ID) throws Exception {
        String query = fetchQuery("fetchTexture.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1, ID);
        ResultSet res = statement.executeQuery();
        if (!res.isBeforeFirst()) {
            throw new SQLException("Texture not found in database");
        }
        Texture tex = resultSetToTexture(res);
        res.close();
        return tex;
    }

    @Override
    public List<Texture> fetchAllTextures() throws Exception {
        String query = fetchQuery("fetchAllTextures.sql");
        ResultSet res = conn.createStatement().executeQuery(query);

        if (!res.isBeforeFirst()) {
            throw new SQLException("No textures found in database");
        }

        List<Texture> textures = new ArrayList<>();
        while (res.next()) {
            textures.add(resultSetToTexture(res));
        }
        res.close();
        return textures;
    }

    @Override
    public void saveMesh(Mesh mesh) throws Exception {
        String query = fetchQuery("saveMesh.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        
        ByteBuffer positionsBuffer = ByteBuffer.allocate(mesh.getPositions().length * Float.BYTES);
        ByteBuffer textureCoordsBuffer = ByteBuffer.allocate(mesh.getTextureCoords().length * Float.BYTES);
        ByteBuffer normalsBuffer = ByteBuffer.allocate(mesh.getNormals().length * Float.BYTES);
        ByteBuffer indicesBuffer = ByteBuffer.allocate(mesh.getIndices().length * Integer.BYTES);

        for (float f : mesh.getPositions()) {
            positionsBuffer.putFloat(f);
        }
        for (float f : mesh.getTextureCoords()) {
            textureCoordsBuffer.putFloat(f);
        }
        for (float f : mesh.getNormals()) {
            normalsBuffer.putFloat(f);
        }
        for (int i : mesh.getIndices()) {
            indicesBuffer.putInt(i);
        }

        statement.setBytes(1, positionsBuffer.array());
        statement.setBytes(2, textureCoordsBuffer.array());
        statement.setBytes(3, normalsBuffer.array());
        statement.setBytes(4, indicesBuffer.array()); 
        
        if (mesh.getTangents() != null) {
            ByteBuffer tangentsBuffer = ByteBuffer.allocate(mesh.getTangents().length * Float.BYTES);
            for (float f : mesh.getTangents()) {
                tangentsBuffer.putFloat(f);
            }
            statement.setBytes(5, tangentsBuffer.array());
        } else {
            statement.setBytes(5, null);
        }

        ResultSet res = statement.executeQuery();
        mesh.setID(res.getInt("id"));
        res.close();
    }

    @Override
    public void saveTexture(Texture texture) throws Exception {
        String query = fetchQuery("saveTexture.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        
        ByteBuffer dataBuffer = ByteBuffer.allocate(
            texture.getPixels().length * Integer.BYTES
        );

        for (int i : texture.getPixels()) {
            dataBuffer.putInt(i);
        }

        statement.setString(1, texture.getFileName());
        statement.setInt(2, texture.getWidth());
        statement.setInt(3, texture.getHeight());
        statement.setBytes(4, dataBuffer.array());

        ResultSet res = statement.executeQuery();
        texture.setID(res.getInt("id"));
        res.close();
    }

    private String fetchQuery(String queryName) {
        try {
            return ResourceManager.readFile("db/queries/" + queryName);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Texture resultSetToTexture(ResultSet res) throws SQLException {
        int ID = res.getInt("id");
        String fileName = res.getString("file_name");
        int width = res.getInt("width");
        int height = res.getInt("height");

        byte[] pixelsByte = res.getBytes("pixels");
        int[] pixels = new int[pixelsByte.length / Integer.BYTES];
        for (int i = 0; i < pixelsByte.length; i += 4) {
            pixels[i / 4] = ByteBuffer.wrap(pixelsByte, i, 4).getInt();
        }
     
        return new Texture(ID, fileName, width, height, pixels);
    }

    private Mesh resultSetToMesh(ResultSet res) throws SQLException {
        int ID = res.getInt("id");

        byte[] positionsByte = res.getBytes("positions");
        float[] positions = new float[positionsByte.length / Float.BYTES];
        for (int i = 0; i < positionsByte.length; i += 4) {
            positions[i / 4] = ByteBuffer.wrap(positionsByte, i, 4).getFloat();
        }

        byte[] textureCoordsByte = res.getBytes("texture_coordinates");
        float[] textureCoords = new float[textureCoordsByte.length / Float.BYTES];
        for (int i = 0; i < textureCoordsByte.length; i += 4) {
            textureCoords[i / 4] = ByteBuffer.wrap(textureCoordsByte, i, 4).getFloat();
        }

        byte[] normalsByte = res.getBytes("normals");
        float[] normals = new float[normalsByte.length / Float.BYTES];
        for (int i = 0; i < normalsByte.length; i += 4) {
            normals[i / 4] = ByteBuffer.wrap(normalsByte, i, 4).getFloat();
        }

        byte[] indicesByte = res.getBytes("indices");
        int[] indices = new int[indicesByte.length / Integer.BYTES];
        for (int i = 0; i < indicesByte.length; i += 4) {
            indices[i / 4] = ByteBuffer.wrap(indicesByte, i, 4).getInt();
        }

        byte[] tangentsByte = res.getBytes("tangents");
        float[] tangents = null;
        if (tangentsByte != null) {
            tangents = new float[tangentsByte.length / Float.BYTES];
            for (int i = 0; i < tangentsByte.length; i += 4) {
                tangents[i / 4] = ByteBuffer.wrap(tangentsByte, i, 4).getFloat();
            }
        }

        Mesh mesh = new Mesh(ID, positions, textureCoords, normals, indices);
        mesh.setTangents(tangents);
        return mesh;
    }



    @Override
    public void updateMesh(int ID, Mesh mesh) throws Exception {
        String query = fetchQuery("updateMesh.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        
        ByteBuffer positionsBuffer = ByteBuffer.allocate(mesh.getPositions().length * Float.BYTES);
        ByteBuffer textureCoordsBuffer = ByteBuffer.allocate(mesh.getTextureCoords().length * Float.BYTES);
        ByteBuffer normalsBuffer = ByteBuffer.allocate(mesh.getNormals().length * Float.BYTES);
        ByteBuffer indicesBuffer = ByteBuffer.allocate(mesh.getIndices().length * Integer.BYTES);

        for (float f : mesh.getPositions()) {
            positionsBuffer.putFloat(f);
        }
        for (float f : mesh.getTextureCoords()) {
            textureCoordsBuffer.putFloat(f);
        }
        for (float f : mesh.getNormals()) {
            normalsBuffer.putFloat(f);
        }
        for (int i : mesh.getIndices()) {
            indicesBuffer.putInt(i);
        }

        statement.setBytes(1, positionsBuffer.array());
        statement.setBytes(2, textureCoordsBuffer.array());
        statement.setBytes(3, normalsBuffer.array());
        statement.setBytes(4, indicesBuffer.array());
        statement.setInt(5, ID);
        if (mesh.getTangents() != null) {
            ByteBuffer tangentsBuffer = ByteBuffer.allocate(mesh.getTangents().length * Float.BYTES);
            for (float f : mesh.getTangents()) {
                tangentsBuffer.putFloat(f);
            }
            statement.setBytes(6, tangentsBuffer.array());
        } else {
            statement.setBytes(6, null);
        }

        statement.executeUpdate();
    }

    @Override
    public void updateTexture(int ID, Texture texture) throws Exception {
        String query = fetchQuery("updateTexture.sql");

        PreparedStatement statement = conn.prepareStatement(query);

        ByteBuffer dataBuffer = ByteBuffer.allocate(
            texture.getPixels().length * Integer.BYTES
        );

        for (int i : texture.getPixels()) {
            dataBuffer.putInt(i);
        }

        statement.setString(1, texture.getFileName());
        statement.setInt(2, texture.getWidth());
        statement.setInt(3, texture.getHeight());
        statement.setBytes(4, dataBuffer.array());
        statement.setInt(5, ID);
    }

    @Override
    public void saveMaterial(Material material) throws Exception {
        String query = fetchQuery("saveMaterial.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        
        statement.setBytes(1, vectorToBytes(material.getAmbient()));
        statement.setBytes(2, vectorToBytes(material.getDiffuse()));
        statement.setBytes(3, vectorToBytes(material.getSpecular()));
        statement.setFloat(4, material.getShininess());

        ResultSet res = statement.executeQuery();
        material.setID(res.getInt("id"));
        res.close();
    }

    @Override
    public void updateMaterial(int ID, Material material) throws Exception {
        String query = fetchQuery("updateMaterial.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        
        statement.setBytes(1, vectorToBytes(material.getAmbient()));
        statement.setBytes(2, vectorToBytes(material.getDiffuse()));
        statement.setBytes(3, vectorToBytes(material.getSpecular()));
        statement.setFloat(4, material.getShininess());
        statement.setInt(5, ID);

        statement.executeUpdate();
    }

    @Override
    public Material fetchMaterial(int ID) throws Exception {
        String query = fetchQuery("fetchMaterial.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1, ID);
        ResultSet res = statement.executeQuery();
        if (!res.isBeforeFirst()) {
            throw new SQLException("Material not found in database");
        }
        Material material = resultSetToMaterial(res);
        res.close();
        return material;
    }

    @Override
    public List<Material> fetchAllMaterials() throws Exception {
        String query = fetchQuery("fetchAllMaterials.sql");
        ResultSet res = conn.createStatement().executeQuery(query);
        if (!res.isBeforeFirst()) {
            throw new SQLException("No materials found in database");
        }
        List<Material> materials = new ArrayList<>();
        while (res.next()) {
            materials.add(resultSetToMaterial(res));
        }
        res.close();
        return materials;
    }

    private Material resultSetToMaterial(ResultSet res) throws SQLException {
        int ID = res.getInt("id");
        Vector4f ambient = bytesToVector(res.getBytes("ambient"));
        Vector4f diffuse = bytesToVector(res.getBytes("diffuse"));
        Vector4f specular = bytesToVector(res.getBytes("specular"));
        float shininess = res.getFloat("shininess");
        return new Material(ID, ambient, diffuse, specular, shininess);
    }

    @Override
    public void deleteMaterial(int ID) throws Exception {
        String query = fetchQuery("deleteMaterial.sql");
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1, ID);
        statement.executeUpdate();
    }

    private byte[] vectorToBytes(Vector4f vector) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * Float.BYTES);
        buffer.putFloat(vector.x);
        buffer.putFloat(vector.y);
        buffer.putFloat(vector.z);
        buffer.putFloat(vector.w);
        return buffer.array();
    }

    private Vector4f bytesToVector(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new Vector4f(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
    }
}
