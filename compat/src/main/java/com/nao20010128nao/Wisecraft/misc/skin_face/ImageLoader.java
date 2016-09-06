package com.nao20010128nao.Wisecraft.misc.skin_face;
import android.graphics.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ImageLoader
{
	LoaderThread loader=new LoaderThread();
	Queue<Map.Entry<URL,ImageStatusListener>> queue=new LinkedList<>();
	public void putInQueue(URL load,ImageStatusListener listener){
		queue.add(new KVP<URL,ImageStatusListener>(load,listener));
		if(!loader.isAlive()){
			loader=new LoaderThread();
			loader.start();
		}
	}
	class LoaderThread extends Thread implements Runnable{
		@Override
		public void run() {
			// TODO: Implement this method
			while(!queue.isEmpty()){
				Log.d("ImageLoader","Starting");
				try {
					Map.Entry<URL,ImageStatusListener> dat=queue.poll();
					if (dat == null) {
						continue;
					}
					Log.d("ImageLoader","Url:"+dat.getKey());
					InputStream is=null;
					Bitmap bmp=null;
					try {
						bmp = BitmapFactory.decodeStream(is = dat.getKey().openStream());
					} catch (Throwable e) {
						DebugWriter.writeToE("ImageLoader", e);
						try {
							dat.getValue().onError(e, dat.getKey());
						} catch (Throwable e_) {
							DebugWriter.writeToE("ImageLoader", e_);
						}
						continue;
					} finally {
						try {
							if (is != null)is.close();
						} catch (IOException e) {}
					}
					try {
						dat.getValue().onSuccess(bmp, dat.getKey());
					} catch (Throwable e_) {
						DebugWriter.writeToE("ImageLoader", e_);
					}
				} catch (Exception e) {}
			}
		}
	}
	public static interface ImageStatusListener{
		public void onSuccess(Bitmap bmp,URL url);
		public void onError(Throwable err,URL url);
	}
}
