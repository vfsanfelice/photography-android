package com.android.photography.activities;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.photography.ImageAdapter;
import com.android.photography.R;

public class GalleryActivity extends Activity {

	AsyncTaskLoadFiles myAsyncTaskLoadFiles;
	static final String IMAGE_DIRECTORY_NAME = "Photography";

	public class AsyncTaskLoadFiles extends AsyncTask<Void, String, Void> {
		File targetDirectory;

		// ImageAdapter used on internal Class AsyncTaskLoadFiles
		ImageAdapter imageAdapter;

		public AsyncTaskLoadFiles(ImageAdapter adapter) {
			imageAdapter = adapter;
		}

		@Override
		protected void onPreExecute() {
			// Get all the Path on SD until /Photography
			String root_sd = Environment.getExternalStorageDirectory() + File.separator + IMAGE_DIRECTORY_NAME;
			Intent intent = getIntent();
			String galleryName = intent.getStringExtra("galleryName");
			// Check if user access gallery from GalleryListActivity
			if (galleryName != null) {
				setTitle(galleryName);
				String galleryPath = (galleryName).replace(" ", "");
				targetDirectory = new File(root_sd + File.separator + galleryPath);
			}

			String markerGallery = intent.getStringExtra("markerGallery");
			// Check if user access gallery from MapsActivity
			if (markerGallery != null) {
				setTitle(markerGallery);
				String galleryPath = (markerGallery).replace(" ", "");
				targetDirectory = new File(root_sd + File.separator + galleryPath);
			}
			imageAdapter.clearListOfItems();

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Returns all the images in each directory
			File[] files = targetDirectory.listFiles();
			for (File file : files) {
				publishProgress(file.getAbsolutePath());
				if (isCancelled())
					break;
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			imageAdapter.addItem(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			imageAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	ImageAdapter GalleryImageAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_layout);

		Intent intent = getIntent();
		String galleryName = intent.getStringExtra("galleryName");
		setTitle(galleryName);

		final GridView gridview = (GridView) findViewById(R.id.actualGridItens);
		GalleryImageAdapter = new ImageAdapter(this);
		gridview.setAdapter(GalleryImageAdapter);
		myAsyncTaskLoadFiles = new AsyncTaskLoadFiles(GalleryImageAdapter);
		myAsyncTaskLoadFiles.execute();

		gridview.setOnItemClickListener(myOnItemClickListener);

	}

	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String file = (String) parent.getItemAtPosition(position);
			Intent intent = new Intent(GalleryActivity.this, PhotoInfoActivity.class);
			intent.putExtra("photoInfo", file);
			startActivity(intent);
		}
	};
}