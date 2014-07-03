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
import com.android.photography.listener.GPSLocationListener;

public class CameraActivity extends Activity {

	private double latitude;
	private double longitude;
	private Uri fileUri;
	static final String IMAGE_DIRECTORY_NAME = "Photography";
	private static String photoLabel;

	// private static String photoLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		openCamera();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	// capture image and callback calls preview activity
	/**
	 * 
	 */
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
			// Successfully captured the image! Display it in ImageView using
			// PhotoActivity.previewCapturedImage();
			GPSLocationListener gps = new GPSLocationListener(this);
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();

			Intent intent = new Intent(this, PhotoActivity.class);
			intent.putExtra("photoLabel", photoLabel);
			intent.putExtra("photoPath", fileUri.getPath());
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);
			startActivity(intent);
			finish();

		} else if (resultCode == RESULT_CANCELED) {
			// User cancelled Image capture
			Intent i = new Intent(CameraActivity.this, MainActivity.class);
			startActivity(i);
			finish();
			Toast.makeText(getApplicationContext(), "Fotografia cancelada!", Toast.LENGTH_SHORT).show();

		} else {
			// Failed to capture image
			Toast.makeText(getApplicationContext(), "Falha ao tentar tirar fotografia!", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Method that creates the folder and the file that will receive the image
	 * taken by the camera
	 * 
	 * @return File object containing the photo taken by user
	 */
	private File getOutputMediaFile() {
		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);

		// Create the directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Ops! Failed on create " + IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create default name for File with the picture (IMG_TIMESTAMP.JPG)
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File photo = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		photoLabel = photo.getName();

		return photo;
	}

	/**
	 * Method that verifies if device has camera
	 * 
	 * @return true if has camera or false if has no camera
	 */
	private boolean isDeviceSupportCamera() {
		// Check if the device has a camera
		if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}
}
