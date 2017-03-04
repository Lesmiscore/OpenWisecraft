package com.nao20010128nao.Wisecraft.misc.json;

import com.google.gson.*;
import java.io.*;

public class WJOUtils 
{
	public static WisecraftJsonObject from(JsonElement je){
		return new GsonWJO(je);
	}
	
	
	
	public static WisecraftJsonObject parse(String json){
		return from(new JsonParser().parse(json));
	}
	public static WisecraftJsonObject parse(Reader json){
		return from(new JsonParser().parse(json));
	}
}
