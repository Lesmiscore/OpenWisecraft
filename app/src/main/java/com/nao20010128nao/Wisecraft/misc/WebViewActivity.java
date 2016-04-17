package com.nao20010128nao.Wisecraft.misc;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.nao20010128nao.Wisecraft.R;

public abstract class WebViewActivity extends AppCompatActivity
{
	WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);
		webView=(WebView)findViewById(R.id.webview);
		webView.setWebViewClient(new WebViewClient(){});
		webView.getSettings().setJavaScriptEnabled(true);
	}
	protected WebView getWebView() {
		return webView;
	}
	public void loadUrl(String url){
		webView.loadUrl(url);
	}
	public void reload(){
		webView.reload();
	}
}
