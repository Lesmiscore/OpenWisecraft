package com.nao20010128nao.Wisecraft.misc.skin_face;

import android.graphics.*;

public class SkinFaceFetcher implements SkinFetcherInterface {
    SkinFetcherInterface sf;

    public SkinFaceFetcher(SkinFetcherInterface sr) {
        sf = sr;
    }

    public void requestLoadSkin(String player, String uuid, SkinFetcher.SkinFetchListener listener) {
        sf.requestLoadSkin(player, uuid, new LoaderListener(player, listener));
    }

    class LoaderListener implements SkinFetcher.SkinFetchListener {
        String player;
        SkinFetcher.SkinFetchListener lis;

        public LoaderListener(String name, SkinFetcher.SkinFetchListener lis) {
            player = name;
            this.lis = lis;
        }

        @Override
        public void onError(String player) {
            lis.onError(player);
        }

        @Override
        public void onSuccess(Bitmap bmp, String player) {
            Bitmap cut = Bitmap.createBitmap(bmp, 8, 8, 8, 8);
            lis.onSuccess(cut, player);
        }
    }
}
