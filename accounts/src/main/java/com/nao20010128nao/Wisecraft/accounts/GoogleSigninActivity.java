package com.nao20010128nao.Wisecraft.accounts;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import com.google.android.gms.auth.api.*;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.*;
import com.google.android.gms.common.api.*;
import com.google.firebase.auth.*;
import com.google.android.gms.tasks.*;
import com.nao20010128nao.Wisecraft.*;

public class GoogleSigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
	GoogleApiClient gac;
	FirebaseAuth auth=FirebaseAuth.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(getResources().getString(R.string.google_api_token))
			.requestEmail().requestProfile().build();
		gac=new GoogleApiClient.Builder(this)
			.enableAutoManage(this,this)
			.addApi(Auth.GOOGLE_SIGN_IN_API,gso)
			.build();
		Intent signin=Auth.GoogleSignInApi.getSignInIntent(gac);
		startActivityForResult(signin,0);
	}

	@Override
	public void onConnectionFailed(ConnectionResult p1) {
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==0){
			GoogleSignInResult gsr=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			processResult(gsr);
		}
	}
	
	private void processResult(GoogleSignInResult gsr){
		if(gsr.isSuccess()){
			GoogleSignInAccount gsa=gsr.getSignInAccount();
			AuthCredential cred=GoogleAuthProvider.getCredential(gsa.getIdToken(),null);
			auth.signInWithCredential(cred)
				.addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
					public void onComplete(Task<AuthResult> task){
						if(task.isSuccessful()){
							setResult(RESULT_OK);
							finish();
						}else{
							WisecraftError.report("GoogleSignIn",task.getException());
							setResult(RESULT_CANCELED);
							finish();
						}
					}
				});
		}else{
			setResult(RESULT_CANCELED);
			finish();
		}
	}
}
