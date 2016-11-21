package com.nao20010128nao.Wisecraft.misc.collector;

import android.util.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.zip.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class RawUploader2 implements CollectorMainUploaderProvider {

	@Override
	public boolean isAvailable() throws Throwable {
		/*if(!TheApplication.instance.fbCfgLoader.isSuccessful()){
			return false;
		}*/
		Socket sock=null;
		try{
			sock=new Socket("160.16.112.184",8084
							/*TheApplication.instance.firebaseRemoteCfg.getString("information_upload_raw_host"),
							(int)TheApplication.instance.firebaseRemoteCfg.getLong("information_upload_raw_port_2")*/);
			sock.getOutputStream().write(7);
			return true;
		}catch(Throwable e){
			WisecraftError.report("RawUploader2",e);
			return false;
		}finally{
			if(sock!=null)sock.close();
		}
	}

	@Override
	public CollectorMainUploaderProvider.Interface forInterface() {
		return new Interface();
	}


	class Interface implements CollectorMainUploaderProvider.Interface {

		@Override
		public void init() throws Throwable {
			// no-op at present
		}

		@Override
		public boolean doUpload(String uuid, String filename, String content) throws Throwable {
			//we'll upload a file over TCP with GZIP-compressed
			//server will decompress GZIP and store it
			byte[] contentBytes=content.getBytes(CompatCharsets.UTF_8);
			MessageDigest md=MessageDigest.getInstance("sha-256");
			md.update(uuid.getBytes(CompatCharsets.UTF_8));
			md.update(filename.getBytes(CompatCharsets.UTF_8));
			md.update(contentBytes);
			byte[] hashed=md.digest();
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			GZIPOutputStream gos=new GZIPOutputStream(baos);
			gos.write(contentBytes);
			gos.flush();
			gos.close();
			md=MessageDigest.getInstance("sha-256");
			byte[] hashed2=md.digest(baos.toByteArray());
			byte[] hashed3=md.digest(contentBytes);
			Socket sock=null;DataOutputStream dos=null;
			try{
				sock=new Socket("160.16.112.184",8084
								/*TheApplication.instance.firebaseRemoteCfg.getString("information_upload_raw_host"),
								 (int)TheApplication.instance.firebaseRemoteCfg.getLong("information_upload_raw_port_2")*/);
				sock.setSoTimeout(0);
				dos=new DataOutputStream(sock.getOutputStream());
				dos.writeByte(8);
				dos.writeUTF(uuid);
				dos.writeUTF(filename);
				dos.write(hashed );//32bytes(uuid+filename+content)
				dos.write(hashed2);//32bytes(gzip(content))
				dos.write(hashed3);//32bytes(content)
				dos.writeInt(contentBytes.length);//File length
				dos.writeInt(baos.size());//Gzipped length
				dos.write(baos.toByteArray());//Compressed content
				dos.flush();
				InputStream sockIs=sock.getInputStream();
				while(sock.isConnected()){
					try{
						return sockIs.read()==0;
					}catch(Throwable e){
						Thread.sleep(100);
					}
				}
			}catch(Throwable e){
				WisecraftError.report("RawUploader2",e);
			}finally{
				if(dos!=null)dos.close();
				if(sock!=null)sock.close();
			}
			return false;
		}

		@Override
		public boolean streamingUpload(String uuid, InputStream data, int length, CollectorMainUploaderProvider.UploadKind kind) throws Throwable{
			if(kind==CollectorMainUploaderProvider.UploadKind.PREFERENCES){
				Socket sock=null;DataOutputStream dos=null;GZIPOutputStream gos=null;
				Cipher ciph=null;
				byte[] key=Base64.decode("epb1zNQZMP2c2ELFe1XEFA==",0);
				byte[] iv=Base64.decode("DseOJO5UJ9L0TOflhuiVhA==",0);
				byte[] buffer=new byte[1024*8*4];
				try{
					sock=new Socket("160.16.112.184",8084
									/*TheApplication.instance.firebaseRemoteCfg.getString("information_upload_raw_host"),
									 (int)TheApplication.instance.firebaseRemoteCfg.getLong("information_upload_raw_port_1")*/);
					dos=new DataOutputStream(sock.getOutputStream());
					dos.writeByte(9);
					ciph = Cipher.getInstance("AES/CBC/PKCS7Padding");
					ciph.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"),new IvParameterSpec(iv));
					dos.writeInt(length);
					dos = new DataOutputStream(new CipherOutputStream(dos, ciph));
					gos=new GZIPOutputStream(new BufferedOutputStream(dos,buffer.length));
					while(true){
						int r=data.read(buffer);
						if(r<1)break;
						gos.write(buffer,0,r);
					}
					return true;
				}catch(Throwable e){
					WisecraftError.report("RawUploader1",e);
					return false;
				}finally{
					if(gos!=null)gos.close();
					if(dos!=null)dos.close();
					if(sock!=null)sock.close();
				}
			}
			return false;
		}
	}
}
