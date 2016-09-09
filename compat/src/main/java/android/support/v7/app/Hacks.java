package android.support.v7.app;

import android.view.*;
import java.lang.reflect.*;
import com.nao20010128nao.Wisecraft.*;

public class Hacks 
{
	public static Window.Callback wrapAppCompatDelegateWindowWrapper(AppCompatActivity aca,Window.Callback cb){
		try {
			Method method=aca.getDelegate().getClass().getMethod("wrapWindowCallback", Window.Callback.class);
			method.setAccessible(true);
			return (Window.Callback)method.invoke(aca.getDelegate(), cb);
		} catch (Throwable e) {
			WisecraftError.report("wrapAppCompatDelegateWindowWrapper",e);
		}
		return cb;
	}
}
