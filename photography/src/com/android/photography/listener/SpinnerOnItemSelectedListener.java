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
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photography.R;
import com.android.photography.activities.GalleryListActivity;
import com.android.photography.database.SQLiteHelper;
import com.android.photography.model.GalleryInfo;
import com.android.photography.webservice.Venue;
import com.android.photography.webservice.VenuesList;
import com.google.android.gms.maps.model.LatLng;

public class SpinnerOnItemSelectedListener implements OnItemSelectedListener {

	VenuesList vl;
	TextView t1, t2, t3;
	static Venue currentVenue;
	static String latitudeGPS, longitudeGPS;
	static final String IMAGE_DIRECTORY_NAME = "Photography";
	static LatLng latlng;

	public SpinnerOnItemSelectedListener(VenuesList vl, TextView t1, TextView t2, TextView t3, String latitudeGPS, String longitudeGPS) {
		this.vl = vl;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.latitudeGPS = latitudeGPS;
		this.longitudeGPS = longitudeGPS;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(parent.getContext(), "OnItemSelectedListener: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

		for (Venue venue : vl.getVenues()) {
			if (venue.getName().equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
				t1.setText("Latitude do FS: " + venue.location.lat);
				t2.setText("Longitude do FS: " + venue.location.lng);
				t3.setText("Localização: " + venue.name);
				currentVenue = venue;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	public static void savePicture(View v, String photoLabel) throws IOException {
		final Context context = v.getContext();
		// Inserir no banco de dados o registro completo
		SQLiteHelper sqlhelper = new SQLiteHelper(context);
		GalleryInfo gi = new GalleryInfo();
		gi.setVenueName(currentVenue.name);
		gi.setLatGPS(latitudeGPS);
		gi.setLngGPS(longitudeGPS);
		gi.setLatVenue(Double.toString(currentVenue.getLocation().getLat()));
		gi.setLngVenue(Double.toString(currentVenue.getLocation().getLng()));
		Date date = new Date();
		gi.setDate(date);
		sqlhelper.add(gi);
		sqlhelper.getAllGalleryInfo();

		/* Criar a pasta com o nome escolhido no spinner e salvar a foto dentro
		/ dela com o nome correto
		/ Salvar a foto na pasta certa com currentVenue.name+Date 
		*/
		createFolderStructure(photoLabel, date);

		// Criar um marcador de acordo com a latlng escolhida no WebService
		latlng = new LatLng(currentVenue.getLocation().getLat(), currentVenue.getLocation().getLng());
		// MapsActivity.addMarker(latlng, currentVenue.name);

	}

	public static void createFolderStructure(String photoLabel, Date date) throws IOException {
		// Cria o nome do diretório, a partir da localização escolhida no
		// spinner, dentro da pasta Photography
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME + (File.separator + currentVenue.name).replace(" ", ""));

		// Verifica se já existe, caso não exista, cria a pasta
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Ops! Falha ao criar o diretório " + IMAGE_DIRECTORY_NAME + File.separator + (currentVenue.name).replace(" ", ""));
			}
		}

		// Início do processo de cópia da foto para pasta de destino correta e
		// deleção do arquivo antigo
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			// Caminho e nome da fotografia tirada no contexto atual
			File oldFile = new File("/storage/emulated/0/Photography/" + photoLabel);

			// Conversão da data do sistema para ser incluído no nome da nova
			// fotografia
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());
			String stringdateformatter = dateFormat.format(date);

			// Caminho e nome da fotografia copiada, sendo adicionada na pasta
			// correta de acordo com a escolha de localização pelo usuário
			File newFile = new File(mediaStorageDir + File.separator + (currentVenue.name).replace(" ", "") + "_" + stringdateformatter + "_1.jpg");

			Log.d("newFile", newFile.getName());
			// newFile.getName() retorna Localizazao_Data.jpg

			inputStream = new FileInputStream(oldFile);
			outputStream = new FileOutputStream(newFile);

			byte[] buffer = new byte[1024];

			int length;
			// Faz a cópia do arquivo byte por byte
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}

			inputStream.close();
			outputStream.close();

			// Deleta o arquivo da foto inicial
			oldFile.delete();
			Log.d("CopyFile", "Arquivo " + photoLabel + " foi copiado com sucesso!");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
