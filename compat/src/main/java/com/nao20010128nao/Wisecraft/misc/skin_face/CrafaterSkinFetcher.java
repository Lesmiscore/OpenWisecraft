package com.nao20010128nao.Wisecraft.misc.skin_face;
import android.graphics.*;
import android.os.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.net.*;

public class CrafaterSkinFetcher implements SkinFetcherInterface
{
	ImageLoader il=new ImageLoader();
	public void requestLoadSkin(String player,String uuid,SkinFetcher.SkinFetchListener listener){
        Bundle bnd=new Bundle();
        bnd.putString("CrafaterSkinFetcher","");
        bnd.putString("enqueued:",uuid);
		try {
			il.putInQueue(new URL("https://crafatar.com/avatars/"+uuid+"?overlay=true&size=1"), new LoaderListener(player, listener));
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
			lis.onError(player);
		}

		@Override
		public void onSuccess(Bitmap bmp, URL url) {
			lis.onSuccess(bmp,player);
		}

	}
}
