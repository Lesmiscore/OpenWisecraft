package com.nao20010128nao.Wisecraft.receivers;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.*;
import android.support.v4.content.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;

public class ServerCrawlerReceiver extends BroadcastReceiver {
    public ServerCrawlerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager ntfMng= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        ServerCrawlerManager crawler=new ServerCrawlerManager(context);
        long id=intent.getLongExtra("id",-1);
        Protobufs.ServerCrawlerEntry entry=crawler.getEntry(id);
        if(!entry.getEnabled()){
            return;
        }
        Server server=crawler.getServer(id);
        if(server==null){
            return;
        }
        NormalServerPingProvider nspp=new NormalServerPingProvider();
        nspp.putInQueue(server, new ServerPingProvider.PingHandler() {
            @Override
            public void onPingArrives(ServerStatus stat) {
                if(entry.getNotifyOnline()){
                    NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
                    builder.setSmallIcon(R.drawable.ic_launcher);
                    builder.setColor(ContextCompat.getColor(context,R.color.mainColor));
                    builder.setContentTitle(server.toString());
                    builder.setContentText(context.getResources().getString(R.string.crawlerServerOnline));
                    ntfMng.notify((int)id,builder.build());
                }
            }

            @Override
            public void onPingFailed(Server server) {
                if(entry.getNotifyOffline()){
                    NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
                    builder.setSmallIcon(R.drawable.ic_launcher);
                    builder.setColor(ContextCompat.getColor(context,R.color.mainColor));
                    builder.setContentTitle(server.toString());
                    builder.setContentText(context.getResources().getString(R.string.crawlerServerOffline));
                    ntfMng.notify((int)id,builder.build());
                }
            }
        });
    }
}
