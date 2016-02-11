package com.nao20010128nao.Wisecraft.pingEngine;
import com.nao20010128nao.MCPing.*;
import java.net.*;
import java.io.*;

public class UnconnectedPing
{
	public static final byte UCP_PID=0x01;
	public static final long MAGIC_1ST=0x00ffff00fefefefe;
	public static final long MAGIC_2ND=0xfdfdfdfd12345678;
	
	public static UnconnectedPingResult doPing(String ip,int port)throws IOException{
		DatagramSocket ds=null;
		try{
			ds=new DatagramSocket();
			ByteArrayOutputStream baos=new ByteArrayOutputStream(25);
			DataOutputStream dos=new DataOutputStream(baos);
			dos.write(UCP_PID);
			dos.writeLong(System.currentTimeMillis());
			dos.writeLong(MAGIC_1ST);
			dos.writeLong(MAGIC_2ND);
			dos.flush();
			DatagramPacket dp=new DatagramPacket(baos.toByteArray(),baos.size(),InetAddress.getByName(ip),port);
			ds.send(dp);
			
			DatagramPacket recDp=new DatagramPacket(new byte[1000],1000);
			ds.receive(recDp);
			byte[] recvBuf=new byte[recDp.getLength()];
			System.arraycopy(recDp.getData(),0,recvBuf,0,recvBuf.length);
			
			DataInputStream dis=new DataInputStream(new ByteArrayInputStream(recvBuf));
			if(dis.readByte()!=0x1c){
				throw new IOException("Server replied with invalid data");
			}
			dis.readLong();//Ping ID
			dis.readLong();//Server ID
			dis.readLong();//MAGIC
			dis.readLong();//MAGIC
			String s=dis.readUTF();
			return new UnconnectedPingResult(s);
		}catch(IOException e){
			throw e;
		}finally{
			if(ds!=null)ds.close();
		}
	}
	
	public static class UnconnectedPingResult implements ServerPingResult{
		String[] serverInfos;
		String raw;
		public UnconnectedPingResult(String s){
			serverInfos=(raw=s).split("\\;");
		}
		public String getServerName(){
			return serverInfos[1];
		}
		public String getRaw(){
			return raw;
		}
	}
}
