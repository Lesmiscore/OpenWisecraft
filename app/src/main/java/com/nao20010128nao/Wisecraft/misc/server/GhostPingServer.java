package com.nao20010128nao.Wisecraft.misc.server;
import android.annotation.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public class GhostPingServer extends Thread {
	private static final int MAGIC_1ST=0x00ffff00;
	private static final int MAGIC_2ND=0xfefefefe;
	private static final int MAGIC_3RD=0xfdfdfdfd;
	private static final int MAGIC_4TH=0x12345678;

	SecureRandom sr=new SecureRandom();
	private DatagramSocket serv = null;
	private ServerSocket servSock=null;
	int localPort = 19500;
	static byte[] MAGIC = {(byte) 0xFE, (byte) 0xFD};
	public void runImpl()throws IOException {
		/*if(Build.VERSION.SDK_INT<9){
			Log.d("ghost_query", "the system is too old to start GhostPingServer");
			return;
		}*/
		while (serv == null) {
			try {
				serv   = new DatagramSocket(localPort);
				servSock = new   ServerSocket(localPort);
				Log.d("ghost_query", "port=" + localPort);
			} catch (BindException e) {
				++localPort; // increment if port is already in use
			}
		}
		new Thread(){
			{
				Looper.prepare();
			}
			public void run() {
				while (true) {
					try {
						servSock.accept().close();
					} catch (IOException e) {}
				}
			}
		}.start();
		while (true) {
			byte[] ba=Factories.byteArray(100 * 1024);
			Arrays.fill(ba, (byte)-1);
			DatagramPacket pak=new DatagramPacket(ba, 0, 1024 * 100);
			serv.receive(pak);
			check(pak);
		}
	}

	@Override
	public void run() {
		try {
			runImpl();
		} catch (IOException e) {}
	}

	@TargetApi(9)
	private void check(DatagramPacket p)throws IOException {
		byte d[]=p.getData();
		dump(p);
		ByteArrayInputStream bais=new ByteArrayInputStream(d);
		DataInputStream dis=new DataInputStream(bais);
		if (d[0] == 0x01) {//UnconnectedPing
			dis.read();
			dis.readLong();
			if (dis.readInt() != MAGIC_1ST) {
				return;
			}
			if (dis.readInt() != MAGIC_2ND) {
				return;
			}
			if (dis.readInt() != MAGIC_3RD) {
				return;
			}
			if (dis.readInt() != MAGIC_4TH) {
				return;
			}
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			DataOutputStream dos=new DataOutputStream(baos);
			dos.write(0x1c);
			dos.writeLong(sr.nextLong());
			dos.writeLong(sr.nextLong());
			dos.writeInt(MAGIC_1ST);
			dos.writeInt(MAGIC_2ND);
			dos.writeInt(MAGIC_3RD);
			dos.writeInt(MAGIC_4TH);

			List<String> datas=new ArrayList<>();
			datas.add("MCPE");//MCPE
			datas.add("§3Wisecraft");//Server name
			datas.add("81");//Protocol (81=0.15.2)
			datas.add("0.15.10");//Version (Displayed on MCPE)
			datas.add(Integer.MAX_VALUE + "");//Players count
			datas.add(Integer.MAX_VALUE + "");//Max players

			StringBuilder sb=new StringBuilder();
			for (String s:datas) {
				sb.append(s).append(';');
			}
			sb.setLength(sb.length() - 1);
			dos.writeUTF(sb.toString());

			DatagramPacket resP=new DatagramPacket(baos.toByteArray(), 0, baos.size());
			resP.setSocketAddress(p.getSocketAddress());
			serv.send(resP);
			return;
		}
		byte[] magicTest=new byte[2];
		dis.readFully(magicTest);
		if (!Arrays.equals(MAGIC, magicTest)) {
			return;
		}
		byte type=(byte)(dis.read() & 0xff);
		int sessionId=dis.readInt();
		if (type == 0) {
			//stat
			Log.d("ghost_query", "len=" + p.getLength());
			int token=dis.readInt();
			int pad=dis.readInt();
			ByteArrayOutputStream result=new ByteArrayOutputStream();
			DataOutputStream resW=new DataOutputStream(result);
			if (pad == 0) {
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
				kv.put("gametype", "SMP");
				kv.put("map", "wisecraft");
				kv.put("server_engine", "Wisecraft Ghost Ping");
				kv.put("hostport", localPort + "");
				kv.put("whitelist", "on");
				kv.put("plugins", "Wisecraft Ghost Ping" + buildPlugins());
				kv.put("hostname", "§3Wisecraft");
				kv.put("numplayers", Integer.MAX_VALUE + "");
				kv.put("version", "v0.15.10 alpha");
				kv.put("game_id", "MINECRAFTPE");
				kv.put("hostip", "0.0.0.0");
				kv.put("maxplayers", Integer.MAX_VALUE + "");
				for (Map.Entry<String,String> ent:kv.entrySet()) {
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
				for (Server s:getServers()) {
					resW.write((s.ip + ":" + s.port + "\0").getBytes(CompatCharsets.UTF_8));
				}
				resW.write(0);
				DatagramPacket resP=new DatagramPacket(result.toByteArray(), 0, result.size());
				resP.setSocketAddress(p.getSocketAddress());
				serv.send(resP);
			} else {
				//basic stat
			}
		} else if (type == 9) {
			//handshake
			ByteArrayOutputStream result=new ByteArrayOutputStream();
			DataOutputStream resW=new DataOutputStream(result);
			resW.write(9);
			resW.writeInt(sessionId);
			int r=Math.abs(sr.nextInt());
			resW.write((r + "").getBytes(CompatCharsets.US_ASCII));
			resW.write(0);
			DatagramPacket resP=new DatagramPacket(result.toByteArray(), 0, result.size());
			resP.setSocketAddress(p.getSocketAddress());
			serv.send(resP);
		}
	}
	String buildPlugins() {
		StringBuilder sb=new StringBuilder(": ");
		String[] plugins=new String[]{"Wisecraft v"+Utils.getVersionName(TheApplication.instance),"LesMiserables25 v20010128"};
		for(String s:plugins)sb.append(s).append("; ");
		sb.setLength(sb.length()-2);
		return sb.toString();
	}
	private Server[] getServers() {
		Server[] sa=new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getString("servers", "[]"), Server[].class);
		return sa;
	}
	void dump(DatagramPacket dp) {
		StringBuilder sb=new StringBuilder(dp.getLength() * 3);
		byte[] b=dp.getData();
		for (int i=0;i < dp.getLength();i++) {
			sb.append(Character.forDigit(b[i] >> 4 & 0xF, 16));
			sb.append(Character.forDigit(b[i] & 0xF, 16));
			sb.append(' ');
		}
		Log.i("ghost_query_dump", sb.toString());
	}
}
