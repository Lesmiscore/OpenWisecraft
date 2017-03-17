package com.nao20010128nao.Wisecraft.misc;

public interface ServerListProvider
{
	public void addIntoList(Server s);
	public boolean contains(Server s);
	public void removeFromList(Server s);
}
