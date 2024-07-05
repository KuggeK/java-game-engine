package kugge.rendering.core.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import kugge.rendering.core.OpenGLConstants;
import kugge.rendering.core.objects.Camera;
import kugge.rendering.core.objects.Instance;
import kugge.rendering.core.objects.RenderScene;
import kugge.rendering.core.objects.SkyBox;
import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.Transform;
import kugge.rendering.core.objects.lights.DirectionalLight;
import kugge.rendering.core.objects.lights.PositionalLight;
import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.core.objects.meshes.Mesh;
import kugge.rendering.core.objects.meshes.Meshes;
import kugge.rendering.core.physics.PhysicsBody;
import kugge.rendering.core.physics.PhysicsCollider;
import kugge.rendering.core.physics.PhysicsWorld;
import kugge.rendering.core.physics.PhysicsCollider.ColliderType;

public class RenderSceneAdapters {    

    /**
     * Register the required adapters for the RenderScene class and its components. 
     * Also registers the required adapters for JOML classes to handle Vectors/Quaternions.
     * This method should be called before deserializing a RenderScene object.
     * @param builder
     * @param debugMode If true, the mesh and texture files will not be loaded and dummy objects will be created instead.
     */
    public static void registerAdapters(GsonBuilder builder, boolean debugMode) {
        JOMLAdapters.registerAdapters(builder);
        builder.registerTypeAdapter(Transform.class, new TransformAdapter());
        builder.registerTypeAdapter(Instance.class, new InstanceAdapter());
        builder.registerTypeAdapter(Mesh.class, new MeshAdapter(debugMode));
        builder.registerTypeAdapter(Texture.class, new TextureAdapter(debugMode));
        builder.registerTypeAdapter(RenderScene.class, new RenderSceneAdapter());
        builder.registerTypeAdapter(SkyBox.class, new SkyBoxAdapter());
        builder.registerTypeAdapter(PhysicsBody.class, new PhysicsBodyAdapter());
        builder.registerTypeAdapter(PhysicsCollider.class, new PhysicsColliderAdapter());
        builder.registerTypeAdapter(PhysicsWorld.class, new PhysicsWorldAdapter());
    }

    /**
     * Register the adapters for the RenderScene class and its components. 
     * This method should be called before deserializing a RenderScene object.
     * @param builder
     */
    public static void registerAdapters(GsonBuilder builder) {
        registerAdapters(builder, false);
    }

    /**
     * Helper method to deserialize an array of elements to a list of elements of type T.
     * Type T must be a class that can be deserialized by the context. 
     * @param <T> The type of the elements in the list
     * @param to The type of the elements in the list
     * @param array The JSON array to deserialize
     * @param context The deserialization context
     * @return A list of elements of type T
     */
    private static <T> List<T> deserializeArrayTo(Type to, JsonArray array, JsonDeserializationContext context) {
        List<T> elements = new ArrayList<>();
        for (JsonElement element : array) {
            elements.add(context.deserialize(element, to));
        }
        return elements;
    }

    private static class InstanceAdapter implements JsonSerializer<Instance> {
        @Override
        public JsonElement serialize(Instance src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("meshID", src.getMeshID());
            jsonObject.addProperty("materialID", src.getMaterialID());
            jsonObject.addProperty("textureIndex", src.getTextureIndex());
            jsonObject.add("transform", context.serialize(src.getTransform(), Transform.class));
            jsonObject.addProperty("bodyID", src.getBodyID());
            jsonObject.addProperty("colliderID", src.getColliderID());
            return jsonObject;
        }
    }

    private static class RenderSceneAdapter implements JsonDeserializer<RenderScene> {

        @Override
        public RenderScene deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            
            JsonObject root = json.getAsJsonObject();

            int ID = root.get("ID").getAsInt();
            Camera camera = context.deserialize(root.get("camera"), Camera.class);
            List<Mesh> meshes = deserializeArrayTo(Mesh.class, root.get("meshes").getAsJsonArray(), context);
            List<Instance> meshInstances = deserializeArrayTo(Instance.class, root.get("meshInstances").getAsJsonArray(), context);
            List<Material> materials = deserializeArrayTo(Material.class, root.get("materials").getAsJsonArray(), context);
            List<Texture> textures = deserializeArrayTo(Texture.class, root.get("textures").getAsJsonArray(), context);
            List<PositionalLight> positionalLights = deserializeArrayTo(PositionalLight.class, root.get("positionalLights").getAsJsonArray(), context);
            DirectionalLight directionalLight = context.deserialize(root.get("directionalLight"), DirectionalLight.class);
            Vector4f globalAmbient = context.deserialize(root.get("globalAmbient"), Vector4f.class);
            PhysicsWorld physicsWorld = context.deserialize(root.get("physicsWorld"), PhysicsWorld.class);
            physicsWorld = physicsWorld == null ? new PhysicsWorld() : physicsWorld;
            SkyBox skyBox = context.deserialize(root.get("skyBox"), SkyBox.class);

            for (Instance instance: meshInstances) {
                int bodyID = instance.getBodyID();
                int colliderID = instance.getColliderID();
                if (bodyID != -1 && colliderID != -1) {
                    physicsWorld.linkBodyAndCollider(bodyID, colliderID);
                    physicsWorld.getCollider(colliderID).updateScale(instance.getTransform().getScale());
                }
            }
            
