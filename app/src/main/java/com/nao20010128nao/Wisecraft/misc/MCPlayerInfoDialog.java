package com.nao20010128nao.Wisecraft.misc;

import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;
import android.text.*;

abstract class MCPlayerInfoDialogImpl extends AppCompatDialog {
	String player;
	TextView username,uuid,reputation;
	ImageView face;
	Handler h=new Handler();
	
	public MCPlayerInfoDialogImpl(android.content.Context context) {
		this(context,ThemePatcher.getDefaultDialogStyle(context));
	}

    public MCPlayerInfoDialogImpl(android.content.Context context, int theme) {
		super(context,theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.minecraft_pc_user_info);
		face=(ImageView)findViewById(R.id.face);
		username=(TextView)findViewById(R.id.username);
		uuid=(TextView)findViewById(R.id.uuid);
		reputation=(TextView)findViewById(R.id.mcbansReputation);
		username.setText(player);
		new Thread(){
			String uuidText=null;
			Bitmap bmp=null;
			String rept=null;
			public void run(){
				try {
					uuidText=UUIDFetcher.getUUIDOf(player).toString();
				} catch (Exception e) {
					WisecraftError.report("MCPlayerInfoDialog",e);
				}
				h.post(new Runnable(){
						public void run(){
							uuid.setText(uuidText);
						}
					});
				try{
					try(InputStream is=new URL("https://crafatar.com/avatars/"+(uuidText!=null?uuidText:player)).openConnection().getInputStream()){
						bmp=BitmapFactory.decodeStream(is);
					}
					Bitmap beforeEdit=bmp;
					bmp=ImageResizer.resizeBitmapPixel(bmp,face.getLayoutParams().width/8);
					beforeEdit.recycle();
				}catch(Throwable e){
					WisecraftError.report("MCPlayerInfoDialog",e);
				}
				h.post(new Runnable(){
						public void run(){
							face.setImageBitmap(bmp);
						}
					});
				try{
					if(uuidText!=null){
						Pattern reptRegex=Pattern.compile("(([1-9]|10)(|\\.[0-9]+) \\/ 10)");
						StringBuilder sb=new StringBuilder();
						BufferedReader reader=null;
						try{
							reader=new BufferedReader(new InputStreamReader(new URL("http://mcbans.com/player/"+uuidText.replace("-","")+"/").openConnection().getInputStream()));
							char[] buf=new char[512];
							while(true){
								int r=reader.read(buf);
								if(r<=0)break;
								sb.append(buf,0,r);
							}
						}finally{
							if(reader!=null)reader.close();
						}
						Matcher matcher=reptRegex.matcher(sb);
						if(matcher.find()){
							rept=matcher.group();
						}
					}
				}catch(Throwable e){
					WisecraftError.report("MCPlayerInfoDialog",e);
				}
				h.post(new Runnable(){
						public void run(){
							reputation.setText(rept);
						}
					});
			}
		}.start();
		face.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				String forPreview;
				if(!TextUtils.isEmpty(uuid.getText())){
					forPreview=uuid.getText().toString().replace("-","");
				}else{
					forPreview=player;
				}
				MCSkinViewerDialog svd=new MCSkinViewerDialog(getContext());
				svd.setPlayer(forPreview);
				svd.show();
			}
		});
	}
	
	public void setPlayer(String player) {
		this.player = player;
	}

	public String getPlayer() {
		return player;
	}
	
}
public class MCPlayerInfoDialog extends MCPlayerInfoDialogImpl{
	public MCPlayerInfoDialog(android.content.Context context) {
		super(context);
	}

    public MCPlayerInfoDialog(android.content.Context context, int theme) {
		super(context,theme);
	}
}

