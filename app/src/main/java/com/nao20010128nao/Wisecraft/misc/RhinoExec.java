package com.nao20010128nao.Wisecraft.misc;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class RhinoExec extends Thread
{

	@Override
	public void run() {
		// TODO: Implement this method
		Context ctx=Context.enter();
		try{
			ctx.setOptimizationLevel(-1);
			Scriptable scr=ctx.initStandardObjects();
			ctx.evaluateString(scr,"android.util.Log.i(\"RhinoExec\",\"It looks like there's no problem in the Javascript executor.\");","<data>",0,null);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
}
