package com.nao20010128nao.Wisecraft.misc.pinger.pe;

import android.annotation.TargetApi;

import com.nao20010128nao.OTC.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import java.util.*;
import android.util.*;

public class FullStat implements ServerPingResult {
	static byte NULL = '\0';
	static byte SPACE = ' ';

	private Map<String, String> datas = new OrderTrustedMap<>();
	private ArrayList<String> playerList = new ArrayList<>();
	private byte[] raw;

	@TargetApi(9)
	public FullStat(byte[] data) {
		raw=PingerUtils.copyOf(data,data.length);
		data = PingerUtils.trim(data);
		byte[][] temp = PingerUtils.split(data);
		byte[] d;
		int dataEnds = 0;
		for (int i = 2; i < temp.length; i++) {
			if ((d = temp[i]).length == 0 ? false : d[0] == 1) {
				dataEnds = i;
				break;
			}
		}

		if ((dataEnds % 2) == 0)
			dataEnds--;

		for (int i = 2; i < dataEnds; i += 2) {
			String k = new String(temp[i], CompatCharsets.UTF_8).trim();
			String v = new String(temp[i + 1], CompatCharsets.UTF_8).trim();
			if ("".equals(k) | "".equals(v))
				continue;
			datas.put(k, v);
		}

		playerList = new ArrayList<String>();
		for (int i = dataEnds + 2; i < temp.length; i++)
			playerList.add(new String(temp[i], CompatCharsets.UTF_8).trim());
			
		{
			for(Map.Entry<String,String> dat:datas.entrySet()){
				Log.d("fullStat",dat.getKey()+":"+dat.getValue());
			}
			for(String s:playerList){
				Log.d("fullStat",s);
			}
		}
	}

	public Map<String, String> getData() {
		return Collections.unmodifiableMap(datas);
	}

	public List<String> getPlayerList() {
		return Collections.unmodifiableList(playerList);
	}

	@Override
	public byte[] getRawResult() {
		// TODO: Implement this method
		return PingerUtils.trim(raw);
	}
}
