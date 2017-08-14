package com.nao20010128nao.Wisecraft.asfsls;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.google.android.gms.tasks.Task;
import com.google.common.io.Files;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.nao20010128nao.Wisecraft.misc.CompatConstants;
import com.nao20010128nao.Wisecraft.misc.compat.CompatCharsets;
import com.nao20010128nao.Wisecraft.services.CollectorMainService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

class TheApplicationImpl extends Application{
    public static TheApplicationImpl implInstance;
    public String uuid;
    public SharedPreferences pref;
    public SharedPreferences stolenInfos;
    public FirebaseAnalytics firebaseAnalytics;
    public FirebaseRemoteConfig firebaseRemoteCfg;
    public Task<Void> fbCfgLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        implInstance = this;

        genPassword();

        /*CollectorMain.INFORMATIONS.add(new MinecraftPeInformationProvider());
        CollectorMain.INFORMATIONS.add(new WisecraftInformationProvider());*/

    }

    private String genPassword() {
        File uuidFile=new File(CompatConstants.mcpeServerList,"../wisecraft/uuid").getAbsoluteFile();
        uuid = pref.getString("uuid", null);
        if(uuidFile.exists()){
            if (uuid == null) {
                try {
                    uuid = UUID.fromString(Files.readFirstLine(uuidFile, CompatCharsets.UTF_8)).toString();
                } catch (IOException e) {
                    if (uuidFile.exists()) uuidFile.delete();
                    return genPassword();
                }
            }
            pref.edit().putString("uuid", uuid).commit();
            if (!pref.contains("uuidShouldBe")) {
                pref.edit().putString("uuidShouldBe", uuid).commit();
            }
        }else{
            if (!TextUtils.isEmpty(Settings.Secure.getString(getContentResolver(), Settings.System.ANDROID_ID)) || !TextUtils.isEmpty(Build.SERIAL)) {
                String seed = Settings.Secure.getString(getContentResolver(), Settings.System.ANDROID_ID) + Build.SERIAL;
                if (uuid == null) uuid = UUID.nameUUIDFromBytes(seed.getBytes()).toString();
                pref.edit().putString("uuid", uuid).commit();
                if (!pref.contains("uuidShouldBe")) {
                    pref.edit().putString("uuidShouldBe", UUID.nameUUIDFromBytes(seed.getBytes()).toString()).commit();
                }
            }else{
                if (uuid == null)uuid=UUID.randomUUID().toString();
                pref.edit().putString("uuid", uuid).commit();
                if (!pref.contains("uuidShouldBe")) {
                    pref.edit().putString("uuidShouldBe", uuid).commit();
                }
            }
        }
        uuidFile.getParentFile().mkdirs();
        try {
            Files.write(uuid,uuidFile,CompatCharsets.UTF_8);
        } catch (IOException e) {}
        return uuid + uuid;
    }


    public void collect() {
        collectImpl();
    }

    private void collectImpl() {
        if ((pref.getBoolean("sendInfos", false) | pref.getBoolean("sendInfos_force", false)) & !isServiceRunning(CollectorMainService.class))
            startService(new Intent(this, CollectorMainService.class));
    }

    public LayoutInflater getLayoutInflater() {
        return (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public boolean isServiceRunning(Class<? extends Service> clazz) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE))
            if (service.service.getClassName().equals(clazz.getName()))
                return true;
        return false;
    }

    public Drawable getTintedDrawable(int res, int color) {
        return getTintedDrawable(res, color, this);
    }

    public static Drawable getTintedDrawable(int res, int color, Context ctx) {
        Drawable d = ctx.getResources().getDrawable(res);
        d = DrawableCompat.wrap(d);
        DrawableCompat.setTint(d, color);
        return d;
    }
}

public class TheApplication extends TheApplicationImpl {
    public static TheApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
