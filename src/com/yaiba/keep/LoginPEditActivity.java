package com.yaiba.keep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPEditActivity extends Activity {
	
	private LoginDB LoginDB;
	
	private EditText Password;
	private EditText Password2;
	private EditText OldPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		LoginDB = new LoginDB(this);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_p_edit);
		
		Button bn_change = (Button)findViewById(R.id.change);
		bn_change.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {  
				   if(change()){
					   Toast.makeText(LoginPEditActivity.this, "修改成功，请牢记新的登录密码" , Toast.LENGTH_SHORT).show();
					   Intent mainIntent = new Intent(LoginPEditActivity.this,MainActivity.class);
					   startActivity(mainIntent);
					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					   setResult(RESULT_OK, mainIntent);  
					   finish();  
				   }
			   }  
			  });

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(LoginPEditActivity.this,MainActivity.class);
            startActivity(myIntent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	

	protected Boolean change(){
		OldPassword = (EditText)findViewById(R.id.old_password);
		Password = (EditText)findViewById(R.id.password);
		Password2 = (EditText)findViewById(R.id.password2);
		
		String oldPassword = OldPassword.getText().toString();
		String password = Password.getText().toString();
		String password2 = Password2.getText().toString();

		
		if (oldPassword.equals("")){
			Toast.makeText(this, "当前密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.equals("")){
			Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password2.equals("")){
			Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!password.equals(password2)){
			Toast.makeText(this, "两组新密码不一致", Toast.LENGTH_SHORT).show();
			return false;
		}
		

		try {
			if(LoginDB.isCurrentPassword(DES.encryptDES(oldPassword)) >= 0){
				LoginDB.update(LoginDB.isCurrentPassword(DES.encryptDES(oldPassword)), DES.encryptDES(password));
				return true;
			}else{
				Toast.makeText(LoginPEditActivity.this, "当前密码不正确" , Toast.LENGTH_SHORT).show();
				return false;
			}
		} catch (Exception e) {
			Toast.makeText(LoginPEditActivity.this, "真的出错了" , Toast.LENGTH_SHORT).show();
			return false;
		}
	}

}
