package com.nao20010128nao.Wisecraft.misc;

import com.google.gson.*;

import java.lang.reflect.*;

/**
 * Created by nao on 2017/06/17.
 */
public class GsonModeConverter implements JsonDeserializer<Protobufs.Server.Mode>,JsonSerializer<Protobufs.Server.Mode>{
    @Override
    public Protobufs.Server.Mode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!json.isJsonPrimitive())throw new JsonParseException("Error: Not a primitive: "+json);
        JsonPrimitive prim=json.getAsJsonPrimitive();
        if(prim.isNumber()){
            return Protobufs.Server.Mode.forNumber(json.getAsInt());
        }else if(prim.isString()){
            return Protobufs.Server.Mode.valueOf(json.getAsString().toUpperCase());
        }else{
            throw new JsonParseException("Error: Denied value: "+json);
        }
    }

    @Override
    public JsonElement serialize(Protobufs.Server.Mode src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getNumber());
    }
}
