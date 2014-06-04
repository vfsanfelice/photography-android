package com.android.photography.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.photography.R;
import com.android.photography.database.SQLiteHelper;
import com.android.photography.model.GalleryInfo;
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
	public GalleryInfo gi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.maps_layout);
		verifyMapAndCreate();
	}
	
	/**
	 * Verify if the map exists and add markers based on database
	 */
	public void verifyMapAndCreate()
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
            	
            	SQLiteHelper db = new SQLiteHelper(this);
            	List<GalleryInfo> listOfGalleryInfo = new ArrayList<GalleryInfo>(db.getAllGalleryInfo());
            	
            	for (int i = 0; i < listOfGalleryInfo.size(); i++){
            		latlng = new LatLng(Double.parseDouble(listOfGalleryInfo.get(i).getLatVenue()), Double.parseDouble(listOfGalleryInfo.get(i).getLngVenue()));
            		addMarker(latlng, listOfGalleryInfo.get(i).getVenueName());
            	}
                Toast.makeText(getApplicationContext(), "Bombou", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
        }
    }
	
	/**
	 * Method to add a marker on the map based on location
	 * 
	 * @param ll
	 * @param venueName
	 */
	public static void addMarker(LatLng ll, String venueName){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(ll);
		markerOptions.title(venueName);
		map.addMarker(markerOptions);
	}
}
