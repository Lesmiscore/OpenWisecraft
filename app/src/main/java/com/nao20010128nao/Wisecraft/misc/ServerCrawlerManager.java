package com.nao20010128nao.Wisecraft.misc;

import android.app.*;
import android.content.*;
import com.annimon.stream.*;
import com.google.common.collect.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.receivers.*;

import java.io.*;
import java.security.*;
import java.util.*;
import java.util.Objects;

import com.google.common.base.*;

/**
 * Created by nao on 2017/06/14.
 */
public class ServerCrawlerManager {
    private final Context context;
    private List<Protobufs.ServerCrawlerEntry> entries;
    private final File file;
    private static final SecureRandom sr = new SecureRandom();
    private static final WeakHashMap<ServerCrawlerManager, Object> instances = new WeakHashMap<>();

    public ServerCrawlerManager(Context c) {
        context = c;
        file = new File(c.getFilesDir(), "server_crawlers_proto.bin");
        instances.put(this, toString());
        reload();
    }

    private void reload() {
        if (file.exists()) {
            try {
                entries = new ArrayList<>(Protobufs.ServerCrawlerSet.parseFrom(Utils.readWholeFileInBytes(file)).getEntriesList());
            } catch (Throwable e) {
                WisecraftError.report("ServerCrawlerManager", e);
                entries = new ArrayList<>();
            }
        } else {
            entries = new ArrayList<>();
        }
    }

    public List<Protobufs.ServerCrawlerEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public boolean commit() {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(Protobufs.ServerCrawlerSet.newBuilder().addAllEntries(entries).build().toByteArray());
            return true;
        } catch (Throwable e) {
            WisecraftError.report("ServerCrawlerManager", e);
            return false;
        } finally {
            Utils.safeClose(os);
            queryReload();
        }
    }

    public void schedule(Protobufs.ServerCrawlerEntry entry) {
        Protobufs.ServerCrawlerEntry work;
        // give an id here: identify one with intent
        work = entry.toBuilder().setId(sr.nextLong()).build();
        work = moveStartToNextExecution(work);
        entries.add(work);
        // schedule next
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME,
            work.getStart(), work.getInterval(),
            PendingIntent.getBroadcast(
                context, (int) work.getId(),
                new Intent(context, ServerCrawlerReceiver.class).putExtra("id", work.getId()),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        );
        // commit now
        commit();
    }

    public void reschedule() {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Stream.of(entries)
            .map(Protobufs.ServerCrawlerEntry::getId)
            .map(Long::intValue)
            .map(a -> PendingIntent.getBroadcast(context, a, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
            .forEach(alarm::cancel);
        for (Protobufs.ServerCrawlerEntry entry : entries) {
            alarm.setRepeating(AlarmManager.ELAPSED_REALTIME,
                entry.getStart(), entry.getInterval(),
                PendingIntent.getBroadcast(
                    context, (int) entry.getId(),
                    new Intent(context, ServerCrawlerReceiver.class).putExtra("id", entry.getId()),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            );
        }
    }

    public boolean setEnabled(long id, boolean value) {
        doTransform(a -> {
            if (a.getId() == id) {
                return a.toBuilder().setEnabled(value).build();
            }
            return a;
        });
        return commit();
    }

    public boolean delete(long id) {
        return entries.remove(getEntry(id)) && commit();
    }

    public boolean setServer(long id, Server server) {
        doTransform(a -> {
            if (a.getId() == id) {
                return a.toBuilder().setServer(server.toProtobufServer()).build();
            }
            return a;
        });
        return commit();
    }

    public Server getServer(long id) {
        return Server.from(getEntry(id).getServer());
    }

    public Protobufs.ServerCrawlerEntry getEntry(long id) {
        for (Protobufs.ServerCrawlerEntry entry : entries) {
            if (entry.getId() == id) {
                return entry;
            }
        }
        return null;
    }

    public void setEntry(long id, Protobufs.ServerCrawlerEntry entry) {
        Protobufs.ServerCrawlerEntry entry_ = entry.toBuilder().setId(id).build();
        doTransform(ent -> ent.getId() == id ? entry_ : ent);
    }

    private void doTransform(Function<Protobufs.ServerCrawlerEntry, Protobufs.ServerCrawlerEntry> func) {
        entries = new ArrayList<>(Lists.transform(entries, func));
    }

    private Protobufs.ServerCrawlerEntry moveStartToNextExecution(Protobufs.ServerCrawlerEntry entry) {
        long start = entry.getStart();
        long interval = entry.getInterval();
        while (start < System.currentTimeMillis()) {
            start += interval;
        }
        if (start == entry.getStart()) {
            return entry;
        } else {
            return entry.toBuilder().setStart(start).build();
        }
    }


    private static synchronized void queryReload() {
        Stream.of(instances.keySet()).filter(Utils::nonNull).forEach(ServerCrawlerManager::reload);
    }
}
