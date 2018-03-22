package net.yaiba.keep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v13.app.ActivityCompat;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import static android.R.id.checkbox;
import static android.content.ContentValues.TAG;

public class DataManagementActivity extends Activity {
	private PasswordDB PasswordDB;
	private Cursor mCursor;
	private int RECORD_ID = 0;
	private int selectBakupFileIndex = 0;
	private String[] bakFileArray ;
	private String FILE_DIR_NAME = "yaiba.net//Mr.Keep";
	//private String fileName = "keepres.xml";
	//private String fileNamePre = "keep_";
	private String fileNameSuff = ".xml";
	private String fileNameMass = "keepres_mass.xml";

	private TextView warmingHaveUnencodeFile;

	private CheckBox encode;
	private Context context;

	private TextView TotalCount;
	private Long lCount;

	public Integer encryptFileCount;
	public Integer unEncryptFileCount;
	public Integer messImportFileCount;

	private TextView FileCount;
	private TextView MessImportfileCount;


	//检测是否有写的权限用
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		PasswordDB = new PasswordDB(this);
		super.onCreate(savedInstanceState);

		//检测是否有写的权限用
		verifyStoragePermissions(DataManagementActivity.this);

		//判断文件名中是否包含20170216020803!!!!!.xml 这种文件，如果目录中包含这种文件，将在画面最下方以红色文字提示。
		List<String> bakupFileList = new ArrayList<String>();
		String keepPath = Environment.getExternalStorageDirectory().toString()  + "/" +FILE_DIR_NAME;
		File[] files = new File(keepPath).listFiles();

		setContentView(R.layout.activity_data_management);

		lCount = PasswordDB.getAllCount("site_name asc");

		encryptFileCount =0 ;
		unEncryptFileCount =0;
		messImportFileCount =0 ;

		if(files != null){
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
//				if(file.getName().indexOf("!!!!!") != -1 || file.getName().indexOf("keepres_mass")!= -1){
//					warmingHaveUnencodeFile = (TextView)findViewById(R.id.warmingHaveUnencodeFile);
//					warmingHaveUnencodeFile.setText(R.string.menu_warming_have_unencode_file);
//					warmingHaveUnencodeFile.setTextColor(getResources().getColor(R.color.royalblue));
//					break;
//				}

				if(file.getName().contains(".xml")){//判断是备份或导入文件
					if(file.getName().contains("!!!!!")){//判断这个文件是非加密的备份文件
						unEncryptFileCount ++;
					} else if(file.getName().contains("keepres_mass")) {//判断这个文件是批量导入文件
						messImportFileCount ++;
					} else {//判断是常规备份文件
						encryptFileCount ++;
					}
				}
			}
			 if(unEncryptFileCount >0 || messImportFileCount>0){
				 warmingHaveUnencodeFile = (TextView)findViewById(R.id.warmingHaveUnencodeFile);
				 warmingHaveUnencodeFile.setText(R.string.menu_warming_have_unencode_file);
				 warmingHaveUnencodeFile.setTextColor(getResources().getColor(R.color.royalblue));
			 }


			TotalCount = (TextView) findViewById(R.id.redordCount);
			TotalCount.setText("可备份记录数：x".replace("x", lCount.toString()));

			FileCount = (TextView) findViewById(R.id.fileCount);
			FileCount.setText("加密/未加密文件数：x/y".replace("x", encryptFileCount.toString()).replace("y",unEncryptFileCount.toString()));

			MessImportfileCount = (TextView) findViewById(R.id.messImportfileCount);
			MessImportfileCount.setText("批量导入文件数：x".replace("x", messImportFileCount.toString()));

		}

		// 注释掉区域暂未开启使用
