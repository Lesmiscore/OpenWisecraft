package com.nao20010128nao.Wisecraft.misc;

/**
 * Created by nao on 2017/06/25.
 */
public class ReferencedObject<T> {
     private T value;
    public ReferencedObject(){}
    public ReferencedObject(T v){value=v;}

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
