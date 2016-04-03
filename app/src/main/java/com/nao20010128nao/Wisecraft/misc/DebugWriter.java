package com.nao20010128nao.Wisecraft.misc;
import java.io.StringWriter;
import java.io.PrintWriter;
import android.util.Log;

public class DebugWriter
{
	public static void writeToD(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		Log.d(tag,sw.toString());
	}
	public static void writeToE(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		Log.e(tag,sw.toString());
	}
	public static void writeToI(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		Log.i(tag,sw.toString());
	}
	public static void writeToV(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		Log.v(tag,sw.toString());
	}
	public static void writeToW(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		Log.w(tag,sw.toString());
	}
}
