package com.nao20010128nao.Wisecraft.misc;
import android.content.SharedPreferences.*;
import android.os.*;
import java.util.*;

public class PreferencesEditorWrapper implements Editor {
	Editor child;

	public PreferencesEditorWrapper(Editor c){
		child=c;
	}
	
	@Override
	public Editor putString(String key, String value) {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		child.putString(key,value);
		return this;
	}

	@Override
	public Editor putStringSet(String key, Set<String> values) {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		if(Build.VERSION.SDK_INT>10)
			child.putStringSet(key,new HashSet<>(values));
		return this;
	}

	@Override
	public Editor putInt(String key, int value) {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		child.putInt(key,value);
		return this;
	}

	@Override
	public Editor putLong(String key, long value) {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		child.putLong(key,value);
		return this;
	}

	@Override
	public Editor putFloat(String key, float value) {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		child.putFloat(key,value);
		return this;
	}

	@Override
	public Editor putBoolean(String key, boolean value) {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		child.putBoolean(key,value);
		return this;
	}

	@Override
	public Editor remove(String key) {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		child.remove(key);
		return this;
	}

	@Override
	public Editor clear() {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		child.clear();
		return this;
	}

	@Override
	public boolean commit() {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		return child.commit();
	}

	@Override
	public void apply() {
		// TODO 自動生成されたメソ�?ド�?�スタ�?
		child.apply();
	}
}
