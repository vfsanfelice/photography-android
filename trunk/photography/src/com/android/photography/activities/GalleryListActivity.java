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
import android.widget.TextView;

import com.android.photography.CustomList;
import com.android.photography.R;

public class GalleryListActivity extends Activity {

	static final String IMAGE_DIRECTORY_NAME = "Photography";
	private File file;
	private List<String> listOfGallery;
	ListView list;
	String[] arrayOfGallery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallerylist_layout);

		listOfGallery = new ArrayList<String>();

		String root_sd = Environment.getExternalStorageDirectory() + File.separator + IMAGE_DIRECTORY_NAME;
		file = new File(root_sd);
		
		File lista[] = file.listFiles();
		
		for (int i = 0; i < lista.length; i++) {
			String galleryName = lista[i].getName();
			if (galleryName.indexOf(".jpg") < 0) {
				listOfGallery.add(galleryName);
			}
		}
		arrayOfGallery = listOfGallery.toArray(new String[listOfGallery.size()]);
		
		if (arrayOfGallery.length > 0) {
			CustomList adapter = new CustomList(GalleryListActivity.this, arrayOfGallery);
			list = (ListView) findViewById(R.id.list);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(GalleryListActivity.this, ActualGalleryActivity.class);
					intent.putExtra("galleryName", arrayOfGallery[+position]);
					startActivity(intent);
				}
			});
		} 
		else {
			TextView noGallery = (TextView) findViewById(R.id.noGallery);
			noGallery.setText("Não existem galerias disponíveis!");
		}
			
	}
}