//		Button bn_back = (Button)findViewById(R.id.back);
//		bn_back.setOnClickListener(new OnClickListener(){
//			   public void  onClick(View v)
//			   {
//					   Intent mainIntent = new Intent(DataManagementActivity.this,MainActivity.class);
//					   startActivity(mainIntent);
//					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//					   setResult(RESULT_OK, mainIntent);
//					   finish();
//			   }
//			  });


		
		
		Button bn_data_bakup = (Button)findViewById(R.id.data_bakup);
		bn_data_bakup.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {  
				   dataBakup();
				   
			   }  
			  });
		
		Button bn_data_recover = (Button)findViewById(R.id.data_recover);
		bn_data_recover.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)   
			   
			   {  
				   
		   //弹出提示，确认后恢复
		   AlertDialog.Builder builder= new AlertDialog.Builder(DataManagementActivity.this);
		   builder.setIcon(android.R.drawable.ic_dialog_info);
		   builder.setTitle("选择要恢复的文件,恢复后原有记录将被清空");

		   List<String> bakupFileList = new ArrayList<String>();
		   String keepPath = Environment.getExternalStorageDirectory().toString()  + "/" +FILE_DIR_NAME;
		   File[] files = new File(keepPath).listFiles();

		   //判断文件夹不存在或文件夹中没有文件时
		   if(files != null){
			   //存在时

			   for (int i = 0; i < files.length; i++) {
				   File file = files[i];
				   if (checkIsXMLFile(file.getPath())) {
					   //判断文件名中是否不包含20170216020803!!!!!.xml 这种文件，这种文件是未加密的文件，禁止在列表中显示
					   if(file.getName().indexOf("!")==-1){
						   bakupFileList.add(file.getName());
					   }
				   }
			   }

			   if(bakupFileList.size()<=0){

				   builder.setMessage("没有找到可用来恢复的备份文件");
				   builder.setNegativeButton("取消", null);
				   builder.create().show();

			   } else {
				   Collections.sort(bakupFileList);
				   Collections.reverse(bakupFileList);

				   bakFileArray = bakupFileList.toArray(new String[bakupFileList.size()]);


				   builder.setIcon(android.R.drawable.ic_dialog_alert);
				   builder.setSingleChoiceItems(bakFileArray, 0, new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int index) {
						   selectBakupFileIndex = index;
						   //Toast.makeText(DataManagementActivity.this, "selectBakupFileIndex:"+selectBakupFileIndex , Toast.LENGTH_SHORT).show();
					   }
				   });
				   builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int whichButton) {
						   dataRecover(bakFileArray[selectBakupFileIndex]);
						   //Toast.makeText(DataManagementActivity.this, "selectBakupFileIndex:"+selectBakupFileIndex , Toast.LENGTH_SHORT).show();
					   }
				   });
				   builder.setNegativeButton("取消", null);
				   builder.create().show();

