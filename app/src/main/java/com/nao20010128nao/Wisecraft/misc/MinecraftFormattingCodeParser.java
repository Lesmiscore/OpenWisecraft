package com.nao20010128nao.Wisecraft.misc;

import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.util.*;

import java.util.*;

public class MinecraftFormattingCodeParser {
    static {
        Map<String, Integer> nameToColor = new HashMap<>();
        nameToColor.put("black", 0xff000000);
        nameToColor.put("dark_blue", 0xff0000AA);
        nameToColor.put("dark_green", 0xff00AA00);
        nameToColor.put("dark_aqua", 0xff00AAAA);
        nameToColor.put("dark_red", 0xffAA0000);
        nameToColor.put("dark_purple", 0xffAA00AA);
        nameToColor.put("gold", 0xffFFAA00);
        nameToColor.put("gray", 0xffAAAAAA);
        nameToColor.put("dark_gray", 0xff555555);
        nameToColor.put("blue", 0xff5555FF);
        nameToColor.put("green", 0xff55FF55);
        nameToColor.put("aqua", 0xff55FFFF);
        nameToColor.put("red", 0xffFF5555);
        nameToColor.put("light_purple", 0xffFF55FF);
        nameToColor.put("yellow", 0xffFFFF55);
        nameToColor.put("white", 0xffFFFFFF);
        NAME_TO_COLOR = Collections.unmodifiableMap(nameToColor);
    }

    public static final Map<String, Integer> NAME_TO_COLOR;
    private static final int[] TEXT_COLORS = new int[]{
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

    public byte[] flags = null;
    public char[] escaped = null;
    public boolean[] noColors = null;

    public void loadFlags(String s) {
        escaped = Utils.deleteDecorations(s).toCharArray();
        flags = new byte[escaped.length];
        noColors = new boolean[flags.length];

        char[] chars = s.toCharArray();
        int offset = 0;
        int undecOffset = 0;
        byte flag = 0;
        boolean noColor = true;
        while (chars.length > offset) {
            if (chars[offset] == '§') {
                offset++;
                char keyChar = chars[offset++];
                switch (keyChar) {
                    /*Colors*/
                    case '0':
                        flag = 0;
                        noColor = false;
                        break;
                    case '1':
                        flag = 1;
                        noColor = false;
                        break;
                    case '2':
                        flag = 2;
                        noColor = false;
                        break;
                    case '3':
                        flag = 3;
                        noColor = false;
                        break;
                    case '4':
                        flag = 4;
                        noColor = false;
                        break;
                    case '5':
                        flag = 5;
                        noColor = false;
                        break;
                    case '6':
                        flag = 6;
                        noColor = false;
                        break;
                    case '7':
                        flag = 7;
                        noColor = false;
                        break;
                    case '8':
                        flag = 8;
                        noColor = false;
                        break;
                    case '9':
                        flag = 9;
                        noColor = false;
                        break;
                    case 'a':
                    case 'A':
                        flag = 10;
                        noColor = false;
                        break;
                    case 'b':
                    case 'B':
                        flag = 11;
                        noColor = false;
                        break;
                    case 'c':
                    case 'C':
                        flag = 12;
                        noColor = false;
                        break;
                    case 'd':
                    case 'D':
                        flag = 13;
                        noColor = false;
                        break;
                    case 'e':
                    case 'E':
                        flag = 14;
                        noColor = false;
                        break;
                    case 'f':
                    case 'F':
                        flag = 15;
                        noColor = false;
                        break;
						
					/*Styles*/
                    case 'l':
                    case 'L':
                        flag |= 0b00010000;
                        break;
                    case 'm':
                    case 'M':
                        flag |= 0b00100000;
                        break;
                    case 'n':
                    case 'N':
                        flag |= 0b01000000;
                        break;
                    case 'o':
                    case 'O':
                        flag |= 0b10000000;
                        break;
					
					/*Reset*/
                    case 'r':
                    case 'R':
                        flag = 0;
                        noColor = true;
                        break;
                }
                continue;
            }
            flags[undecOffset] = flag;
            noColors[undecOffset] = noColor;
            offset++;
            undecOffset++;
        }

        Log.d("MFCP", "offset:" + offset + ",undecOffset:" + undecOffset + ",flags[0]:" + flags[0]);
    }

    public Spannable build() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(new String(escaped));
        for (int i = 0; i < escaped.length; i++) {
            if (!noColors[i]) {
                int textColor = TEXT_COLORS[flags[i] & 0xF];
                ForegroundColorSpan fcs = new ForegroundColorSpan(textColor);
                ssb.setSpan(fcs, i, i + 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (0 != (flags[i] & 0b00010000)) {
                ssb.setSpan(new StyleSpan(Typeface.BOLD), i, i + 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (0 != (flags[i] & 0b00100000)) {
                ssb.setSpan(new StrikethroughSpan(), i, i + 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (0 != (flags[i] & 0b01000000)) {
                ssb.setSpan(new UnderlineSpan(), i, i + 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (0 != (flags[i] & 0b10000000)) {
                ssb.setSpan(new StyleSpan(Typeface.ITALIC), i, i + 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ssb;
    }
}
