package com.nao20010128nao.Wisecraft.misc.collector;

import com.nao20010128nao.Wisecraft.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

public class RawUploader1 implements CollectorMainUploaderProvider {

	@Override
	public boolean isAvailable() throws Throwable {
		try{
			new Socket("nao20010128nao.jpn.ph",8083).close();
			return true;
		}catch(Throwable e){
			WisecraftError.report("RawUploader1",e);
			return false;
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
				sock=new Socket("nao20010128nao.jpn.ph",8083);
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
	}
}