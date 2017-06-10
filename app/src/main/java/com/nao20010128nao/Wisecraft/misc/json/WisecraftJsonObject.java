package com.nao20010128nao.Wisecraft.misc.json;

public interface WisecraftJsonObject extends Iterable<WisecraftJsonObject>
{

	boolean getAsBoolean() ;
	
    java.lang.Number getAsNumber() ;

    java.lang.String getAsString() ;

    double getAsDouble() ;

    float getAsFloat() ;

    long getAsLong() ;

    int getAsInt() ;

    byte getAsByte() ;

    char getAsCharacter() ;

    java.math.BigDecimal getAsBigDecimal() ;

    java.math.BigInteger getAsBigInteger() ;

    short getAsShort() ;
	
	boolean isJsonArray() ;

    boolean isJsonObject() ;
	
	// JsonObject
    int size() ;

    boolean has(java.lang.String memberName) ;

    WisecraftJsonObject get(java.lang.String memberName) ;
	
	// JsonArray
    boolean contains(WisecraftJsonObject element) ;

    java.util.Iterator<WisecraftJsonObject> iterator() ;

    WisecraftJsonObject get(int i) ;
}
