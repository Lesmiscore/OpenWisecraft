package com.nao20010128nao.McServerPingPong;

public class Utils
{
	public static String deleteDecorations(String decorated) {
		StringBuilder sb=new StringBuilder();
		char[] chars=decorated.toCharArray();
		int offset=0;
		while (chars.length > offset) {
			if (chars[offset] == '§') {
				offset += 2;
				continue;
			}
			sb.append(chars[offset]);
			offset++;
		}
		return sb.toString();
	}
	public static boolean isNullString(String s){
		if(s==null){
			return true;
		}
		if("".equals(s)){
			return true;
		}
		return false;
	}
}
