package com.yaiba.keep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DataManagementActivity extends Activity {
	private PasswordDB PasswordDB;
	private Cursor mCursor;
	private int RECORD_ID = 0;
	private int selectBakupFileIndex = 0;
	private String[] bakFileArray ;
	private String FILE_DIR_NAME = "keep";
	//private String fileName = "keepres.xml";
	//private String fileNamePre = "keep_";
	private String fileNameSuff = ".xml";
	private String fileNameMass = "keepres_mass.xml";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		PasswordDB = new PasswordDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_management);
		
		Button bn_back = (Button)findViewById(R.id.back);
		bn_back.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {  
					   Intent mainIntent = new Intent(DataManagementActivity.this,MainActivity.class);
					   startActivity(mainIntent);
					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					   setResult(RESULT_OK, mainIntent);  
					   finish();  
			   }  
			  });
		
		
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
				   
				   String keepPath = Environment.getExternalStorageDirectory().toString()  + "//" +FILE_DIR_NAME; 
				   
				   File[] files = new File(keepPath).listFiles(); 
				   
				   for (int i = 0; i < files.length; i++) {
					   File file = files[i];
					   if (checkIsXMLFile(file.getPath())) {
						   bakupFileList.add(file.getName());
					   }
					  } 
				   
				   if(bakupFileList.size()<=0){
					   
					   builder.setMessage("没有找到可用来恢复的备份文件");
					   builder.setNegativeButton("取消", null);
					   
				   } else {
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
		
		try {
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		
			if (sdCardExist) {
				String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//" + FILE_DIR_NAME;
				Date dt = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");  
				//String fileName = fileNamePre + sdf.format(dt)+ fileNameSuff;
				String fileName = sdf.format(dt)+ fileNameSuff;
				
				File f = new File(baseDir + "//"+ fileName );
				if(!f.getParentFile().exists()){  
					f.getParentFile().mkdirs();
				}  
				if(!f.exists()){  
					f.createNewFile();
				}  
				FileWriter fileWriter = new FileWriter(f,false);
				BufferedWriter bf = new BufferedWriter(fileWriter);
				bf.write(writeToString(mCursor));
				bf.flush();
				showAboutDialog("完成","备份文件"+fileName+"已输出到SD卡。");
			}
		} catch (IOException e) {}
	
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
 	
 	
 	private String writeToString(Cursor mCursor){
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
					serializer.attribute("", "password", mCursor.getString(3));
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
 	


}
