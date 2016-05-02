package com.nao20010128nao.Wisecraft.misc;
import com.nao20010128nao.Wisecraft.Utils;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.content.Context;
import com.nao20010128nao.Wisecraft.R;
import android.text.style.TextAppearanceSpan;

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
				char keyChar=chars[++offset];
				offset++;
				switch(keyChar){
					case '0':
						flags[undecOffset]=0;
						break;
					case '1':
						flags[undecOffset]=1;
						break;
					case '2':
						flags[undecOffset]=2;
						break;
					case '3':
						flags[undecOffset]=3;
						break;
					case '4':
						flags[undecOffset]=4;
						break;
					case '5':
						flags[undecOffset]=5;
						break;
					case '6':
						flags[undecOffset]=6;
						break;
					case '7':
						flags[undecOffset]=7;
						break;
					case '8':
						flags[undecOffset]=8;
						break;
					case '9':
						flags[undecOffset]=9;
						break;
					case 'a':case 'A':
						flags[undecOffset]=10;
						break;
					case 'b':case 'B':
						flags[undecOffset]=11;
						break;
					case 'c':case 'C':
						flags[undecOffset]=12;
						break;
					case 'd':case 'D':
						flags[undecOffset]=13;
						break;
					case 'e':case 'E':
						flags[undecOffset]=14;
						break;
					case 'f':case 'F':
						flags[undecOffset]=15;
						break;
					case 'r':case 'R':
						flags[undecOffset]=defaultFlag;
						break;
					default:
						if(undecOffset!=0)
							flags[undecOffset]=flags[undecOffset-1];
						else
							flags[undecOffset]=defaultFlag;
						break;
				}
				continue;
			}
			offset++;
			undecOffset++;
			if(undecOffset!=0)
				flags[undecOffset]=flags[undecOffset-1];
			else
				flags[undecOffset]=defaultFlag;
		}
	}
	
	public Spannable build(Context x,int size){
		int[] resArray;
		switch(size){
			case SIZE_MEDIUM:resArray=SIZE_MEDIUM_RESOURCES;break;
			case SIZE_SMALL:resArray=SIZE_SMALL_RESOURCES;break;
			case SIZE_LARGE:resArray=SIZE_LARGE_RESOURCES;break;
			default:throw new RuntimeException(size+" is invalid value for size parameter.");
		}
		SpannableStringBuilder ssb=new SpannableStringBuilder();
		ssb.append(new String(escaped));
		for(int i=0;i<escaped.length;i++){
			TextAppearanceSpan tas=new TextAppearanceSpan(x,resArray[flags[i]&0xF]);
			ssb.setSpan(tas,i,i,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return ssb;
	}
}
