package com.android.photography.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.photography.R;
import com.android.photography.listener.GPSLocationListener;

public class MainActivity extends Activity {

	private Button button;
	private double latitude;
	private double longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		openCamera();
		openGallery();
		mapsButton();

		GPSLocationListener gps = new GPSLocationListener(this);

		if (gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			//Toast.makeText(getApplicationContext(), "Sua localização atual é: \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
		} else {
			gps.showSettingsAlert();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	/**
	 * Method that starts the CameraActivity when button clicked on Main Screen
	 */
	public void openCamera() {
		final Context context = this;
		button = (Button) findViewById(R.id.camerabutton);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CameraActivity.class);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * Method that starts the GalleryListActivity when button clicked on Main Screen
	 */
	public void openGallery() {
		final Context context = this;
		button = (Button) findViewById(R.id.gallerybutton);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, GalleryListActivity.class);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * Method that starts the MapsActivity when button clicked on Main Screen
	 */
	public void mapsButton() {
		final Context context = this;
		button = (Button) findViewById(R.id.mapsbutton);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MapsActivity.class);
				startActivity(intent);
			}
		});
	}
}
