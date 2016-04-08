package com.nao20010128nao.Wisecraft.misc.skin_face;
import java.net.URL;
import android.graphics.Bitmap;
import java.net.MalformedURLException;
import com.nao20010128nao.Wisecraft.misc.DebugWriter;

public class SkinFetcher
{
	ImageLoader il=new ImageLoader();
	public void requestLoadSkin(String player,SkinFetchListener listener){
		try {
			il.putInQueue(new URL("http://skins.minecraft.net/MinecraftSkins/"+player+".png"), new LoaderListener(player, listener));
		} catch (MalformedURLException e) {
			DebugWriter.writeToE("SkinFetcher",e);
		}
	}
	class LoaderListener implements ImageLoader.ImageStatusListener {
		String player;
		SkinFetchListener lis;
		public LoaderListener(String name,SkinFetchListener lis){
			player=name;
			this.lis=lis;
		}
		@Override
		public void onError(Throwable err, URL url) {
			// TODO: Implement this method
			lis.onError(player);
		}

		@Override
		public void onSuccess(Bitmap bmp, URL url) {
			// TODO: Implement this method
			lis.onSuccess(bmp,player);
		}

	}
	public static interface SkinFetchListener{
		public void onSuccess(Bitmap bmp,String player);
		public void onError(String player);
	}
}
