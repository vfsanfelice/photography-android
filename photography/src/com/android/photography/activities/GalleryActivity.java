package com.android.photography.activities;

import com.android.photography.PictureAdapter;
import com.android.photography.R;
import com.android.photography.R.drawable;
import com.android.photography.R.id;
import com.android.photography.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryActivity extends Activity{
	
	private int[] imagens = {R.drawable.ic_launcher, R.drawable.ic_launcher};
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_layout);
		
		GridView gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new PictureAdapter(this, imagens));
		
		Intent intent = getIntent();
		String galleryName = intent.getStringExtra("galleryName");
		
		((TextView)findViewById(R.id.textViewGallery)).setText(galleryName);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int posicao, long id) {
				Toast.makeText(GalleryActivity.this, "Imagem selecionada é: " + posicao, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
