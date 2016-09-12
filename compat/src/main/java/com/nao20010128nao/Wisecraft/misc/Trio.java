package com.nao20010128nao.Wisecraft.misc;

public class Trio<A,B,C>{
	protected A a;
	protected B b;
	protected C c;
	
	public Trio(){}
	public Trio(A a,B b,C c){
		this.a=a;
		this.b=b;
		this.c=c;
	}

	public void setA(A a) {
		this.a = a;
	}

	public A getA() {
		return a;
	}

	public void setB(B b) {
		this.b = b;
	}

	public B getB() {
		return b;
	}

	public void setC(C c) {
		this.c = c;
	}

	public C getC() {
		return c;
	}
}
