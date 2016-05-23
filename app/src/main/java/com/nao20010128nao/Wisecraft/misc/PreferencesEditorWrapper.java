package com.nao20010128nao.Wisecraft.misc;
import android.content.SharedPreferences.*;
import java.util.*;

public class PreferencesEditorWrapper implements Editor {
	Editor child;

	public PreferencesEditorWrapper(Editor c){
		child=c;
	}
	
	@Override
	public Editor putString(String key, String value) {
		// TODO 自動生成されたメソッド・スタブ
		child.putString(key,value);
		return this;
	}

	@Override
	public Editor putStringSet(String key, Set<String> values) {
		// TODO 自動生成されたメソッド・スタブ
		child.putStringSet(key,new HashSet<>(values));
		return this;
	}

	@Override
	public Editor putInt(String key, int value) {
		// TODO 自動生成されたメソッド・スタブ
		child.putInt(key,value);
		return this;
	}

	@Override
	public Editor putLong(String key, long value) {
		// TODO 自動生成されたメソッド・スタブ
		child.putLong(key,value);
		return this;
	}

	@Override
	public Editor putFloat(String key, float value) {
		// TODO 自動生成されたメソッド・スタブ
		child.putFloat(key,value);
		return this;
	}

	@Override
	public Editor putBoolean(String key, boolean value) {
		// TODO 自動生成されたメソッド・スタブ
		child.putBoolean(key,value);
		return this;
	}

	@Override
	public Editor remove(String key) {
		// TODO 自動生成されたメソッド・スタブ
		child.remove(key);
		return this;
	}

	@Override
	public Editor clear() {
		// TODO 自動生成されたメソッド・スタブ
		child.clear();
		return this;
	}

	@Override
	public boolean commit() {
		// TODO 自動生成されたメソッド・スタブ
		return child.commit();
	}

	@Override
	public void apply() {
		// TODO 自動生成されたメソッド・スタブ
		child.apply();
	}
}
