package com.nao20010128nao.Wisecraft.misc;

public class Duo<A,B>
{
	protected A a;
	protected B b;
	public Duo(){}
	public Duo(A a,B b){this.a=a;this.b=b;}

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
}
