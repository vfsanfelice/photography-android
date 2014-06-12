package com.android.photography;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] arrayOfGallery;

	public CustomList(Activity context, String[] arrayOfGallery) {
		super(context, R.layout.customlist_layout, arrayOfGallery);
		this.context = context;
		this.arrayOfGallery = arrayOfGallery;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.customlist_layout, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		txtTitle.setText(arrayOfGallery[position]);
		return rowView;
	}
}