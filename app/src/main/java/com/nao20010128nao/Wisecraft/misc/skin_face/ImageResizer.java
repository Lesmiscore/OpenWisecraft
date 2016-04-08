package com.nao20010128nao.Wisecraft.misc.skin_face;
import android.graphics.Bitmap;


public class ImageResizer
{
	public static Bitmap resizeBitmapPixel(Bitmap base,int cellSize,Bitmap.Config cfg){
		Bitmap result=Bitmap.createBitmap(base.getWidth()*cellSize,base.getHeight()*cellSize,cfg==null?base.getConfig():cfg);
		for(int x=0;x<base.getWidth();x++){
			for(int y=0;y<base.getHeight();y++){
				int cellCol=base.getPixel(x,y);
				int startOfsX=x*cellSize;
				int startOfsY=y*cellSize;
				for(int pX=0;pX<cellSize;pX++){
					for(int pY=0;pY<cellSize;pY++){
						result.setPixel(startOfsX+pX,startOfsY+pY,cellCol);
					}
				}
			}
		}
		return result;
	}
	public static Bitmap resizeBitmapPixel(Bitmap base,int cellSize){
		return resizeBitmapPixel(base,cellSize,null);
	}
}
