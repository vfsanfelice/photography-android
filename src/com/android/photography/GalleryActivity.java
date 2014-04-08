package com.android.photography;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class GalleryActivity extends Activity{
	
	//array de imagens
	private int[] imagens = {R.drawable.ic_launcher, R.drawable.ic_launcher};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_layout);
		GridView gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new PictureAdapter(this, imagens));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView parent, View v, int posicao, long id) {
				Toast.makeText(GalleryActivity.this, "Imagem selecionada é: " + posicao, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
