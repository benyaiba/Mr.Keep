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

public class AddRecordActivity extends Activity {
	
	private PasswordDB PasswordDB;
	private EditText SiteName;
	private EditText UserName;
	private EditText PasswordValue;
	private EditText Remark;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		PasswordDB = new PasswordDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_record);
		
		Button bn_add = (Button)findViewById(R.id.add);
		bn_add.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {  
				   if(add()){
					   Intent mainIntent = new Intent(AddRecordActivity.this,MainActivity.class);
					   startActivity(mainIntent);
					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					   setResult(RESULT_OK, mainIntent);  
					   finish();  
				   }
			   }  
			  });
		
//		Button bn_back = (Button)findViewById(R.id.back);
//		bn_back.setOnClickListener(new OnClickListener(){
//			   public void  onClick(View v)
//			   {
//					   Intent mainIntent = new Intent(AddRecordActivity.this,MainActivity.class);
//					   startActivity(mainIntent);
//					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//					   setResult(RESULT_OK, mainIntent);
//					   finish();
//			   }
//			  });
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(AddRecordActivity.this,MainActivity.class);
            startActivity(myIntent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	

	protected Boolean add(){
		SiteName = (EditText)findViewById(R.id.site_name);
		UserName = (EditText)findViewById(R.id.user_name);
		PasswordValue = (EditText)findViewById(R.id.word_value);
		Remark = (EditText)findViewById(R.id.remark);
		
		String sitename = SiteName.getText().toString();
		String username = UserName.getText().toString();
		String passwordvalue = PasswordValue.getText().toString();
		String remark = Remark.getText().toString();
		
		if (sitename.equals("")){
			Toast.makeText(this, "网站名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (username.equals("")){
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (passwordvalue.equals("")){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (remark.equals("")){
			remark = "";
		}
		
		try {
			PasswordDB.insert(sitename, username, DES.encryptDES(passwordvalue), remark);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
		return true;
	}

}
