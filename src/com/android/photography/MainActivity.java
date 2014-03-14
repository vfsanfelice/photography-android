package com.android.photography;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		openCamera();
		openGallery();
		openMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void openCamera(){
		button = (Button) findViewById(R.id.camerabutton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				startActivity(intent);
				setContentView(R.layout.camera_layout);
			}
		});
	}
	
	public void openGallery(){
		button = (Button) findViewById(R.id.gallerybutton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(R.layout.gallery_layout);
			}
		});
	}
	
	public void openMap(){
		button = (Button) findViewById(R.id.mapsbutton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(R.layout.maps_layout);
			}
		});
	}

}
