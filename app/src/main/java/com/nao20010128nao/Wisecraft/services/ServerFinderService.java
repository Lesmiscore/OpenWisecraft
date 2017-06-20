package com.nao20010128nao.Wisecraft.services;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pingMethods.*;

import java.util.*;

public class ServerFinderService extends Service {
    public static final String EXTRA_IP = "ip";
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_START_PORT = "sport";
    public static final String EXTRA_END_PORT = "eport";
    public static final String EXTRA_TAG = "tag";

    private static final String ACTION_DELETED = "action_deleted";
    private static final String ACTION_CANCEL = "action_cancel";

    private static Map<String, State> sessions = new SuppliedHashMap<>(tag -> {
        State stt = new State();
        stt.tag = tag;
        return stt;
    }, true);

    @Override
    public IBinder onBind(Intent p1) {
        return new InternalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String ip = intent.getStringExtra(EXTRA_IP);
        Protobufs.Server.Mode mode = (Protobufs.Server.Mode) intent.getSerializableExtra(EXTRA_MODE);
        int start = intent.getIntExtra(EXTRA_START_PORT, 0);
        int end = intent.getIntExtra(EXTRA_END_PORT, 0);
        explore(ip, start, end, mode);
        return START_NOT_STICKY;
    }

    private void updateNotification(String tag, int now, int max) {
        int id = tag.hashCode();
        Notification ntf = createProgressNotification(this, now, max, tag, sessions.get(tag).detected);
        NotificationManagerCompat.from(this).notify(id, ntf);
    }

