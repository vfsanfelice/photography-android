package com.android.photography.activities;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.photography.R;
import com.android.photography.database.SQLiteHelper;
import com.android.photography.model.GalleryInfo;

public class PhotoInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.photoinfo_layout);

		Intent intent = getIntent();
		String photoInfo = intent.getStringExtra("photoInfo");
		if (photoInfo != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(photoInfo);
			ImageView photoInfoPreview = (ImageView) findViewById(R.id.photoInfo);
			photoInfoPreview.setImageBitmap(bitmap);
			
			String[] parts = photoInfo.split("/");
			String last = parts[parts.length - 1];
			
			SQLiteHelper db = new SQLiteHelper(this);
			GalleryInfo gi;
			gi = db.getPhotoInfo(last);
			
			TextView location = (TextView) findViewById(R.id.locationLabel);
			location.setText("Localização: " + gi.getVenueName());
			
			TextView fileName = (TextView) findViewById(R.id.fileNameLabel);
			fileName.setText("Nome do Arquivo: " + gi.getFileName());
			
			TextView dateOfPicture = (TextView) findViewById(R.id.dateOfPictureLabel);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
			String stringdateformatter = dateFormat.format(gi.getDate());
			
			dateOfPicture.setText("Data da Foto: " + stringdateformatter);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}