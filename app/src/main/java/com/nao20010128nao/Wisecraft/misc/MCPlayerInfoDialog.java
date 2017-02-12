package com.nao20010128nao.Wisecraft.misc;

import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import java.io.*;
import java.net.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

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
						Document doc=Jsoup.connect("http://mcbans.com/player/"+uuidText+"/")
							.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
							.get();
						Elements elems=doc.select("fieldset > section > div.section-content");
						Log.d("MCPlayerInfoDialog","size: "+elems.size());
						for(Element element:elems){
							String value=element.text();
							Log.d("MCPlayerInfoDialog","text: "+element.text()+",html: "+element.html()+",val: "+element.val());
							if(value.matches("^(0|1|2|3|4|5|6|7|8|9|10)(|\\.[0-9]+) / 10$")){
								rept=value;
								break;
							}
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

