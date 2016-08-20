package com.nao20010128nao.Wisecraft.misc.pinger;

import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.pingEngine.*;
import java.io.*;
import java.util.*;

public class PingSerializeProvider 
{
	public static final Map<Class<? extends ServerPingResult>,Integer> PING_CLASS_NUMBER;
	
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
	
	static{
		Map<Class<? extends ServerPingResult>,Integer> pingClassNumber=new HashMap<>();
		//0x0000
		//0xABCD
		//A is mode(0 is PE, 1 is PC, f is other(SprPair))
		//B is reserved
		//C is reserved
		//D is id
		pingClassNumber.put(FullStat.class,0x0000);
		pingClassNumber.put(UnconnectedPing.UnconnectedPingResult.class,0x0001);
		pingClassNumber.put(Reply.class,0x1002);
		pingClassNumber.put(Reply19.class,0x1003);
		pingClassNumber.put(SprPair.class,0xf004);
		
		PING_CLASS_NUMBER=Collections.unmodifiableMap(pingClassNumber);
	}
}
