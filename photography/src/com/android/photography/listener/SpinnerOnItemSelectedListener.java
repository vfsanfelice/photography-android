package com.android.photography.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.photography.database.SQLiteHelper;
import com.android.photography.model.GalleryInfo;
import com.android.photography.webservice.Venue;
import com.android.photography.webservice.VenuesList;
import com.google.android.gms.maps.model.LatLng;

public class SpinnerOnItemSelectedListener implements OnItemSelectedListener {

	VenuesList vl;
	TextView location, name, actualDate;
	static Venue currentVenue;
	static String latitudeGPS, longitudeGPS;
	static final String IMAGE_DIRECTORY_NAME = "Photography";
	static LatLng latlng;
	public static String fileName;
	public EditText editText;

	public SpinnerOnItemSelectedListener(VenuesList vl, TextView name, TextView actualDate, String latitudeGPS, String longitudeGPS, EditText editText) {
		this.vl = vl;
		this.name = name;
		this.actualDate = actualDate;
		this.latitudeGPS = latitudeGPS;
		this.longitudeGPS = longitudeGPS;
		this.editText = editText;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// Toast.makeText(parent.getContext(), "OnItemSelectedListener: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

		for (Venue venue : vl.getVenues()) {
			if (venue.getName().equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
				editText.setText("");
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
				String stringdateformatter = dateFormat.format(date);
				actualDate.setText("Data: " + stringdateformatter);

				currentVenue = venue;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}

	/**
	 * Realize all operations to create the physic structure in SD card and save the picture
	 * 
	 * @param v
	 * @param photoLabel
	 * @throws IOException
	 */
	public static void savePicture(View v, String photoLabel, String eventLabel) throws IOException {
		final Context context = v.getContext();
		Date date = new Date();
		if (eventLabel.equals("@123@")) {
			createGalleryStructure(photoLabel, date, "@123@");
			insertPictureOnDatabase(context, date, "@123@");
		} else {
			createGalleryStructure(photoLabel, date, eventLabel);
			insertPictureOnDatabase(context, date, eventLabel);
		}
	}

	/**
	 * Insert on database the information about the picture taken
	 * 
	 * @param context
	 * @param date
	 */
	private static void insertPictureOnDatabase(final Context context, Date date, String eventLabel) {
		SQLiteHelper sqlhelper = new SQLiteHelper(context);
		GalleryInfo gi = new GalleryInfo();
		if (eventLabel.equals("@123@")) {
			gi.setVenueName(currentVenue.name);
		} else {
			gi.setVenueName(eventLabel);
		}
		gi.setLatGPS(latitudeGPS);
		gi.setLngGPS(longitudeGPS);
		gi.setLatVenue(Double.toString(currentVenue.getLocation().getLat()));
		gi.setLngVenue(Double.toString(currentVenue.getLocation().getLng()));
		gi.setFileName(fileName);
		gi.setDate(date);
		sqlhelper.add(gi);

		sqlhelper.getAllGalleryInfo();
	}

	/**
	 * Create the folder with the name chosen by the user and save the picture
	 * in this folder
	 * 
	 * @param photoLabel
	 * @param date
	 * @throws IOException
	 */
	public static void createGalleryStructure(String photoLabel, Date date, String eventLabel) throws IOException {
		if (eventLabel.equals("@123@")) {
			// Create directory name inside Photography root folder using the location chosen by spinner
			File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME + (File.separator + currentVenue.name).replace(" ", ""));
			
			// Verify if exists. If not, create the folder.
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d(IMAGE_DIRECTORY_NAME, "Ops! Falha ao criar o diretório " + IMAGE_DIRECTORY_NAME + File.separator + (currentVenue.name).replace(" ", ""));
				}
			}

			moveFileToNewFolder(photoLabel, date, mediaStorageDir, "@123@");

		} else {
			// Create directory name inside Photography root folder using the location type by used on edittext
			File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME + (File.separator + eventLabel).replace(" ", ""));

			// Verifica se já existe, caso não exista, cria a pasta
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d(IMAGE_DIRECTORY_NAME, "Ops! Falha ao criar o diretório " + IMAGE_DIRECTORY_NAME + File.separator + (eventLabel).replace(" ", ""));
				}
			}

			moveFileToNewFolder(photoLabel, date, mediaStorageDir, eventLabel);
		}

	}

	/**
	 * Move the initial picture file to the destination folder based on location or event
	 * that user selects. Execute the copy and delete process to the original
	 * file.
	 * 
	 * @param photoLabel
	 * @param date
	 * @param mediaStorageDir
	 */
	private static void moveFileToNewFolder(String photoLabel, Date date, File mediaStorageDir, String eventLabel) {
		// Start the process of copy the picture to the right folder and delete the old one
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			// First path and name of the picture taken (old name and root folder)
			File oldFile = new File("/storage/emulated/0/Photography/" + photoLabel);

			// Convert data format
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());
			String stringdateformatter = dateFormat.format(date);

			if (eventLabel.equals("@123@")) {
				// Path and name of the picture copied, adding in the right folder by spinner value
				File newFile = new File(mediaStorageDir + File.separator + (currentVenue.name).replace(" ", "") + "_" + stringdateformatter + ".jpg");

				// Auxiliar Variable to help picture name increment
				int num = 0;

				while (newFile.exists()) {
					num++;
					newFile = new File(mediaStorageDir + File.separator + (currentVenue.name).replace(" ", "") + "_" + stringdateformatter + "_" + num + ".jpg");
				}

				Log.d("newFile", newFile.getName());
				fileName = newFile.getName();

				inputStream = new FileInputStream(oldFile);
				outputStream = new FileOutputStream(newFile);

				byte[] buffer = new byte[1024];

				int length;
				// Copy file byte per byte
				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}

				inputStream.close();
				outputStream.close();

				// Delete the initial picture file
				oldFile.delete();
				Log.d("CopyFile", "Arquivo " + photoLabel + " foi copiado com sucesso!");
			} else {
				// Path and name of the picture copied, adding in the right folder by edittext value
				File newFile = new File(mediaStorageDir + File.separator + (eventLabel).replace(" ", "") + "_" + stringdateformatter + ".jpg");

				// Auxiliar Variable to help picture name increment
				int num = 0;

				while (newFile.exists()) {
					num++;
					newFile = new File(mediaStorageDir + File.separator + (eventLabel).replace(" ", "") + "_" + stringdateformatter + "_" + num + ".jpg");
				}

				Log.d("newFile", newFile.getName());
				fileName = newFile.getName();

				inputStream = new FileInputStream(oldFile);
				outputStream = new FileOutputStream(newFile);

				byte[] buffer = new byte[1024];

				int length;
				// Copy file byte per byte
				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}

				inputStream.close();
				outputStream.close();

				// Delete the initial picture file
				oldFile.delete();
				Log.d("CopyFile", "Arquivo " + photoLabel + " foi copiado com sucesso!");

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
