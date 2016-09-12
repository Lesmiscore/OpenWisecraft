package com.nao20010128nao.Wisecraft;

import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;

public class GenerateWisecraftOpenLinkActivity extends AppCompatActivity {
	static final String PREFIX="wisecraft://";
	
	static final String ADD_SERVER="addserver";
	static final String SERVER_DETAILS="info";
	
	static final String PC="java";
	static final String PE="mobile";
	
	EditText ip,port;
	RadioGroup action,mode;
	Button generate;
	TextView result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_wisecraft_open_link);
		
		ip=(EditText)findViewById(R.id.ip);
		port=(EditText)findViewById(R.id.port);
		
		action=(RadioGroup)findViewById(R.id.action);
		mode=(RadioGroup)findViewById(R.id.mode);
		
		generate=(Button)findViewById(R.id.generate);
		result=(TextView)findViewById(R.id.result);
		
		generate.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					StringBuilder sb=new StringBuilder();
					sb.append(PREFIX);
					switch(action.getCheckedRadioButtonId()){
						case R.id.addServer:sb.append(ADD_SERVER);break;
						case R.id.serverDetails:sb.append(SERVER_DETAILS);break;
					}
					sb.append('/').append(ip).append('/').append(port).append('/');
					switch(mode.getCheckedRadioButtonId()){
						case R.id.pc:sb.append(PC);break;
						case R.id.pe:sb.append(PE);break;
					}
					result.setVisibility(View.VISIBLE);
					result.setText(sb);
				}
			});
	}
}
