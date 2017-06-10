package com.nao20010128nao.Wisecraft.misc;

import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.misc.compat.BuildConfig;
import com.nao20010128nao.Wisecraft.misc.compat.R;
import eu.fiskur.markdownview.*;

import java.io.*;

public abstract class OpenSourceActivity2Base extends AppCompatActivity
{
	MarkdownView markdownView;
	File oslMdCache;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.only_toolbar);
		getLayoutInflater().inflate(R.layout.open_source_markdown,(ViewGroup)findViewById(R.id.frame));
		setSupportActionBar(CompatUtils.getToolbar(this));
		markdownView=(MarkdownView)findViewById(R.id.markdownView);
		oslMdCache=new File(getCacheDir(),"openSourceLicense.md");
		markdownView.allowGestures(true);
		markdownView.showMarkdown(BuildConfig.OPEN_SOURCE_LICENSE);
	}
}
