package com.android.photography;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button button;
	private double latitude;
	private double longitude;
	private Uri fileUri; // file url to store image
	private static final String IMAGE_DIRECTORY_NAME = "Photography"; // image
																		// directory
																		// name

	private static String photoLabel; // label takes the photo path
	private static String photoLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		// openCamera();
		openGallery();
		mapsButton();

		GPSTracker gps = new GPSTracker(this);

		if (gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			Toast.makeText(
					getApplicationContext(),
					"Sua localização atual é: \nLat: " + latitude + "\nLong: "
							+ longitude, Toast.LENGTH_LONG).show();
		} else {
			gps.showSettingsAlert();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// capture image and callback calls preview activity
	public void openCamera(View v) {
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// successfully captured the image
			// display it in image view
			// previewCapturedImage();

			Intent intent = new Intent(this, PhotoActivity.class);
			intent.putExtra("photoLabel", this.photoLabel);
			intent.putExtra("photoPath", fileUri.getPath());
			// intent.putExtra("photoLocation", this.photoLocation);

			startActivity(intent);

			// Toast.makeText(getApplicationContext(), "Success!!!",
			// Toast.LENGTH_SHORT).show();
		} else if (resultCode == RESULT_CANCELED) {
			// user cancelled Image capture
			Toast.makeText(getApplicationContext(), "CANCELARO O BAGULHO",
					Toast.LENGTH_SHORT).show();
		} else {
			// failed to capture image
			Toast.makeText(getApplicationContext(), "NUM DEU",
					Toast.LENGTH_SHORT).show();
		}
	}

	// nomeia e cria o arquivo que vai receber a foto
	private File getOutputMediaFile() {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());

		File photo = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		photoLabel = photo.getPath();
		// photoLocation = LocationManager.getLocation(mediaFile);

		return photo;
	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}
}
