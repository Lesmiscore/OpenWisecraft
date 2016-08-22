package com.nao20010128nao.Wisecraft.misc;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import java.io.*;
import com.nao20010128nao.Wisecraft.*;

public class SprPair implements ServerPingResult {
	ServerPingResult a,b;

	public void setA(ServerPingResult a) {
		this.a = a;
	}

	public ServerPingResult getA() {
		return a;
	}

	public void setB(ServerPingResult b) {
		this.b = b;
	}

	public ServerPingResult getB() {
		return b;
	}

	@Override
	public byte[] getRawResult() {
		// TODO: Implement this method
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.write(PingSerializeProvider.doRawDump(a));
			dos.write(PingSerializeProvider.doRawDump(b));
			dos.flush();
		} catch (IOException e) {
			WisecraftError.report("SprPair#getRawResult",e);
		}
		return baos.toByteArray();
	}
}
