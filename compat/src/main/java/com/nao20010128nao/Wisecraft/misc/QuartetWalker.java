package com.nao20010128nao.Wisecraft.misc;

import java.util.*;
import static com.nao20010128nao.Wisecraft.misc.CompatUtils.*;

public class QuartetWalker<A,B,C,D> extends ArrayList<Quartet<A,B,C,D>> 
{
	public Quartet<A,B,C,D> findByA(A o){
		for(Quartet<A,B,C,D> i:this)if(CompatUtils.equals(o,i.getA()))return i;
		return null;
	}
	public Quartet<A,B,C,D> findByB(B o){
		for(Quartet<A,B,C,D> i:this)if(CompatUtils.equals(o,i.getB()))return i;
		return null;
	}
	public Quartet<A,B,C,D> findByC(C o){
		for(Quartet<A,B,C,D> i:this)if(CompatUtils.equals(o,i.getC()))return i;
		return null;
	}
	public Quartet<A,B,C,D> findByD(D o){
		for(Quartet<A,B,C,D> i:this)if(CompatUtils.equals(o,i.getD()))return i;
		return null;
	}
}
