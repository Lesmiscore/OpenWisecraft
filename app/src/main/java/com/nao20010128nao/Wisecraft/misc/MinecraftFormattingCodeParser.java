package com.nao20010128nao.Wisecraft.misc;
import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.util.*;

public class MinecraftFormattingCodeParser
{
	private static final int[] TEXT_COLORS=new int[]{
		0xff000000,
		0xff0000AA,
		0xff00AA00,
		0xff00AAAA,
		0xffAA0000,
		0xffAA00AA,
		0xffFFAA00,
		0xffAAAAAA,
		0xff555555,
		0xff5555FF,
		0xff55FF55,
		0xff55FFFF,
		0xffFF5555,
		0xffFF55FF,
		0xffFFFF55,
		0xffFFFFFF
	};
	
	public byte[] flags=null;
	public char[] escaped=null;
	public boolean[] noSpans=null;
	public void loadFlags(String s){
		escaped=Utils.deleteDecorations(s).toCharArray();
		flags=new byte[escaped.length];
		noSpans=new boolean[flags.length];
		
		char[] chars=s.toCharArray();
		int offset=0;
		int undecOffset=0;
		byte flag=0;
		boolean noSpan=false;
		while (chars.length > offset) {
			if (chars[offset] == '§') {
				offset++;
				char keyChar=chars[offset++];
				switch(keyChar){
					/*Colors*/
					case '0':
						flag=0;noSpan=false;
						break;
					case '1':
						flag=1;noSpan=false;
						break;
					case '2':
						flag=2;noSpan=false;
						break;
					case '3':
						flag=3;noSpan=false;
						break;
					case '4':
						flag=4;noSpan=false;
						break;
					case '5':
						flag=5;noSpan=false;
						break;
					case '6':
						flag=6;noSpan=false;
						break;
					case '7':
						flag=7;noSpan=false;
						break;
					case '8':
						flag=8;noSpan=false;
						break;
					case '9':
						flag=9;noSpan=false;
						break;
					case 'a':case 'A':
						flag=10;noSpan=false;
						break;
					case 'b':case 'B':
						flag=11;noSpan=false;
						break;
					case 'c':case 'C':
						flag=12;noSpan=false;
						break;
					case 'd':case 'D':
						flag=13;noSpan=false;
						break;
					case 'e':case 'E':
						flag=14;noSpan=false;
						break;
					case 'f':case 'F':
						flag=15;noSpan=false;
						break;
						
					/*Styles*/
					case 'l':case 'L':
						flag|=0b00010000;noSpan=false;
						break;
					case 'm':case 'M':
						flag|=0b00100000;noSpan=false;
						break;
					case 'n':case 'N':
						flag|=0b01000000;noSpan=false;
						break;
					case 'o':case 'O':
						flag|=0b10000000;noSpan=false;
						break;
					
					/*Reset*/
					case 'r':case 'R':
						flag=0;noSpan=true;
						break;
				}
				continue;
			}
			flags[undecOffset]=flag;
			noSpans[undecOffset]=noSpan;
			offset++;
			undecOffset++;
		}
		
		Log.d("MFCP","offset:"+offset+",undecOffset:"+undecOffset+",flags[0]:"+flags[0]);
	}
	
	public Spannable build(){
		SpannableStringBuilder ssb=new SpannableStringBuilder();
		for(int i=0;i<escaped.length;i++){
			if(!noSpans[i]){
				ForegroundColorSpan fcs=new ForegroundColorSpan(TEXT_COLORS[flags[i]&0xF]);
				ssb.append(escaped[i]);
				ssb.setSpan(fcs,ssb.length()-1,ssb.length(),SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
				if(0!=(flags[i]&0b00010000)){
					ssb.setSpan(new StyleSpan(Typeface.BOLD),ssb.length()-1,ssb.length(),SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if(0!=(flags[i]&0b00100000)){
					ssb.setSpan(new StrikethroughSpan(),ssb.length()-1,ssb.length(),SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if(0!=(flags[i]&0b01000000)){
					ssb.setSpan(new UnderlineSpan(),ssb.length()-1,ssb.length(),SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if(0!=(flags[i]&0b10000000)){
					ssb.setSpan(new StyleSpan(Typeface.ITALIC),ssb.length()-1,ssb.length(),SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return ssb;
	}
}
