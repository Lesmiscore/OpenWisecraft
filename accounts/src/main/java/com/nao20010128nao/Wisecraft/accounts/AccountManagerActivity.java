package com.nao20010128nao.Wisecraft.accounts;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import com.google.firebase.auth.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import java.net.*;
import java.util.*;

public class AccountManagerActivity extends AppCompatActivity
{
	AccountHeaderBuilder ahb;
	Uri userImage;
    ImageLoader imageLoader=new ImageLoader();
	WorkingDialog wd;
	Drawer content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
			startActivityForResult(new Intent(this,GoogleSigninActivity.class),0);
		}
		wd=new WorkingDialog(this);
		ahb=new AccountHeaderBuilder()
			.withActivity(this)
			.withHeaderBackground(R.color.mainColor)
			.withSelectionListEnabled(false)
			.withSelectionListEnabledForSingleProfile(false);
		loadUserInfo();
		final DrawerBuilder builder=new DrawerBuilder();
		builder.withAccountHeader(ahb.build());
		builder.withActivity(this);
		List<IDrawerItem> items=new ArrayList<>();
		items.add(
			new LineWrappingPrimaryDrawerItem()
				.withName(R.string.signout)
				.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){
        			public boolean onItemClick(View p1, int p2, IDrawerItem p3){
						FirebaseAuth.getInstance().signOut();
						finish();
						return true;
					}
				})
		);
		if(user!=null&&user.isAnonymous()){
			items.add(
				new LineWrappingPrimaryDrawerItem()
				.withName(R.string.addEmail)
				.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){
						public boolean onItemClick(View p1, int p2, IDrawerItem p3){
							startActivityForResult(new Intent(AccountManagerActivity.this,SigninActivity.class).putExtra("add",true),0);
							return true;
						}
					})
			);
		}
		
		builder.withDrawerItems(items);
		builder.withSelectedItemByPosition(-1);
		builder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){
				public boolean onItemClick(View p1, int p2, IDrawerItem p3){
					builder.withSelectedItemByPosition(-1);
					return true;
				}
			});
		setContentView((content=builder.buildView()).getSlider());
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
            try {
                userImage = user.getPhotoUrl();
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
            } catch (Throwable e) {

            }
        }
		if(content!=null)content.getAdapter().notifyDataSetChanged();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0){
			if(resultCode==RESULT_OK){
				loadUserInfo();
			}else{
				setResult(resultCode);
				finish();
			}
		}
	}
	
	<T> List<T> emptyList(){
		return new ArrayList<T>();
	}
}
