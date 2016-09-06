package com.nao20010128nao.Wisecraft.accounts;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import com.google.firebase.auth.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import java.net.*;
import java.util.*;

public class AccountManagerActivity extends AppCompatActivity
{
	AccountHeaderBuilder ahb;
	Uri userImage;
    ImageLoader imageLoader=new ImageLoader();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
			startActivity(new Intent(this,SigninActivity.class));
			finish();
			return;
		}
		ahb=new AccountHeaderBuilder()
			.withActivity(this)
			.withHeaderBackground(R.color.mainColor)
			.withSelectionListEnabled(false)
			.withSelectionListEnabledForSingleProfile(false);
		loadUserInfo();
		DrawerBuilder builder=new DrawerBuilder();
		builder.withAccountHeader(ahb.build());
		builder.withActivity(this);
		setContentView(builder.buildView().getSlider());
	}
	
	private void loadUserInfo() throws Resources.NotFoundException {
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        ahb.withProfiles(this.<com.mikepenz.materialdrawer.model.interfaces.IProfile>emptyList());
        if (user == null) {
            ahb.addProfiles(
                new ProfileDrawerItem()
                .withName(getResources().getString(R.string.noLogin))
                .withEmail(getResources().getString(R.string.noLogin))
                .withIcon(getResources().getDrawable(getApplicationInfo().icon))
            );
        } else if (user.isAnonymous()) {
            ahb.addProfiles(
                new ProfileDrawerItem()
                .withName(getResources().getString(R.string.anonymous))
                .withEmail(getResources().getString(R.string.anonymous))
                .withIcon(getResources().getDrawable(getApplicationInfo().icon))
            );
        } else {
            ahb.addProfiles(
                new ProfileDrawerItem()
                .withName(user.getDisplayName())
                .withEmail(user.getEmail())
                .withIcon(getResources().getDrawable(getApplicationInfo().icon))
            );
            userImage = user.getPhotoUrl();
            try {
                imageLoader.putInQueue(new URL(userImage.toString()), new ImageLoader.ImageStatusListener(){
                        public void onSuccess(Bitmap bmp, URL url) {
                            ahb.withProfiles(AccountManagerActivity.this.<com.mikepenz.materialdrawer.model.interfaces.IProfile>emptyList());
                            BitmapDrawable bd=new BitmapDrawable(bmp);
                            //bd.setTileModeXY(Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
                            ahb.addProfiles(
                                new ProfileDrawerItem()
                                .withName(user.getDisplayName())
                                .withEmail(user.getEmail())
                                .withIcon(bd)
                            );
                        }
                        public void onError(Throwable err, URL url) {

                        }
                    });
            } catch (MalformedURLException e) {

            }
        }
    }
	
	<T> List<T> emptyList(){
		return new ArrayList<T>();
	}
}
