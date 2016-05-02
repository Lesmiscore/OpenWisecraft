package com.nao20010128nao.Wisecraft.misc;
import com.nao20010128nao.Wisecraft.Utils;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.content.Context;
import com.nao20010128nao.Wisecraft.R;
import android.text.style.TextAppearanceSpan;
import java.util.Arrays;
import android.util.Log;
import android.text.style.ForegroundColorSpan;

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
					case '0':
						flag=(byte)((flag&(0xf0))|0);
						break;
					case '1':
						flag=(byte)((flag&(0xf0))|1);
						break;
					case '2':
						flag=(byte)((flag&(0xf0))|2);
						break;
					case '3':
						flag=(byte)((flag&(0xf0))|3);
						break;
					case '4':
						flag=(byte)((flag&(0xf0))|4);
						break;
					case '5':
						flag=(byte)((flag&(0xf0))|5);
						break;
					case '6':
						flag=(byte)((flag&(0xf0))|6);
						break;
					case '7':
						flag=(byte)((flag&(0xf0))|7);
						break;
					case '8':
						flag=(byte)((flag&(0xf0))|8);
						break;
					case '9':
						flag=(byte)((flag&(0xf0))|9);
						break;
					case 'a':case 'A':
						flag=(byte)((flag&(0xf0))|10);
						break;
					case 'b':case 'B':
						flag=(byte)((flag&(0xf0))|11);
						break;
					case 'c':case 'C':
						flag=(byte)((flag&(0xf0))|12);
						break;
					case 'd':case 'D':
						flag=(byte)((flag&(0xf0))|13);
						break;
					case 'e':case 'E':
						flag=(byte)((flag&(0xf0))|14);
						break;
					case 'f':case 'F':
						flag=(byte)((flag&(0xf0))|15);
						break;
					case 'r':case 'R':
						flag=0;
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
			ssb.append(escaped[i]+"",fcs,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return ssb;
	}
}
