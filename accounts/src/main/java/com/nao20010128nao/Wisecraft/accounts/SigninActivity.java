package com.nao20010128nao.Wisecraft.accounts;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.nao20010128nao.Wisecraft.misc.*;
import org.apache.commons.validator.*;

public class SigninActivity extends AppCompatActivity
{
	EditText email,password;
	FirebaseAuth auth;
	WorkingDialog wd;
	Snackbar snackbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts_signin_screen);
		auth=FirebaseAuth.getInstance();
		wd=new WorkingDialog(this);
		snackbar=Snackbar.make(findViewById(android.R.id.content),"",Snackbar.LENGTH_LONG);
        
		email=(EditText)findViewById(R.id.email);
		password=(EditText)findViewById(R.id.password);
		findViewById(R.id.signin).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					if(!validateEmail(email.getText().toString())){
						snackbar.setText(R.string.invalidEmail).show();
						return;
					}
					if(!validatePassword(password.getText().toString())){
						snackbar.setText(R.string.invalidPassword).show();
						return;
					}
					wd.showWorkingDialog(getResources().getString(R.string.signingin));
					if(getIntent().getBooleanExtra("add",false)){
						auth.getCurrentUser().linkWithCredential(EmailAuthProvider.getCredential(email.getText().toString(),password.getText().toString()))
							.addOnCompleteListener(SigninActivity.this,new OnCompleteListener<AuthResult>(){
								public void onComplete(Task<AuthResult> task){
									wd.hideWorkingDialog();
									setResult(RESULT_OK);
									finish();
								}
							})
							.addOnFailureListener(SigninActivity.this,new OnFailureListener(){
								public void onFailure(Exception err){
									wd.hideWorkingDialog();
									DebugWriter.writeToE("Sign-In",err);
									snackbar.setText(R.string.unableSignin).show();
								}
							});
					}else{
						auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
							.addOnCompleteListener(SigninActivity.this,new OnCompleteListener<AuthResult>(){
								public void onComplete(Task<AuthResult> task){
									wd.hideWorkingDialog();
									setResult(RESULT_OK);
									finish();
								}
							})
							.addOnFailureListener(SigninActivity.this,new OnFailureListener(){
								public void onFailure(Exception err){
									wd.hideWorkingDialog();
									DebugWriter.writeToE("Sign-In",err);
									snackbar.setText(R.string.unableSignin).show();
								}
							});
					}
				}
			});
		findViewById(R.id.signinAnon).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					wd.showWorkingDialog(getResources().getString(R.string.signingin));
					auth.signInAnonymously()
						.addOnCompleteListener(SigninActivity.this,new OnCompleteListener<AuthResult>(){
							public void onComplete(Task<AuthResult> task){
								wd.hideWorkingDialog();
								setResult(RESULT_OK);
								finish();
							}
						})
						.addOnFailureListener(SigninActivity.this,new OnFailureListener(){
							public void onFailure(Exception err){
								wd.hideWorkingDialog();
								DebugWriter.writeToE("Sign-In",err);
								snackbar.setText(R.string.unableSigninAnon).show();
							}
						});
				}
			});
		findViewById(R.id.close).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					setResult(RESULT_CANCELED);
					finish();
				}
			});
		findViewById(R.id.newAccount).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					startActivityForResult(new Intent(SigninActivity.this,RegisterActivity.class).putExtras(getIntent()),0);
				}
			});
			
		if(getIntent().getBooleanExtra("add",false)){
			findViewById(R.id.signinAnon).setEnabled(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
			case 0://register
				if(resultCode==RESULT_OK){
					finish();
					return;
				}
				break;
		}
	}
	
	public boolean validateEmail(String mail){
		return EmailValidator.getInstance().isValid(mail);
	}
	public boolean validatePassword(String password){
		if(password.length()<8){
			return false;
		}
		return true;
	}
}
