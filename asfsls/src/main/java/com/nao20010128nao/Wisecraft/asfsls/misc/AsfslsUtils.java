package com.nao20010128nao.Wisecraft.asfsls.misc;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;

import com.annimon.stream.Stream;
import com.nao20010128nao.Wisecraft.asfsls.R;
import com.nao20010128nao.Wisecraft.asfsls.misc.serverList.MslServer;
import com.nao20010128nao.Wisecraft.misc.CompatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lesmi on 17/08/14.
 */

public class AsfslsUtils extends CompatUtils{
    public static int getMenuTintColor(Context context) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{R.attr.wcMenuTintColor});
        int color = ta.getColor(0, Color.BLACK);
        ta.recycle();
        return color;
    }
    public static List<Server> convertServerObject(List<MslServer> from) {
        ArrayList<Server> result = new ArrayList<>();
        for (MslServer obj : from) {
            Server wcs = new Server();
            wcs.ip = obj.ip;
            wcs.port = obj.port;
            wcs.mode = obj.isPE ? Server.Mode.PE : Server.Mode.PC;
            result.add(wcs);
        }
        return result;
    }
    public static Server makeServerFromBundle(Bundle bnd) {
        String ip = bnd.getString("com.nao20010128nao.Wisecraft.misc.Server.ip");
        int port = bnd.getInt("com.nao20010128nao.Wisecraft.misc.Server.port");
        int mode = bnd.getInt("com.nao20010128nao.Wisecraft.misc.Server.mode");
        Server s = new Server();
        s.ip = ip;
        s.port = port;
        s.mode = Server.Mode.forNumber(mode);
        return s;
    }

    public static Server[] makeServersFromBundle(Bundle bnd) {
        return Stream.of(bnd.getParcelableArray("com.nao20010128nao.Wisecraft.SERVERS"))
            .map(a -> (Bundle) a)
            .map(AsfslsUtils::makeServersFromBundle)
            .toArray(Server[]::new);
    }

    public static void putServerIntoBundle(Bundle bnd, Server s) {
        bnd.putString("com.nao20010128nao.Wisecraft.SERVER_IP", s.ip);
        bnd.putInt("com.nao20010128nao.Wisecraft.SERVER_PORT", s.port);
        bnd.putInt("com.nao20010128nao.Wisecraft.SERVER_MODE", s.mode.mode);
    }

    public static Bundle putServerIntoBundle(Server s) {
        Bundle bnd = new Bundle();
        putServerIntoBundle(bnd, s);
        return bnd;
    }

    public static void putServersIntoBundle(Bundle bnd, Server[] s) {
        bnd.putParcelableArray(
            "com.nao20010128nao.Wisecraft.SERVERS",
            Stream.of(s).map(AsfslsUtils::putServerIntoBundle).toArray(Bundle[]::new)
        );
    }

    public static Intent makeMagicSpell(List<Server> servers){
        return makeMagicSpell(Stream.of(servers).toArray(Server[]::new));
    }
    public static Intent makeMagicSpell(Server[] servers){
        return new Intent()
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setAction("com.nao20010128nao.Wisecraft.ADD_MULTIPLE_SERVERS")
            .putExtra(
                "com.nao20010128nao.Wisecraft.SERVERS",
                Stream.of(servers)
                    .map(AsfslsUtils::putServerIntoBundle)
                    .toArray(Bundle[]::new)
            );
    }
}
