package com.nao20010128nao.Wisecraft.accounts;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.nao20010128nao.Wisecraft.misc.WorkingDialog;
import android.view.View;
import java.util.regex.Pattern;
import org.apache.commons.validator.EmailValidator;

public class SigninActivity extends AppCompatActivity
{
	EditText email,password;
	FirebaseAuth auth;
	WorkingDialog wd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts_signin_screen);
		auth=FirebaseAuth.getInstance();
		wd=new WorkingDialog(this);
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
