package com.nao20010128nao.Wisecraft.widget;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.preference.*;
import android.support.v4.content.*;
import android.util.*;
import android.widget.*;
import com.google.gson.*;
import com.google.gson.annotations.*;
import com.google.gson.reflect.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.api.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.json.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pc.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pe.*;
import com.nao20010128nao.Wisecraft.misc.ping.processors.*;

import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

abstract class PingWidgetImpl extends WisecraftWidgetBase {
    public static final int STATUS_ONLINE = 0;
    public static final int STATUS_OFFLINE = 1;
    public static final int STATUS_PENDING = 2;
    public static final String STATUS_OBSERVE_ACTION = "com.nao20010128nao.Wisecraft.PING_RESULT_WIDGET";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences widgetPref = getWidgetPref(context);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = Utils.newGson();
        widgetPref.edit().putInt("_version", 2).putInt("_version.data", 0).commit();
        {/*
            int version=widgetPref.getInt("_version", 2);
			switch (version) {
				case 0:{
						for(String key:new HashSet<>(widgetPref.getAll().keySet())){
			 				if("_version".startsWith(key))continue;
							
						}
						OldServerW1_3[] sa=gson.fromJson(pref.getString("servers", "[]"), OldServerW1_3[].class);
						List<OldServer35> ns=new ArrayList<>();
						for (OldServerW1_3 s:sa) {
							OldServer35 nso=new OldServer35();
							nso.ip = s.ip;
							nso.port = s.port;
							nso.mode = s.isPC ?1: 0;
							ns.add(nso);
						}
						pref.edit().putInt("serversJsonVersion", 1).putString("servers", gson.toJson(ns)).commit();
					}
				case 1:{
					
					
					}
				case 2:{
						/*Server[] sa=gson.fromJson(pref.getString("servers", "[]"), Server[].class);
						int prevLen=list.size();
						list.clear();
						sl.notifyItemRangeRemoved(0, prevLen);
						int curLen=sa.length;
						list.addAll(Arrays.asList(sa));
						sl.notifyItemRangeInserted(0, curLen);
					}*/
        }
        ServerPingProvider nspp;
        if (!pref.getBoolean("useAltServer", false)) {
            nspp = new NormalServerPingProvider();
        } else {
            nspp = new TcpServerPingProvider("160.16.103.57", 15687);
        }
        for (int wid : appWidgetIds) {
            Log.d("WisecraftWidgets", "onUpdate: " + wid);
            if (!widgetPref.contains(wid + "")) {
                RemoteViews rvs = new RemoteViews(context.getPackageName(), R.layout.ping_widget_init);
                appWidgetManager.updateAppWidget(wid, rvs);
                Log.d("WisecraftWidgets", "none: " + wid);
                continue;
            }
            Server s = gson.fromJson(widgetPref.getString(wid + "", "{}"), Server.class);
            NonBrodRevPingHandler ph = new NonBrodRevPingHandler();
            ph.id = wid;
            ph.c = context;
            ph.awm = appWidgetManager;
            nspp.putInQueue(s, ph);
            ServerStatusRemoteViewsWrapper viewHolder = new ServerStatusRemoteViewsWrapper(context, wid);
            RemoteViews rvs = (RemoteViews) viewHolder.getTag();
            viewHolder.pending(s, context);
            setupHandlers(rvs, context, wid);
            appWidgetManager.updateAppWidget(wid, rvs);
            Log.d("WisecraftWidgets", "with: " + wid + ": " + s);
        }
    }

    public static SharedPreferences getWidgetPref(Context context) {
        return context.getSharedPreferences("widgets", Context.MODE_PRIVATE);
    }

    public static WidgetData getWidgetData(Context c, int wid) {
        return Utils.newGson().fromJson(getWidgetPref(c).getString(wid + ".data", "{}"), WidgetData.class);
    }

    public static void setWidgetData(Context c, int wid, WidgetData data) {
        getWidgetPref(c).edit().putString(wid + ".data", Utils.newGson().toJson(data)).commit();
    }

    public static Server getServer(Context c, int wid) {
        return Utils.newGson().fromJson(getWidgetPref(c).getString(wid + "", "{}"), Server.class);
    }

    public static void setServer(Context c, int wid, Server data) {
        getWidgetPref(c).edit().putString(wid + "", Utils.newGson().toJson(data)).commit();
    }

    public static void setWidgetStatus(Context c, int wid, int status, boolean notify) {
        getWidgetPref(c).edit().putInt(wid + ".status", status).commit();
        if (notify)
            c.sendBroadcast(new Intent(STATUS_OBSERVE_ACTION).putExtra("wid", wid).putExtra("status", status));
    }

    public static void setWidgetStatus(Context c, int wid, int status) {
        setWidgetStatus(c, wid, status, false);
    }

    public static int getWidgetStatus(Context c, int wid) {
        return getWidgetPref(c).getInt(wid + ".status", STATUS_PENDING);
    }

    static void setupHandlers(RemoteViews rvs, Context context, int wid) {
        SharedPreferences widgetPref = getWidgetPref(context);
        Server s = Utils.newGson().fromJson(widgetPref.getString("" + wid, "{}"), Server.class);
        String addr = new StringBuilder("wisecraft://info/")
            .append(s.ip)
            .append('/')
            .append(s.port)
            .append('/')
            .append(s.mode == Protobufs.Server.Mode.PE ? "PE" : "PC")
            .toString();

        rvs.setOnClickPendingIntent(R.id.update, PendingIntent.getBroadcast(context, wid, new Intent(context, PingWidget.PingHandler.class).setAction("update").putExtra("wid", wid), 0));
        rvs.setOnClickPendingIntent(R.id.openServerStatus, PendingIntent.getActivity(context, wid * 100, new Intent(context, RequestedServerInfoActivity.class).setData(Uri.parse(addr)), 0));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int i : appWidgetIds) Log.d("WisecraftWidgets", "onDeleted: " + i);
        SharedPreferences widgetPref = getWidgetPref(context);
        SharedPreferences.Editor edt = widgetPref.edit();
        for (int i : appWidgetIds) edt.remove(i + "").remove(i + ".data");
        edt.commit();
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        SharedPreferences widgetPref = getWidgetPref(context);
        Map<String, Object> datas = new HashMap<>(widgetPref.getAll());
        SharedPreferences.Editor edt = widgetPref.edit();
        for (int i = 0; i < oldWidgetIds.length; i++) {
            Log.d("WisecraftWidgets", "onRestored(1): " + oldWidgetIds[i] + "=>" + newWidgetIds[i]);
            edt.remove(oldWidgetIds[i] + "").remove(oldWidgetIds[i] + ".data");
        }
        edt.commit();
        edt = widgetPref.edit();
        for (int i = 0; i < oldWidgetIds.length; i++) {
            Log.d("WisecraftWidgets", "onRestored(2): " + oldWidgetIds[i] + "=>" + newWidgetIds[i]);
            edt.putString(newWidgetIds[i] + "", datas.get(oldWidgetIds[i] + "") + "")
                .putString(newWidgetIds[i] + ".data", datas.get(oldWidgetIds[i] + ".data") + "");
        }
        edt.commit();
    }

    @Override
    public void onDisabled(Context context) {
        SharedPreferences widgetPref = getWidgetPref(context);
        SharedPreferences.Editor edt = widgetPref.edit();
        for (String key : new HashSet<>(widgetPref.getAll().keySet())) {
            if ("_version".startsWith(key)) continue;
            edt.remove(key);
        }
        edt.commit();
    }

    public static int styleToId(int style) {
        switch (style) {
            case 0:
                return R.layout.ping_widget_content;
            case 1:
                return R.layout.ping_widget_content_2;
            case 2:
                return R.layout.ping_widget_content_3;
        }
        return 0;
    }


    abstract static class PingHandlerImpl extends BroadcastReceiver {

        @Override
        public void onReceive(Context p1, Intent p2) {
            int wid = p2.getIntExtra("wid", 0);
            Log.d("WisecraftWidgets", "Update Issued: " + wid);
            SharedPreferences widgetPref = getWidgetPref(p1);
            Gson gson = Utils.newGson();
            Server s = gson.fromJson(widgetPref.getString(wid + "", "{}"), Server.class);
            NormalServerPingProvider nspp = new NormalServerPingProvider();
            NonBrodRevPingHandler ph = new NonBrodRevPingHandler();
            ph.id = wid;
            ph.c = p1;
            ph.awm = AppWidgetManager.getInstance(p1);
            ServerStatusRemoteViewsWrapper ssrvw = new ServerStatusRemoteViewsWrapper(p1, wid);
            RemoteViews rvs = (RemoteViews) ssrvw.getTag();
            ssrvw.pending(s, p1);
            setupHandlers(rvs, p1, wid);
            ph.awm.updateAppWidget(wid, rvs);
            setWidgetStatus(p1, wid, STATUS_PENDING, true);
            nspp.putInQueue(s, ph);
        }
    }

    static class NonBrodRevPingHandler implements ServerPingProvider.PingHandler {
        int id;
        Context c;
        AppWidgetManager awm;

        @Override
        @ServerInfoParser
        public void onPingArrives(ServerStatus s) {
            Log.d("WisecraftWidgets", "Ping OK for: " + id);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);

            ServerStatusRemoteViewsWrapper ssrvw = new ServerStatusRemoteViewsWrapper(c, id);
            RemoteViews rvs = (RemoteViews) ssrvw.getTag();

            ssrvw.setStatColor(ContextCompat.getColor(c, R.color.stat_ok));
            final CharSequence title;
            List<String> players = Collections.emptyList();
            if (s.response instanceof FullStat) {//PE
                FullStat fs = (FullStat) s.response;
                Map<String, String> m = fs.getDataAsMap();
                if (m.containsKey("hostname")) {
                    title = m.get("hostname");
                } else if (m.containsKey("motd")) {
                    title = m.get("motd");
                } else {
                    title = s.toString();
                }
                ssrvw.setServerPlayers(m.get("numplayers"), m.get("maxplayers"));
                players = fs.getPlayerList();
            } else if (s.response instanceof Reply19) {//PC 1.9~
                Reply19 rep = (Reply19) s.response;
                if (rep.description == null) {
                    title = s.toString();
                } else {
                    title = rep.description.text;
                }
                ssrvw.setServerPlayers(rep.players.online, rep.players.max);
                if (rep.players != null) {
                    if (rep.players.sample != null) {
                        final ArrayList<String> sort = new ArrayList<>();
                        for (Reply19.Player o : rep.players.sample) {
                            sort.add(o.name);
                        }
                        players = sort;
                    }
                }
            } else if (s.response instanceof Reply) {//PC
                Reply rep = (Reply) s.response;
                if (rep.description == null) {
                    title = s.toString();
                } else {
                    title = rep.description;
                }
                ssrvw.setServerPlayers(rep.players.online, rep.players.max);
                if (rep.players != null) {
                    if (rep.players.sample != null) {
                        final ArrayList<String> sort = new ArrayList<>();
                        for (Reply.Player o : rep.players.sample) {
                            sort.add(o.name);
                        }
                        players = sort;
                    }
                }
            } else if (s.response instanceof RawJsonReply) {//PC (Obfuscated)
                WisecraftJsonObject rep = ((RawJsonReply) s.response).json;
                if (!rep.has("description")) {
                    title = s.toString();
                } else {
                    title = Utils.parseMinecraftDescriptionJson(rep.get("description"));
                }
                ssrvw.setServerPlayers(rep.get("players").get("online").getAsInt(), rep.get("players").get("max").getAsInt());
                if (rep.has("players")) {
                    if (rep.get("players").has("sample")) {
                        final ArrayList<String> sort = new ArrayList<>();
                        for (WisecraftJsonObject o : rep.get("players").get("sample")) {
                            sort.add(o.get("name").getAsString());
                        }
                        players = sort;
                    }
                }
            } else if (s.response instanceof SprPair) {//PE?
                SprPair sp = ((SprPair) s.response);
                if (sp.getA() instanceof FullStat) {
                    FullStat fs = (FullStat) sp.getA();
                    Map<String, String> m = fs.getDataAsMap();
                    if (m.containsKey("hostname")) {
                        title = m.get("hostname");
                    } else if (m.containsKey("motd")) {
                        title = m.get("motd");
                    } else {
                        title = s.toString();
                    }
                    ssrvw.setServerPlayers(m.get("numplayers"), m.get("maxplayers"));
                    players = fs.getPlayerList();
                } else if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
                    UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) sp.getB();
                    title = res.getServerName();
                    ssrvw.setServerPlayers(res.getPlayersCount(), res.getMaxPlayers());
                } else {
                    title = s.toString();
                    ssrvw.setServerPlayers();
                }
            } else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {//PE
                UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) s.response;
                title = res.getServerName();
                ssrvw.setServerPlayers(res.getPlayersCount(), res.getMaxPlayers());
            } else {//Unreachable
                title = s.toString();
                ssrvw.setServerPlayers();
            }
            if (pref.getBoolean("serverListColorFormattedText", false)) {
                if (title instanceof String) {
                    ssrvw.setServerName(parseMinecraftFormattingCode(title.toString()));
                } else {
                    ssrvw.setServerName(title);
                }
            } else {
                if (title instanceof String) {
                    ssrvw.setServerName(deleteDecorations(title.toString()));
                } else {
                    ssrvw.setServerName(title.toString());
                }
            }
            players = new ArrayList<>(players);//cast List into ArrayList exactly to sort
            if (pref.getBoolean("sortPlayerNames", true))
                Collections.sort(players);
            Log.d("WisecraftWidgets", "size of players: " + players.size());

            ssrvw
                .setPingMillis(s.ping)
                .setServer(s)
                .setServerPlayers(players);

            setupHandlers(rvs, c, id);
            setWidgetStatus(c, id, STATUS_ONLINE, true);
            awm.updateAppWidget(id, rvs);
            awm.notifyAppWidgetViewDataChanged(id, R.id.players);
        }

        @Override
        public void onPingFailed(Server server) {
            Log.d("WisecraftWidgets", "Ping NG for: " + id);
            ServerStatusRemoteViewsWrapper ssrvw = new ServerStatusRemoteViewsWrapper(c, id);
            RemoteViews rvs = (RemoteViews) ssrvw.getTag();
            ssrvw.offline(server, c).setServerPlayers(Collections.emptyList());
            setupHandlers(rvs, c, id);
            setWidgetStatus(c, id, STATUS_OFFLINE, true);
            awm.updateAppWidget(id, rvs);
            awm.notifyAppWidgetViewDataChanged(id, R.id.players);
        }
    }

    public static class WidgetData {
        @SerializedName("style")
        public int style = 0;
    }


    abstract static class ListViewUpdaterImpl extends RemoteViewsService {

        @Override
        public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent p1) {
            List<String> array = p1.getStringArrayListExtra("list");
            Log.d("ListViewUpdater", "size of array: " + array.size());

            for (String s : array) {
                Log.d("ListViewUpdater", "array: " + s);
            }
            return new Factory(array, this, p1.getIntExtra("wid", AppWidgetManager.INVALID_APPWIDGET_ID));
        }


        static class Factory implements RemoteViewsService.RemoteViewsFactory {
            List<String> array;
            Context c;
            int wid;

            public Factory(List<String> list, Context service, int wid) {
                array = list;
                c = service;
                this.wid = wid;
            }

            @Override
            public void onCreate() {
                SharedPreferences widgetPref = getWidgetPref(c);
                if (widgetPref.contains(wid + ".players")) {
                    array = Utils.newGson().fromJson(widgetPref.getString(wid + ".players", "[]"), new TypeToken<ArrayList<String>>() {
                    }.getType());
                }
                for (String s : array) {
                    Log.d("ListViewUpdater", "final array for" + wid + ": " + s);
                }
            }

            @Override
            public RemoteViews getViewAt(int p1) {
                RemoteViews view = new RemoteViews(c.getPackageName(), R.layout.simple_list_item_1);
                view.setTextColor(android.R.id.text1, Color.WHITE);
                view.setTextViewText(android.R.id.text1, array.get(p1));
                Log.d("ListViewUpdater", "getViewAt: " + p1 + ",array: " + array.get(p1));
                return view;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public RemoteViews getLoadingView() {
                RemoteViews view = new RemoteViews(c.getPackageName(), R.layout.simple_list_item_1);
                view.setTextColor(android.R.id.text1, ServerInfoActivity.translucent(Color.WHITE));
                view.setTextViewText(android.R.id.text1, c.getResources().getString(R.string.loading));
                return view;
            }

            @Override
            public void onDataSetChanged() {
                onCreate();
            }

            @Override
            public int getCount() {
                return array.size();
            }

            @Override
            public int getViewTypeCount() {
                return getCount();
            }

            @Override
            public long getItemId(int p1) {
                return array.get(p1).hashCode();
            }

            @Override
            public void onDestroy() {

            }
        }
    }
}

public class PingWidget extends PingWidgetImpl {
    public static class Type2 extends PingWidget {
    }

    public static class Type3 extends PingWidget {
    }

    public static class PingHandler extends PingHandlerImpl {
    }

    public static class ListViewUpdater extends ListViewUpdaterImpl {
    }
}
