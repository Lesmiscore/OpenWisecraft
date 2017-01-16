package com.nao20010128nao.Wisecraft.misc.pinger;

import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import java.io.*;
import java.util.*;

public class PingSerializeProvider 
{
	public static final Map<Class<? extends ServerPingResult>,Integer> PING_CLASS_NUMBER;
	public static final Map<Integer,Class<? extends ServerPingResult>> PING_CLASS_NUMBER_REVERSED;
	
	public static byte[] doRawDump(ServerPingResult spr){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		byte[] raw=spr.getRawResult();
		try {
			dos.writeInt(PING_CLASS_NUMBER.get(spr.getClass()));
			dos.writeInt(raw.length);
			dos.write(raw);
			dos.flush();
		} catch (IOException e) {
			WisecraftError.report("PingSerializeProvider#doRawDump",e);
		}
		return baos.toByteArray();
	}
	public static byte[] doRawDumpForFile(ServerPingResult spr){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			baos.write("WisecraftPngRDmp".getBytes());//16bytes
			baos.write(doRawDump(spr));
		} catch (IOException e) {
			WisecraftError.report("PingSerializeProvider#doRawDumpForFile",e);
		}
		return baos.toByteArray();
	}
    
    public static byte[] dumpServerForFile(ServerStatus server){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        try {
            baos.write("WisecraftPngRngD".getBytes());//16bytes
            baos.write(doRawDump(server.response));
            DataOutputStream dos=new DataOutputStream(baos);
            dos.writeLong(server.ping);
            dos.writeUTF(server.ip);
            dos.writeInt(server.port);
			dos.writeInt(server.mode);
        } catch (IOException e) {
            WisecraftError.report("PingSerializeProvider#doRawDumpForFile",e);
        }
		return baos.toByteArray();
    }
	
	public static ServerPingResult loadFromRawDump(byte[] data){
		return loadFromRawDump(new ByteArrayInputStream(data));
	}
	public static ServerPingResult loadFromRawDump(InputStream is){
		return loadFromRawDump(new DataInputStream(is));
	}
	public static ServerPingResult loadFromRawDump(DataInputStream dis){
		try {
			ServerPingResult result;int resultClassNumber=dis.readInt();
			Class resultClass=PING_CLASS_NUMBER_REVERSED.get(resultClassNumber);
			byte[] resultBytes=new byte[dis.readInt()];
			dis.readFully(resultBytes);
			switch (resultClassNumber) {
				case 0x0000:
					result = new FullStat(resultBytes);
					break;
				case 0x0001:
					result = new UnconnectedPing.UnconnectedPingResult(new String(resultBytes,32,resultBytes.length-32, CompatCharsets.UTF_8), 0, resultBytes);
					break;
				case 0x1002:case 0x1003:
					String json=new String(resultBytes, CompatCharsets.UTF_8);
					result = new Gson().fromJson(json, (Class<? extends ServerPingResult>)resultClass);
					((PCQueryResult)result).setRaw(json);
					break;
				case 0xf004:
					SprPair pair = new SprPair();
					DataInputStream dis2 = new DataInputStream(new ByteArrayInputStream(resultBytes));
					pair.setA(loadFromRawDump(dis2));
					pair.setB(loadFromRawDump(dis2));
					result = pair;
					break;
				case 0x1005:
					String json2=new String(resultBytes, CompatCharsets.UTF_8);
					result = new RawJsonReply(json2);
					((PCQueryResult)result).setRaw(json2);
					break;

				default:
					result = null;
					break;
			}
			return result;
		} catch (Throwable e) {
			WisecraftError.report("PingSerializeProvider#loadFromRawDump(DataInputStream)",e);
			return null;
		}
	}
	
	public static ServerPingResult loadFromRawDumpFile(DataInputStream dis){
        int version;
		try {
			byte[] rdataHeader=new byte["WisecraftPngRDmp".length()];
			dis.readFully(rdataHeader);
            version=checkHeader(rdataHeader);
			if (version==-1) {
				throw new IllegalArgumentException("InputStream has an invalid content!");
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to check the header of the InputStream.");
		}
		return loadFromRawDump(dis);
	}
	public static ServerPingResult loadFromRawDumpFile(InputStream dis){
		return loadFromRawDumpFile(new DataInputStream(dis));
	}
	public static ServerPingResult loadFromRawDumpFile(byte[] data){
		return loadFromRawDumpFile(new ByteArrayInputStream(data));
	}
    
    public static ServerStatus loadFromServerDumpFile(DataInputStream dis){
        int version;
        try {
            byte[] rdataHeader=new byte["WisecraftPngRDmp".length()];
            dis.readFully(rdataHeader);
            version=checkHeader(rdataHeader);
            if (version==-1) {
                throw new IllegalArgumentException("InputStream has an invalid content!");
            }
            if(version==0){
                throw new IllegalArgumentException("Unsupported type! Use loadFromDumpFile() instead.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to check the header of the InputStream.");
        }
        try {
            ServerStatus result=new ServerStatus();
            result.response = loadFromRawDump(dis);
            result.ping = dis.readLong();
            result.ip = dis.readUTF();
            result.port = dis.readInt();
			result.mode=dis.readInt();
            return result;
        } catch (IOException e) {
            return null;
        }
    }
    public static ServerStatus loadFromServerDumpFile(InputStream dis){
        return loadFromServerDumpFile(new DataInputStream(dis));
    }
    public static ServerStatus loadFromServerDumpFile(byte[] data){
        return loadFromServerDumpFile(new ByteArrayInputStream(data));
    }
    
    private static int checkHeader(byte[] header){
        {
            byte[] head="WisecraftPngRDmp".getBytes();
            if(Arrays.equals(header,head))return 0;//only ping data
        }
        {
            byte[] head="WisecraftPngRngD".getBytes();
            if(Arrays.equals(header,head))return 1;//includes ip&port&mode
        }
        return -1;
    }
	
	static{
		Map<Class<? extends ServerPingResult>,Integer> pingClassNumber=new HashMap<>();
		//0x0000
		//0xABCD
		//A is mode(0 is PE, 1 is PC, f is other(SprPair))
		//B is reserved
		//C is reserved
		//D is id
		pingClassNumber.put(FullStat.class                              ,0x0000);
		pingClassNumber.put(UnconnectedPing.UnconnectedPingResult.class ,0x0001);
		pingClassNumber.put(Reply.class                                 ,0x1002);
		pingClassNumber.put(Reply19.class                               ,0x1003);
		pingClassNumber.put(SprPair.class                               ,0xf004);
		pingClassNumber.put(RawJsonReply.class                          ,0x1005);
		
		PING_CLASS_NUMBER=Collections.unmodifiableMap(pingClassNumber);
		
		
		Map<Integer,Class<? extends ServerPingResult>> pingClassNumbetReversed=new HashMap<>();
		pingClassNumbetReversed.put(0x0000,FullStat.class);
		pingClassNumbetReversed.put(0x0001,UnconnectedPing.UnconnectedPingResult.class);
		pingClassNumbetReversed.put(0x1002,Reply.class);
		pingClassNumbetReversed.put(0x1003,Reply19.class);
		pingClassNumbetReversed.put(0xf004,SprPair.class);
		pingClassNumbetReversed.put(0x1005,RawJsonReply.class);
		
		PING_CLASS_NUMBER_REVERSED=Collections.unmodifiableMap(pingClassNumbetReversed);
	}
}
