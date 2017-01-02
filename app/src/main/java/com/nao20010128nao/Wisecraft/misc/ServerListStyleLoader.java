package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.preference.*;
import android.support.v4.content.*;
import android.support.v7.graphics.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import java.io.*;

import com.nao20010128nao.Wisecraft.R;

public class ServerListStyleLoader {
	public static final int BACKGROUND_WHITE=0;
	public static final int BACKGROUND_BLACK=1;
	public static final int BACKGROUND_DIRT=2;
	public static final int BACKGROUND_SINGLE_COLOR=3;
	public static final int BACKGROUND_IMAGE=4;
	
	Context ctx;SharedPreferences pref;
	public ServerListStyleLoader(Context c){
		ctx=c;
		pref=PreferenceManager.getDefaultSharedPreferences(c);
		if(pref.contains("colorFormattedText")|pref.contains("darkBackgroundForServerName")){
			boolean willColorText;
			if (pref.getBoolean("colorFormattedText", false)) {
				if (pref.getBoolean("darkBackgroundForServerName", false)) {
					setTextColor(Color.WHITE);
					setDirtBg();
				} else {
					setTextColor(Color.BLACK);
					setWhiteBg();
				}
				willColorText=true;
			} else {
				setTextColor(Color.BLACK);
				setWhiteBg();
				willColorText=false;
			}
			pref.edit()
				.putBoolean("serverListColorFormattedText",willColorText)
				.remove("colorFormattedText")
				.remove("darkBackgroundForServerName")
				.commit();
		}
	}
	
	public Drawable load(){
		switch(getBgId()){
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
				return new ColorDrawable(pref.getInt("serverListBgColor",0));
			case BACKGROUND_IMAGE:
				Bitmap bmp=getImageBgBitmap();
				ForceExpandBitmapDrawable last=new ForceExpandBitmapDrawable(bmp);
				//last.setTargetDensity(ctx.getResources().getDisplayMetrics());
				return last;
		}
		return null;
	}
	
	public int getBackgroundSimpleColor(){
		switch(getBgId()){
			case BACKGROUND_WHITE:
				//return ServerInfoActivity.darker(Color.WHITE);
				return ContextCompat.getColor(ctx,R.color.material_grey_600);
			case BACKGROUND_BLACK:
				return Color.BLACK;
			case BACKGROUND_DIRT:
				return ServerInfoActivity.DIRT_DARK;
			case BACKGROUND_SINGLE_COLOR:
				return ServerInfoActivity.darker(pref.getInt("serverListBgColor",Color.BLACK));
			case BACKGROUND_IMAGE:
				Bitmap bmp=null;
				try{
					bmp=getImageBgBitmap();
					Palette palette=Palette.generate(bmp);
					return ServerInfoActivity.darker(palette.getDarkVibrantColor(Color.BLACK));
				}finally{
					if(bmp!=null)bmp.recycle();
				}
				
		}
		return 0;
	}
	
	public void setWhiteBg(){
		pref.edit()
			.putInt("serverListBgId",BACKGROUND_WHITE)
			.remove("serverListBgColor").remove("serverListBgImg")
			.commit();
	}
	
	public void setBlackBg(){
		pref.edit()
			.putInt("serverListBgId",BACKGROUND_BLACK)
			.remove("serverListBgColor").remove("serverListBgImg")
			.commit();
	}
	
	public void setDirtBg(){
		pref.edit()
			.putInt("serverListBgId",BACKGROUND_DIRT)
			.remove("serverListBgColor").remove("serverListBgImg")
			.commit();
	}
	
	public void setSingleColorBg(int color){
		pref.edit()
			.putInt("serverListBgId",BACKGROUND_SINGLE_COLOR)
			.putInt("serverListBgColor",color).remove("serverListBgImg")
			.commit();
	}
	
	public void setImageBg(Bitmap bmp){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG,100,baos);
		String encoded=Base64.encodeToString(baos.toByteArray(),ServerInfoActivity.BASE64_FLAGS);
		pref.edit()
			.putInt("serverListBgId",BACKGROUND_IMAGE)
			.putString("serverListBgImg",encoded)
			.remove("serverListBgColor")
			.commit();
	}
	
	public int getBgId(){
		return pref.getInt("serverListBgId",0);
	}
	
	public void setTextColor(int color){
		pref.edit().putInt("serverListTextColor",color).commit();
	}
	
	public int getTextColor(){
		return pref.getInt("serverListTextColor",Color.BLACK);
	}
	
	public int getOnlineColor(){
		return ContextCompat.getColor(ctx,R.color.stat_ok);
	}
	
	public int getOfflineColor(){
		return ContextCompat.getColor(ctx,R.color.stat_error);
	}
	
	public int getPingingColor(){
		return ContextCompat.getColor(ctx,R.color.stat_pending);
	}
	
	public void applyTextColorTo(ServerStatusWrapperViewHolder vh){
		vh.setTextColor(getTextColor());
	}
	
	public int getSingleColorBgColor(){
		return pref.getInt("serverListBgColor",Color.BLACK);
	}
	
	public Bitmap getImageBgBitmap(){
		byte[] data=Base64.decode(pref.getString("serverListBgImg",""),ServerInfoActivity.BASE64_FLAGS);
		Bitmap bmp=BitmapFactory.decodeByteArray(data,0,data.length);
		return bmp;
	}
}
