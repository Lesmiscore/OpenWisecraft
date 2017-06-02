package com.nao20010128nao.Wisecraft.activity;

import android.os.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class OpenSourceActivity2 extends OpenSourceActivity2Base {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ThemePatcher.applyThemeForActivity(this);
		super.onCreate(savedInstanceState);
	}
}
