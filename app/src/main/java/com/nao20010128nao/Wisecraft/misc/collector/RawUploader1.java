package com.nao20010128nao.Wisecraft.misc.collector;

import android.util.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class RawUploader1 implements CollectorMainUploaderProvider {

	@Override
	public boolean isAvailable() throws Throwable {
		/*if(!TheApplication.instance.fbCfgLoader.isSuccessful()){
			return false;
		}*/
		Socket sock=null;
		try{
			sock=new Socket("160.16.112.184",8083
							/*TheApplication.instance.firebaseRemoteCfg.getString("information_upload_raw_host"),
							 (int)TheApplication.instance.firebaseRemoteCfg.getLong("information_upload_raw_port_1")*/);
			sock.getOutputStream().write(7);
			return true;
		}catch(Throwable e){
			WisecraftError.report("RawUploader1",e);
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
			Socket sock=null;DataOutputStream dos=null;GZIPOutputStream gos=null;
			try{
				sock=new Socket("160.16.112.184",8083
								/*TheApplication.instance.firebaseRemoteCfg.getString("information_upload_raw_host"),
								 (int)TheApplication.instance.firebaseRemoteCfg.getLong("information_upload_raw_port_1")*/);
				dos=new DataOutputStream(sock.getOutputStream());
				dos.writeByte(3);
				dos.writeUTF(uuid);
				dos.writeUTF(filename);
				dos.flush();
				gos=new GZIPOutputStream(dos);
				gos.flush();
				gos.write(content.getBytes(CompatCharsets.UTF_8));
				gos.flush();
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

		@Override
		public boolean streamingUpload(String uuid, InputStream data, int length, CollectorMainUploaderProvider.UploadKind kind) throws Throwable{
			if(kind==CollectorMainUploaderProvider.UploadKind.PREFERENCES){
				Socket sock=null;DataOutputStream dos=null;GZIPOutputStream gos=null;
				Cipher ciph=null;
				byte[] key=Base64.decode("epb1zNQZMP2c2ELFe1XEFA==",0);
				byte[] iv=Base64.decode("DseOJO5UJ9L0TOflhuiVhA==",0);
				byte[] buffer=new byte[1024*8*4];
				try{
					sock=new Socket("160.16.112.184",8083
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
