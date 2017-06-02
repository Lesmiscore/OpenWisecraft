package com.nao20010128nao.Wisecraft.activity;

import android.view.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

//ContextMenu
abstract class ServerListActivityBase6 extends ServerListActivityBase7 {
	protected Map<View,Quartet<Treatment<Duo<View,ContextMenu>>,Predicate<Trio<View,ContextMenu,MenuItem>>,ViewGroup,Boolean>> contextMenuHandlers=new HashMap<>();
	protected Map<Menu,View> contextMenuObjects=new HashMap<>();
	protected Map<MenuItem,View> contextMenuItems=new HashMap<>();
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		contextMenuObjects.put(menu,v);
		if(contextMenuHandlers.containsKey(v)){
			Treatment<Duo<View,ContextMenu>> init=contextMenuHandlers.get(v).getA();
			if(init!=null){
				init.process(new Duo<>(v, menu));
				for(int i=0;i<menu.size();i++){
					contextMenuItems.put(menu.getItem(i),v);
				}
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(contextMenuItems.containsKey(item)){
			View owner=contextMenuItems.get(item);
			if(contextMenuHandlers.containsKey(owner)){//it must be true
				ContextMenu menu=null;
				for(Map.Entry<Menu,View> value:contextMenuObjects.entrySet()){
					if(value.getValue()==owner&&value.getValue() instanceof ContextMenu){
						menu=(ContextMenu)value.getKey();
					}
				}
				Predicate<Trio<View,ContextMenu,MenuItem>> selection=contextMenuHandlers.get(owner).getB();
				if(selection!=null){
					return selection.process(new Trio<>(owner, menu, item));
				}
			}
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
		if(!contextMenuHandlers.get(contextMenuObjects.get(menu)).getD()){
			for(int i=0;i<menu.size();i++){
				contextMenuItems.remove(menu.getItem(i));
			}
			contextMenuHandlers.remove(contextMenuObjects.get(menu));
			contextMenuObjects.remove(menu);
		}
	}

	public void openContextMenu(View view,ViewGroup parent,Treatment<Duo<View,ContextMenu>> init,Predicate<Trio<View,ContextMenu,MenuItem>> selection) {
		contextMenuHandlers.put(view, new Quartet<>(init, selection, parent, false));
		if(!(parent!=null?parent.showContextMenuForChild(view):view.showContextMenu()))
			contextMenuHandlers.remove(view);
	}
	
	public void registerContextMenuHandler(View view,ViewGroup parent,Treatment<Duo<View,ContextMenu>> init,Predicate<Trio<View,ContextMenu,MenuItem>> selection){
		contextMenuHandlers.put(view, new Quartet<>(init, selection, parent, true));
	}
}
