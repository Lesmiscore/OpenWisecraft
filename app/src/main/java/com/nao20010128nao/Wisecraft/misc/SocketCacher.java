package com.nao20010128nao.Wisecraft.misc;

import com.google.common.collect.*;

import java.net.*;
import java.util.*;

/**
 * Created by lesmi on 17/10/06.
 */

public class SocketCacher {
    ThrowableFunction<Socket> socketSpawner=Socket::new;
    Queue<Socket> cached= Queues.newConcurrentLinkedQueue();

    public SocketCacher(){}
    public SocketCacher(ThrowableFunction<Socket> socketSpawner){
        this.socketSpawner=socketSpawner;
    }

    public void cache(Socket sock){
        if(sock.isClosed())return;
        cached.add(sock);
    }

    public Socket get() throws Throwable {
        if (cached.isEmpty()){
            return socketSpawner.call();
        }else{
            Socket sock;
            while ((sock=cached.poll()).isClosed());
            return sock;
        }
    }
}
