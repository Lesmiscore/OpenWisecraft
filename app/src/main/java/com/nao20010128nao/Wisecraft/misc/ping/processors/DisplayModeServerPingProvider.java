package com.nao20010128nao.Wisecraft.misc.ping.processors;

import com.google.common.base.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.localServerList.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pe.*;

import java.io.*;
import java.security.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.ping.methods.pe.UnconnectedPing.*;

/**
 * Created by lesmi on 17/10/21.
 */
@ForDisplayMode
public class DisplayModeServerPingProvider implements ServerPingProvider {
    @Override
    public void putInQueue(Server server, PingHandler handler) {
        new Thread(()->{
            if(!"localhost".equals(server.ip)){
                handler.onPingFailed(server);
                return;
            }
            DisplayModeLocalServerList data=new DisplayModeLocalServerList();
            switch (server.port){
                case 19132:// online
                case 19500:{// named
                    FullStat fullStat;
                    {
                        ByteArrayOutputStream res=new ByteArrayOutputStream();
                        DataOutputStream resW=new DataOutputStream(res);

                        try {
                            //full stat
                            resW.write(0);
                            resW.writeInt(1);
                            //73 70 6C 69 74 6E 75 6D 00 80 00
                            resW.write((byte) 0x73);
                            resW.write((byte) 0x70);
                            resW.write((byte) 0x6c);
                            resW.write((byte) 0x69);
                            resW.write((byte) 0x74);
                            resW.write((byte) 0x6e);
                            resW.write((byte) 0x75);
                            resW.write((byte) 0x6d);
                            resW.write((byte) 0x00);
                            resW.write((byte) 0x80);
                            resW.write((byte) 0x00);
                            //KV
                            Map<String, String> kv = new HashMap<>();
                            kv.put("gametype", "SMP");
                            kv.put("map", "wisecraft");
                            kv.put("server_engine", "Wisecraft Ghost Ping");
                            kv.put("hostport", "");
                            kv.put("whitelist", "on");
                            kv.put("plugins", "Wisecraft Ghost Ping: ");
                            kv.put("hostname", "§3Wisecraft");
                            kv.put("numplayers", Integer.MAX_VALUE + "");
                            kv.put("version", "v0.15.10 alpha");
                            kv.put("game_id", "MINECRAFTPE");
                            kv.put("hostip", "0.0.0.0");
                            kv.put("maxplayers", Integer.MAX_VALUE + "");
                            for (Map.Entry<String, String> ent : kv.entrySet()) {
                                resW.write(ent.getKey().getBytes(CompatCharsets.UTF_8));
                                resW.write(0);
                                resW.write(ent.getValue().getBytes(CompatCharsets.UTF_8));
                                resW.write(0);
                            }
                            resW.write(0);
                            //01 70 6C 61 79 65 72 5F 00 00
                            resW.write((byte) 0x01);
                            resW.write((byte) 0x70);
                            resW.write((byte) 0x6c);
                            resW.write((byte) 0x61);
                            resW.write((byte) 0x79);
                            resW.write((byte) 0x65);
                            resW.write((byte) 0x72);
                            resW.write((byte) 0x5f);
                            resW.write((byte) 0x00);
                            resW.write((byte) 0x00);
                            //players
                            for (Server s : data.load()) {
                                resW.write((s.ip + ":" + s.port + "\0").getBytes(CompatCharsets.UTF_8));
                            }
                            resW.write(0);
                        } catch (IOException e) {
                            WisecraftError.report("DisplayModeServerPingProvider",e);
                        }
                        fullStat=new FullStat(res.toByteArray());
                    }
                    UnconnectedPing.UnconnectedPingResult ucp;
                    {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream(baos);
                        SecureRandom sr=new SecureRandom();
                        List<String> datas = new ArrayList<>();
                        datas.add("MCPE");//MCPE
                        datas.add("§3Wisecraft");//Server name
                        datas.add("81");//Protocol (81=0.15.2)
                        datas.add("0.15.10");//Version (Displayed on MCPE)
                        datas.add(Integer.MAX_VALUE + "");//Players count
                        datas.add(Integer.MAX_VALUE + "");//Max players
                        String info=Joiner.on(';').join(datas);
                        try {
                            dos.write(0x1c);
                            dos.writeLong(sr.nextLong());
                            dos.writeLong(sr.nextLong());
                            dos.writeInt(MAGIC_1ST);
                            dos.writeInt(MAGIC_2ND);
                            dos.writeInt(MAGIC_3RD);
                            dos.writeInt(MAGIC_4TH);

                            dos.writeUTF(info);
                        }catch (IOException e) {
                            WisecraftError.report("DisplayModeServerPingProvider",e);
                        }
                        ucp=new UnconnectedPingResult(info,100,baos.toByteArray());
                    }
                    ServerStatus status=new ServerStatus();
                    server.cloneInto(status);
                    status.ping=100;
                    status.response=new SprPair(fullStat,ucp);
                    handler.onPingArrives(status);
                }
                break;
                case 19133:// waiting (no reply)
                    break;
                case 19134:// offline
                    handler.onPingFailed(server);
                    break;
                case 25565:// PC
                    break;
            }
        }).start();
    }

    @Override
    public int getQueueRemain() {
        return 0;
    }

    @Override
    public void stop() {

    }

    @Override
    public void clearQueue() {

    }

    @Override
    public void offline() {

    }

    @Override
    public void online() {

    }

    @Override
    public void clearAndStop() {

    }

    @Override
    public String getClassName() {
        return null;
    }
}
