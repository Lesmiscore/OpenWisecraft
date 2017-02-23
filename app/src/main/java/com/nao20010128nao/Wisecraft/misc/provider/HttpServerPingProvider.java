package com.nao20010128nao.Wisecraft.misc.provider;
import android.text.*;
import android.util.*;
import com.google.common.collect.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class HttpServerPingProvider implements ServerPingProvider
{
    String head;
    boolean offline;
    Queue<Map.Entry<Server,PingHandler>> queue=Queues.synchronizedQueue(Lists.<Map.Entry<Server,PingHandler>>newLinkedList());
	Thread pingThread=new PingThread();
    
    public HttpServerPingProvider(String host){
        if(TextUtils.isEmpty(host))throw new IllegalArgumentException("host");
        if(host.startsWith("http://")|host.startsWith("https://")){
            head=host;
        }else{
            head="http://"+host;
        }
        if(!head.endsWith("/"))head+="/";
    }
    
    public void putInQueue(Server server, PingHandler handler) {
        Utils.requireNonNull(server);
        Utils.requireNonNull(handler);
		Utils.prepareLooper();
        queue.add(new KVP<Server,PingHandler>(server, handler));
        if (!pingThread.isAlive()) {
            pingThread = new PingThread();
            pingThread.start();
        }
    }
    @Override
    public int getQueueRemain() {
        return queue.size();
    }
    @Override
    public void stop() {
        pingThread.interrupt();
    }

    @Override
    public void clearQueue() {
        queue.clear();
    }
	@Override
	public void clearAndStop() {
		clearAndStop();
		stop();
	}
    @Override
    public void offline() {
        offline=true;
    }

    @Override
    public void online() {
        offline=false;
    }


    private class PingThread extends Thread implements Runnable {
        @Override
        public void run() {
            Map.Entry<Server,PingHandler> now=null;
            while (!(queue.isEmpty()|isInterrupted())) {
                Log.d("HSPP", "Starting ping");
                try {
                    now = queue.poll();
                    if(offline){
                        Log.d("HSPP", "Offline");
                        try {
                            now.getValue().onPingFailed(now.getKey());
                        } catch (Throwable ex_) {

                        }
                        continue;
                    }
                    try {
                        ServerStatus stat=null;
                        Server s=now.getKey();
                        InputStream is=null;
                        try{
                            is=new URL(head + "ping?ip=" + s.ip + "&port=" + s.port + "&mode=" + s.mode).openConnection().getInputStream();
                            stat = PingSerializeProvider.loadFromServerDumpFile(is);
                        }finally{
                            if(is!=null)is.close();
                        }
                        try {
                            now.getValue().onPingArrives(stat);
                        } catch (Throwable f) {

                        }
                    } catch (Throwable e) {
                        WisecraftError.report("HttpServerPingProvider",e);
                        try {
                            now.getValue().onPingFailed(now.getKey());
                        } catch (Throwable ex_) {

                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                Log.d("HSPP", "Next");
            }
        }
	}
}
