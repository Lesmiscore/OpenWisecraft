package com.nao20010128nao.McServerPingPong;
import java.io.*;
import java.util.*;

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
	public static String[] lines(String s)throws IOException{
		BufferedReader br=new BufferedReader(new StringReader(s));
		List<String> tmp=new ArrayList<>(4);
		String line=null;
		while(null!=(line=br.readLine()))tmp.add(line);
		return tmp.toArray(new String[tmp.size()]);
	}
}
