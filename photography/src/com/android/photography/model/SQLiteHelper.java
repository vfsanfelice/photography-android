package com.android.photography.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper{
	//Database version
	private static final int DATABASE_VERSION = 3;
	//Database name
	private static final String DATABASE_NAME = "PhotographyDB";
	
	public SQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//SQL to create PictureInfo table
		String CREATE_PICTUREINFO_TABLE = "CREATE TABLE pictureinfo ( "+
										  "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
										  "latitude REAL, " +
										  "longitude REAL, " +
										  "date TEXT)";
		
		//Create PictureInfo table
		db.execSQL(CREATE_PICTUREINFO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Drop older PictureInfo table if existed
		db.execSQL("DROP TABLE IF EXISTS pictureinfo");
		//Recreate new table
		this.onCreate(db);
	}
	
	private static final String TABLE_PICTUREINFO = "pictureinfo";
	private static final String FIELD_ID = "id"; 
	private static final String FIELD_LAT = "latitude";
	private static final String FIELD_LNG = "longitude";
	private static final String FIELD_DATE = "date";
	private static final String[] COLUMNS = {FIELD_ID, FIELD_LAT, FIELD_LNG};
	
	public void add(PictureInfo pi){
		Log.d("add", "add register on database");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_LAT, pi.getLatitude());
		cv.put(FIELD_LNG, pi.getLongitude());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		cv.put(FIELD_DATE, dateFormat.format(pi.getDate()));
		db.insert(TABLE_PICTUREINFO, null, cv);
		db.close();
	}
	
	public void delete(PictureInfo pi){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PICTUREINFO, FIELD_ID + " = ?", new String[] {String.valueOf(pi.getId()) });
		db.close();
	}
	
	public List<PictureInfo> getAllPictureInfo(){
		List<PictureInfo> list = new LinkedList<PictureInfo>();
		
		String query = "SELECT * FROM " + TABLE_PICTUREINFO;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		PictureInfo pi = null;
		try {
			if (cursor.moveToFirst()){
				do {
					pi = new PictureInfo();
					pi.setId(Integer.parseInt(cursor.getString(0)));
					pi.setLatitude(Double.parseDouble(cursor.getString(1)));
					pi.setLongitude(Double.parseDouble(cursor.getString(2)));
					pi.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString((3))));
					list.add(pi);
				} while (cursor.moveToNext());
			}
		} catch (ParseException e) {
			Log.e("error on parse", "error on parse");
		}
		
		for (PictureInfo pictureInfo : list) {
			Log.d("getAllPictureInfo", pictureInfo.toString());
		}
		
		return list;
		
	}
			
}
