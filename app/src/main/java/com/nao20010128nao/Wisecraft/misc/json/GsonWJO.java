package com.nao20010128nao.Wisecraft.misc.json;

import com.google.gson.*;

import java.math.*;
import java.util.*;

public class GsonWJO implements WisecraftJsonObject
{
	JsonElement element;

	public GsonWJO(JsonElement element){
		this.element=element;
	}
	
	@Override
	public Iterator<WisecraftJsonObject> iterator() {
		// TODO: Implement this method
		return new IteratorWrapper();
	}

	@Override
	public BigInteger getAsBigInteger() {
		// TODO: Implement this method
		return element.getAsBigInteger();
	}

	@Override
	public Number getAsNumber() {
		// TODO: Implement this method
		return element.getAsNumber();
	}

	@Override
	public BigDecimal getAsBigDecimal() {
		// TODO: Implement this method
		return element.getAsBigDecimal();
	}

	@Override
	public double getAsDouble() {
		// TODO: Implement this method
		return element.getAsDouble();
	}

	@Override
	public int getAsInt() {
		// TODO: Implement this method
		return element.getAsInt();
	}

	@Override
	public WisecraftJsonObject get(int i) {
		// TODO: Implement this method
		return new GsonWJO(element.getAsJsonArray().get(i));
	}

	@Override
	public float getAsFloat() {
		// TODO: Implement this method
		return element.getAsFloat();
	}

	@Override
	public short getAsShort() {
		// TODO: Implement this method
		return element.getAsShort();
	}

	@Override
	public long getAsLong() {
		// TODO: Implement this method
		return element.getAsLong();
	}

	@Override
	public boolean getAsBoolean() {
		// TODO: Implement this method
		return element.getAsBoolean();
	}

	@Override
	public byte getAsByte() {
		// TODO: Implement this method
		return element.getAsByte();
	}

	@Override
	public boolean contains(WisecraftJsonObject element) {
		// TODO: Implement this method
		if(element instanceof GsonWJO){
			return this.element.getAsJsonArray().contains(((GsonWJO)element).element);
		}
		return false;
	}

	@Override
	public boolean isJsonObject() {
		// TODO: Implement this method
		return element.isJsonObject();
	}

	@Override
	public boolean has(String memberName) {
		// TODO: Implement this method
		return element.getAsJsonObject().has(memberName);
	}

	@Override
	public WisecraftJsonObject get(String memberName) {
		// TODO: Implement this method
		return new GsonWJO(element.getAsJsonObject().get(memberName));
	}

	@Override
	public boolean isJsonArray() {
		// TODO: Implement this method
		return element.isJsonArray();
	}

	@Override
	public int size() {
		// TODO: Implement this method
		if(element.isJsonObject()){
			return element.getAsJsonObject().size();
		}else if(element.isJsonArray()){
			return element.getAsJsonArray().size();
		}else{
			return -1;
		}
	}

	@Override
	public char getAsCharacter() {
		// TODO: Implement this method
		return element.getAsCharacter();
	}

	@Override
	public String getAsString() {
		// TODO: Implement this method
		return element.getAsString();
	}
	
	class IteratorWrapper implements Iterator<WisecraftJsonObject>{
		Iterator<JsonElement> iter=element.getAsJsonArray().iterator();

		@Override
		public boolean hasNext() {
			// TODO: Implement this method
			return iter.hasNext();
		}

		@Override
		public WisecraftJsonObject next() {
			// TODO: Implement this method
			return new GsonWJO(iter.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
