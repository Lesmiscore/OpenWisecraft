package pref;
import android.content.Context;
import android.util.AttributeSet;
import com.nao20010128nao.ToolBox.HandledPreference;

public class StartPref extends HandledPreference{
	public static AttributeSet as;
	public StartPref(Context c,AttributeSet attrs){
		super(c,as=attrs);
	}
	public StartPref(Context c){
		super(c,as);
	}
}
