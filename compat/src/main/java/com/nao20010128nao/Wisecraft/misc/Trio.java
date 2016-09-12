package com.nao20010128nao.Wisecraft.misc;

public class Trio<A,B,C> extends Duo<A,B>{
	protected C c;
	
	public Trio(){}
	public Trio(A a,B b){super(a,b);}
	public Trio(A a,B b,C c){this(a,b);this.c=c;}

	public void setC(C c) {
		this.c = c;
	}

	public C getC() {
		return c;
	}
}
