package com.nao20010128nao.Wisecraft.misc;

import com.google.gson.*;
import com.nao20010128nao.Wisecraft.misc.json.*;

import java.lang.reflect.*;

/**
 * Created by nao on 2017/06/17.
 */
public class GsonModeConverter implements JsonDeserializer<Protobufs.Server.Mode>, JsonSerializer<Protobufs.Server.Mode> {
    @Override
    public Protobufs.Server.Mode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Utils.jsonElementToMode(WJOUtils.from(json));
    }

    @Override
    public JsonElement serialize(Protobufs.Server.Mode src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getNumber());
    }
}
