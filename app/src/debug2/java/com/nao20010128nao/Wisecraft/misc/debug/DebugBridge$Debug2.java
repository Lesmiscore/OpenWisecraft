package com.nao20010128nao.Wisecraft.misc.debug;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.util.*;
import android.support.v7.preference.*;
import com.annimon.stream.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pref.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import dalvik.system.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.channels.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

import static com.nao20010128nao.Wisecraft.BuildConfig.*;
import static com.nao20010128nao.Wisecraft.misc.compat.BuildConfig.*;

class DebugBridge$Debug2 extends DebugBridge {
	boolean init=false;
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void init(Context ctx) {
        if(init)return;
        
        // try loading GroovyObject
        try{
            Class.forName("groovy.lang.GroovyObject");
            Log.d("DebugBridge","GroovyObject is here!");
        }catch(Throwable e){
            WisecraftError.report("DebugBridge", e);
            return;
        }
        init=true;
    }

    @Override
    public void openDebugActivity(Context ctx) {
        Intent intent=new Intent(ctx,DebugList.class);
        if(!(ctx instanceof Activity))intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
    
    @Override
    public void addDebugMenus(SextetWalker<Integer, Integer, Consumer<Activity>, Consumer<Activity>, IDrawerItem, UUID> list){
        list.add(new Sextet<>(R.string.dbgMenu,R.drawable.ic_add_black_48dp,this::openDebugActivity,null,null,UUID.fromString("2ee5ea67-99b2-4f75-b7a8-19deaee2e4ed")));
    }
    
    @Override
    public void addDebugInfos(Context ctx,PreferenceScreen preferences){
        Context c = CompatUtils.wrapContextForPreference(ctx);
        List<Preference> buildData = new ArrayList<>();
        buildData.add(new SimplePref(c, "Build ID",    CI_BUILD_ID));
        buildData.add(new SimplePref(c, "Build Ref",   CI_BUILD_REF_NAME));
        buildData.add(new SimplePref(c, "Runner ID",   CI_RUNNER_ID));
        buildData.add(new SimplePref(c, "Build Stage", CI_BUILD_STAGE));
        buildData.add(new SimplePref(c, "Build Name",  CI_BUILD_NAME));
        Stream.of(buildData)
            .peek(preferences::addPreference)
            .forEach(a->a.setVisible(true));
    }
}
