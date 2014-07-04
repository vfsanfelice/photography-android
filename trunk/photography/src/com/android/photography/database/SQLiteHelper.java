package com.android.photography.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.photography.model.GalleryInfo;

public class SQLiteHelper extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 13;
	private static final String DATABASE_NAME = "PhotographyDB";
	
	public SQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL to create GalleryInfo Table
		String CREATE_GALLERYINFO_TABLE = "CREATE TABLE galleryinfo ( "+
										  "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
										  "venue_name TEXT, " +
										  "lat_gps TEXT, " +
										  "lng_gps TEXT, " + 
										  "lat_venue TEXT, " +
										  "lng_venue TEXT, " +
										  "file_name TEXT, " +
										  "date TEXT)";
		
		db.execSQL(CREATE_GALLERYINFO_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop table GalleryInfo if already exists
		db.execSQL("DROP TABLE IF EXISTS galleryinfo");
		
		// Change DATABASE_VERSION and remove comment from line below to reset sequence in database 
		//db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_GALLERYINFO + "'");
		
		// Recreate the table
		this.onCreate(db);
	}
	
	// Auxiliary variables
	private static final String TABLE_GALLERYINFO = "galleryinfo";
	private static final String FIELD_ID = "id"; 
	private static final String FIELD_VENUENAME = "venue_name";
	private static final String FIELD_LATGPS = "lat_gps";
	private static final String FIELD_LNGGPS = "lng_gps";
	private static final String FIELD_LATVENUE = "lat_venue";
	private static final String FIELD_LNGVENUE = "lng_venue";
	private static final String FIELD_FILENAME = "file_name";
	private static final String FIELD_DATE = "date";
	private static final String[] COLUMNS = {FIELD_ID, FIELD_VENUENAME, FIELD_LATGPS, FIELD_LNGGPS, FIELD_LATVENUE, FIELD_LNGVENUE, FIELD_FILENAME, FIELD_DATE };
	
	/**
	 * Method to insert one record in GalleryInfo Table
	 * @param gi
	 */
	public void add(GalleryInfo gi){
		Log.d("add", "add register on database");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_VENUENAME, gi.getVenueName());
		cv.put(FIELD_LATGPS, gi.getLatGPS());
		cv.put(FIELD_LNGGPS, gi.getLngGPS());
		cv.put(FIELD_LATVENUE, gi.getLatVenue());
		cv.put(FIELD_LNGVENUE, gi.getLngVenue());
		cv.put(FIELD_FILENAME, gi.getFileName());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
		cv.put(FIELD_DATE, dateFormat.format(gi.getDate()));
		db.insert(TABLE_GALLERYINFO, null, cv);
		db.close();
	}
	
	/**
	 * Method to remove one record in GalleryInfo Table based on ID
	 * @param gi
	 */
	public void delete(GalleryInfo gi){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GALLERYINFO, FIELD_ID + " = ?", new String[] {String.valueOf(gi.getId()) });
		db.close();
	}
	
	/**
	 * Method
	 * @return List<GalleryInfo> containing all records in GalleryInfo Table
	 */
	public List<GalleryInfo> getAllGalleryInfo(){
		List<GalleryInfo> list = new ArrayList<GalleryInfo>();
		
		String query = "SELECT * FROM " + TABLE_GALLERYINFO;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		GalleryInfo gi = null;
		try {
			if (cursor.moveToFirst()){
				do {
					gi = new GalleryInfo();
					gi.setId(Integer.parseInt(cursor.getString(0)));
					gi.setVenueName(cursor.getString(1));
					gi.setLatGPS(cursor.getString(2));
					gi.setLngGPS(cursor.getString(3));
					gi.setLatVenue(cursor.getString(4));
					gi.setLngVenue(cursor.getString(5));
					gi.setFileName(cursor.getString(6));
					gi.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString((7))));
					list.add(gi);
				} while (cursor.moveToNext());
			}
		} catch (ParseException e) {
			Log.e("Error on parse SQL", "Error on parse getAllGalleryInfo");
		}
		
		for (GalleryInfo galleryInfo : list) {
			Log.d("getAllGalleryInfo", galleryInfo.toString());
		}
		
		return list;
	}
	
	public GalleryInfo getPhotoInfo(String fileName){
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("getAllGalleryInfoDB", getAllGalleryInfo().toString());
		String query = "SELECT * FROM " + TABLE_GALLERYINFO + " WHERE FILE_NAME = ?";
		Cursor cursor = db.rawQuery(query, new String[] { fileName });
		
		GalleryInfo gi = null;
		try {
			if (cursor.moveToFirst() == true) {
				gi = new GalleryInfo();
				gi.setId(Integer.parseInt(cursor.getString(0)));
				gi.setVenueName(cursor.getString(1));
				gi.setLatGPS(cursor.getString(2));
				gi.setLngGPS(cursor.getString(3));
				gi.setLatVenue(cursor.getString(4));
				gi.setLngVenue(cursor.getString(5));
				gi.setFileName(cursor.getString(6));
				gi.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString((7))));
			}
		} catch (ParseException e) {
			Log.e("Error on parse SQL", "Error on parse getPhotoInfo");
		}
		return gi;
	}
}
