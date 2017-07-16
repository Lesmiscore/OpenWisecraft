package com.nao20010128nao.Wisecraft.activity;

import com.annimon.stream.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

//Server Sort Part
abstract class ServerListActivityBase2 extends ServerListActivityBase3 {
    public void doSort(final List<Server> sl, final SortKind sk, final SortFinishedCallback sfc) {
        new Thread(() -> {
            final List<Server> sortingServer = sk.doSort(sl);
            runOnUiThread(() -> sfc.onSortFinished(sortingServer));
        }).start();
    }

    public enum SortKind {
        BRING_ONLINE_SERVERS_TO_TOP {
            public List<Server> doSort(List<Server> list) {
                Stream<Server> online, offline;
                online = Stream.of(list).filter(s->s instanceof ServerStatus);
                offline = Stream.of(list).filterNot(s->s instanceof ServerStatus);
                return Stream.of(online,offline)
                    .reduce(Stream.<Server>of(),Stream::concat)
                    .toList();
            }
        },
        IP_AND_PORT {
            public List<Server> doSort(List<Server> list) {
                return Stream.of(list)
                    .sorted(ServerSorter.INSTANCE)
                    .toList();
            }
        },
        ONLINE_AND_OFFLINE {
            public List<Server> doSort(List<Server> list) {
                Stream<Server> online, offline;
                online = Stream.of(list).filter(s->s instanceof ServerStatus);
                offline = Stream.of(list).filterNot(s->s instanceof ServerStatus);
                return Stream.of(online,offline)
                    .map(s->s.sorted(ServerSorter.INSTANCE))
                    .reduce(Stream.<Server>of(),Stream::concat)
                    .toList();
            }
        };

        public abstract List<Server> doSort(List<Server> list);
    }

    public interface SortFinishedCallback {
        void onSortFinished(List<Server> result);
    }
}
