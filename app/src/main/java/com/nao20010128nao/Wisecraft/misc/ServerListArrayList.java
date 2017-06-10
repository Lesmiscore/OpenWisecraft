package com.nao20010128nao.Wisecraft.misc;
import java.util.*;
import java.util.concurrent.*;

public class ServerListArrayList extends CopyOnWriteArrayList<Server> implements ServerListProvider
{
	public ServerListArrayList(){
		
	}
	public ServerListArrayList(Collection<Server> col){
		super(col);
	}
	public ServerListArrayList(int cap){
	}

	@Override
	public boolean contains(Object object) {
		if(object==null)return false;
		for(Server s:this)
			if(s.equals(object))
				return true;
		return false;
	}

	@Override
	public int indexOf(Object object) {
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
		if(object==null)return false;
		return super.add(object);
	}

	@Override
	public void addIntoList(Server s) {
		add(s);
	}

	@Override
	public void removeFromList(Server s) {
		remove(s);
	}

	@Override
	public boolean contains(Server s) {
		return contains((Object)s);
	}
}
