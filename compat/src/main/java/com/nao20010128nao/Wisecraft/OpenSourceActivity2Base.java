package com.nao20010128nao.Wisecraft;

import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.webkit.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.io.*;
import java.net.*;

public abstract class OpenSourceActivity2Base extends AppCompatActivity {
    WebView markdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.only_toolbar);
        getLayoutInflater().inflate(R.layout.open_source_markdown, (ViewGroup) findViewById(R.id.frame));
        setSupportActionBar(CompatUtils.getToolbar(this));
        markdownView = (WebView) findViewById(R.id.markdownView);
        markdownView.loadData(loadHtml().replace("+", "%20"), "text/html", "utf-8");
    }

    private String loadHtml() {
        try {
            return URLEncoder.encode(BuildConfig.OPEN_SOURCE_LICENSE, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
