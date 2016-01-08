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
			DatagramPacket pak=new DatagramPacket(Factories.byteArray(100*1024),0,1024*100);
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
}
