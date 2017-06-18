package com.nao20010128nao.Wisecraft.misc;

import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.webkit.*;
import com.nao20010128nao.Wisecraft.misc.compat.BuildConfig;
import com.nao20010128nao.Wisecraft.misc.compat.R;
import eu.fiskur.markdownview.*;

import java.io.*;

public abstract class OpenSourceActivity2Base extends AppCompatActivity
{
	WebView markdownView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.only_toolbar);
		getLayoutInflater().inflate(R.layout.open_source_markdown,(ViewGroup)findViewById(R.id.frame));
		setSupportActionBar(CompatUtils.getToolbar(this));
		markdownView=(WebView)findViewById(R.id.markdownView);
		markdownView.loadData(BuildConfig.OPEN_SOURCE_LICENSE,"text/html","utf-8");
	}
}
