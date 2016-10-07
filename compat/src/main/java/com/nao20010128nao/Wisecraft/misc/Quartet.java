package com.nao20010128nao.Wisecraft.misc;

public class Quartet<A,B,C,D> extends Trio<A,B,C>
{
	protected D d;
	public Quartet(){}
	public Quartet(A a,B b,C c){super(a,b,c);}
	public Quartet(A a,B b,C c,D d){this(a,b,c);this.d=d;}

	public void setD(D d) {
		this.d = d;
	}

	public D getD() {
		return d;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Quartet))return false;
		Quartet q=(Quartet)o;
		return super.equals(o)&CompatUtils.equals(d,q.d);
	}
}
