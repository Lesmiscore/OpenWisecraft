package com.nao20010128nao.Wisecraft;
import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import java.lang.reflect.Field;

public class TrickSheet extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trick_sheet);
		findViewById(R.id.selectFont).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					String[] choice=getFontChoices();
					final List<String> choiceList=Arrays.asList(choice);
					new AlertDialog.Builder(TrickSheet.this)
						.setSingleChoiceItems(choice, choiceList.indexOf(TheApplication.instance.getFontFieldName())
						, new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface di, int w) {
								di.cancel();
								TheApplication.instance.setFontFieldName(choiceList.get(w));
							}
						})
						.show();
				}
			});
		findViewById(R.id.die).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					System.exit(0);		
				}
			});
	}
	String[] getFontChoices() {
		List<String> l=new ArrayList();
		for (Field f:TheApplication.fonts) {
			l.add(f.getName());
		}
		l.remove("icomoon1");
		return Factories.strArray(l);
	}
}
