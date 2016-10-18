package com.nao20010128nao.Wisecraft.misc.provider;
import android.text.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class TcpServerPingProvider implements ServerPingProvider
{
    String host;int port;
    boolean offline;
    Queue<Map.Entry<Server,PingHandler>> queue=new LinkedList<>();
	Thread pingThread=new PingThread();
    
    public TcpServerPingProvider(String host,int port){
        if(TextUtils.isEmpty(host))throw new IllegalArgumentException("host");
		if(port<1|port>65535)throw new IllegalArgumentException("port");
        this.host=host;
		this.port=port;
    }
    
    public void putInQueue(Server server, PingHandler handler) {
        Utils.requireNonNull(server);
        Utils.requireNonNull(handler);
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
                        Log.d("TSPP", "Offline");
                        try {
                            now.getValue().onPingFailed(now.getKey());
                        } catch (Throwable ex_) {

                        }
                        continue;
                    }
                    try {
                        ServerStatus stat=null;
                        Server s=now.getKey();
						Socket sock=null;
                        InputStream is=null;
                        try{
							sock=new Socket(host,port);
							DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
							dos.writeUTF(s.ip);
							dos.writeInt(s.port);
							dos.writeInt(s.mode);
							dos.flush();
                            is=new BufferedInputStream(sock.getInputStream());
                            stat = PingSerializeProvider.loadFromServerDumpFile(is);
                        }finally{
							try {
								if (sock != null)sock.close();
								if (is != null)is.close();
							} catch (Throwable e) {
								WisecraftError.report("TcpServerPingProvider",e);
							}
                        }
                        try {
                            now.getValue().onPingArrives(stat);
                        } catch (Throwable f) {

                        }
                    } catch (Throwable e) {
                        WisecraftError.report("TcpServerPingProvider",e);
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
