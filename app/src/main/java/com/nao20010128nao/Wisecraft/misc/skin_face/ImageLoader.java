package com.nao20010128nao.Wisecraft.misc.skin_face;
import android.graphics.Bitmap;
import java.util.Queue;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import com.nao20010128nao.Wisecraft.misc.KVP;

public class ImageLoader
{
	LoaderThread loader=new LoaderThread();
	Queue<Map.Entry<URL,ImageStatusListener>> queue=new LinkedList<>();
	public void putInQueue(URL load,ImageStatusListener listener){
		queue.offer(new KVP<URL,ImageStatusListener>(load,listener));
		if(!loader.isAlive()){
			loader=new LoaderThread();
			loader.start();
		}
	}
	class LoaderThread extends Thread {
		@Override
		public void run() {
			// TODO: Implement this method
			
		}
	}
	public static interface ImageStatusListener{
		public void onSuccess(Bitmap bmp);
		public void onError(Throwable err);
	}
}
