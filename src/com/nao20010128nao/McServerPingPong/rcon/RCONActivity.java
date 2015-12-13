package com.nao20010128nao.McServerPingPong.rcon;
import android.support.v4.app.*;
import android.os.*;
import com.nao20010128nao.McServerPingPong.*;
import android.widget.*;
import android.view.*;

public class RCONActivity extends FragmentActivity
{
	FragmentTabHost fth;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rconmain);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);
		
		TabHost.TabSpec test=fth.newTabSpec("test");
		test.setIndicator("test");
		fth.addTab(test,TestFragment.class,null);
	}
	public static class TestFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.console,null,false);
		}
	}
}
