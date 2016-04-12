package com.nao20010128nao.Wisecraft.misc;
import java.util.ArrayList;
import java.util.Collection;

public class ServerListArrayList extends ArrayList<Server>
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
		return super.contains(object);
	}

	@Override
	public int indexOf(Object object) {
		// TODO: Implement this method
		return super.indexOf(object);
	}

	@Override
	public boolean add(Server object) {
		// TODO: Implement this method
		if(object==null)return false;
		return super.add(object);
	}
}