            RenderScene scene = new RenderScene(ID, camera, meshes, meshInstances, materials, textures, positionalLights, globalAmbient, directionalLight, physicsWorld, skyBox);
            return scene;
        }

    }

    private static class SkyBoxAdapter implements JsonDeserializer<SkyBox>, JsonSerializer<SkyBox> {

        @Override
        public JsonElement serialize(SkyBox src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("right", context.serialize(src.getRight(), Texture.class));
            jsonObject.add("left", context.serialize(src.getLeft(), Texture.class));
            jsonObject.add("top", context.serialize(src.getTop(), Texture.class));
            jsonObject.add("bottom", context.serialize(src.getBottom(), Texture.class));
            jsonObject.add("front", context.serialize(src.getFront(), Texture.class));
            jsonObject.add("back", context.serialize(src.getBack(), Texture.class));
            return jsonObject;
        }

        @Override
        public SkyBox deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            Texture right = context.deserialize(root.get("right"), Texture.class);
            Texture left = context.deserialize(root.get("left"), Texture.class);
            Texture top = context.deserialize(root.get("top"), Texture.class);
            Texture bottom = context.deserialize(root.get("bottom"), Texture.class);
            Texture front = context.deserialize(root.get("front"), Texture.class);
            Texture back = context.deserialize(root.get("back"), Texture.class);
            return new SkyBox(right, left, top, bottom, front, back);
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


            // Deserialize texture parameters from the constant names to their integer values (e.g. GL_TEXTURE_WRAP_S -> 10242)
            Map<String, String> textureParametersString = context.deserialize(root.get("textureParameters").getAsJsonObject(), HashMap.class);
            Map<Integer, Integer> textureParameters = textureParametersString.entrySet().stream().collect(Collectors.toMap(
                e -> OpenGLConstants.getValue(e.getKey()),
                e -> OpenGLConstants.getValue(e.getValue())
            ));
            mesh.setTextureParameters(textureParameters);

            List<Integer> textureIDs = deserializeArrayTo(Integer.class, root.get("textureIDs").getAsJsonArray(), context);
            mesh.setTextureIDs(textureIDs);
            return mesh;
        }

        @Override
        public JsonElement serialize(Mesh src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ID", src.getID());
            jsonObject.addProperty("fileName", src.getFileName());
            jsonObject.add("textureIDs", context.serialize(src.getTextureIDs()));
            
            // Serialize texture parameters from the integer values to their constant names (e.g. 10242 -> GL_TEXTURE_WRAP_S)
            Map<String, String> textureParametersString = src.getTextureParameters().entrySet().stream().collect(Collectors.toMap(
                e -> OpenGLConstants.getConstant(e.getKey()),
                e -> OpenGLConstants.getConstant(e.getValue())
            ));
            jsonObject.add("textureParameters", context.serialize(textureParametersString));
            
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

    private static class TransformAdapter implements JsonSerializer<Transform> {

        @Override
        public JsonElement serialize(Transform src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("position", context.serialize(src.getPosition()));
            jsonObject.add("rotation", context.serialize(src.getRotation()));
            jsonObject.add("scale", context.serialize(src.getScale()));
            return jsonObject;
        }
    }
    
    private static class PhysicsBodyAdapter implements JsonSerializer<PhysicsBody> {

        @Override
        public JsonElement serialize(PhysicsBody src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ID", src.getID());
            jsonObject.addProperty("isKinematic", src.isKinematic());
            jsonObject.addProperty("restitution", src.getRestitution());
            jsonObject.addProperty("mass", src.getMass());
            return jsonObject;
        }
    }

    public static class PhysicsColliderAdapter implements JsonSerializer<PhysicsCollider>, JsonDeserializer<PhysicsCollider> {

        @Override
        public JsonElement serialize(PhysicsCollider src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ID", src.getID());
            jsonObject.addProperty("colliderType", src.getColliderType().toString());
            return jsonObject;
        }

        @Override
        public PhysicsCollider deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            int ID = root.get("ID").getAsInt();
            String colliderTypeString = root.get("colliderType").getAsString();
            ColliderType colliderType = ColliderType.valueOf(colliderTypeString);
            return new PhysicsCollider(ID, colliderType);
        }
    }

    public static class PhysicsWorldAdapter implements JsonSerializer<PhysicsWorld>, JsonDeserializer<PhysicsWorld> {

        @Override
        public JsonElement serialize(PhysicsWorld src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("gravity", context.serialize(src.getGravity()));

            JsonArray bodies = new JsonArray();
            for (PhysicsBody body : src.getBodies()) {
                bodies.add(context.serialize(body));
            }
            jsonObject.add("bodies", bodies);

            JsonArray colliders = new JsonArray();
            for (PhysicsCollider collider : src.getColliders()) {
                colliders.add(context.serialize(collider));
            }
            jsonObject.add("colliders", colliders);
            return jsonObject;
        }

        @Override
        public PhysicsWorld deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            
            PhysicsWorld world = new PhysicsWorld();
            Vector3f gravity = context.deserialize(root.get("gravity"), Vector3f.class);
            world.setGravity(gravity);

            JsonArray bodiesArray = root.get("bodies").getAsJsonArray();
            for (JsonElement bodyElement : bodiesArray) {
                JsonObject bodyObject = bodyElement.getAsJsonObject();
                int ID = bodyObject.get("ID").getAsInt();
                boolean isKinematic = bodyObject.get("isKinematic").getAsBoolean();
                double restitution = bodyObject.get("restitution").getAsDouble();
                double mass = bodyObject.get("mass").getAsDouble();
                PhysicsBody body = world.addBody(ID, isKinematic);
                body.setRestitution(restitution);
                body.setMass(mass);
            }

            List<PhysicsCollider> colliders = deserializeArrayTo(PhysicsCollider.class, root.get("colliders").getAsJsonArray(), context);
            for (PhysicsCollider collider : colliders) {
                world.addCollider(collider);
            }
            
            return world;
        }

    } 

}
