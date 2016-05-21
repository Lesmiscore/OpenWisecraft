package com.nao20010128nao.Wisecraft.misc;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatListActivity;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.support.v7.widget.LinearLayoutCompat;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.nao20010128nao.Wisecraft.R;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.LinearLayout;

//ViewDragHelper support
public class ServerListActivityBase3 extends AppCompatListActivity
{
	static int MENU_VIEW_ID=R.id.app_menu;
	SharedPreferences pref;
	ViewDragHelper vdh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		switch (pref.getInt("main_style", 0)) {
			case 0:case 1:case 2:break;//ignore, ServerListActivity does
			case 3:
				{
					getSupportActionBar().setElevation(0);
					LinearLayout ll=(LinearLayout)getLayoutInflater().inflate(R.layout.server_list_dragging,null);
					DraggingView dv=(DraggingView)ll.findViewById(R.id.content);
					FrameLayout menu=(FrameLayout)getLayoutInflater().inflate(R.layout.content_scrolling,dv);
					getLayoutInflater().inflate(R.layout.server_list_dragging_title,dv);
					dv.setUnmoveableViewOffset(1);
					getLayoutInflater().inflate(R.layout.server_list_content_nodrawer,ll);
					menu.setId(MENU_VIEW_ID);
					setContentView(ll);
				}
				break;
			case 4:
				{
					LinearLayout ll=(LinearLayout)getLayoutInflater().inflate(R.layout.server_list_dragging,null);
					DraggingView dv=(DraggingView)ll.findViewById(R.id.content);
					getLayoutInflater().inflate(R.layout.server_list_dragging_title,dv);
					FrameLayout menu=(FrameLayout)getLayoutInflater().inflate(R.layout.content_scrolling,dv);
					dv.setUnmoveableViewOffset(0);
					getLayoutInflater().inflate(R.layout.server_list_content_nodrawer,ll);
					menu.setId(MENU_VIEW_ID);
					setContentView(ll);
				}
				break;
		}
	}
}