    private static Notification createProgressNotification(Context c, int now, int max, String tag, Map<Integer, ServerStatus> servers) {
        NotificationCompat.Builder ntf = new NotificationCompat.Builder(c);
        // Add title like "Server Finder - ** servers found"
        ntf.setContentTitle(c.getResources().getString(R.string.serverFinderFound).replace("[COUNT]", servers.size() + ""));
        ntf.setContentText(c.getResources().getString(R.string.serverFinderChecked).replace("[PORTS]", now + ""));
        ntf.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        ntf.setColor(ThemePatcher.getMainColor(c));
        ntf.setProgress(max, now, false);
        if (servers.size() != 0) {
            List<Integer> l = Factories.arrayList(servers.keySet());
            Collections.sort(l);
            NotificationCompat.InboxStyle bts = new NotificationCompat.InboxStyle();
            for (int port : l) {
                bts.addLine(servers.get(port).toString());
            }
            ntf.setStyle(bts);
        }
        ntf.setSmallIcon(R.drawable.ic_search_black_48dp);
        ntf.setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_search_black_48dp));
        ntf.setContentIntent(PendingIntent.getActivity(c, tag.hashCode() ^ 800, new Intent(c, ServerFinderActivity.class).putExtra(EXTRA_TAG, tag), PendingIntent.FLAG_UPDATE_CURRENT));
        ntf.setOngoing(true);
        return ntf.build();
    }

    private void updateNotificationFinished(String tag) {
        updateNotificationFinished(this, tag);
    }

    private static void updateNotificationFinished(Context c, String tag) {
        int id = tag.hashCode();
        Notification ntf = createFinishedNotification(c, tag, sessions.get(tag).detected);
        NotificationManagerCompat.from(c).notify(id, ntf);
    }

    private static Notification createFinishedNotification(Context c, String tag, Map<Integer, ServerStatus> servers) {
        NotificationCompat.Builder ntf = new NotificationCompat.Builder(c);
        // Add title like "Server Finder - ** servers found"
        ntf.setContentTitle(c.getResources().getString(R.string.serverFinderFound).replace("[COUNT]", servers.size() + ""));
        ntf.setContentText(c.getResources().getString(R.string.serverFinderFinished));
        ntf.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        ntf.setColor(ThemePatcher.getMainColor(c));
        if (servers.size() != 0) {
            List<Integer> l = Factories.arrayList(servers.keySet());
            Collections.sort(l);
            NotificationCompat.InboxStyle bts = new NotificationCompat.InboxStyle();
            for (int port : l) {
                bts.addLine(servers.get(port).toString());
            }
            bts.setSummaryText(c.getResources().getString(R.string.serverFinderFinished));
            ntf.setStyle(bts);
        }
        ntf.setSmallIcon(R.drawable.ic_search_black_48dp);
        ntf.setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_search_black_48dp));
        ntf.setContentIntent(PendingIntent.getActivity(c, tag.hashCode() ^ 800, new Intent(c, ServerFinderActivity.class).putExtra(EXTRA_TAG, tag), PendingIntent.FLAG_UPDATE_CURRENT));
        return ntf.build();
    }

    private String explore(final String ip, final int startPort, final int endPort, final Protobufs.Server.Mode mode) {
        final String tag = Utils.randomText();
        sessions.get(tag).ip = ip;
        sessions.get(tag).start = startPort;
        sessions.get(tag).end = endPort;
        sessions.get(tag).mode = mode;

        AsyncTask<Void, ServerStatus, Void> at = new AsyncTask<Void, ServerStatus, Void>() {
            public Void doInBackground(Void... l) {
                final int max = endPort - startPort;

                int threads = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(ServerFinderService.this).getString("parallels", "6"));
                ServerPingProvider spp;
                if (mode == Protobufs.Server.Mode.PC) {
                    spp = new PCMultiServerPingProvider(threads);
                } else {
                    spp = new UnconnectedMultiServerPingProvider(threads);
                }
                sessions.get(tag).pinger = spp;

                update(0, max);
                for (int p = startPort; p <= endPort; p++) {
                    Server s = new Server();
                    s.ip = ip;
                    s.port = p;
                    s.mode = mode;
                    spp.putInQueue(s, new ServerPingProvider.PingHandler() {
                        public void onPingArrives(ServerStatus s) {
                            publishProgress(s);
                            update(-1, max);
                        }

                        public void onPingFailed(Server s) {
                            update(-1, max);
                        }
                    });
                }
                return null;
            }

            public void onProgressUpdate(ServerStatus... s) {
                ServerStatus ss = s[0];
                sessions.get(tag).detected.put(ss.port, ss);
            }

            private void update(final int now, final int max) {
                int remain = now == -1 ? sessions.get(tag).pinger.getQueueRemain() : now;
                updateNotification(tag, max - remain, max);
                if (remain == 0) {
                    sessions.get(tag).finished = true;
                    updateNotificationFinished(tag);
                }
            }
        };
        (sessions.get(tag).worker = at).execute();
        return tag;
    }

    public void cancel(String tag) {
        cancel(this, tag);
    }

    public static void cancel(Context c, String tag) {
        sessions.get(tag).worker.cancel(sessions.get(tag).cancelled = true);
        sessions.get(tag).pinger.clearAndStop();
        NotificationManagerCompat.from(c).cancel(tag.hashCode());
        updateNotificationFinished(c, tag);
    }

    public static void checkDead(String tag) {
        State state = sessions.get(tag);
        if ((state.finished | state.cancelled) & state.activityClosed & state.notificationRemoved) {
            sessions.remove(tag);
        }
    }

    public class InternalBinder extends Binder {
        public String startExploration(String ip, Protobufs.Server.Mode mode, int start, int end) {
            return explore(ip, start, end, mode);
        }

        public State getState(String tag) {
            return sessions.get(tag);
        }

        public void cancel(String tag) {
            ServerFinderService.this.cancel(tag);
            checkDead(tag);
        }
    }

    public static class NotificationDetector extends BroadcastReceiver {
        @Override
        public void onReceive(Context p1, Intent p2) {
            String tag = p2.getStringExtra(EXTRA_TAG);
            if (ACTION_DELETED.equals(p2.getAction())) {
                // notification was removed
                sessions.get(tag).notificationRemoved = true;
            } else if (ACTION_CANCEL.equals(p2.getAction())) {
                // cancel button was clicked
                cancel(p1, tag);
            }
            checkDead(tag);
        }
    }

    public static class State {
        public final Map<Integer, ServerStatus> detected = Collections.synchronizedMap(new HashMap<>());
        public volatile String tag, ip;
        public volatile AsyncTask<Void, ServerStatus, Void> worker;
        public volatile boolean finished = false, notificationRemoved = false, activityClosed = true, cancelled = false;
        public volatile int start, end;
        public volatile Protobufs.Server.Mode mode;
        ServerPingProvider pinger;
    }
}
