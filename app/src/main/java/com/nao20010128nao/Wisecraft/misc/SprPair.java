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
		byte[] rawA,rawB;
		rawA=a.getRawResult();
		rawB=b.getRawResult();
		try {
			dos.writeInt(rawA.length);
			dos.write(rawA);
			dos.writeInt(rawB.length);
			dos.write(rawB);
			dos.flush();
		} catch (IOException e) {
			WisecraftError.report("SprPair#getRawResult",e);
		}
		return baos.toByteArray();
	}
}
