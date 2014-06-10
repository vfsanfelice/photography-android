package com.android.photography.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GalleryInfo {

	private int id;
	private String venueName;
	private String latGPS;
	private String lngGPS;
	private String latVenue;
	private String lngVenue;
	private Date date;

	public GalleryInfo() {
	};

	public GalleryInfo(String venueName, String latGPS, String lngGPS, String latVenue, String lngVenue, Date date) {
		super();
		this.venueName = venueName;
		this.latGPS = latGPS;
		this.lngGPS = lngGPS;
		this.latVenue = latVenue;
		this.lngVenue = lngVenue;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public String getLatGPS() {
		return latGPS;
	}

	public void setLatGPS(String latGPS) {
		this.latGPS = latGPS;
	}

	public String getLngGPS() {
		return lngGPS;
	}

	public void setLngGPS(String lngGPS) {
		this.lngGPS = lngGPS;
	}

	public String getLatVenue() {
		return latVenue;
	}

	public void setLatVenue(String latVenue) {
		this.latVenue = latVenue;
	}

	public String getLngVenue() {
		return lngVenue;
	}

	public void setLngVenue(String lngVenue) {
		this.lngVenue = lngVenue;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
		String stringdateformatter = dateFormat.format(date);
		return "{id:" + id + ", venueName: " + venueName + ", latGPS:" + latGPS + ", lngGPS:" + lngGPS + ", latVenue: " + latVenue + ", lngVenue: " + lngVenue + ", date:" + stringdateformatter;
	}
}
