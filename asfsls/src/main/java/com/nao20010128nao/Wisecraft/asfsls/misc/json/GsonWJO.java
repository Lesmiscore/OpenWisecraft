package com.nao20010128nao.Wisecraft.asfsls.misc.json;

import com.annimon.stream.Stream;
import com.google.gson.JsonElement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

public class GsonWJO implements WisecraftJsonObject {
    JsonElement element;

    public GsonWJO(JsonElement element) {
        this.element = element;
    }

    @Override
    public Iterator<WisecraftJsonObject> iterator() {
        // TODO: Implement this method
        return Stream.of(element.getAsJsonArray())
            .map(WJOUtils::from)
            .toList()
            .iterator();
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
        return element instanceof GsonWJO && this.element.getAsJsonArray().contains(((GsonWJO) element).element);
    }

    @Override
    public boolean isJsonObject() {
        // TODO: Implement this method
        return element.isJsonObject();
    }

    @Override
    public boolean isNumber() {
        return isPrimitive() && element.getAsJsonPrimitive().isNumber();
    }

    @Override
    public boolean isString() {
        return isPrimitive() && element.getAsJsonPrimitive().isString();
    }

    @Override
    public boolean isPrimitive() {
        return element.isJsonPrimitive();
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
        if (element.isJsonObject()) {
            return element.getAsJsonObject().size();
        } else if (element.isJsonArray()) {
            return element.getAsJsonArray().size();
        } else {
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
}
