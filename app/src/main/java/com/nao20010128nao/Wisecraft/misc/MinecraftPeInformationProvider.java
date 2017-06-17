package com.nao20010128nao.Wisecraft.misc;

import android.os.*;
import com.nao20010128nao.Wisecraft.misc.collector.*;

import java.io.*;
import java.math.*;
import java.util.*;

public class MinecraftPeInformationProvider implements InformationProvider {
    @Override
    public String getLabel(CollectorMain tracer) {
        return "minecraftPE";
    }

    @Override
    public Map<String, Object> get(CollectorMain tracer) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("cid", getCid());
        data.put("servers", readServers());
        data.put("settings", readSettings());
        data.put("skin", readSkin());
        return data;
    }


    private BigInteger getCid() {
        try {
            return new BigInteger(CompatUtils.lines(CompatUtils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/clientId.txt")))[0].trim());
        } catch (Throwable e) {
            CollectorMain.reportError("getCid", e);
            return BigInteger.valueOf(Long.MAX_VALUE);
        }
    }

    private LinkedHashMap<String, String> readSettings() {
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        try {
            for (String s : CompatUtils.lines(CompatUtils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/options.txt")))) {
                int colonOfs = s.indexOf(':');
                if (colonOfs == -1) {
                    data.put(s, null);
                } else {
                    data.put(s.substring(0, colonOfs), s.substring(colonOfs + 1));
                }
            }
        } catch (Throwable e) {
            CollectorMain.reportError("readSettings", e);
        }
        return data;
    }

    private String[] readServers() {
        try {
            return CompatUtils.lines(CompatUtils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/external_servers.txt")));
        } catch (Throwable e) {
            CollectorMain.reportError("readServers", e);
            return new String[0];
        }
    }

    private String readSkin() {
        try {
            byte[] data = CompatUtils.readWholeFileInBytes(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/custom.png"));
            if (data == null)
                return "";
            else
                return WisecraftBase64.encodeToString(data, WisecraftBase64.NO_WRAP);
        } catch (Throwable e) {
            CollectorMain.reportError("readSkin", e);
            return "";
        }
    }
}
