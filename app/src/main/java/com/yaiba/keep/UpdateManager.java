package com.yaiba.keep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
 
     public class UpdateManager {

     private String newVersion;
     private String updateFile;
     private int curVersionCode;
     private int newVersionCode;
     private String updateInfo;
     private UpdateCallback callback;
     private Context ctx;
    
     private int progress;  
     private Boolean hasNewVersion;
     private Boolean canceled;

     public static String UPDATE_DOWNURL = "http://yaiba.b2cn.tk/apk/";
     public static final String UPDATE_DOWNURL2 = "http://sae.yaiba.net/";
     public static final String UPDATE_CHECKFILENAME = "keep_update.txt";
     public static final String UPDATE_CHECKURL = UPDATE_DOWNURL + UPDATE_CHECKFILENAME;
     public static final String UPDATE_APKNAME = "keep_update.apk";
     public static final String UPDATE_SAVENAME = "keep_ver.apk";
     private static final int UPDATE_CHECKCOMPLETED = 1;
     private static final int UPDATE_DOWNLOADING = 2; 
     private static final int UPDATE_DOWNLOAD_ERROR = 3; 
     private static final int UPDATE_DOWNLOAD_COMPLETED = 4; 
     private static final int UPDATE_DOWNLOAD_CANCELED = 5;
     private String FILE_DIR_NAME = "keep";
     private String savefolder = "/mnt/sdcard/keep/";

         /////////////////////////////
         //本class暂时不用，请勿调用//
         /////////////////////////////


    public UpdateManager(Context context, UpdateCallback updateCallback) {
    	
    	boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		
		if (sdCardExist) {
			savefolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "//" + FILE_DIR_NAME + "//";
		}else{
			savefolder = Environment.getDownloadCacheDirectory().getAbsolutePath() + "//" + FILE_DIR_NAME + "//";
		}
		File f = new File(savefolder);
		if(!f.exists()){  
			f.mkdirs();
		} 
        ctx = context;
        callback = updateCallback;
        //savefolder = context.getFilesDir();
        canceled = false;
        getCurVersion();
    }
    
    public String getNewVersionName()
    {
        return newVersion;
    }
    
    public String getUpdateInfo()
    {
        return updateInfo;
    }

    private void getCurVersion() {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            curVersionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.e("update", e.getMessage());
            curVersionCode = 1;
        }

    }

    public void checkUpdate() {        
        hasNewVersion = false;
        new Thread(){
            @Override
            public void run() {
               // Log.i("@@@@@", ">>>>>>>>>>>>>>>>>>>>>>>>>>>getServerVerCode() ");
                try {

                    String verjson = NetHelper.httpStringGet(UPDATE_CHECKURL);
                   // Log.i("@@@@", verjson + "**************************************************");
                    JSONArray array = new JSONArray(verjson);

                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        try {
                            newVersionCode = Integer.parseInt(obj.getString("verCode"));
                            newVersion = obj.getString("verName");
                            updateFile = obj.getString("updateFile");
                            updateInfo = "";
                            //Log.i("newVerCode", newVersionCode + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                           // Log.i("newVerName", newVersion + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            if (newVersionCode > curVersionCode) {
                                hasNewVersion = true;
                            }
                        } catch (Exception e) {
                            newVersionCode = -1;
                            newVersion = "";
                            updateFile = "";
                            updateInfo = "";
                            
                        }
                    } 
                    
                } catch (Exception e) {
                	
                	try {
                	UPDATE_DOWNURL = UPDATE_DOWNURL2;
                	String verjson2 = NetHelper.httpStringGet(UPDATE_DOWNURL2 + UPDATE_CHECKFILENAME);
                    JSONArray array2 = new JSONArray(verjson2);
                    if (array2.length() > 0) {
                        JSONObject obj = array2.getJSONObject(0);
                        try {
                            newVersionCode = Integer.parseInt(obj.getString("verCode"));
                            newVersion = obj.getString("verName");
                            updateFile = obj.getString("updateFile");
                            updateInfo = "";
                            if (newVersionCode > curVersionCode) {
                                hasNewVersion = true;
                            }
                        } catch (Exception es) {
                            newVersionCode = -1;
                            newVersion = "";
                            updateFile = "";
                            updateInfo = "";
                        }
                    }
                	}catch (Exception ex) {
                		
                		Log.e("update", ex.getMessage());
                	}
                	
                
                    Log.e("update", e.getMessage());
                }
                updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
            };
        }.start();

    }

    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        
        intent.setDataAndType(
                Uri.fromFile(new File(savefolder, UPDATE_SAVENAME)),
                "application/vnd.android.package-archive");
        ctx.startActivity(intent);
    }


    public void downloadPackage() 
    {
        
        
        new Thread() {            
             @Override  
                public void run() {  
                    try {  
                        URL url = new URL(UPDATE_DOWNURL+updateFile);  
                      
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                        conn.connect();  
                        int length = conn.getContentLength();  
                        InputStream is = conn.getInputStream();  
                          
                       
                        File ApkFile = new File(savefolder,UPDATE_SAVENAME);
                        
                        
                        if(ApkFile.exists()) {
                            
                            ApkFile.delete();
                        }
                        
                        
                        FileOutputStream fos = new FileOutputStream(ApkFile);  
                         
                        int count = 0;  
                        byte buf[] = new byte[512];  
                          
                        do{  
                            
                            int numread = is.read(buf);  
                            count += numread;  
                            progress =(int)(((float)count / length) * 100);  
                           
                            updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING)); 
                            if(numread <= 0){      
                                
                                updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
                                break;  
                            }  
                            fos.write(buf,0,numread);  
                        }while(!canceled);  
                        if(canceled)
                        {
                            updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
                        }
                        fos.close();  
                        is.close();  
                    } catch (MalformedURLException e) {  
                        e.printStackTrace(); 
                        
                        updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                    } catch(IOException e){  
                        e.printStackTrace();  
                        
                        updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                    }  
                      
                } 
        }.start();
    }

    public void cancelDownload()
    {
        canceled = true;
    }
    
    Handler updateHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
            case UPDATE_CHECKCOMPLETED:
                
                callback.checkUpdateCompleted(hasNewVersion, newVersion);
                break;
            case UPDATE_DOWNLOADING:
                
                callback.downloadProgressChanged(progress);
                break;
            case UPDATE_DOWNLOAD_ERROR:
                
                callback.downloadCompleted(false, msg.obj.toString());
                break;
            case UPDATE_DOWNLOAD_COMPLETED:
                
                callback.downloadCompleted(true, "");
                break;
            case UPDATE_DOWNLOAD_CANCELED:
                
                callback.downloadCanceled();
            default:
                break;
            }
        }
    };

    public interface UpdateCallback {
        public void checkUpdateCompleted(Boolean hasUpdate,
                CharSequence updateInfo);

        public void downloadProgressChanged(int progress);
        public void downloadCanceled();
        public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
    }
    
    // keep_update.txt
//  [ 
//  {"verCode" : "1","verName" : "0.1.1"}
//  ]

}