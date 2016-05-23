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
	public void loadFlags(String s,byte defaultFlag){
		escaped=Utils.deleteDecorations(s).toCharArray();
		flags=new byte[escaped.length];
		
		char[] chars=s.toCharArray();
		int offset=0;
		int undecOffset=0;
		byte flag=defaultFlag;
		while (chars.length > offset) {
			if (chars[offset] == '§') {
				offset++;
				char keyChar=chars[offset++];
				switch(keyChar){
					/*Colors*/
					case '0':
						flag=0;
						break;
					case '1':
						flag=1;
						break;
					case '2':
						flag=2;
						break;
					case '3':
						flag=3;
						break;
					case '4':
						flag=4;
						break;
					case '5':
						flag=5;
						break;
					case '6':
						flag=6;
						break;
					case '7':
						flag=7;
						break;
					case '8':
						flag=8;
						break;
					case '9':
						flag=9;
						break;
					case 'a':case 'A':
						flag=10;
						break;
					case 'b':case 'B':
						flag=11;
						break;
					case 'c':case 'C':
						flag=12;
						break;
					case 'd':case 'D':
						flag=13;
						break;
					case 'e':case 'E':
						flag=14;
						break;
					case 'f':case 'F':
						flag=15;
						break;
						
					/*Styles*/
					case 'l':case 'L':
						flag=(byte)(flag|0b00010000);
						break;
					case 'm':case 'M':
						flag=(byte)(flag|0b00100000);
						break;
					case 'n':case 'N':
						flag=(byte)(flag|0b01000000);
						break;
					case 'o':case 'O':
						flag=(byte)(flag|0b10000000);
						break;
					
					/*Reset*/
					case 'r':case 'R':
						flag=defaultFlag;
						break;
				}
				continue;
			}
			flags[undecOffset]=flag;
			offset++;
			undecOffset++;
		}
		
		Log.d("MFCP","offset:"+offset+",undecOffset:"+undecOffset+",flags[0]:"+flags[0]);
	}
	
	public Spannable build(){
		SpannableStringBuilder ssb=new SpannableStringBuilder();
		for(int i=0;i<escaped.length;i++){
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
		return ssb;
	}
}
