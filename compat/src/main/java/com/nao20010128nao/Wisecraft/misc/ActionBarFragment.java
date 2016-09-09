package com.nao20010128nao.Wisecraft.misc;

import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.support.v7.view.menu.*;

public abstract class ActionBarFragment<Parent extends AppCompatActivity> extends BaseFragment<Parent> 
{
	ActionBar ab;
	CharSequence title;
	public void setSupportActionBar(Toolbar toolbar){
		if(ab!=null){
			throw new RuntimeException("ActionBar is already set");
		}
		ab=new AccesibleToolbarActionBar(toolbar,toolbar.getTitle(),Hacks.wrapAppCompatDelegateWindowWrapper(getParentActivity(),getParentActivity()));
		toolbar.setMenuCallbacks(new MenuPresenter.Callback(){
				public void onCloseMenu(android.support.v7.view.menu.MenuBuilder p1, boolean p2){
					
				}

				public boolean onOpenSubMenu(android.support.v7.view.menu.MenuBuilder p1){
					return onCreateOptionsMenu(p1);
				}
		},new MenuBuilder.Callback(){
				public boolean onMenuItemSelected(android.support.v7.view.menu.MenuBuilder p1, android.view.MenuItem p2){
					return onOptionsItemSelected(p2);
				}

				public void onMenuModeChange(android.support.v7.view.menu.MenuBuilder p1){
					
				}
		});
	}
	public ActionBar getSupportActionBar(){
		return ab;
	}
	public CharSequence getTitle(){
		return title;
	}
	public void setTitle(CharSequence title){
		this.title=title;
		ab.setTitle(title);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}
}
