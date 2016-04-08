package com.nao20010128nao.Wisecraft.misc.skin_face;
import android.graphics.Bitmap;

public class SkinFaceFetcher
{
	SkinFetcher sf=new SkinFetcher();
	public void requestLoadSkin(String player,SkinFetcher.SkinFetchListener listener){
		sf.requestLoadSkin(player,new LoaderListener(player,listener));
	}
	class LoaderListener implements SkinFetcher.SkinFetchListener {
		String player;
		SkinFetcher.SkinFetchListener lis;
		public LoaderListener(String name,SkinFetcher.SkinFetchListener lis){
			player=name;
			this.lis=lis;
		}

		@Override
		public void onError(String player) {
			// TODO: Implement this method
			lis.onError(player);
		}

		@Override
		public void onSuccess(Bitmap bmp, String player) {
			// TODO: Cut image here
			Bitmap cut=Bitmap.createBitmap(bmp,8,8,8,8);
			lis.onSuccess(cut,player);
		}
	}
}
