package com.nao20010128nao.Wisecraft.misc;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugWriter
{
	public static void writeToD(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		Log.d(tag,sw.toString());
	}
	public static void writeToE(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		Log.e(tag,sw.toString());
	}
	public static void writeToI(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		Log.i(tag,sw.toString());
	}
	public static void writeToV(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		Log.v(tag,sw.toString());
	}
	public static void writeToW(String tag,Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		Log.w(tag,sw.toString());
	}
}
