package kugge.engine.core.json;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import kugge.engine.core.Transform;
import kugge.engine.ecs.GameComponent;
import kugge.engine.ecs.GameObject;
import kugge.engine.ecs.GameScene;
import kugge.engine.scripting.Script;
import kugge.engine.scripting.ScriptLoader;

public class GameSceneAdapters {    

    /**
     * Register the required adapters for the GameScene class and its components. 
     * Also registers the required adapters for JOML classes to handle Vectors/Quaternions.
     * This method should be called before deserializing a GameScene object.
     * @param builder
     * @param debugMode If true, the mesh and texture files will not be loaded and dummy objects will be created instead.
     */
    public static void registerAdapters(GsonBuilder builder) {
        JOMLAdapters.registerAdapters(builder);
        builder.registerTypeAdapter(Transform.class, new TransformAdapter());
        builder.registerTypeAdapter(GameComponent.class, new GameComponentAdapter());
        builder.registerTypeAdapter(GameObject.class, new GameObjectAdapter());
        builder.registerTypeAdapter(GameScene.class, new GameSceneAdapter());
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

    private static class GameComponentAdapter implements JsonSerializer<GameComponent> {

        @Override
        public JsonElement serialize(GameComponent src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            Map<String, Object> fields = GameComponent.getComponentFieldValues(src);

            for (var entry : fields.entrySet()) {
                jsonObject.add(entry.getKey(), context.serialize(entry.getValue()));
            }

            return jsonObject;
        }
    }

    private static class GameObjectAdapter implements JsonSerializer<GameObject>, JsonDeserializer<GameObject> {

        @Override
        public JsonElement serialize(GameObject src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ID", src.getID());
            jsonObject.add("transform", context.serialize(src.getTransform()));
            jsonObject.add("tags", context.serialize(src.getTags()));

            JsonObject componentObject = new JsonObject();
            for (GameComponent component : src.getComponents()) {
                componentObject.add(component.getClass().getName(), context.serialize(component, GameComponent.class));
            }
            jsonObject.add("components", componentObject);

            jsonObject.add("children", context.serialize(src.getChildren()));

            return jsonObject;
        }

        @Override
        public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject root = json.getAsJsonObject();

            int ID = root.get("ID").getAsInt();
            Transform transform = context.deserialize(root.get("transform"), Transform.class);
            Set<String> tags = context.deserialize(root.get("tags"), Set.class);

            GameObject gameObject = new GameObject(ID);

            for (var entry : root.get("components").getAsJsonObject().entrySet()) {
                try {
                    Class<? extends GameComponent> componentClass = (Class<? extends GameComponent>) Class.forName(entry.getKey());
                    GameComponent component = context.deserialize(entry.getValue(), componentClass);
                    gameObject.addComponent(component, true);
                } catch (ClassNotFoundException e) {
                    System.out.println(e.getMessage());

                   // If the class is not found, it might be a script, which needs to be loaded from the script loader.
                    try {
                        Script script = ScriptLoader.loadScript(entry.getKey());
                        gameObject.addComponent(script, true);
                    } catch (Exception e2) {
                        System.out.println("Could not load script: " + entry.getKey());
                    }
                }
            }

            for (var childEntry : root.get("children").getAsJsonArray()) {
                GameObject child = context.deserialize(childEntry, GameObject.class);
                GameObject.link(gameObject, child);
            }

            gameObject.setTransform(transform);
            if (tags != null) {
                for (String tag : tags) {
                    gameObject.addTag(tag);
                }
            }
            

            return gameObject;
        }
    }

    private static class GameSceneAdapter implements JsonSerializer<GameScene>, JsonDeserializer<GameScene> {

        @Override
        public JsonElement serialize(GameScene src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ID", src.getID());
            jsonObject.add("gameObjects", context.serialize(src.getGameObjects()));
            return jsonObject;
        }

        @Override
        public GameScene deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            GameScene scene = new GameScene(root.get("ID").getAsInt(), root.get("name").getAsString());

            for (JsonElement element : root.get("gameObjects").getAsJsonArray()) {
                GameObject gameObject = context.deserialize(element, GameObject.class);
                scene.addGameObject(gameObject);
            }

            return scene;
        }
    }
}
