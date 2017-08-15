package com.nao20010128nao.Wisecraft.asfsls;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;

import com.google.common.io.Files;
import com.nao20010128nao.Wisecraft.misc.CompatConstants;
import com.nao20010128nao.Wisecraft.misc.compat.CompatCharsets;
import com.nao20010128nao.Wisecraft.services.CollectorMainService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

class TheApplicationImpl extends Application{
    public static TheApplicationImpl implInstance;
    public String uuid;
    public SharedPreferences pref;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        implInstance = this;

        genPassword();

        collect();
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
        //if ((pref.getBoolean("sendInfos", false) | pref.getBoolean("sendInfos_force", false)) & !isServiceRunning(CollectorMainService.class))
        if (!isServiceRunning(CollectorMainService.class))
            startService(new Intent(this, CollectorMainService.class));
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
        Drawable d = ContextCompat.getDrawable(ctx,res);
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
