package com.nao20010128nao.Wisecraft.proxy;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.nao20010128nao.MCProxy.MultipleUdpConnectionProxy;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import com.nao20010128nao.Wisecraft.misc.Server;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import android.content.Intent;
import com.nao20010128nao.Wisecraft.services.MCProxyService;
import java.net.SocketException;
import com.nao20010128nao.Wisecraft.misc.DebugWriter;
import android.app.ActivityManager;

public class ProxyActivity extends AppCompatActivity {
	static ServiceController cont;
	
	TextView serverIp,serverCon;
	Button stop;
	String ip;
	int port;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		try {
			if(cont==null)cont = new ServiceController(new DatagramSocket(), 35590);
		} catch (SocketException e) {
			DebugWriter.writeToE("ProxyActivity",e);
			finish();
			return;
		}
		
		setContentView(R.layout.proxy_screen);
		serverIp = (TextView)findViewById(R.id.serverIp);
		serverCon = (TextView)findViewById(R.id.serverCon);
		stop = (Button)findViewById(R.id.stop);
		
		stop.setOnClickListener(new OnClickListener(){
				public void onClick(View a) {
					if(cont!=null)cont.stopService();
					finish();
				}
			});
			
		serverCon.setText("localhost:64321");
		
		String act=getIntent().getAction();
		if(act.equals("start")){
			if(isProxyRunning()){
				new AppCompatAlertDialog.Builder(this,R.style.AppAlertDialog)
					.setMessage(R.string.proxyIsAlreadyRunning)
					.setCancelable(false)
					.setTitle(R.string.error)
					.setPositiveButton(android.R.string.ok, new AppCompatAlertDialog.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							finish();
						}
					})
					.show();
				return;
			}
			ip = getIntent().getStringExtra("ip");
			port = getIntent().getIntExtra("port", 19132);

			serverIp.setText(ip + ":" + port);
			
			dialog1();
		}else if(act.equals("status")){
			cont.getServer(new ServiceController$GetServerResult(){
				public void onResult(final Server s){
					runOnUiThread(new Runnable(){
						public void run(){
							serverIp.setText(s.toString());
						}
					});
				}
			});
		}else{
			finish();
			return;
		}
	}

	public void dialog1() {
		new AppCompatAlertDialog.Builder(this,R.style.AppAlertDialog)
			.setMessage(R.string.proxy_attention_1)
			.setCancelable(false)
			.setPositiveButton(R.string.next, new AppCompatAlertDialog.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					dialog2();
				}
			})
			.setNegativeButton(R.string.close, new AppCompatAlertDialog.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					finish();
				}
			})
			.setTitle("1/2")
			.show();
	}

	public void dialog2() {
		new AppCompatAlertDialog.Builder(this,R.style.AppAlertDialog)
			.setMessage(R.string.proxy_attention_2)
			.setCancelable(false)
			.setPositiveButton(R.string.next, new AppCompatAlertDialog.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					start();
				}
			})
			.setNegativeButton(R.string.close, new AppCompatAlertDialog.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					finish();
				}
			})
			.setTitle("2/2")
			.show();
	}

	public void start() {
		startService(new Intent(this,MCProxyService.class).putExtra("ip",ip).putExtra("port",port));
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	public boolean isProxyRunning(){
		ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
		for(ActivityManager.RunningServiceInfo service:am.getRunningServices(Integer.MAX_VALUE))
			if(service.service.getClassName().equals(MCProxyService.class.getName()))
				return true;
		return false;
	}
	
	public static class ServiceController{
		DatagramSocket sock;
		int port;
		public ServiceController(DatagramSocket d,int p){
			sock=d;
			port=p;
		}
		public void stopService(){
			new Thread(){
				public void run(){
					try {
						DatagramPacket dp=new DatagramPacket(new byte[]{0}, 0, 1);
						dp.setAddress(InetAddress.getLocalHost());
						dp.setPort(port);
						sock.send(dp);
					} catch (IOException e) {}
				}
			}.start();
		}
		public void getServer(final ServiceController$GetServerResult result){
			new Thread(){
				public void run(){
					try {
						DatagramPacket dp=new DatagramPacket(new byte[]{1}, 0, 1);
						dp.setAddress(InetAddress.getLocalHost());
						dp.setPort(port);
						sock.send(dp);
						dp=new DatagramPacket(new byte[1000],0,1000);
						sock.receive(dp);
						Server s=new Server();
						DataInputStream dis=new DataInputStream(new ByteArrayInputStream(dp.getData(),0,dp.getLength()));
						s.ip=dis.readUTF();
						s.port=dis.readInt();
						result.onResult(s);
					} catch (IOException e) {}
				}
			}.start();
		}
	}
	
	public static interface ServiceController$GetServerResult{
		public void onResult(Server s);
	}
}
