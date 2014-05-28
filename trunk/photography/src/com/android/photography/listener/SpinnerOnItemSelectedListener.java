package com.android.photography.listener;

import java.util.Date;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photography.database.SQLiteHelper;
import com.android.photography.model.GalleryInfo;
import com.android.photography.webservice.Venue;
import com.android.photography.webservice.VenuesList;

public class SpinnerOnItemSelectedListener implements OnItemSelectedListener {
	
	VenuesList vl;
	TextView t1, t2;
	static Venue currentVenue;
	static String latitudeGPS, longitudeGPS;
	
	public SpinnerOnItemSelectedListener(VenuesList vl, TextView t1, TextView t2, String latitudeGPS, String longitudeGPS){
		this.vl = vl;
		this.t1 = t1;
		this.t2 = t2;
		this.latitudeGPS = latitudeGPS;
		this.longitudeGPS = longitudeGPS;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(parent.getContext(),
					  "OnItemSelectedListener: "+ parent.getItemAtPosition(position).toString(),
					  Toast.LENGTH_SHORT).show();
		
		for (Venue venue : vl.getVenues()) {
			if(venue.getName().equalsIgnoreCase(parent.getItemAtPosition(position).toString())){
				t1.setText(venue.location.lat);
				t2.setText(venue.location.lng);
				currentVenue = venue;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
	
	public static void savePicture(View v) {
		final Context context = v.getContext();
		//Inserir no banco de dados o registro completo
		SQLiteHelper sqlhelper = new SQLiteHelper(context);
		GalleryInfo gi = new GalleryInfo();
		gi.setVenueName(currentVenue.name);
		gi.setLatGPS(latitudeGPS);
		gi.setLngGPS(longitudeGPS);
		gi.setLatVenue(currentVenue.getLocation().getLat());
		gi.setLngVenue(currentVenue.getLocation().getLng());
		Date date = new Date(); 
		gi.setDate(date);
		sqlhelper.add(gi);
				
		sqlhelper.getAllGalleryInfo();
		
				
		//CRIAR UMA PASTA NOVA E SALVAR A FOTO NELA
		//NOMEDOLUGAR_DATA.jpg
	}

}
