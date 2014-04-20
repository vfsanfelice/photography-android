package com.android.photography.model;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PictureInfo {
	
	private int id;
	private double latitude;
	private double longitude;
	private Date date;
	
	public PictureInfo(){};
	
	public PictureInfo(double latitude, double longitude, Date date){
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.date = date;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String stringdateformatter = dateFormat.format(date);
		return "{id:" + id + ", lat:" + latitude + ", lng:" + longitude + ", date:" + stringdateformatter;
	}
}
