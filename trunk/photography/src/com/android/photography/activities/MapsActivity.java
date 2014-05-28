package com.android.photography.activities;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.android.photography.R;
import com.android.photography.R.id;
import com.android.photography.R.layout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapsActivity extends Activity {
	
	protected GoogleMap map;
	protected Marker marker;
	protected LatLng latlng;
	protected Location location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps_layout);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		//latlng = new LatLng(gpstracker.getLatitude(), gpstracker.getLongitude());
		//CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latlng, 5);
		//map.animateCamera(yourLocation);
	}
	
	//método que adiciona um marcador
}
