package com.nao20010128nao.Wisecraft.proxy;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.nao20010128nao.MCProxy.LoggerProxy;
import com.nao20010128nao.Wisecraft.R;

public class ProxyActivity extends Activity {
	LoggerProxy prox;
	TextView serverIp,serverCon;
	String ip;
	int port;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.proxy_screen);
		serverIp = (TextView)findViewById(R.id.serverIp);
		serverCon = (TextView)findViewById(R.id.serverCon);
		ip = getIntent().getStringExtra("ip");
		port = getIntent().getIntExtra("port", 19132);
	}
}
