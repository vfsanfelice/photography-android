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
import android.widget.Toast;

import com.android.photography.ImageAdapter;
import com.android.photography.R;

public class GalleryActivity extends Activity {

	AsyncTaskLoadFiles myAsyncTaskLoadFiles;
	static final String IMAGE_DIRECTORY_NAME = "Photography";

	public class AsyncTaskLoadFiles extends AsyncTask<Void, String, Void> {
		File targetDirectory;

		// ImageAdapter used on all methods less in onCreate
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
			if (galleryName != null) {
				setTitle(galleryName);
				String galleryPath = (galleryName).replace(" ", "");
				targetDirectory = new File(root_sd + File.separator + galleryPath);
			}

			String markerGallery = intent.getStringExtra("markerGallery");
			if (markerGallery != null) {
				setTitle(markerGallery);
				String galleryPath = (markerGallery).replace(" ", "");
				targetDirectory = new File(root_sd + File.separator + galleryPath);
			}
			imageAdapter.clear();

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
			imageAdapter.add(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			imageAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

	}

	ImageAdapter myImageAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_layout);

		Intent intent = getIntent();
		String galleryName = intent.getStringExtra("galleryName");
		setTitle(galleryName);

		final GridView gridview = (GridView) findViewById(R.id.actualGridItens);
		myImageAdapter = new ImageAdapter(this);
		gridview.setAdapter(myImageAdapter);
		myAsyncTaskLoadFiles = new AsyncTaskLoadFiles(myImageAdapter);
		myAsyncTaskLoadFiles.execute();

		gridview.setOnItemClickListener(myOnItemClickListener);

		// Button buttonReload = (Button) findViewById(R.id.reloadButton);
		// buttonReload.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		//
		// // Cancel the previous running task, if exist.
		// myAsyncTaskLoadFiles.cancel(true);
		//
		// // new another ImageAdapter, to prevent the adapter have
		// // mixed files
		// myImageAdapter = new ImageAdapter(ActualGalleryActivity.this);
		// gridview.setAdapter(myImageAdapter);
		// myAsyncTaskLoadFiles = new AsyncTaskLoadFiles(myImageAdapter);
		// myAsyncTaskLoadFiles.execute();
		// }
		// });

	}

	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String prompt = "remove " + (String) parent.getItemAtPosition(position);
			Toast.makeText(getApplicationContext(), prompt, Toast.LENGTH_SHORT).show();
			myImageAdapter.remove(position);
			myImageAdapter.notifyDataSetChanged();

		}
	};

}