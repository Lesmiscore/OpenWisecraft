package com.nao20010128nao.Wisecraft.misc;
import android.app.*;
import android.util.*;
import android.view.*;
import java.io.*;
import java.util.*;

public class HierarchyDumper extends Thread
{
	View parent;
	public HierarchyDumper(Activity a){
		this(a.getWindow().getDecorView());
	}
	public HierarchyDumper(View v){
		parent=v;
	}
	private void dumpSingle(View v,Writer w,int indent)throws IOException{
		char[] indentC=new char[indent];
		Arrays.fill(indentC,')');
		String indentS=new String(indentC);
		if(v==null){
			w.write(indentS);
			w.write("null");
			w.write('\n');
		}else{
			w.write(indentS);
			w.write(v.getClass().toString());
			w.write('@');
			w.write(Integer.toString(v.getId(),16));
			w.write('\n');
			if(v instanceof ViewGroup){
				ViewGroup vg=(ViewGroup)v;
				for(int i=0;i<vg.getChildCount();i++){
					dumpSingle(vg.getChildAt(i),w,indent+1);
				}
			}
		}
	}

	@Override
	public void run() {
		// TODO: Implement this method
		StringWriter sw=new StringWriter();
		try {
			dumpSingle(parent, sw, 0);
		} catch (IOException e) {}
		Log.i("HierarchyDumper",sw.toString());
	}
}
