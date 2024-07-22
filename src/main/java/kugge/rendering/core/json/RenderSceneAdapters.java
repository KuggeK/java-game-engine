package kugge.rendering.core.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import kugge.rendering.core.objects.SkyBox;
import kugge.rendering.core.objects.SkyBox.SkyBoxType;
import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.meshes.Mesh;
import kugge.rendering.core.objects.meshes.Meshes;

public class RenderSceneAdapters {
    
    
    private static class SkyBoxAdapter implements JsonDeserializer<SkyBox>, JsonSerializer<SkyBox> {

        @Override
        public JsonElement serialize(SkyBox src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            if (src.getType() == SkyBoxType.SINGLE) {
                jsonObject.addProperty("type", SkyBoxType.SINGLE.toString());
                jsonObject.add("texture", context.serialize(src.getBaseTexture(), Texture.class));
            } else if (src.getType() == SkyBoxType.WRAPPED) {
                jsonObject.addProperty("type", SkyBoxType.WRAPPED.toString());
                jsonObject.add("texture", context.serialize(src.getBaseTexture(), Texture.class));
            } else {
                jsonObject.addProperty("type", SkyBoxType.MULTIPLE.toString());
                jsonObject.add("right", context.serialize(src.getRight(), Texture.class));
                jsonObject.add("left", context.serialize(src.getLeft(), Texture.class));
                jsonObject.add("top", context.serialize(src.getTop(), Texture.class));
                jsonObject.add("bottom", context.serialize(src.getBottom(), Texture.class));
                jsonObject.add("front", context.serialize(src.getFront(), Texture.class));
                jsonObject.add("back", context.serialize(src.getBack(), Texture.class));
            }
            return jsonObject;
        }

        @Override
        public SkyBox deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            SkyBoxType type = SkyBoxType.valueOf(root.get("type").getAsString());
            SkyBox skyBox;

            if (type == SkyBoxType.WRAPPED) {
                Texture texture = context.deserialize(root.get("texture"), Texture.class);
                skyBox = SkyBox.unwrapSkyboxTexture(texture);
            } else if (type == SkyBoxType.SINGLE) {
                Texture texture = context.deserialize(root.get("texture"), Texture.class);
                skyBox = new SkyBox(texture);
            } else {
                Texture right = context.deserialize(root.get("right"), Texture.class);
                Texture left = context.deserialize(root.get("left"), Texture.class);
                Texture top = context.deserialize(root.get("top"), Texture.class);
                Texture bottom = context.deserialize(root.get("bottom"), Texture.class);
                Texture front = context.deserialize(root.get("front"), Texture.class);
                Texture back = context.deserialize(root.get("back"), Texture.class);
                skyBox = new SkyBox(right, left, top, bottom, front, back, SkyBoxType.MULTIPLE);
            }
            return skyBox;
        }

    }

    private static class MeshAdapter implements JsonDeserializer<Mesh>, JsonSerializer<Mesh> {

        /**
         * If true, the mesh file will not be loaded and a dummy mesh will be created instead.
         */
        private boolean debug = false;

        public MeshAdapter(boolean debug) {
            this.debug = debug;
        }

        @Override
        public Mesh deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            
            JsonObject root = json.getAsJsonObject();
            // Get mesh file from the pathToMeshes folder
            
            JsonElement fileNameElement = root.get("fileName");
            String fileName;

            if (fileNameElement == null) {
                fileName = null;
            } else {
                fileName = fileNameElement.getAsString();
            }

            Mesh mesh;
            int meshID = root.get("ID").getAsInt();
            
            if (debug) {
                mesh = new Mesh(meshID, new float[3], new float[2], new float[3], new int[1]);
                mesh.setFileName(fileName);
            } else {

                if (Meshes.DEFAULT_MESHES.containsKey(meshID)) {
                    mesh = Meshes.DEFAULT_MESHES.get(meshID);
                } else {
                    if (fileName == null) {
                        throw new JsonParseException("Mesh file name is null");
                    }
                    try {
                        mesh = Meshes.loadMesh(fileName);
                        mesh.setID(root.get("ID").getAsInt());
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                        throw new JsonParseException(e);
                    }
                }
            }

            return mesh;
        }

        @Override
        public JsonElement serialize(Mesh src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ID", src.getID());
            jsonObject.addProperty("fileName", src.getFileName());
            
            return jsonObject;
        }
    }

    private static class TextureAdapter implements JsonDeserializer<Texture>, JsonSerializer<Texture> {

        /**
         * If true, the texture file will not be loaded and a dummy texture will be created instead.
         */
        private boolean debug = false;

        public TextureAdapter(boolean debug) {
            this.debug = debug;
        }

        @Override
        public Texture deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        
            JsonObject root = json.getAsJsonObject();
            String fileName = root.get("fileName").getAsString();
            Texture texture;
            if (debug) {
                texture = new Texture(2, 2, new int[4]);
            } else {
                try {
                    texture = Texture.loadTexture(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new JsonParseException(e);
                }
            }

            texture.setID(root.get("ID").getAsInt());
            return texture;
        }

        @Override
        public JsonElement serialize(Texture src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ID", src.getID());
            jsonObject.addProperty("fileName", src.getFileName());
            return jsonObject;
        }
    }

    public static void registerAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(SkyBox.class, new SkyBoxAdapter());
        builder.registerTypeAdapter(Mesh.class, new MeshAdapter(false));
        builder.registerTypeAdapter(Texture.class, new TextureAdapter(false));
    }
}
