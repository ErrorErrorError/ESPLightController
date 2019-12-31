package com.errorerrorerror.esplightcontroller.utils;

import androidx.room.TypeConverter;

import com.errorerrorerror.esplightcontroller.data.modes.BaseMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class Converters {
    private static final JsonSerializer<BaseMode> serializer = (src, typeOfSrc, context) -> {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src));
        return result;
    };


    private static final JsonDeserializer<BaseMode> deserializer = (json, typeOfT, context) -> {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(BaseMode.class.getPackage().getName() + "." + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    };

    @TypeConverter
    public static String modeToString(BaseMode mode) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BaseMode.class, serializer)
                .create();

        return gson.toJson(mode, BaseMode.class);
    }

    @TypeConverter
    public static BaseMode stringToMode(String string) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BaseMode.class, deserializer)
                .create();

        return gson.fromJson(string, BaseMode.class);
    }
}
