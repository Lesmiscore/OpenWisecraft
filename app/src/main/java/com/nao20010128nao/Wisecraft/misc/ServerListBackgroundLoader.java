package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.preference.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.*;
import java.io.*;

public class ServerListBackgroundLoader 
{
	public static final int BACKGROUND_WHITE=0;
	public static final int BACKGROUND_BLACK=1;
	public static final int BACKGROUND_DIRT=2;
	public static final int BACKGROUND_SINGLE_COLOR=3;
	public static final int BACKGROUND_IMAGE=4;
	
	Context ctx;SharedPreferences pref;
	public ServerListBackgroundLoader(Context c){
		ctx=c;
		pref=PreferenceManager.getDefaultSharedPreferences(c);
	}
	
	public Drawable load(){
		switch(pref.getInt("bgId",0)){
			case BACKGROUND_WHITE:
				return new ColorDrawable(Color.WHITE);
			case BACKGROUND_BLACK:
				return new ColorDrawable(Color.BLACK);
			case BACKGROUND_DIRT:
				BitmapDrawable bd=(BitmapDrawable)ctx.getResources().getDrawable(R.drawable.soil);
				bd.setTargetDensity(ctx.getResources().getDisplayMetrics());
				bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
				return bd;
			case BACKGROUND_SINGLE_COLOR:
				return new ColorDrawable(pref.getInt("bgColor",0));
			case BACKGROUND_IMAGE:
				byte[] data=Base64.decode(pref.getString("bgImg",""),ServerInfoActivity.BASE64_FLAGS);
				Bitmap bmp=BitmapFactory.decodeByteArray(data,0,data.length);
				BitmapDrawable last=new BitmapDrawable(bmp);
				last.setTargetDensity(ctx.getResources().getDisplayMetrics());
				return last;
		}
		return null;
	}
	
	public void setWhiteBg(){
		pref.edit()
			.putInt("bgId",BACKGROUND_WHITE)
			.remove("bgColor").remove("bgImg")
			.commit();
	}
	
	public void setBlackBg(){
		pref.edit()
			.putInt("bgId",BACKGROUND_BLACK)
			.remove("bgColor").remove("bgImg")
			.commit();
	}
	
	public void setDirtBg(){
		pref.edit()
			.putInt("bgId",BACKGROUND_DIRT)
			.remove("bgColor").remove("bgImg")
			.commit();
	}
	
	public void setSingleColorBg(int color){
		pref.edit()
			.putInt("bgId",BACKGROUND_SINGLE_COLOR)
			.putInt("bgColor",color).remove("bgImg")
			.commit();
	}
	
	public void setImageBg(Bitmap bmp){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG,100,baos);
		String encoded=Base64.encodeToString(baos.toByteArray(),ServerInfoActivity.BASE64_FLAGS);
		pref.edit()
			.putInt("bgId",BACKGROUND_IMAGE)
			.putString("bgImg",encoded)
			.remove("bgColor")
			.commit();
	}
}
