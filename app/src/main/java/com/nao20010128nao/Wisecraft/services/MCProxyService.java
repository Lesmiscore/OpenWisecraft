package com.nao20010128nao.Wisecraft.services;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import com.nao20010128nao.MCProxy.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.io.*;
import java.net.*;
import java.security.*;

public class MCProxyService extends Service
{
	MultipleUdpConnectionProxy prox;
	String ip;
	int port,ctrlPort;
	Thread proxyThread;
	
	int ntfId=new SecureRandom().nextInt();
	@Override
	public IBinder onBind(Intent p1) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ip=intent.getStringExtra("ip");
		port=intent.getIntExtra("port",19132);
		ctrlPort=intent.getIntExtra("control",35590);
		
		NotificationCompat.Builder nb=new NotificationCompat.Builder(this);
		nb.setContentTitle(getResources().getString(R.string.app_name));
		nb.setContentText(getResources().getString(R.string.mtlIsWorking));
		NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
		style.setBigContentTitle(getResources().getString(R.string.mtl));
		style.setSummaryText(getResources().getString(R.string.app_name));
		style.addLine(getResources().getString(R.string.connect_to)+"localhost:64321");
		style.addLine(getResources().getString(R.string.connecting_colon)+ip+":"+port);
		nb.setStyle(style);
		nb.setContentIntent(PendingIntent.getActivity(this,hashCode(),new Intent(this,ProxyActivity.class).setAction("status"),0));
		nb.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
		nb.setColor(ThemePatcher.getMainColor(this));
		nb.setSmallIcon(R.drawable.ic_launcher);
		startForeground(ntfId,nb.build());
		
		proxyThread = new Thread(prox = new MultipleUdpConnectionProxy(ip, port, 64321));
		proxyThread.start();
		
		new ControlHandler().start();
		return START_NOT_STICKY;
	}
	
	class ControlHandler extends Thread {
		@Override
		public void run() {
			DatagramSocket ds=null;
			try {
				ds = new DatagramSocket(ctrlPort);
				DatagramPacket dp;
				while (!isInterrupted()) {
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
								//System.exit(0);
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
							case 2://ping to the service
								DatagramPacket reply2=new DatagramPacket(new byte[2],0,1);
								reply2.setSocketAddress(dp.getSocketAddress());
								ds.send(reply2);
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
