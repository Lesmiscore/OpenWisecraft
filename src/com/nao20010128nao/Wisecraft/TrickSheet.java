package com.nao20010128nao.Wisecraft;
import android.app.*;
import android.os.*;
import android.view.*;
import java.util.*;
import java.lang.reflect.*;
import android.content.*;

public class TrickSheet extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trick_sheet);
		findViewById(R.id.selectFont).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				String[] choice=getFontChoices();
				final List<String> choiceList=Arrays.asList(choice);
				new AlertDialog.Builder(TrickSheet.this)
					.setSingleChoiceItems(choice,choiceList.indexOf(TheApplication.instance.getFontFieldName())
						,new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface di,int w){
								di.cancel();
								TheApplication.instance.setFontFieldName(choiceList.get(w));
							}
						})
					.show();
			}
		});
		findViewById(R.id.die).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				System.exit(0);		
			}
		});
	}
	String[] getFontChoices(){
		List<String> l=new ArrayList();
		for(Field f:TheApplication.fonts){
			l.add(f.getName());
		}
		l.remove("icomoon1");
		return Factories.strArray(l);
	}
}
