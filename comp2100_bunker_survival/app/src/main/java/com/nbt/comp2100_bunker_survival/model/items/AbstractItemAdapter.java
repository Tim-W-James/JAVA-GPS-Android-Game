package com.nbt.comp2100_bunker_survival.model.items;

import com.google.gson.*;
import java.lang.reflect.Type;

// DERIVED FROM: https://ovaraksin.blogspot.com/2011/05/json-with-gson-and-abstract-classes.html
// class to handle gson serialization/deserialization for abstract items
public class AbstractItemAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName("com.nbt.comp2100_bunker_survival.model.items." + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }
}