package com.android.photography;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class GalleryListActivity extends Activity {
	ListView list;
	String[] web = { "Galeria1", "Galeria2", "Galeria3", "Galeria4", "Galeria5", "Galeria6", "Galeria7" };
	Integer[] imageId = { R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallerylist_layout);
		CustomList adapter = new CustomList(GalleryListActivity.this, web, imageId);
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Toast.makeText(GalleryListActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(GalleryListActivity.this, GalleryActivity.class);
				intent.putExtra("galleryName", web[+position]);
				startActivity(intent);
			}
		});
	}
}


