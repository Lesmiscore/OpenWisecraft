package com.nao20010128nao.Wisecraft.misc.pinger.pe;

import android.annotation.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;

import java.util.*;

public class FullStat implements ServerPingResult,PEPingResult {
	static byte NULL = '\0';
	static byte SPACE = ' ';

	private List<Map.Entry<String, String>> datas = new ArrayList<>();
	private Map<String, String> mapDatas = new OrderTrustedMap<>();
	private List<String> playerList = new ArrayList<>();
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
			if ("".equals(k))
				continue;
			datas.add(new KVP<>(k, v));
			mapDatas.put(k,v);
		}

		playerList = new ArrayList<>();
		for (int i = dataEnds + 2; i < temp.length; i++)
			playerList.add(new String(temp[i], CompatCharsets.UTF_8).trim());
	}

	public List<Map.Entry<String, String>> getData() {
		return Collections.unmodifiableList(datas);
	}
	
	public Map<String, String> getDataAsMap() {
		return Collections.unmodifiableMap(mapDatas);
	}
	
	public List<String> getPlayerList() {
		return Collections.unmodifiableList(playerList);
	}

	@Override
	public byte[] getRawResult() {
		return PingerUtils.trim(raw);
	}
}
