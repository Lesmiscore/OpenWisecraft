package com.nao20010128nao.Wisecraft.misc.server;
import java.net.*;
import java.io.*;
import com.nao20010128nao.Wisecraft.*;
import java.util.*;
import java.security.*;
import java.nio.charset.*;
import android.util.*;
import com.google.gson.*;
import android.preference.*;
import android.os.*;
import com.nao20010128nao.Wisecraft.misc.compat.CompatCharsets;

public class GhostPingServer extends Thread
{
	SecureRandom sr=new SecureRandom();
	private DatagramSocket socket = null;
	private ServerSocket servSock=null;
	int localPort = 19500;
	static byte[] MAGIC = {(byte) 0xFE, (byte) 0xFD};
	public void runImpl()throws IOException {
		// TODO: Implement this method
		while(socket == null)
		{
			try {
				socket = new DatagramSocket(localPort);
				servSock=new   ServerSocket(localPort);
				Log.d("ghost_query","port="+localPort);
			} catch (BindException e) {
				++localPort; // increment if port is already in use
			}
		}
		new Thread(){
			{
				Looper.prepare();
			}
			public void run(){
				while(true){
					try {
						servSock.accept().close();
					} catch (IOException e) {}
				}
			}
		}.start();
		while(true){
			byte[] ba=Factories.byteArray(100*1024);
			Arrays.fill(ba,(byte)-1);
			DatagramPacket pak=new DatagramPacket(ba,0,1024*100);
			socket.receive(pak);
			check(pak);
		}
	}

	@Override
	public void run() {
		// TODO: Implement this method
		try {
			runImpl();
		} catch (IOException e) {}
	}
	
	private void check(DatagramPacket p)throws IOException{
		byte d[]=p.getData();
		dump(p);
		ByteArrayInputStream bais=new ByteArrayInputStream(d);
		DataInputStream dis=new DataInputStream(bais);
		byte[] magicTest=new byte[2];
		dis.readFully(magicTest);
		if(!Arrays.equals(MAGIC,magicTest)){
			return;
		}
		byte type=(byte)(dis.read()&0xff);
		int sessionId=dis.readInt();
		if(type==0){
			//stat
			Log.d("ghost_query","len="+p.getLength());
			int token=dis.readInt();
			int pad=dis.readInt();
			ByteArrayOutputStream result=new ByteArrayOutputStream();
			DataOutputStream resW=new DataOutputStream(result);
			if(pad==0){
				//full stat
				resW.write(0);resW.writeInt(sessionId);
				//73 70 6C 69 74 6E 75 6D 00 80 00
				resW.write((byte)0x73);
				resW.write((byte)0x70);
				resW.write((byte)0x6c);
				resW.write((byte)0x69);
				resW.write((byte)0x74);
				resW.write((byte)0x6e);
				resW.write((byte)0x75);
				resW.write((byte)0x6d);
				resW.write((byte)0x00);
				resW.write((byte)0x80);
				resW.write((byte)0x00);
				//KV
				Map<String,String> kv=new HashMap();
				kv.put("gametype","SMP");
				kv.put("map","wisecraft");
				kv.put("server_engine","Wisecraft Ghost Ping");
				kv.put("hostport",localPort+"");
				kv.put("whitelist","on");
				kv.put("plugins","Wisecraft Ghost Ping"+buildPlugins());
				kv.put("hostname","§5Wisecraft");
				kv.put("numplayers","0");
				kv.put("version","v0.13.1 alpha");
				kv.put("game_id","MINECRAFTPE");
				kv.put("hostip","0.0.0.0");
				kv.put("maxplayers","0");
				for(Map.Entry<String,String> ent:kv.entrySet()){
					resW.write(ent.getKey().getBytes(CompatCharsets.UTF_8));
					resW.write(0);
					resW.write(ent.getValue().getBytes(CompatCharsets.UTF_8));
					resW.write(0);
				}
				resW.write(0);
				//01 70 6C 61 79 65 72 5F 00 00
				resW.write((byte)0x01);
				resW.write((byte)0x70);
				resW.write((byte)0x6c);
				resW.write((byte)0x61);
				resW.write((byte)0x79);
				resW.write((byte)0x65);
				resW.write((byte)0x72);
				resW.write((byte)0x5f);
				resW.write((byte)0x00);
				resW.write((byte)0x00);
				//players
				for(ServerListActivity.Server s:getServers()){
					resW.write((s.ip+":"+s.port+"\0").getBytes(CompatCharsets.UTF_8));
				}
				resW.write(0);
				DatagramPacket resP=new DatagramPacket(result.toByteArray(),0,result.size());
				resP.setSocketAddress(p.getSocketAddress());
				socket.send(resP);
			}else{
				//basic stat
			}
		}else if(type==9){
			//handshake
			ByteArrayOutputStream result=new ByteArrayOutputStream();
			DataOutputStream resW=new DataOutputStream(result);
			resW.write(9);
			resW.writeInt(sessionId);
			int r=Math.abs(sr.nextInt());
			resW.write((r+"").getBytes(CompatCharsets.US_ASCII));
			resW.write(0);
			DatagramPacket resP=new DatagramPacket(result.toByteArray(),0,result.size());
			resP.setSocketAddress(p.getSocketAddress());
			socket.send(resP);
		}
	}
	String buildPlugins(){
		return "";
	}
	private ServerListActivity.Server[] getServers(){
		ServerListActivity.Server[] sa=new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getString("servers","[]"),ServerListActivity.Server[].class);
		return sa;
	}
	void dump(DatagramPacket dp){
		StringBuilder sb=new StringBuilder(dp.getLength()*3);
		byte[] b=dp.getData();
		for(int i=0;i<dp.getLength();i++){
			sb.append(Character.forDigit(b[i] >> 4 & 0xF, 16));
			sb.append(Character.forDigit(b[i] & 0xF, 16));
			sb.append(' ');
		}
		Log.i("ghost_query_dump",sb.toString());
	}
}
