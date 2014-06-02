package com.android.photography.activities;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.android.photography.PictureAdapter;
import com.android.photography.R;

public class GalleryActivity extends Activity{
	
	ArrayList<String> f = new ArrayList<String>();
	File[] listFile;
	private int[] imagens = {R.drawable.ic_launcher};
	final Context context = this;
	PictureAdapter imgAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_layout);
		Intent intent = getIntent();
		String galleryName = intent.getStringExtra("galleryName");
		setTitle(galleryName);
		
		GridView gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new PictureAdapter(this, imagens));
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int posicao, long id) {
				Toast.makeText(GalleryActivity.this, "Imagem selecionada é: " + posicao, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
