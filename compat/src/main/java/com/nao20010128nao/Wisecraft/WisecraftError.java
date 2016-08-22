package com.nao20010128nao.Wisecraft;
import com.google.firebase.crash.*;
public class WisecraftError extends RuntimeException {
	public WisecraftError(String mes, Throwable e) {
		super(mes, e);
	}
	public static void report(String mes,Throwable e){
		WisecraftError err;
		if(e instanceof WisecraftError){
			err=(WisecraftError)e;
		}else{
			err=new WisecraftError(mes,e);
		}
		FirebaseCrash.report(err);
	}
}
	
