package com.nao20010128nao.Wisecraft.misc;

public class Quintet<A,B,C,D,E> extends Quartet<A,B,C,D>
{
	protected E e;
	public Quintet(){}
	public Quintet(A a,B b){super(a,b);}
	public Quintet(A a,B b,C c){super(a,b,c);}
	public Quintet(A a,B b,C c,D d){super(a,b,c,d);}
	public Quintet(A a,B b,C c,D d,E e) {super(a,b,c,d);this.e=e;}

	public void setE(E e) {
		this.e = e;
	}

	public E getE() {
		return e;
	}
	

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Quintet))return false;
		Quintet q=(Quintet)o;
		return super.equals(o)&CompatUtils.equals(e,q.e);
	}

	@Override
	public int hashCode() {
		return CompatUtils.hash(a,b,c,d,e);
	}
}
