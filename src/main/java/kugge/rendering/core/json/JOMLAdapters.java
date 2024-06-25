package kugge.rendering.core.json;

import java.lang.reflect.Type;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JOMLAdapters {

    public static void registerAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(Vector3f.class, new Vector3fAdapter());
        builder.registerTypeAdapter(Vector4f.class, new Vector4fAdapter());
        builder.registerTypeAdapter(Quaternionf.class, new QuaternionfAdapter());
    }

    private static class Vector3fAdapter implements JsonSerializer<Vector3f>, JsonDeserializer<Vector3f> {
        @Override
        public JsonElement serialize(Vector3f src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(src.x);
            jsonArray.add(src.y);
            jsonArray.add(src.z);
            return jsonArray;
        }

        @Override
        public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            Vector3f vector = new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
            return vector;
        }
    }

    private static class Vector4fAdapter implements JsonSerializer<Vector4f>, JsonDeserializer<Vector4f> {
        @Override
        public JsonElement serialize(Vector4f src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(src.x);
            jsonArray.add(src.y);
            jsonArray.add(src.z);
            jsonArray.add(src.w);
            return jsonArray;
        }

        @Override
        public Vector4f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            Vector4f vector = new Vector4f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat());
            return vector;
        }
    }

    private static class QuaternionfAdapter implements JsonSerializer<Quaternionf>, JsonDeserializer<Quaternionf> {
        @Override
        public JsonElement serialize(Quaternionf src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(src.x);
            jsonArray.add(src.y);
            jsonArray.add(src.z);
            jsonArray.add(src.w);
            return jsonArray;
        }

        @Override
        public Quaternionf deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            Quaternionf quaternion = new Quaternionf(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat());
            return quaternion;
        }
    }
}
