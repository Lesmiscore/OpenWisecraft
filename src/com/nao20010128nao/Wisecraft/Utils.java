package com.nao20010128nao.Wisecraft;
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
	public static boolean writeToFile(File f,String content){
		FileWriter fw=null;
		try{
			(fw=new FileWriter(f)).write(content);
			return true;
		}catch(Throwable e){
			return false;
		}finally{
			try {
				if (fw != null)fw.close();
			} catch (IOException e) {}
		}
	}
	public static String readWholeFile(File f){
		FileReader fr=null;char[] buf=new char[8192];
		StringBuilder sb=new StringBuilder(8192);
		try{
			fr=new FileReader(f);
			while(true){
				int r=fr.read(buf);
				if(r<=0){
					break;
				}
				sb.append(buf,0,r);
			}
			return sb.toString();
		}catch(Throwable e){
			return null;
		}finally{
			try {
				if (fr != null)fr.close();
			} catch (IOException e) {}
		}
	}
}
