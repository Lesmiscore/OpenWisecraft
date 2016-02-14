package com.nao20010128nao.Wisecraft.proxy;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.nao20010128nao.MCProxy.LoggerProxy;
import com.nao20010128nao.Wisecraft.R;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;

public class ProxyActivity extends Activity {
	LoggerProxy prox;
	TextView serverIp,serverCon;
	Button stop;
	String ip;
	int port;
	Thread proxyThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.proxy_screen);
		serverIp = (TextView)findViewById(R.id.serverIp);
		serverCon = (TextView)findViewById(R.id.serverCon);
		stop=(Button)findViewById(R.id.stop);
		ip = getIntent().getStringExtra("ip");
		port = getIntent().getIntExtra("port", 19132);
		
		proxyThread=new Thread(prox=new LoggerProxy(ip,port,64321));
		proxyThread.start();
		serverIp.setText(ip+":"+port);
		serverCon.setText("localhost:64321");
		
		stop.setOnClickListener(new OnClickListener(){
			public void onClick(View a){
				finish();
			}
		});
	}

	@Override
	public void finish() {
		// TODO: Implement this method
		super.finish();
		proxyThread.interrupt();
	}
}
