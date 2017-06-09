package com.yaiba.keep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener; 
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yaiba.keep.data.ListViewData;

import com.yaiba.keep.PasswordDB;
import com.yaiba.keep.UpdateTask;


public class MainActivity extends Activity implements  AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
	
	private static final int MENU_ABOUT = 0;
	private static final int MENU_SUPPORT = 1;
	private static final int MENU_WHATUPDATE = 2;
	private static final int MENU_IMPORT_EXPOERT = 3;
	private static final int MENU_CHANGE_LOGIN_PASSWORD = 4;
	private static final int MENU_CHECK_UPDATE = 5;
	private PasswordDB PasswordDB;
	private Cursor mCursor;
	
	private EditText SiteName;
	private EditText UserName;
	private EditText PasswordValue;
	private EditText Remark;
	private EditText SearchInput;
	private ListView RecordList;
	 
	private int RECORD_ID = 0;
	private UpdateManager updateMan;
	private UpdateTask updateTask;
	private ProgressDialog updateProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_list);
		setUpViews("all",null);

		//返回前设置前次的位置值
		setRecordListPosition();

		Button bn_go_add = (Button)findViewById(R.id.go_add);
		bn_go_add.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {
				   Intent mainIntent = new Intent(MainActivity.this,AddRecordActivity.class);
				   startActivity(mainIntent);
				   setResult(RESULT_OK, mainIntent);  
				   finish();  
			   }  
			  });
		
		
		SearchInput = (EditText)findViewById(R.id.searchInput);
		SearchInput.clearFocus();
		SearchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	if(SearchInput.getText().toString().trim().length()!=0){
            		try {
            			setUpViews("search",SearchInput.getText().toString().trim());
					} catch (Exception e) {
						e.printStackTrace();
					}
            	} else {
            		setUpViews("all",null);
            	}
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            	//Toast.makeText(LoginActivity.this, "beforeTextChanged", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            	//Toast.makeText(LoginActivity.this, "afterTextChanged", Toast.LENGTH_SHORT).show();
            }

		});
		
		//检查是否有更新
        //如果有更新提示下载
        //updateMan = new UpdateManager(MainActivity.this, appUpdateCb);
        //updateMan.checkUpdate();
		
		//updateTask = new UpdateTask(MainActivity.this,true);
		//updateTask.update();
		

	}
	
	private static Boolean isExit = false;
    private static Boolean hasTask = false;
    Timer tExit = new Timer();
    TimerTask task = new TimerTask(){
          
        @Override
        public void run() {
            isExit = true;
            hasTask = true;
        }
    };
	
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isExit == false ) {
                isExit = true;
                Toast.makeText(this, "再按一次后退键退出应用程序", Toast.LENGTH_SHORT).show();
                if(!hasTask) {
                    tExit.schedule(task, 2000);
                }
            } else {
                finish();
                System.exit(0);
            }
        }
        return false;
    }
    

	public void setUpViews(String type, String value){
		PasswordDB = new PasswordDB(this);
		if("all".equals(type)){
			mCursor = PasswordDB.getAll("id desc");
		} else if("search".equals(type)) {
			mCursor = PasswordDB.getForSearch(value);
		}
		
		 
		/*SiteName = (EditText)findViewById(R.id.site_name);
		UserName = (EditText)findViewById(R.id.login_name);
		PasswordValue = (EditText)findViewById(R.id.word_value);*/
		RecordList = (ListView)findViewById(R.id.recordslist);
		
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        
        for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()) {  
            int nameColumn = mCursor.getColumnIndex("site_name");  
            int phoneColumn = mCursor.getColumnIndex("user_name");  
            /*String resNo = "["+mCursor.getString(resNoColumn)+"]"; */
            String siteName = mCursor.getString(nameColumn);  
            String userName = mCursor.getString(phoneColumn); 
            
            HashMap<String, Object> map = new HashMap<String, Object>();  
            //map.put("ItemImage", R.drawable.checked);//图像资源的ID  
            /*map.put("res_no", resNo);*/
            map.put("site_name", siteName);  
            map.put("user_name", userName);  
            listItem.add(map);
        }  
        	
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,R.layout.record_items,
            /*new String[] {"res_no", "site_name", "user_name"},   
            new int[] {R.id.res_no, R.id.site_name,R.id.user_name} */ 
        	new String[] {"site_name", "user_name"},   
        	new int[] {R.id.site_name,R.id.user_name} 
        ); 
		
		RecordList.setAdapter(listItemAdapter);
		RecordList.setOnItemClickListener(this);
		RecordList.setOnItemLongClickListener(this);
		RecordList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
              
	        @Override  
	        public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
	            //menu.setHeaderTitle("操作");     
	            menu.add(0, 0, 0, "编辑");  
	            menu.add(0, 1, 0, "删除");     
	        }  
        });
		
		}
	
    @Override  
    public boolean onContextItemSelected(MenuItem item) {  
        //setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");   
        //Toast.makeText(this, "点击了长按菜单里面的第"+item.getItemId()+"个项目", Toast.LENGTH_SHORT).show();
        super.onContextItemSelected(item);  
        switch (item.getItemId())
		{
			case 0:
				go_update();
			break;
			case 1:
				AlertDialog.Builder builder= new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle("确认");
				builder.setMessage("确定要删除这条记录吗？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	delete();
                    	setUpViews("all",null);
                    }  
                });
				builder.setNegativeButton("取消", null);
				builder.create().show();
			break;
		}
		return true;
    }  
		 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_IMPORT_EXPOERT, 0, this.getString(R.string.menu_inport_export));//备份与恢复
		menu.add(Menu.NONE, MENU_CHANGE_LOGIN_PASSWORD, 0, this.getString(R.string.menu_change_login_password));//修改登录密码
		menu.add(Menu.NONE, MENU_WHATUPDATE, 0, this.getString(R.string.menu_whatupdate));//更新信息
		menu.add(Menu.NONE, MENU_CHECK_UPDATE, 0, this.getString(R.string.menu_checkupdate));//检查更新
		menu.add(Menu.NONE, MENU_SUPPORT, 0, this.getString(R.string.menu_support));//技术支持
		menu.add(Menu.NONE, MENU_ABOUT, 0, this.getString(R.string.menu_about));//关于Keep
		return true;
	}
	 
	public boolean onOptionsItemSelected(MenuItem item)
	{
		String title = "";
		String msg = "";
		//Context mContext = null;
		
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
			case MENU_ABOUT:
				title = this.getString(R.string.menu_about);//关于Keep
				msg = this.getString(R.string.about_keep);
				msg = msg + "\n\n";
				msg = msg + "@"+getAppVersion();
				showAboutDialog(title,msg);
			break;
			case MENU_SUPPORT:
				title = this.getString(R.string.menu_support);//技术支持
				msg = this.getString(R.string.partners);
				showAboutDialog(title,msg);
			break;
			case MENU_WHATUPDATE:
				title = this.getString(R.string.menu_whatupdate);//更新信息
				msg = msg + this.getString(R.string.what_updated);
				msg = msg + "\n\n\n";
				showAboutDialog(title,msg);
			break;
			case MENU_CHECK_UPDATE:
				title = this.getString(R.string.menu_checkupdate);//检查更新
				msg = "";//1.增加双服务器检测更新机制\n2.检查更新\n\n以上功能
				
				updateTask = new UpdateTask(MainActivity.this,true);
				updateTask.update();
				
				//showAboutDialog(title,msg); 
			break;
			case MENU_IMPORT_EXPOERT://备份与恢复
				Intent mainIntent = new Intent(MainActivity.this, DataManagementActivity.class);
			    startActivity(mainIntent);
			    setResult(RESULT_OK, mainIntent);  
			    finish(); 
			break;
			case MENU_CHANGE_LOGIN_PASSWORD:
				Intent mainIntent2 = new Intent(MainActivity.this, LoginPEditActivity.class);
			    startActivity(mainIntent2);
			    setResult(RESULT_OK, mainIntent2);  
			    finish(); 
			break;
		}
		return true;
	}
	 
	public void showAboutDialog(String title,String msg){
		AlertDialog.Builder builder= new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("确定", null);
		builder.create().show();
	}
	
	public void showConfirmDialog(String title,String msg){
		Dialog alertDialog = new AlertDialog.Builder(this).   
            setTitle("确定删除？").   
            setMessage("您确定删除该条信息吗？").   
            setIcon(R.drawable.ic_launcher).   
            setPositiveButton("确定", new DialogInterface.OnClickListener() {   
                   
                @Override   
                public void onClick(DialogInterface dialog, int which) {   
                    // TODO Auto-generated method stub    
                }   
            }).   
            setNegativeButton("取消", new DialogInterface.OnClickListener() {   
                   
                @Override   
                public void onClick(DialogInterface dialog, int which) {   
                    // TODO Auto-generated method stub    
                }   
            }).   
            create();   
    alertDialog.show();
	}
	
	
	
	
	 
	@SuppressWarnings("deprecation")
	public void delete(){
		if (RECORD_ID == 0) {
			return;
		}
		PasswordDB.delete(RECORD_ID);
		mCursor.requery();
		Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
	}
	 
	public void go_update(){
		//保存当前位置
		saveListViewPositionAndTop();
		//画面迁移到edit画面
		 Intent mainIntent = new Intent(MainActivity.this,RecordEditActivity.class);
		 mainIntent.putExtra("INT", RECORD_ID);
		 startActivity(mainIntent);
		 setResult(RESULT_OK, mainIntent);  
		 finish(); 
	}
	
	
	@SuppressWarnings("deprecation")
	public void update(){
		String sitename = SiteName.getText().toString();
		String username = UserName.getText().toString();
		String passwordvalue = PasswordValue.getText().toString();
		String remark = Remark.getText().toString();

		if (sitename.equals("") || username.equals("") || passwordvalue.equals("")){
			return;
		}
		PasswordDB.update(RECORD_ID, sitename, username, passwordvalue, remark);
		mCursor.requery();
		RecordList.invalidateViews();
		SiteName.setText("");
		UserName.setText("");
		PasswordValue.setText("");
		Remark.setText("");
		Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
	}
	 
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//保存当前一览位置
		saveListViewPositionAndTop();
		//迁移到详细页面
		Intent mainIntent = new Intent(MainActivity.this,RecordDetailActivity.class);
		 mCursor.moveToPosition(position);
		 RECORD_ID = mCursor.getInt(0);
		 mainIntent.putExtra("INT", RECORD_ID);
		 startActivity(mainIntent);
		 setResult(RESULT_OK, mainIntent);  
		 finish(); 
	}

	/**
	 * 保存当前页签listView的第一个可见的位置和top
	 */
	private void saveListViewPositionAndTop() {

		final ListViewData app = (ListViewData)getApplication();

		app.setFirstVisiblePosition(RecordList.getFirstVisiblePosition());
		View item = RecordList.getChildAt(0);
		app.setFirstVisiblePositionTop((item == null) ? 0 : item.getTop());
	}



	 
	public class RecordListAdapter extends BaseAdapter{
		private Context mContext;
		private Cursor mCursor;
		public RecordListAdapter(Context context,Cursor cursor) {
		 
		mContext = context;
		mCursor = cursor;
	}
	
	@Override
	public int getCount() {
		return mCursor.getCount();
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView mTextView = new TextView(mContext);
		mCursor.moveToPosition(position);
		mTextView.setText(mCursor.getString(1) + "___" + mCursor.getString(2)+ "___" + mCursor.getString(3)+ "___" + mCursor.getString(4));
		return mTextView;
	}
}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		mCursor.moveToPosition(position);
		RECORD_ID = mCursor.getInt(0);
		return false;
	}
	
	private String getAppVersion() {
 		try {
 			String pkName = this.getPackageName();
 			String versionName = this.getPackageManager().getPackageInfo(pkName, 0).versionName;
 			//int versionCode = this.getPackageManager().getPackageInfo(pkName, 0).versionCode;
 			return versionName;
 		} catch (Exception e) {
 		}
 		return null;
 	}
	
	// 自动更新回调函数
    UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback() 
    {

        public void downloadProgressChanged(int progress) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.setProgress(progress);
            }

        }

        public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.dismiss();
            }
            if (sucess) {
                updateMan.update();
            } else {
                DialogHelper.Confirm(MainActivity.this,
                        R.string.dialog_error_title,
                        R.string.dialog_downfailed_msg,
                        R.string.dialog_downfailed_btndown,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                                updateMan.downloadPackage();

                            }
                        }, R.string.dialog_downfailed_btnnext, null);
            }
        }

        public void downloadCanceled() 
        {
            // TODO Auto-generated method stub

        }

        public void checkUpdateCompleted(Boolean hasUpdate, CharSequence updateInfo) {
            if (hasUpdate) {
                DialogHelper.Confirm(MainActivity.this,
                        getText(R.string.dialog_update_title),
                        getText(R.string.dialog_update_msg).toString()
                        +updateInfo+
                        getText(R.string.dialog_update_msg2).toString(),
                                getText(R.string.dialog_update_btnupdate),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                updateProgressDialog = new ProgressDialog(MainActivity.this);
                                updateProgressDialog.setMessage(getText(R.string.dialog_downloading_msg));
                                updateProgressDialog.setIndeterminate(false);
                                updateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                updateProgressDialog.setMax(100);
                                updateProgressDialog.setProgress(0);
                                updateProgressDialog.show();

                                updateMan.downloadPackage();
                            }
                        },getText( R.string.dialog_update_btnnext), null);
            }

        }
    };

	//返回前设置前次的位置值
	public void setRecordListPosition(){
		ListViewData app = (ListViewData)getApplication();
		RecordList.setSelectionFromTop(app.getFirstVisiblePosition(), app.getFirstVisiblePositionTop());
	}
	
		
}
