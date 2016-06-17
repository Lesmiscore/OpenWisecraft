package com.nao20010128nao.Wisecraft.misc;
import android.util.*;
import java.io.*;

public class DebugWriter
{
	public static void writeToD(String tag,Throwable e){
		Log.d(tag,getStacktraceAsString(e));
	}
	public static void writeToE(String tag,Throwable e){
		Log.e(tag,getStacktraceAsString(e));
	}
	public static void writeToI(String tag,Throwable e){
		Log.i(tag,getStacktraceAsString(e));
	}
	public static void writeToV(String tag,Throwable e){
		Log.v(tag,getStacktraceAsString(e));
	}
	public static void writeToW(String tag,Throwable e){
		Log.w(tag,getStacktraceAsString(e));
	}
	
	

	public static String getStacktraceAsString(Throwable e) {
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}
}
