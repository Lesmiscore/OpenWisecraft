package com.nao20010128nao.Wisecraft.services;
import java.io.*;
import java.net.*;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.nao20010128nao.MCProxy.MultipleUdpConnectionProxy;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.proxy.ProxyActivity;
import java.security.SecureRandom;

public class MCProxyService extends Service
{
	MultipleUdpConnectionProxy prox;
	String ip;
	int port,ctrlPort;
	Thread proxyThread;
	
	int ntfId=new SecureRandom().nextInt();
	@Override
	public IBinder onBind(Intent p1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO: Implement this method
		ip=intent.getStringExtra("ip");
		port=intent.getIntExtra("port",19132);
		ctrlPort=intent.getIntExtra("control",35590);
		
		NotificationCompat.Builder nb=new NotificationCompat.Builder(this);
		nb=nb.setContentTitle(getResources().getString(R.string.app_name));
		nb=nb.setContentText(getResources().getString(R.string.proxyIsWorking));
		NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
		style.setBigContentTitle(getResources().getString(R.string.proxy));
		style.setSummaryText(getResources().getString(R.string.app_name));
		style.addLine(getResources().getString(R.string.connect_to)+"localhost:64321");
		style.addLine(getResources().getString(R.string.connecting_colon)+ip+":"+port);
		nb=nb.setStyle(style);
		nb=nb.setContentIntent(PendingIntent.getActivity(this,0,new Intent(this,ProxyActivity.class).setAction("status"),0));
		nb=nb.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
		nb=nb.setColor(getResources().getColor(R.color.upd_2));
		nb=nb.setSmallIcon(R.drawable.ic_launcher);
		startForeground(ntfId,nb.build());
		
		proxyThread = new Thread(prox = new MultipleUdpConnectionProxy(ip, port, 64321));
		proxyThread.start();
		
		new ControlHandler().start();
		return START_REDELIVER_INTENT;
	}
	
	class ControlHandler extends Thread {
		@Override
		public void run() {
			// TODO: Implement this method
			DatagramSocket ds=null;
			try {
				ds = new DatagramSocket(ctrlPort);
				DatagramPacket dp;
				while (true) {
					try {
						dp = new DatagramPacket(new byte[200], 200);
						ds.receive(dp);
						DataInputStream dis=new DataInputStream(new ByteArrayInputStream(dp.getData(),0,dp.getLength()));
						switch(dis.readByte()){
							case 0://stop
								stopForeground(true);
								proxyThread.interrupt();
								stopSelf();
								interrupt();
								System.exit(0);
								break;
							case 1://info
								ByteArrayOutputStream baos=new ByteArrayOutputStream();
								DataOutputStream dos=new DataOutputStream(baos);
								dos.writeUTF(ip);
								dos.writeInt(port);
								DatagramPacket reply1=new DatagramPacket(baos.toByteArray(),0,baos.size());
								reply1.setSocketAddress(dp.getSocketAddress());
								ds.send(reply1);
								break;
						}
					} catch (IOException e) {

					}
				}
			} catch (SocketException e) {
				
			}
		}
	}
}
