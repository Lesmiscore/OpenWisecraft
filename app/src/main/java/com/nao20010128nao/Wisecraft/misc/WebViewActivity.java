package com.nao20010128nao.Wisecraft.misc;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.nao20010128nao.Wisecraft.R;

public abstract class WebViewActivity extends Activity
{
	WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);
		webView=(WebView)findViewById(R.id.webview);
	}
	protected WebView getWebView() {
		return webView;
	}
}
