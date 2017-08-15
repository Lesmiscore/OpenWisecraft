package com.nao20010128nao.Wisecraft.asfsls.misc.json;

public interface WisecraftJsonObject extends Iterable<WisecraftJsonObject> {

    boolean getAsBoolean();

    Number getAsNumber();

    String getAsString();

    double getAsDouble();

    float getAsFloat();

    long getAsLong();

    int getAsInt();

    byte getAsByte();

    char getAsCharacter();

    java.math.BigDecimal getAsBigDecimal();

    java.math.BigInteger getAsBigInteger();

    short getAsShort();

    boolean isJsonArray();

    boolean isJsonObject();

    boolean isNumber();

    boolean isString();

    boolean isPrimitive();

    // JsonObject
    int size();

    boolean has(String memberName);

    WisecraftJsonObject get(String memberName);

    // JsonArray
    boolean contains(WisecraftJsonObject element);

    java.util.Iterator<WisecraftJsonObject> iterator();

    WisecraftJsonObject get(int i);
}
