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
	private static final int[] SIZE_MEDIUM_RESOURCES=new int[]{
		R.style.minecraftFormattingColor0,
		R.style.minecraftFormattingColor1,
		R.style.minecraftFormattingColor2,
		R.style.minecraftFormattingColor3,
		R.style.minecraftFormattingColor4,
		R.style.minecraftFormattingColor5,
		R.style.minecraftFormattingColor6,
		R.style.minecraftFormattingColor7,
		R.style.minecraftFormattingColor8,
		R.style.minecraftFormattingColor9,
		R.style.minecraftFormattingColorA,
		R.style.minecraftFormattingColorB,
		R.style.minecraftFormattingColorC,
		R.style.minecraftFormattingColorD,
		R.style.minecraftFormattingColorE,
		R.style.minecraftFormattingColorF
	};
	private static final int[] SIZE_SMALL_RESOURCES=new int[]{
		R.style.minecraftFormattingColor0_Small,
		R.style.minecraftFormattingColor1_Small,
		R.style.minecraftFormattingColor2_Small,
		R.style.minecraftFormattingColor3_Small,
		R.style.minecraftFormattingColor4_Small,
		R.style.minecraftFormattingColor5_Small,
		R.style.minecraftFormattingColor6_Small,
		R.style.minecraftFormattingColor7_Small,
		R.style.minecraftFormattingColor8_Small,
		R.style.minecraftFormattingColor9_Small,
		R.style.minecraftFormattingColorA_Small,
		R.style.minecraftFormattingColorB_Small,
		R.style.minecraftFormattingColorC_Small,
		R.style.minecraftFormattingColorD_Small,
		R.style.minecraftFormattingColorE_Small,
		R.style.minecraftFormattingColorF_Small
	};
	private static final int[] SIZE_LARGE_RESOURCES=new int[]{
		R.style.minecraftFormattingColor0_Large,
		R.style.minecraftFormattingColor1_Large,
		R.style.minecraftFormattingColor2_Large,
		R.style.minecraftFormattingColor3_Large,
		R.style.minecraftFormattingColor4_Large,
		R.style.minecraftFormattingColor5_Large,
		R.style.minecraftFormattingColor6_Large,
		R.style.minecraftFormattingColor7_Large,
		R.style.minecraftFormattingColor8_Large,
		R.style.minecraftFormattingColor9_Large,
		R.style.minecraftFormattingColorA_Large,
		R.style.minecraftFormattingColorB_Large,
		R.style.minecraftFormattingColorC_Large,
		R.style.minecraftFormattingColorD_Large,
		R.style.minecraftFormattingColorE_Large,
		R.style.minecraftFormattingColorF_Large
	};
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
	
	public static final int SIZE_MEDIUM=0;
	public static final int SIZE_LARGE=1;
	public static final int SIZE_SMALL=2;
	
	public byte[] flags=null;
	public char[] escaped=null;
	public void loadFlags(String s,byte defaultFlag){
		escaped=Utils.deleteDecorations(s).toCharArray();
		flags=new byte[escaped.length];
		
		char[] chars=s.toCharArray();
		int offset=0;
		int undecOffset=0;
		while (chars.length > offset) {
			if (chars[offset] == '§') {
				offset++;
				char keyChar=chars[offset++];
				switch(keyChar){
					case '0':
						Arrays.fill(flags,undecOffset,flags.length,(byte)0);
						break;
					case '1':
						Arrays.fill(flags,undecOffset,flags.length,(byte)1);
						break;
					case '2':
						Arrays.fill(flags,undecOffset,flags.length,(byte)2);
						break;
					case '3':
						Arrays.fill(flags,undecOffset,flags.length,(byte)3);
						break;
					case '4':
						Arrays.fill(flags,undecOffset,flags.length,(byte)4);
						break;
					case '5':
						Arrays.fill(flags,undecOffset,flags.length,(byte)5);
						break;
					case '6':
						Arrays.fill(flags,undecOffset,flags.length,(byte)6);
						break;
					case '7':
						Arrays.fill(flags,undecOffset,flags.length,(byte)7);
						break;
					case '8':
						Arrays.fill(flags,undecOffset,flags.length,(byte)8);
						break;
					case '9':
						Arrays.fill(flags,undecOffset,flags.length,(byte)9);
						break;
					case 'a':case 'A':
						Arrays.fill(flags,undecOffset,flags.length,(byte)10);
						break;
					case 'b':case 'B':
						Arrays.fill(flags,undecOffset,flags.length,(byte)11);
						break;
					case 'c':case 'C':
						Arrays.fill(flags,undecOffset,flags.length,(byte)12);
						break;
					case 'd':case 'D':
						Arrays.fill(flags,undecOffset,flags.length,(byte)13);
						break;
					case 'e':case 'E':
						Arrays.fill(flags,undecOffset,flags.length,(byte)14);
						break;
					case 'f':case 'F':
						Arrays.fill(flags,undecOffset,flags.length,(byte)15);
						break;
					case 'r':case 'R':
						Arrays.fill(flags,undecOffset,flags.length,defaultFlag);
						break;
				}
				continue;
			}
			offset++;
			undecOffset++;
		}
		
		Log.d("MFCP","offset:"+offset+",undecOffset:"+undecOffset+",flags[0]:"+flags[0]);
	}
	
	public Spannable build(Context x,int size){
		SpannableStringBuilder ssb=new SpannableStringBuilder();
		for(int i=0;i<escaped.length;i++){
			ForegroundColorSpan fcs=new ForegroundColorSpan(TEXT_COLORS[flags[i]&0xF]);
			ssb.append(escaped[i]+"",fcs,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return ssb;
	}
}
