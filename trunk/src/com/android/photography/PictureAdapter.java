package com.android.photography;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PictureAdapter extends BaseAdapter {
	
	private Context context;
	private final int[] pictures;
	
	public PictureAdapter(Context c, int[] pics){
		this.context = c;
		this.pictures = pics;
	}
	
	@Override
	public int getCount() {
		return pictures.length;
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ImageView img = new ImageView(context);
		img.setImageResource(pictures[position]);
		img.setAdjustViewBounds(true);
		return img;
	}

}
