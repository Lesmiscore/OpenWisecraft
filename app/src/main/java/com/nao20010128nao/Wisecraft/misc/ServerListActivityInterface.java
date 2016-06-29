package com.nao20010128nao.Wisecraft.misc;
import android.content.Intent;

public interface ServerListActivityInterface
{
	public void onActivityResult(int requestCode, int resultCode, Intent data);
	public void addIntoList(Server s);
}
