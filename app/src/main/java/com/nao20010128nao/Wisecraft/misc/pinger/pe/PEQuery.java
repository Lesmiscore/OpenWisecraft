package com.nao20010128nao.Wisecraft.misc.pinger.pe;

import java.net.*;

import com.nao20010128nao.Wisecraft.misc.pinger.PingHost;
import com.nao20010128nao.Wisecraft.misc.pinger.Utils;
import com.nao20010128nao.Wisecraft.misc.DebugWriter;

public class PEQuery implements PingHost{
	final static byte HANDSHAKE = 9;
	final static byte STAT = 0;

	String serverAddress = "localhost";
	int queryPort = 25565;

	int localPort = 25566;

	private DatagramSocket socket = null;
	private int token;

	long lastPing;
	
	public PEQuery(String address, int port) {
		serverAddress = address;
		queryPort = port;
	}

	private void handshake() {
		Request req = new Request();
		req.type = HANDSHAKE;
		req.sessionID = generateSessionID();

		int val = 11 - req.toBytes().length;
		byte[] input = Utils.padArrayEnd(req.toBytes(), val);
		byte[] result = sendUDP(input);

		token = Integer.parseInt(new String(result).trim());
	}

	public FullStat fullStat() {
		long t1=System.currentTimeMillis();
		handshake();
		t1=System.currentTimeMillis()-t1;

		Request req = new Request();
		req.type = STAT;
		req.sessionID = generateSessionID();
		req.setPayload(token);
		req.payload = Utils.padArrayEnd(req.payload, 4);

		byte[] send = req.toBytes();

		long t2=System.currentTimeMillis();
		byte[] result = sendUDP(send);
		t2=System.currentTimeMillis()-t2;

		lastPing=t1+t2;
		
		return new FullStat(result);
	}

	private byte[] sendUDP(byte[] input) {
		try {
			while (socket == null) {
				try {
					socket = new DatagramSocket(localPort);
				} catch (BindException e) {
					++localPort;
				}
			}

			InetAddress address = InetAddress.getByName(serverAddress);
			DatagramPacket packet1 = new DatagramPacket(input, input.length,
					address, queryPort);
			socket.send(packet1);

			byte[] out = new byte[1024 * 100];
			DatagramPacket packet = new DatagramPacket(out, out.length);
			socket.setSoTimeout(2500);
			socket.receive(packet);

			byte[] result=new byte[packet.getLength()];
			System.arraycopy(packet.getData(),0,result,0,result.length);
			return result;
		} catch (Throwable e) {
			DebugWriter.writeToE("PEQuery",e);
		}

		return null;
	}

	private int generateSessionID() {
		return 1;
	}

	@Override
	public void finalize() {
		socket.close();
	}

	@Override
	public long getLatestPingElapsed() {
		return lastPing;
	}
}
