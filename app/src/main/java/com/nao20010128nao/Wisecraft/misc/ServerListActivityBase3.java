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
import android.widget.SlidingDrawer;

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
					setContentView(R.layout.server_list_sliding_top);
				}
				break;
			case 4:
				{
					setContentView(R.layout.server_list_sliding_bottom);
				}
				break;
		}
	}
}
