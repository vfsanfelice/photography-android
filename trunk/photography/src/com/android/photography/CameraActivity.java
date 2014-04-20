package com.android.photography;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

public class CameraActivity extends Activity {
	
	private File file;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivityForResult(intent, 0);
		
//		GPSTracker gps = new GPSTracker(this);
//		
//        if(gps.canGetLocation()){
//            double latitude = gps.getLatitude();
//            double longitude = gps.getLongitude();
//            Toast.makeText(getApplicationContext(), "Sua localização atual é: \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
//        }else{
//            gps.showSettingsAlert();
//        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
