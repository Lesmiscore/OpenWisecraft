package com.nao20010128nao.Wisecraft.misc.compat;

import android.os.*;
import android.support.v7.app.*;
import android.webkit.*;

public abstract class CompatWebViewActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity_compat);
        scanWebView();
    }

    protected void scanWebView() {
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
        });
        webView.getSettings().setJavaScriptEnabled(true);
    }

    protected WebView getWebView() {
        return webView;
    }

    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public void reload() {
        webView.reload();
    }
}
