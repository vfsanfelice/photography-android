package com.android.photography;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


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
		
		GPSTracker gps = new GPSTracker(this);
		
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Sua localização atual é: \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
        }else{
            gps.showSettingsAlert();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void openCamera(){
		final Context context = this;
		button = (Button) findViewById(R.id.camerabutton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, CameraActivity.class);
				startActivity(intent);
			}
		});
	}
	
	public void openGallery(){
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
