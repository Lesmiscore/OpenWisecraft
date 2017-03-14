package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import java.util.*;

public class Constant {
	public static final int ACTIVITY_RESULT_UPDATE=5;
	public static final int ACTIVITY_RESULT_DELETE=6;
	public static final List ONE_LENGTH_NULL_LIST=Collections.unmodifiableList(Arrays.asList(new Object[1]));
	public static final List TEN_LENGTH_NULL_LIST=Collections.unmodifiableList(Arrays.asList(new Object[10]));
	public static final List ONE_HUNDRED_LENGTH_NULL_LIST=Collections.unmodifiableList(Arrays.asList(new Object[100]));
	public static final List ONE_THOUSAND_LENGTH_NULL_LIST=Collections.unmodifiableList(Arrays.asList(new Object[1000]));
	public static final String[] EMPTY_STRING_ARRAY=new String[0];
	public static final DialogInterface.OnClickListener BLANK_DIALOG_CLICK_LISTENER=new DialogInterface.OnClickListener(){public void onClick(DialogInterface di, int w) {}};
	public static final String FROM_ZERO_TO_255="(1?\\d{1,2}|2[0-4]\\d|25[0-5])";
	public static final String IPV4_PATTERN = "^"+FROM_ZERO_TO_255+"(\\."+FROM_ZERO_TO_255+"\\.){1,3}$";
	public static final String IPV6_PATTERN = "^(?i)(([0-9a-f]{1,4}(:[0-9a-f]{1,4}){7}|::|:(:[0-9a-f]{1,4}){1,7}|([0-9a-f]{1,4}:){1,7}:|([0-9a-f]{1,4}:){1}(:[0-9a-f]{1,4}){1,6}|([0-9a-f]{1,4}:){2}(:[0-9a-f]{1,4}){1,5}|([0-9a-f]{1,4}:){3}(:[0-9a-f]{1,4}){1,4}|([0-9a-f]{1,4}:){4}(:[0-9a-f]{1,4}){1,3}|([0-9a-f]{1,4}:){5}(:[0-9a-f]{1,4}){1,2}|([0-9a-f]{1,4}:){6}(:[0-9a-f]{1,4}){1})|([0-9a-f]{1,4}(:[0-9a-f]{1,4}){5}|:|:(:[0-9a-f]{1,4}){1,5}|([0-9a-f]{1,4}:){1,5}|([0-9a-f]{1,4}:){1}(:[0-9a-f]{1,4}){1,4}|([0-9a-f]{1,4}:){2}(:[0-9a-f]{1,4}){1,3}|([0-9a-f]{1,4}:){3}(:[0-9a-f]{1,4}){1,2}|([0-9a-f]{1,4}:){4}(:[0-9a-f]{1,4}){1}):(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})){3})$";
}
