package com.nao20010128nao.Wisecraft.misc;

import android.support.annotation.*;

/**
 * Created by nao on 2017/06/25.
 */
public class ReferencedObject<T> {
    private T value;

    public ReferencedObject() {
    }

    public ReferencedObject(T v) {
        value = v;
    }

    @Nullable
    public T get() {
        return value;
    }

    @NonNull
    public T checked() {
        if (value == null) throw new NullPointerException();
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
