package com.nao20010128nao.Wisecraft.misc.skin_face;
import android.graphics.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.net.*;

public class CrafaterSkinFetcher extends SkinFaceFetcher
{
	ImageLoader il=new ImageLoader();
	public void requestLoadSkin(String player,SkinFetcher.SkinFetchListener listener){
		try {
			il.putInQueue(new URL("https://crafatar.com/avatars/"+player+"?overlay=true&size=1"), new LoaderListener(player, listener));
		} catch (MalformedURLException e) {
			DebugWriter.writeToE("SkinFetcher",e);
		}
	}
	class LoaderListener implements ImageLoader.ImageStatusListener {
		String player;
		SkinFetcher.SkinFetchListener lis;
		public LoaderListener(String name,SkinFetcher.SkinFetchListener lis){
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
}
