package com.nao20010128nao.Wisecraft.misc.pinger.pe;

import android.util.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;

import java.io.*;
import java.math.*;
import java.net.*;

public class UnconnectedPing {
	public static final byte UCP_PID=0x01;
	public static final int MAGIC_1ST=0x00ffff00;
	public static final int MAGIC_2ND=0xfefefefe;
	public static final int MAGIC_3RD=0xfdfdfdfd;
	public static final int MAGIC_4TH=0x12345678;

	public static UnconnectedPingResult doPing(String ip, int port)throws IOException {
		DatagramSocket ds=null;
		try {
			ds = new DatagramSocket();
			ds.setSoTimeout(2500);
			ByteArrayOutputStream baos=new ByteArrayOutputStream(25);
			DataOutputStream dos=new DataOutputStream(baos);
			dos.write(UCP_PID);
			dos.writeLong(System.currentTimeMillis());
			dos.writeInt(MAGIC_1ST);
			dos.writeInt(MAGIC_2ND);
			dos.writeInt(MAGIC_3RD);
			dos.writeInt(MAGIC_4TH);
			dos.flush();

			long t=System.currentTimeMillis();
			DatagramPacket dp=new DatagramPacket(baos.toByteArray(), baos.size(), InetAddress.getByName(ip), port);
			ds.send(dp);

			DatagramPacket recDp=new DatagramPacket(new byte[1500], 1500);
			ds.receive(recDp);
			byte[] recvBuf=new byte[recDp.getLength()];
			System.arraycopy(recDp.getData(), 0, recvBuf, 0, recvBuf.length);
			t = System.currentTimeMillis() - t;

			DataInputStream dis=new DataInputStream(new ByteArrayInputStream(recvBuf));
			if (dis.readByte() != 0x1c) {
				throw new IOException("Server replied with invalid data");
			}
			dis.readLong();//Ping ID
			dis.readLong();//Server ID
			dis.readLong();//MAGIC
			dis.readLong();//MAGIC
			String s=dis.readUTF();
			Log.d("UCP",s);
			return new UnconnectedPingResult(s, t,recvBuf);
		} catch (IOException e) {
			throw e;
		} finally {
			if (ds != null)ds.close();
		}
	}

	public static class UnconnectedPingResult implements ServerPingResult, PingHost, PEPingResult {
		String[] serverInfos;
		String raw;
		long latestPing;
		byte[] data;
		public UnconnectedPingResult(String s, long elapsed,byte[] rdata) {
			serverInfos = (raw=s.substring((/*raw=*/s).indexOf("MCPE;"))).split("\\;");
			latestPing = elapsed;
			data=rdata;
            if(serverInfos.length<5)throw new IllegalArgumentException("Splitted length is invalid. The length was "+serverInfos.length);
		}
		public String getServerName() {
			return serverInfos[1];
		}
		public BigInteger getPlayersCount() {
			return new BigInteger(serverInfos[4]);
		}
		public BigInteger getMaxPlayers() {
			return new BigInteger(serverInfos[5]);
		}
		public String getRaw() {
			return raw;
		}

		@Override
		public long getLatestPingElapsed() {
			return latestPing;
		}

		@Override
		public byte[] getRawResult() {
			return PingerUtils.copyOf(data,data.length);
		}
	}
}
