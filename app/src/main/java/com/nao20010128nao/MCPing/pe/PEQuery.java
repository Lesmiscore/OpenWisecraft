package com.nao20010128nao.MCPing.pe;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.nao20010128nao.MCPing.Utils;
import com.nao20010128nao.MCPing.*;
import com.nao20010128nao.Wisecraft.misc.DebugWriter;
import android.util.Log;

/**
 * A class that handles Minecraft Query protocol requests
 *
 * @author Ryan McCann
 */
public class PEQuery implements PingHost{
	final static byte HANDSHAKE = 9;
	final static byte STAT = 0;

	String serverAddress = "localhost";
	int queryPort = 25565; // the default minecraft query port

	int localPort = 25566; // the local port we're connected to the server on

	private DatagramSocket socket = null; // prevent socket already bound
											// exception
	private int token;

	long lastPing;
	
	public PEQuery(String address, int port) {
		serverAddress = address;
		queryPort = port;
	}

	// used to get a session token
	private void handshake() {
		Request req = new Request();
		req.type = HANDSHAKE;
		req.sessionID = generateSessionID();

		int val = 11 - req.toBytes().length; // should be 11 bytes total
		byte[] input = Utils.padArrayEnd(req.toBytes(), val);
		byte[] result = sendUDP(input);

		token = Integer.parseInt(new String(result).trim());
	}

	/**
	 * Use this to get basic status information from the server.
	 *
	 * @return a <code>QueryResponse</code> object
	 */
	public BasicStat basicStat() {
		handshake(); // get the session token first

		Request req = new Request(); // create a request
		req.type = STAT;
		req.sessionID = generateSessionID();
		req.setPayload(token);
		byte[] send = req.toBytes();

		byte[] result = sendUDP(send);

		return new BasicStat(result);
	}

	/**
	 * Use this to get more information, including players, from the server.
	 *
	 * @return a <code>QueryResponse</code> object
	 */
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
					socket = new DatagramSocket(localPort); // create the socket
				} catch (BindException e) {
					++localPort; // increment if port is already in use
				}
			}

			// create a packet from the input data and send it on the socket
			InetAddress address = InetAddress.getByName(serverAddress);
			DatagramPacket packet1 = new DatagramPacket(input, input.length,
					address, queryPort);
			socket.send(packet1);

			// receive a response in a new packet
			byte[] out = new byte[1024 * 100]; // TODO guess at max size
			DatagramPacket packet = new DatagramPacket(out, out.length);
			socket.setSoTimeout(2500); // one half second timeout
			socket.receive(packet);

			return packet.getData();
		} catch (SocketException e) {
			DebugWriter.writeToE("PEQuery",e);
		} catch (SocketTimeoutException e) {
			Log.e("PEQuery","Socket Timeout! Is the server offline?");
			DebugWriter.writeToE("PEQuery",e);
		} catch (UnknownHostException e) {
			Log.e("PEQuery","Unknown host!");
			DebugWriter.writeToE("PEQuery",e);
		} catch (Exception e) {
			DebugWriter.writeToE("PEQuery",e);
		}

		return null;
	}

	private int generateSessionID() {
		/*
		 * Can be anything, so we'll just use 1 for now. Apparently it can be
		 * omitted altogether. TODO: increment each time, or use a random int
		 */
		return 1;
	}

	@Override
	public void finalize() {
		socket.close();
	}

	@Override
	public long getLatestPingElapsed() {
		// TODO: Implement this method
		return lastPing;
	}
}
