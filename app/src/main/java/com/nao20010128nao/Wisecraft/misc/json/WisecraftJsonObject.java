package com.nao20010128nao.Wisecraft.misc.json;

public interface WisecraftJsonObject extends Iterable<WisecraftJsonObject>
{

	public boolean getAsBoolean() ;
	
    public java.lang.Number getAsNumber() ;

    public java.lang.String getAsString() ;

    public double getAsDouble() ;

    public float getAsFloat() ;

    public long getAsLong() ;

    public int getAsInt() ;

    public byte getAsByte() ;

    public char getAsCharacter() ;

    public java.math.BigDecimal getAsBigDecimal() ;

    public java.math.BigInteger getAsBigInteger() ;

    public short getAsShort() ;
	
	public boolean isJsonArray() ;

    public boolean isJsonObject() ;
	
	// JsonObject
	public int size() ;

    public boolean has(java.lang.String memberName) ;

    public WisecraftJsonObject get(java.lang.String memberName) ;
	
	// JsonArray
    public boolean contains(WisecraftJsonObject element) ;

    public java.util.Iterator<WisecraftJsonObject> iterator() ;

    public WisecraftJsonObject get(int i) ;
}
