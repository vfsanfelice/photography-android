package com.android.photography.activities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.android.photography.R;
import com.android.photography.listener.GPSTracker;

public class CameraActivity extends Activity {

	private double latitude;
	private double longitude;
	private Uri fileUri; // file url to store image
	static final String IMAGE_DIRECTORY_NAME = "Photography";
	private static String photoLabel; // label takes the photo path
	private static String photoLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		openCamera();
	}

	// capture image and callback calls preview activity
	public void openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// cria a pasta e o arquivo de imagem temporario
		fileUri = Uri.fromFile(getOutputMediaFile());

		// salva a foto no local descrito
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		if (isDeviceSupportCamera()) {
			// start the image capture Intent
			startActivityForResult(intent, 100);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// Successfully captured the image! Display it in image view using previewCapturedImage();
			GPSTracker gps = new GPSTracker(this);
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();

			Intent intent = new Intent(this, PhotoActivity.class);
			intent.putExtra("photoLabel", this.photoLabel);
			intent.putExtra("photoPath", fileUri.getPath());
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);
			startActivity(intent);

		} else if (resultCode == RESULT_CANCELED) {
			// User cancelled Image capture
			Toast.makeText(getApplicationContext(), "Fotografia cancelada!", Toast.LENGTH_SHORT).show();
		} else {
			// Failed to capture image
			Toast.makeText(getApplicationContext(), "Falha ao tentar tirar fotografia!", Toast.LENGTH_SHORT).show();
		}
	}

	// Names and creates the file that will receive the photo
	private File getOutputMediaFile() {
		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}
		
		// Create first name for file
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File photo = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		photoLabel = photo.getName();

		return photo;
	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// Device has a camera
			return true;
		} else {
			// Device has no camera
			return false;
		}
	}
}
