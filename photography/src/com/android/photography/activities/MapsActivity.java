package com.android.photography.activities;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.photography.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Activity {

	protected static GoogleMap map;
	protected Marker marker;
	protected LatLng latlng;
	protected Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.maps_layout);
		//map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//map.setMyLocationEnabled(true);
		setUpMap();
	}
	
	public void setUpMap()
    {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null)
        {
            // Try to obtain the map from the SupportMapFragment.
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (map != null)
            {	
            	map.setMyLocationEnabled(true);
                Toast.makeText(getApplicationContext(), "Bombou", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
        }
    }
	
	// método que adiciona um marcador
//	public static void addMarker(LatLng ll, String venueName){
//		MarkerOptions markerOptions = new MarkerOptions();
//		markerOptions.position(ll);
//		markerOptions.title(venueName);
//		map.addMarker(markerOptions);
//	}
}
