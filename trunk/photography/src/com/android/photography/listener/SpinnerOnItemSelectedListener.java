package com.android.photography.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
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
	static final String IMAGE_DIRECTORY_NAME = "Photography";

	public SpinnerOnItemSelectedListener(VenuesList vl, TextView t1,
			TextView t2, String latitudeGPS, String longitudeGPS) {
		this.vl = vl;
		this.t1 = t1;
		this.t2 = t2;
		this.latitudeGPS = latitudeGPS;
		this.longitudeGPS = longitudeGPS;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(
				parent.getContext(),
				"OnItemSelectedListener: "
						+ parent.getItemAtPosition(position).toString(),
				Toast.LENGTH_SHORT).show();

		for (Venue venue : vl.getVenues()) {
			if (venue.getName().equalsIgnoreCase(
					parent.getItemAtPosition(position).toString())) {
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

	public static void savePicture(View v, String photoLabel)
			throws IOException {
		final Context context = v.getContext();
		// Inserir no banco de dados o registro completo
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
		
		// Criar a pasta com o nome escolhido no spinner e salvar a foto dentro dela com o nome correto		
		createFolderStructure(photoLabel);

		// Salvar a foto na pasta certa com currentVenue.name+Date
		// NOMEDOLUGAR_DATA.jpg
	}

	public static void createFolderStructure(String photoLabel) throws IOException {
		// Cria o nome do diretório, a partir da localização escolhida no
		// spinner, dentro da pasta Photography
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME
						+ (File.separator + currentVenue.name).replace(" ", ""));

		// Verifica se já existe, caso não exista, cria a pasta
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Ops! Falha ao criar o diretório "
						+ IMAGE_DIRECTORY_NAME
						+ (File.separator + currentVenue.name).replace(" ", ""));
			}
		}

		File rootFolder = new File(Environment.getExternalStorageDirectory(),
				IMAGE_DIRECTORY_NAME);
		File sd = Environment.getExternalStorageDirectory();
		if (sd.canWrite()) {
			String imagePath = rootFolder + File.separator;
			String destinationImagePath = mediaStorageDir + File.separator;
			File source = new File(imagePath, photoLabel);
			File destination = new File(destinationImagePath+File.separator);

			if (source.exists()) {
				FileChannel src = new FileInputStream(source).getChannel();
				FileChannel dst = new FileOutputStream(destination).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
			}
		}
	}

}
