package com.nao20010128nao.Wisecraft.misc;

import android.content.SharedPreferences.*;
import android.os.*;

import java.util.*;

public class PreferencesEditorWrapper implements Editor {
    Editor child;

    public PreferencesEditorWrapper(Editor c) {
        child = c;
    }

    @Override
    public Editor putString(String key, String value) {
        child.putString(key, value);
        return this;
    }

    @Override
    public Editor putStringSet(String key, Set<String> values) {
        if (Build.VERSION.SDK_INT > 10)
            child.putStringSet(key, new HashSet<>(values));
        return this;
    }

    @Override
    public Editor putInt(String key, int value) {
        child.putInt(key, value);
        return this;
    }

    @Override
    public Editor putLong(String key, long value) {
        child.putLong(key, value);
        return this;
    }

    @Override
    public Editor putFloat(String key, float value) {
        child.putFloat(key, value);
        return this;
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        child.putBoolean(key, value);
        return this;
    }

    @Override
    public Editor remove(String key) {
        child.remove(key);
        return this;
    }

    @Override
    public Editor clear() {
        child.clear();
        return this;
    }

    @Override
    public boolean commit() {
        return child.commit();
    }

    @Override
    public void apply() {
        child.apply();
    }
}
