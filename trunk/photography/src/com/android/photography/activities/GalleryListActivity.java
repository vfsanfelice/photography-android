package com.android.photography.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.photography.CustomList;
import com.android.photography.R;

public class GalleryListActivity extends Activity {

	static final String IMAGE_DIRECTORY_NAME = "Photography";
	private File file;
	private List<String> listOfGallery;
	ListView list;
	String[] web = { "Galeria1", "Galeria2", "Galeria3", "Galeria4", "Galeria5", "Galeria6", "Galeria7" };
	Integer[] imageId = { R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallerylist_layout);

		listOfGallery = new ArrayList<String>();

		String root_sd = Environment.getExternalStorageDirectory() + File.separator + IMAGE_DIRECTORY_NAME;
		file = new File(root_sd);

		File lista[] = file.listFiles();
		for (int i = 0; i < lista.length; i++) {
			listOfGallery.add(lista[i].getName());
		}
		
		CustomList adapter = new CustomList(GalleryListActivity.this, web, imageId);
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Toast.makeText(GalleryListActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(GalleryListActivity.this, GalleryActivity.class);
				intent.putExtra("galleryName", web[+position]);
				startActivity(intent);
			}
		});
	}
}
