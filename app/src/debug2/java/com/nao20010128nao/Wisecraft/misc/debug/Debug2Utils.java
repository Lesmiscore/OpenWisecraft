package com.nao20010128nao.Wisecraft.misc.debug;

import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

/**
 * Created by lesmi on 17/07/14.
 */
public class Debug2Utils extends Utils {
    public static boolean testGroovy(){
        try{
            Class.forName("groovy.lang.GroovyObject");
            return true;
        }catch(Throwable e){
            return false;
        }
    }
    public static <R,A> R barrier(OneArgThrowableFunction<R,A> func,A a){
        try {
            return func.call(a);
        } catch (Throwable e) {
            WisecraftError.report("Utils",e);
            return null;
        }
    }

    public static <R,A,B> R barrier(TwoArgThrowableFunction<R,A,B> func,A a,B b){
        try {
            return func.call(a,b);
        } catch (Throwable e) {
            WisecraftError.report("Utils",e);
            return null;
        }
    }

    public static <R,A,B,C> R barrier(ThreeArgThrowableFunction<R,A,B,C> func,A a,B b,C c){
        try {
            return func.call(a,b,c);
        } catch (Throwable e) {
            WisecraftError.report("Utils",e);
            return null;
        }
    }

    public interface OneArgThrowableFunction<R,A>{
        R call(A a) throws Throwable;
    }
    public interface TwoArgThrowableFunction<R,A,B>{
        R call(A a,B b) throws Throwable;
    }
    public interface ThreeArgThrowableFunction<R,A,B,C>{
        R call(A a,B b,C c) throws Throwable;
    }
}
