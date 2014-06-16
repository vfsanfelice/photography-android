package com.android.photography.activities;

import com.android.photography.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

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
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}