//					   builder.setMessage("程序将读取SD卡中的备份文件"+fileName+"，恢复后原有记录将被清空。确定要执行吗？");
//						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//		                    public void onClick(DialogInterface dialog, int whichButton) {
//		                    	dataRecover();
//		                    }
//		                });
//						builder.setNegativeButton("取消", null);
//						builder.create().show();
			   }
		   } else {
			   //不存在时
			   builder.setMessage("没有找到可用来恢复的备份文件");
			   builder.setNegativeButton("取消", null);
			   builder.create().show();
		   }
	   }
	  });
		
		Button bn_data_mess_import = (Button)findViewById(R.id.data_mess_import);
		bn_data_mess_import.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {  
					   
				 //弹出提示，确认后恢复
				AlertDialog.Builder builder= new AlertDialog.Builder(DataManagementActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle("确认");
				builder.setMessage("程序将读取SD卡中的文件"+fileNameMass+"并导入数据,程序中的原有记录不会被删除。确定要执行吗？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int whichButton) {  
						dataMessImport();
					}  
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();
				   
			   }  
			  });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent mainIntent = new Intent(DataManagementActivity.this,MainActivity.class);
			startActivity(mainIntent);
			overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			setResult(RESULT_OK, mainIntent);  
			finish(); 
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@SuppressWarnings("resource")
	@SuppressLint("SimpleDateFormat")
	public void dataBakup(){
		//Toast.makeText(this, "他很懒，备份程序还没做好", Toast.LENGTH_SHORT).show();
		PasswordDB = new PasswordDB(this);
		mCursor = PasswordDB.getAll(null);

		//检查目录并确定生成目录结构
		//boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		String path = getBasePath(this);
		String baseDir = path + "/" + FILE_DIR_NAME;
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm_ss");
		//String fileName = fileNamePre + sdf.format(dt)+ fileNameSuff;


		encode = (CheckBox)findViewById(R.id.encode);
		if(encode.isChecked()){
			//showAboutDialog("准备","正在处理加密数据");
			try {
				if (!path.isEmpty()) {
					String fileName = sdf.format(dt)+ fileNameSuff;
					File f = createFile(baseDir,fileName);

					FileWriter fileWriter = new FileWriter(f,false);
					BufferedWriter bf = new BufferedWriter(fileWriter);
					bf.write(writeToString(mCursor,true));
					bf.flush();
					showAboutDialog("完成","备份文件"+fileName+"已输出到SD卡，包含"+lCount.toString()+"条记录。(已加密处理)");
					refresh();
				}
			} catch (IOException e) {
				showAboutDialog("错误","处理数据时出错，文件未生成");
			}

		} else {
			//showAboutDialog("准备","正在处理未加密数据");
			try {
				if (!path.isEmpty()) {
					String fileName = sdf.format(dt) + "!!!!!"+ fileNameSuff;
					File f = createFile(baseDir, fileName);

					FileWriter fileWriter = new FileWriter(f,false);
					BufferedWriter bf = new BufferedWriter(fileWriter);
					bf.write(writeToString(mCursor,false));
					bf.flush();
					showAboutDialog("完成","备份文件"+fileName+"已输出到SD卡，包含"+lCount.toString()+"条记录。(备份数据未加密，请妥善保存。)");
					refresh();
				}
			} catch (IOException e) {
				showAboutDialog("错误","处理数据时出错，文件未生成");
			}
		}
	}


	public String getBasePath(Context pContext){

		final StorageManager storageManager = (StorageManager) pContext.getSystemService(Context.STORAGE_SERVICE);

		try {
			//得到StorageManager中的getVolumeList()方法的对象
			final Method getVolumeList = storageManager.getClass().getMethod("getVolumeList");
			//---------------------------------------------------------------------

			//得到StorageVolume类的对象
			final Class<?> storageValumeClazz = Class.forName("android.os.storage.StorageVolume");
			//---------------------------------------------------------------------
			//获得StorageVolume中的一些方法
			final Method getPath = storageValumeClazz.getMethod("getPath");
			Method isRemovable = storageValumeClazz.getMethod("isRemovable");

			Method mGetState = null;
			//getState 方法是在4.4_r1之后的版本加的，之前版本（含4.4_r1）没有
			// （http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.4_r1/android/os/Environment.java/）
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
				try {
					mGetState = storageValumeClazz.getMethod("getState");
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			//---------------------------------------------------------------------

			//调用getVolumeList方法，参数为：“谁”中调用这个方法
			final Object invokeVolumeList = getVolumeList.invoke(storageManager);
			//---------------------------------------------------------------------
			final int length = Array.getLength(invokeVolumeList);
			for (int i = 0; i < length; i++) {
				final Object storageValume = Array.get(invokeVolumeList, i);//得到StorageVolume对象
				final String path = (String) getPath.invoke(storageValume);
				final boolean removable = (Boolean) isRemovable.invoke(storageValume);
				//removable=true时，指的是可拔出外置存储/storage/emulated/0/，false时是内置存储/storage/emulated/0/
				if(!removable){
					String state = null;
					if (mGetState != null) {
						state = (String) mGetState.invoke(storageValume);
					} else {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
							state = Environment.getStorageState(new File(path));
						} else {
							if (removable) {
								state = EnvironmentCompat.getStorageState(new File(path));
							} else {
								//不能移除的存储介质，一直是mounted
								state = Environment.MEDIA_MOUNTED;
							}
							final File externalStorageDirectory = Environment.getExternalStorageDirectory();
							Log.e("debug", "externalStorageDirectory==" + externalStorageDirectory);
						}
					}

					final String msg = "path==" + path
							+ " ,removable==" + removable
							+ ",state==" + state;
					Log.e("debug", msg);

					return  path;
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public File createFile(String baseDir, String fileName) {

		File f = new File(baseDir+ "/" + fileName );
		Log.v("debug",baseDir);
		Log.v("debug",baseDir+ "/" + fileName);
		Log.v("debug",f.getParentFile().toString());
		if(!f.getParentFile().exists()){
			Log.v("debug","dir不存在");
			f.getParentFile().mkdirs();
		}
		if(!f.exists()){
			try {
				Log.v("debug","文件不存在");
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return f;
	}


	public void dataRecover(String fileName){

		showAboutDialog("完成",dataRecoverFromXml("normal",fileName));

	}
	
	public void dataMessImport(){
		showAboutDialog("完成",dataRecoverFromXml("mass",""));
	}
	
    private void showAboutDialog(String title,String msg){
		AlertDialog.Builder builder= new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("确认", null);
		builder.create().show();
	} 
	
	
 	public void setUpViews(){
		PasswordDB = new PasswordDB(this);
		mCursor = PasswordDB.getOne(RECORD_ID);

	}
 	
 	
 	private String writeToString(Cursor mCursor,Boolean encode){
 	    XmlSerializer serializer = Xml.newSerializer();
 	    StringWriter writer = new StringWriter();
 	    try {
 	        serializer.setOutput(writer);
 	        serializer.startDocument("UTF-8", true);
 	        serializer.startTag("", "resources");
 	        serializer.attribute("", "count", String.valueOf(mCursor.getCount()));
 	        
 	       if(mCursor.moveToFirst()) {
		        while(!mCursor.isAfterLast()) {
		        	
					serializer.startTag("", "record");
					serializer.attribute("", "id", mCursor.getString(0));
					serializer.attribute("", "site", mCursor.getString(1));
					serializer.attribute("", "user", mCursor.getString(2));
					//加密场合原样输出，未加密场合解密处理
					serializer.attribute("", "password", encode?mCursor.getString(3):DES.decryptDES(mCursor.getString(3)));
					serializer.attribute("", "mark", mCursor.getString(4));
					serializer.endTag("", "record");

		            mCursor.moveToNext();
		         }
		      }
 	        serializer.endTag("", "resources");
 	        serializer.endDocument();
 	        return writer.toString();
 	    } catch (Exception e) {
 	        throw new RuntimeException(e);
 	    } finally {
 	    	mCursor.close();
 	    }
 	}
 	
 	
 	private String dataRecoverFromXml(String recoverType, String fileName)
	{
		PasswordDB = new PasswordDB(this);
		String returnString = "";
		try {

			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			
			if (sdCardExist) {
				String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//" + FILE_DIR_NAME;
				
				String fn = "";
				if(recoverType.equals("normal")){
					fn = fileName;
				} else if(recoverType.equals("mass")){
					fn = fileNameMass;
				}
				File f = new File(baseDir + "//"+ fn );
				
				if(f.exists()){
					if(recoverType.equals("normal")){
						PasswordDB.deleteAllPassword();
					}
					
					 int counter = 0;   
		               StringBuilder sb = new StringBuilder("");   
		               XmlPullParser xrp = Xml.newPullParser();  
		               FileInputStream fin = new FileInputStream(f);
		               xrp.setInput(fin, "UTF-8");  
		                while (xrp.getEventType() != XmlPullParser.END_DOCUMENT) {    
		                         if (xrp.getEventType() == XmlPullParser.START_TAG) {
		                              String tagName = xrp.getName();   
		                              if(tagName.equals("record")){   
		                                  counter++;   
		                                  sb.append("第"+counter+"条信息："+"\n");
		                                  //sb.append(xrp.getAttributeValue(0)+"\n"); 
		                                  String site = xrp.getAttributeValue(1);
		                                  String user = xrp.getAttributeValue(2);
		                                  String password = xrp.getAttributeValue(3);
		                                  String remark = xrp.getAttributeValue(4);
		                                  if(recoverType.equals("mass")){
		                                	  password = DES.encryptDES(password);
		              					   }
		                                  if(!site.isEmpty() && !user.isEmpty() && !password.isEmpty() ){
		                                	  if(remark.equals(null)){
		                                		  remark="";
		                                	  }
		                                	  PasswordDB.insert(site, user, password, remark);
		                                  }
		                              }   
		                         } else if (xrp.getEventType() == XmlPullParser.END_TAG) {
		                         } else if (xrp.getEventType() == XmlPullParser.TEXT) { 
		                         }    
		                         xrp.next();  
		                    }   
		                
		                if(recoverType.equals("normal")){
		                	returnString = counter +"条数据已恢复";
						}else if(recoverType.equals("mass")){
							returnString = counter +"条数据已导入";
						}
		                
		                return returnString;
				}else{
					if(recoverType.equals("normal")){
						//showAboutDialog("错误","未检测到备份程序");
						returnString = "未检测到备份文件"+fileName;
					}else if(recoverType.equals("mass")){
						//showAboutDialog("错误","未检测到批量导入文件");
						returnString = "未检测到批量导入文件"+fileNameMass;
					}
					return returnString;
				}
			}else{
				//showAboutDialog("错误","程序未检测到SD卡");
				returnString = "程序未检测到SD卡";
				return returnString;
			}
			
		} catch (Exception e) {
			return "真的出错了";
		} finally {
			PasswordDB.close();
 	    }
		
	}
 	
 	
 	@SuppressLint("DefaultLocale")
	private boolean checkIsXMLFile(String fName) {
 		 boolean isXMLFile = false;
 		 String fileEnd = fName.substring(fName.lastIndexOf(".") + 1,  fName.length()).toLowerCase();
 		 if (fileEnd.equals("xml")) {
 			isXMLFile = true;
 		 } else {
 			isXMLFile = false;
 		 }
 		 return isXMLFile;
 	}

	//检测是否有写的权限用
	public static void verifyStoragePermissions(Activity activity) {

		try {
			//检测是否有写的权限
			int permission = ActivityCompat.checkSelfPermission(activity,
					"android.permission.WRITE_EXTERNAL_STORAGE");
			if (permission != PackageManager.PERMISSION_GRANTED) {
				// 没有写的权限，去申请写的权限，会弹出对话框
				ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refresh() {
		onCreate(null);
	}

}
