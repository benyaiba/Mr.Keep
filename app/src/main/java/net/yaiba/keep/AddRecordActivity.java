package net.yaiba.keep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddRecordActivity extends Activity {
	
	private PasswordDB PasswordDB;

	private EditText SiteName;
	private EditText UserName;
	private EditText PasswordValue;
	private EditText Remark;
	private EditText PasswordLength;

	private CheckBox CheckboxUseNum;
	private CheckBox CheckboxUseWordLowcase;
	private CheckBox CheckboxUseWordUpcase;
	private CheckBox CheckboxUseSymbol;

	private LinearLayout passwordOption;

	private boolean isButton = true;
	
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



		//设置密码配置区域展开和隐藏
		Button bn_random_password = (Button)findViewById(R.id.random_password);
		passwordOption = (LinearLayout) findViewById(R.id.password_option);
		bn_random_password.setOnClickListener(new OnClickListener(){
			public void  onClick(View v)
			{
				if(isButton){
					passwordOption.setVisibility(View.VISIBLE);
					isButton = false;
				}else {
					passwordOption.setVisibility(View.GONE);
					isButton = true;
				}
			}
		});


		//按照选定的值生成随机密码，并填写到密码栏中
		Button bn_create_random_password = (Button)findViewById(R.id.create_random_password);
		bn_create_random_password.setOnClickListener(new OnClickListener(){
			public void  onClick(View v)
			{
				String setPassword = "";
				int PasswordLengthString = 8;
				Boolean isNumChecked = false;
				Boolean isUseWordLowcaseChecked = false;
				Boolean isUseWordUpcaseChecked = false;
				Boolean isUseSymbolChecked = false;

				CheckboxUseNum=(CheckBox)findViewById(R.id.checkbox_use_num);
				if(CheckboxUseNum.isChecked()){
					isNumChecked = true;
				}

				CheckboxUseWordLowcase=(CheckBox)findViewById(R.id.checkbox_use_word_lowcase);
				if(CheckboxUseWordLowcase.isChecked()){
					isUseWordLowcaseChecked =  true;
				}

				CheckboxUseWordUpcase=(CheckBox)findViewById(R.id.checkbox_use_word_upcase);
				if(CheckboxUseWordUpcase.isChecked()){
					isUseWordUpcaseChecked = true;
				}

				CheckboxUseSymbol=(CheckBox)findViewById(R.id.checkbox_use_symbol);
				if(CheckboxUseSymbol.isChecked()){
					isUseSymbolChecked = true;
				}

				PasswordLength=(EditText)findViewById(R.id.txt_password_length);

				if(PasswordLength.getText().toString().isEmpty() || Integer.parseInt(PasswordLength.getText().toString()) <=4){
					PasswordLengthString = 4;
				} else if(Integer.parseInt(PasswordLength.getText().toString())>16) {
					PasswordLengthString = 16;
				} else {
					PasswordLengthString = Integer.parseInt(PasswordLength.getText().toString());
				}


				String PasswordContainStr = ((EditText)findViewById(R.id.txt_contain)).getText().toString();
				String PasswordNotContainStr = ((EditText)findViewById(R.id.txt_not_contain)).getText().toString();
				String PasswordIndexStr = ((EditText)findViewById(R.id.txt_index)).getText().toString();

				//PasswordContain=(EditText)findViewById(R.id.txt_contain);
				//PasswordNotContain=(EditText)findViewById(R.id.txt_not_contain);
				//PasswordIndex=(EditText)findViewById(R.id.txt_index);


				PassWordCreate createNewPassword = new PassWordCreate();
				setPassword = createNewPassword.getRandomString(
						PasswordLengthString,
						isNumChecked,
						isUseWordLowcaseChecked,
						isUseWordUpcaseChecked,
						isUseSymbolChecked,
						PasswordContainStr,
						PasswordNotContainStr,
						PasswordIndexStr);

				PasswordValue = (EditText) findViewById(R.id.word_value);
				PasswordValue.setText(setPassword);

			}
		});


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
		
		String sitename = SiteName.getText().toString().replace("\n","");
		String username = UserName.getText().toString().replace("\n","");
		String passwordvalue = PasswordValue.getText().toString().replace("\n","");
		String remark = Remark.getText().toString();
		
		if (sitename.equals("")){
			Toast.makeText(this, "[名称]不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (sitename.length() >30){
			Toast.makeText(this, "[名称]长度不能超过30个文字，当前文字"+sitename.length(), Toast.LENGTH_SHORT).show();
			return false;
		}

		if (username.equals("")){
			Toast.makeText(this, "[登陆名]不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (username.length() >30){
			Toast.makeText(this, "[登陆名]长度不能超过30个文字，当前文字"+username.length(), Toast.LENGTH_SHORT).show();
			return false;
		}

		if (passwordvalue.equals("")){
			Toast.makeText(this, "[密码]不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (passwordvalue.length() >30){
			Toast.makeText(this, "[密码]长度不能超过30个文字，当前文字"+passwordvalue.length(), Toast.LENGTH_SHORT).show();
			return false;
		}

		if (remark.equals("")){
			remark = "";
		}

		if (remark.length() >2000){
			Toast.makeText(this, "[备注]长度不能超过2000个文字，当前文字"+remark.length(), Toast.LENGTH_SHORT).show();
			return false;
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
