package com.yaiba.keep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PasswordDB extends SQLiteOpenHelper{
	
	private final static int DATABASE_VERSION = 2;
	private final static String DATABASE_NAME = "KEEP.db";
	private final static String TABLE_NAME = "password_table";
	private final static String RECORD_ID = "id";
	private final static String SITE_NAME = "site_name";
	private final static String USER_NAME = "user_name";
	private final static String PASSWORD_VALUE = "password_value";
	private final static String REMARK = "remark";
	//public final static String SOFT_DELETE_FLAG = "soft_delete_flag";
	//public final static String CREATE_TIME = "create_time";
	//public final static String UPDATE_TIME = "update_time";
	 
	public PasswordDB(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	//创建table
	@Override
	public void onCreate(SQLiteDatabase db) {
		//, "+ SOFT_DELETE_FLAG +" enum('open', 'deleted') NOT NULL DEFAULT 'open', "+ CREATE_TIME +" timestamp NOT NULL DEFAULT '2011-01-01 00:00:00', "+ UPDATE_TIME +" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
//		String sql = "CREATE TABLE " + TABLE_NAME + " (" + RECORD_ID
//		+ " INTEGER primary key autoincrement, " + SITE_NAME + " NVARCHAR(100), "+ USER_NAME +" NVARCHAR(100), "+ PASSWORD_VALUE +" NVARCHAR(100), "+ REMARK +" NVARCHAR(100));";
//		db.execSQL(sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
//		db.execSQL(sql);
//		onCreate(db);
	}
	 
	public Cursor getAll(String orderBy) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy);
		return cursor;
	}
	
	 public Cursor getOne(long rowId) {
		 SQLiteDatabase db = this.getReadableDatabase();
		 Cursor cursor = db.query(true, TABLE_NAME, new String[] {RECORD_ID, SITE_NAME,USER_NAME,PASSWORD_VALUE,REMARK}, RECORD_ID + " = " + rowId, null, null, null, null, null);
         if(cursor != null) {
        	 cursor.moveToFirst();
         }
         return cursor;
     }
	 
	 public Cursor getForSearch(String siteName) {
		 SQLiteDatabase db = this.getReadableDatabase();
		 Cursor cursor = db.query(true, TABLE_NAME, new String[] {RECORD_ID, SITE_NAME,USER_NAME,PASSWORD_VALUE,REMARK}, SITE_NAME + " LIKE '%" + siteName + "%'", null, null, null, null, null);
         return cursor;
     }
	
	public long insert(String sitename, String username, String passwordvalue, String remark){
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(SITE_NAME, sitename);
		cv.put(USER_NAME, username);
		cv.put(PASSWORD_VALUE, passwordvalue);
		if(remark.equals(null)){
			remark = "";
		}
		cv.put(REMARK, remark);
		//cv.put(SOFT_DELETE_FLAG, "open");
		//cv.put(CREATE_TIME, now());
		//cv.put(UPDATE_TIME, "open");
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}
	
	public void delete(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = RECORD_ID + " = ?";
		String[] whereValue ={ Integer.toString(id) };
		db.delete(TABLE_NAME, where, whereValue);
	}
	
	public void deleteAllPassword(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		
		//set ai=0
		String where = "name = ?";
		String[] whereValue = { TABLE_NAME };
		ContentValues cv = new ContentValues();
		cv.put("seq", 0);
		db.update("sqlite_sequence", cv, where, whereValue);
	}
	
	public void update(int id, String sitename,String username,String passwordvalue,String remark){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = RECORD_ID + " = ?";
		String[] whereValue = { Integer.toString(id) };
		ContentValues cv = new ContentValues();
		cv.put(SITE_NAME, sitename);
		cv.put(USER_NAME, username);
		cv.put(PASSWORD_VALUE, passwordvalue);
		cv.put(REMARK, remark);
		db.update(TABLE_NAME, cv, where, whereValue);
	}

}
