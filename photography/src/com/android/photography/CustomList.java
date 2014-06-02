package com.android.photography;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {
	private final Activity activityContext;
	private final String[] arrayOfGallery;
	//private final Integer[] imageId;

	public CustomList(Activity activityContext, String[] arrayOfGallery) {
		super(activityContext, R.layout.list_single, arrayOfGallery);
		this.activityContext = activityContext;
		this.arrayOfGallery = arrayOfGallery;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = activityContext.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		//ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		txtTitle.setText(arrayOfGallery[position]);
		//imageView.setImageResource(imageId[position]);
		return rowView;
	}
}