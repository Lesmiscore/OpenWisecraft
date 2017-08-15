package com.nao20010128nao.Wisecraft.asfsls.misc.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Reader;

public class WJOUtils {
    public static WisecraftJsonObject from(JsonElement je) {
        return new GsonWJO(je);
    }


    public static WisecraftJsonObject parse(String json) {
        return from(new JsonParser().parse(json));
    }

    public static WisecraftJsonObject parse(Reader json) {
        return from(new JsonParser().parse(json));
    }
}
