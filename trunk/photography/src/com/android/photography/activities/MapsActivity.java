package com.android.photography.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.photography.R;
import com.android.photography.database.SQLiteHelper;
import com.android.photography.model.GalleryInfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Activity {

	protected GoogleMap map;
	protected Marker marker;
	protected LatLng latlng;
	protected Location location;
	public GalleryInfo gi;
	static final String IMAGE_DIRECTORY_NAME = "Photography";
	private File file;
	String[] arrayOfGallery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.maps_layout);
		configureMap();
	}

	@Override
	protected void onResume() {
		super.onResume();
		configureMap();
	}

	/**
	 * Verify if the map exists and add markers based on database
	 */
	public void configureMap() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			if (map != null) {
				map.clear();
				map.setMyLocationEnabled(true);

				checkDatabaseAndFolders();

			} else {
				Toast.makeText(getApplicationContext(), "Não foi possível criar o mapa.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Method to add a marker on the map based on location
	 * 
	 * @param ll
	 * @param venueName
	 */
	public void addMarker(LatLng ll, String venueName) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(ll);
		markerOptions.title(venueName);
		map.addMarker(markerOptions);

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(MapsActivity.this, GalleryActivity.class);
				intent.putExtra("markerGallery", marker.getTitle());
				startActivity(intent);
			}
		});
	}

	/**
	 * Method to verify database and SD card to keep consistency between them
	 */
	public void checkDatabaseAndFolders() {
		SQLiteHelper db = new SQLiteHelper(this);
		List<GalleryInfo> listOfGalleryInfo = new ArrayList<GalleryInfo>(db.getAllGalleryInfo());

		for (int i = 0; i < listOfGalleryInfo.size(); i++) {
			String root_sd = Environment.getExternalStorageDirectory() + File.separator + IMAGE_DIRECTORY_NAME;
			file = new File(root_sd);
			File lista[] = file.listFiles();

			for (int j = 0; j < lista.length; j++) {
				String galleryName = lista[j].getName();
				if (galleryName.indexOf(".jpg") < 0) {
					if (lista[j].getName().equals((listOfGalleryInfo.get(i).getVenueName()).replace(" ", ""))) {
						latlng = new LatLng(Double.parseDouble(listOfGalleryInfo.get(i).getLatVenue()), Double.parseDouble(listOfGalleryInfo.get(i).getLngVenue()));
						addMarker(latlng, listOfGalleryInfo.get(i).getVenueName());
					} else {
						db.delete(listOfGalleryInfo.get(i));
					}
				}
			}
		}
	}
}
