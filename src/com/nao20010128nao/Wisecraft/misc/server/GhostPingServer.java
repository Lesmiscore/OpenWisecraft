package com.nao20010128nao.Wisecraft.misc.server;
import java.net.*;
import java.io.*;
import com.nao20010128nao.Wisecraft.*;
import java.util.*;
import java.security.*;
import java.nio.charset.*;
import android.util.*;

public class GhostPingServer extends Thread
{
	SecureRandom sr=new SecureRandom();
	private DatagramSocket socket = null; //prevent socket already bound exception
	int localPort = 19500;
	static byte[] MAGIC = {(byte) 0xFE, (byte) 0xFD};
	public void runImpl()throws IOException {
		// TODO: Implement this method
		while(socket == null)
		{
			try {
				socket = new DatagramSocket(localPort); //create the socket
				Log.d("ghost_query","port="+localPort);
			} catch (BindException e) {
				++localPort; // increment if port is already in use
			}
		}
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
				resW.write("gametype\0SMP\0".getBytes(StandardCharsets.US_ASCII));//gametype
				resW.write("map\0wisecraft\0".getBytes(StandardCharsets.US_ASCII));//map
				resW.write("server_engine\0Wisecraft Ghost Ping\0".getBytes(StandardCharsets.US_ASCII));//server_engine
				resW.write(("hostport\0"+localPort+"\0").getBytes(StandardCharsets.US_ASCII));//hostport
				resW.write("whitelist\0on\0".getBytes(StandardCharsets.US_ASCII));//whitelist
				resW.write(("plugins\0Wisecraft Ghost Ping"+buildPlugins()+"\0").getBytes(StandardCharsets.US_ASCII));//server_engine
				resW.write("hostname\0§5Wisecraft\0".getBytes(StandardCharsets.UTF_8));//whitelist
				resW.write("numplayers\00\0".getBytes(StandardCharsets.UTF_8));//whitelist
				resW.write("version\0v0.13.1 alpha\0".getBytes(StandardCharsets.UTF_8));//whitelist
				resW.write("game_id\0MINECRAFTPE\0".getBytes(StandardCharsets.UTF_8));//whitelist
				resW.write("hostip\00.0.0.0\0".getBytes(StandardCharsets.UTF_8));//whitelist
				resW.write("maxplayers\00\0".getBytes(StandardCharsets.UTF_8));//whitelist
				//01 70 6C 61 79 65 72 5F 00 00
				resW.write((byte)0x01);
				resW.write((byte)0x70);
				resW.write((byte)0x6c);
				resW.write((byte)0x69);
				resW.write((byte)0x74);
				resW.write((byte)0x6e);
				resW.write((byte)0x75);
				resW.write((byte)0x6d);
				resW.write((byte)0x00);
				resW.write((byte)0x00);
				
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
			resW.write((r+"").getBytes(StandardCharsets.US_ASCII));
			resW.write(0);
			DatagramPacket resP=new DatagramPacket(result.toByteArray(),0,result.size());
			resP.setSocketAddress(p.getSocketAddress());
			socket.send(resP);
		}
	}
	String buildPlugins(){
		return "";
	}
}
