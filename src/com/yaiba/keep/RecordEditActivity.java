package com.yaiba.keep;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RecordEditActivity extends Activity {
	private PasswordDB PasswordDB;
	private Cursor mCursor;
	private EditText SiteName;
	private EditText UserName;
	private EditText PasswordValue;
	private EditText Remark;
	private int RECORD_ID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		PasswordDB = new PasswordDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_edit);
		RECORD_ID = this.getIntent().getIntExtra("INT", RECORD_ID);
		
		setUpViews();
		
		Button bn_add = (Button)findViewById(R.id.add);
		bn_add.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {  
				   if(update()){
					   Intent mainIntent = new Intent(RecordEditActivity.this,MainActivity.class);
					   startActivity(mainIntent);
					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					   setResult(RESULT_OK, mainIntent);  
					   finish();  
				   }
			   }  
			  });
		
		Button bn_back = (Button)findViewById(R.id.back);
		bn_back.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {  
					   Intent mainIntent = new Intent(RecordEditActivity.this,MainActivity.class);
					   startActivity(mainIntent);
					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					   setResult(RESULT_OK, mainIntent);  
					   finish();  
			   }  
			  });
	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent mainIntent = new Intent(RecordEditActivity.this,MainActivity.class);
			   startActivity(mainIntent);
			   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			   setResult(RESULT_OK, mainIntent);  
			   finish(); 
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@SuppressWarnings("deprecation")
	public Boolean update(){
	
		String sitename = SiteName.getText().toString();
		String username = UserName.getText().toString();
		String passwordvalue = PasswordValue.getText().toString();
		String remark = Remark.getText().toString();

		if (sitename.equals("") || username.equals("") || passwordvalue.equals("")){
			return false;
		}
		try {
			PasswordDB.update(RECORD_ID, sitename, username, DES.encryptDES(passwordvalue), remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mCursor.requery();
		Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	public void setUpViews(){
		PasswordDB = new PasswordDB(this);
		mCursor = PasswordDB.getOne(RECORD_ID);
		 
		SiteName = (EditText)findViewById(R.id.site_name);
		UserName = (EditText)findViewById(R.id.user_name);
		PasswordValue = (EditText)findViewById(R.id.word_value);
		Remark = (EditText)findViewById(R.id.remark);
	
		SiteName.setText(mCursor.getString(1));
		UserName.setText(mCursor.getString(2));
		try {
			PasswordValue.setText(DES.decryptDES(mCursor.getString(3)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Remark.setText(mCursor.getString(4));
	}
}
