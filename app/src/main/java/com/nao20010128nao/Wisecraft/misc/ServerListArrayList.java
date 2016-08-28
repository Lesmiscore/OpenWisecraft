package com.nao20010128nao.Wisecraft.misc;
import java.util.*;

public class ServerListArrayList extends ArrayList<Server> implements ServerListProvider
{
	public ServerListArrayList(){
		
	}
	public ServerListArrayList(Collection<Server> col){
		super(col);
	}
	public ServerListArrayList(int cap){
		super(cap);
	}

	@Override
	public boolean contains(Object object) {
		// TODO: Implement this method
		if(object==null)return false;
		Iterator<Server> i=iterator();
		while(i.hasNext())
			if(i.next().equals(object))
				return true;
		return false;
	}

	@Override
	public int indexOf(Object object) {
		// TODO: Implement this method
		if(object==null)return -1;
		Iterator<Server> i=iterator();
		Server s=null;
		while(i.hasNext())
			if((s=i.next()).equals(object))
				return super.indexOf(s);
		return -1;
	}

	@Override
	public boolean add(Server object) {
		// TODO: Implement this method
		if(object==null)return false;
		return super.add(object);
	}

	@Override
	public void addIntoList(Server s) {
		// TODO: Implement this method
		add(s);
	}
}
