package com.android.photography.webservice;

public class Location {

	public String lat;
	
	public String lng;

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}
	
	/*
	{ "address" : "Itapema",
        "cc" : "BR",
        "city" : "Itapema",
        "country" : "Brasil",
        "distance" : 1176,
        "lat" : -27.10729603036265,
        "lng" : -48.60859473486084,
        "postalCode" : "88220-000",
        "state" : "SC"
      },*/
}
