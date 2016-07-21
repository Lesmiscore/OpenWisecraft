package com.nao20010128nao.Wisecraft.accounts;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.nao20010128nao.Wisecraft.misc.WorkingDialog;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.EditText;
import android.view.View;
import org.apache.commons.validator.EmailValidator;
import android.support.design.widget.Snackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.nao20010128nao.Wisecraft.misc.DebugWriter;

public class RegisterActivity extends AppCompatActivity
{
	EditText email,password,pw_again;
	FirebaseAuth auth;
	WorkingDialog wd;
	Snackbar snackbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts_register_screen);
		auth=FirebaseAuth.getInstance();
		wd=new WorkingDialog(this);
		snackbar=Snackbar.make(findViewById(android.R.id.content),"",Snackbar.LENGTH_LONG);
		if(auth.getCurrentUser()!=null){
			finish();
			return;
		}
		email=(EditText)findViewById(R.id.email);
		password=(EditText)findViewById(R.id.password);
		pw_again=(EditText)findViewById(R.id.password_again);
		findViewById(R.id.register).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					if(!validateEmail(email.getText().toString())){
						snackbar.setText(R.string.invalidEmail).show();
						return;
					}
					if(!password.getText().toString().equals(pw_again.getText().toString())){
						snackbar.setText(R.string.mismatchPassword).show();
						return;
					}
					if(!validatePassword(password.getText().toString())){
						snackbar.setText(R.string.invalidPassword).show();
						return;
					}
					wd.showWorkingDialog(getResources().getString(R.string.registering));
					auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
						.addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>(){
							public void onComplete(Task<AuthResult> task){
								wd.hideWorkingDialog();
								setResult(RESULT_OK);
								finish();
							}
						})
						.addOnFailureListener(RegisterActivity.this,new OnFailureListener(){
							public void onFailure(Exception err){
								wd.hideWorkingDialog();
								DebugWriter.writeToE("Register",err);
								snackbar.setText(R.string.unableSignin).show();
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
