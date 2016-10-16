package com.nao20010128nao.Wisecraft.misc;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.CompatUtils.*;

public class QuintetWalker<A,B,C,D,E> extends ArrayList<Quintet<A,B,C,D,E>> 
{
	public Quintet<A,B,C,D,E> findByA(A o){
		for(Quintet<A,B,C,D,E> i:this)if(CompatUtils.equals(o,i.getA()))return i;
		return null;
	}
	public Quintet<A,B,C,D,E> findByB(B o){
		for(Quintet<A,B,C,D,E> i:this)if(CompatUtils.equals(o,i.getB()))return i;
		return null;
	}
	public Quintet<A,B,C,D,E> findByC(C o){
		for(Quintet<A,B,C,D,E> i:this)if(CompatUtils.equals(o,i.getC()))return i;
		return null;
	}
	public Quintet<A,B,C,D,E> findByD(D o){
		for(Quintet<A,B,C,D,E> i:this)if(CompatUtils.equals(o,i.getD()))return i;
		return null;
	}
	public Quintet<A,B,C,D,E> findByE(E o){
		for(Quintet<A,B,C,D,E> i:this)if(CompatUtils.equals(o,i.getE()))return i;
		return null;
	}
}
