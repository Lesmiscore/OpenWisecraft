package com.nao20010128nao.Wisecraft.accounts;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.nao20010128nao.Wisecraft.misc.WorkingDialog;
import android.view.View;
import java.util.regex.Pattern;
import org.apache.commons.validator.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import android.support.design.widget.Snackbar;
import com.nao20010128nao.Wisecraft.misc.DebugWriter;
import android.content.Intent;

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
		if(auth.getCurrentUser()!=null){
			finish();
			return;
		}
		email=(EditText)findViewById(R.id.email);
		password=(EditText)findViewById(R.id.password);
		findViewById(R.id.signin).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					if(!validateEmail(email.getText().toString())){
						//invalid email
						return;
					}
					if(!validatePassword(password.getText().toString())){
						//invalid password
						return;
					}
					wd.showWorkingDialog(getResources().getString(R.string.signingin));
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
					startActivityForResult(new Intent(SigninActivity.this,RegisterActivity.class),0);
				}
			});
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
