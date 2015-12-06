package com.nao20010128nao.McServerPingPong;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity
{
	SharedPreferences pref;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		pref=PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		((EditText)findViewById(R.id.ip)).setText(pref.getString("ip",""));
		((EditText)findViewById(R.id.port)).setText(""+pref.getInt("port",25565));
		((EditText)findViewById(R.id.threads)).setText(""+pref.getInt("threads",150));
		findViewById(R.id.start).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				String ip=((EditText)findViewById(R.id.ip)).getText().toString();
				int port=Integer.parseInt(((EditText)findViewById(R.id.port)).getText().toString());
				int threads=Integer.parseInt(((EditText)findViewById(R.id.threads)).getText().toString());
				pref
					.edit()
					.putString("ip",ip)
					.putInt("port",port)
					.putInt("threads",threads)
					.commit();
				startActivity(new Intent(MainActivity.this,TabsDDoS.class).putExtra("ip",ip).putExtra("threads",threads).putExtra("port",port));
			}
		});
    }
}
