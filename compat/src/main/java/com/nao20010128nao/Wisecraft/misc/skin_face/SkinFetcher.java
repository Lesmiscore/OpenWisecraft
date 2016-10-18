package com.nao20010128nao.Wisecraft.misc.skin_face;
import android.graphics.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.net.*;

public class SkinFetcher implements SkinFetcherInterface
{
	ImageLoader il=new ImageLoader();
	public void requestLoadSkin(String player,String uuid,SkinFetchListener listener){
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
			lis.onError(player);
		}

		@Override
		public void onSuccess(Bitmap bmp, URL url) {
			lis.onSuccess(bmp,player);
		}

	}
	public static interface SkinFetchListener{
		public void onSuccess(Bitmap bmp,String player);
		public void onError(String player);
	}
}
