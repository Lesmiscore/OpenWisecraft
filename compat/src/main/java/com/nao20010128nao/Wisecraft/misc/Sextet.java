package com.nao20010128nao.Wisecraft.misc;

public class Sextet<A,B,C,D,E,F> extends Quintet<A,B,C,D,E>{
	protected F f;
	public Sextet(){}
	public Sextet(A a,B b){super(a,b);}
	public Sextet(A a,B b,C c){super(a,b,c);}
	public Sextet(A a,B b,C c,D d){super(a,b,c,d);}
	public Sextet(A a,B b,C c,D d,E e) {super(a,b,c,d,e);}
	public Sextet(A a,B b,C c,D d,E e,F f) {super(a,b,c,d,e);this.f=f;}

	public void setF(F f) {
		this.f = f;
	}

	public F getF() {
		return f;
	}


	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Sextet))return false;
		Sextet s=(Sextet)o;
		return super.equals(o)&CompatUtils.equals(f,s.f);
	}

	@Override
	public int hashCode() {
		return CompatUtils.hash(a,b,c,d,e,f);
	}
}
