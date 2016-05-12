package com.nao20010128nao.Wisecraft.misc.pinger.pe;

import java.util.*;

import com.nao20010128nao.Wisecraft.misc.pinger.ServerPingResult;
import com.nao20010128nao.Wisecraft.misc.pinger.Utils;
import com.nao20010128nao.OTC.OrderTrustedMap;

public class FullStat implements ServerPingResult {
	static byte NULL = 00;
	static byte SPACE = 20;

	private Map<String, String> datas = new OrderTrustedMap<>();
	private ArrayList<String> playerList = new ArrayList<>();

	public FullStat(byte[] data) {
		data = Utils.trim(data);
		byte[][] temp = Utils.split(data);
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
			String k = new String(temp[i]);
			String v = new String(temp[i + 1]);
			if ("".equals(k) | "".equals(v))
				continue;
			datas.put(k, v);
		}

		playerList = new ArrayList<String>();
		for (int i = dataEnds + 2; i < temp.length; i++)
			playerList.add(new String(temp[i]));
	}

	public Map<String, String> getData() {
		return Collections.unmodifiableMap(datas);
	}

	public List<String> getPlayerList() {
		return Collections.unmodifiableList(playerList);
	}
}
