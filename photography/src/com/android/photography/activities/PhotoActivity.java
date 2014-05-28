package com.android.photography.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.photography.R;
import com.android.photography.R.id;
import com.android.photography.R.layout;
import com.android.photography.database.SQLiteHelper;
import com.android.photography.listener.SpinnerOnItemSelectedListener;
import com.android.photography.model.GalleryInfo;
import com.android.photography.webservice.Venue;
import com.android.photography.webservice.VenuesList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PhotoActivity extends Activity {
	
	
	private Button button;
	private Spinner locationSpinner;
	static final String IMAGE_DIRECTORY_NAME = "Photography";
	static VenuesList venues;
	// photo attributes
	private static String photoLabel;
	private static String photoPath;
	static Double latitude;
	static Double longitude;
	static String location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_layout);
		
		button = (Button) findViewById(R.id.save);
		button.setEnabled(false);
		
		Intent iin = getIntent();
		Bundle b = iin.getExtras();

		if (b != null) {
			this.photoLabel = (String) b.get("photoLabel");
			this.photoPath = (String) b.get("photoPath");
			this.latitude = (Double) b.get("latitude");
			this.longitude = (Double) b.get("longitude");
		}
		previewCapturedImage(this.photoPath);
	}

	private void previewCapturedImage(String fileUri) {
		try {
			// imgPreview.setVisibility(View.VISIBLE);

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 8;

			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri, options);

			ImageView photoPreview = (ImageView) findViewById(R.id.photoPreview);
			photoPreview.setImageBitmap(bitmap);

			new FoursquareAsyncTask().execute();

			if (null != tryToCreateFolder()) {
			// TODO copy from IMAGE_DIRECTORY_NAME and move to location
				
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public String tryToCreateFolder() {
		String path = "";

		// External sdcard location
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory() + File.separator
						+ MainActivity.IMAGE_DIRECTORY_NAME, location);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(location, "Oops! Failed create " + location
						+ " directory");
				return null;
			}

			Log.i("checkForExistingFolder", location + " already exists!");
		}

		return mediaStorageDir.getAbsolutePath();
	}

	public boolean deleteFile(String path) {
		try {
			new File(path).delete();
		}

		catch (Exception ef) {
			Log.e("Delete file", ef.getMessage());
			return false;
		}

		return true;
	}

	/*
	 * path = caminho do arquivo antigo a ser deletado, tem salvo no this.photoPath
	 */
	public boolean copyFile(String path) {
		InputStream in = null;
		OutputStream out = null;
		String destiny = tryToCreateFolder();
		
		File file = new File(path);
		String fileName = file.getName();

		try {
			in = new FileInputStream(path);
			out = new FileOutputStream(destiny + File.separator + fileName);
		} catch (Exception e) {

		}

		return true;
	}
	
	/*
	 * Salvar foto com localização escolhida
	 *  
	 */
	
	public void savePicture(View v) {
		final Context context = this;
		//Inserir no banco de dados o registro completo
		SQLiteHelper sqlhelper = new SQLiteHelper(context);
		GalleryInfo gi = new GalleryInfo();
		gi.setVenueName(venues.getVenues().get(0).name);
		gi.setLatGPS(Double.toString(latitude));
		gi.setLngGPS(Double.toString(longitude));
		gi.setLatVenue(venues.getVenues().get(0).location.lat);
		gi.setLngVenue(venues.getVenues().get(0).location.lng);
		Date date = new Date(); 
		gi.setDate(date);
		sqlhelper.add(gi);
				
		sqlhelper.getAllGalleryInfo();
		
				
		//CRIAR UMA PASTA NOVA E SALVAR A FOTO NELA
		//NOMEDOLUGAR_DATA.jpg
	}
	
	
	public void populateLocationSpinner(VenuesList vl) {
		locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
		//for no json, pegando os nomes das locations e populando o spinner
		List<String> list = new ArrayList<String>();
		for (Venue venue : vl.getVenues()) {
			list.add(venue.getName());
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		locationSpinner.setAdapter(dataAdapter);
	}
	
	public void addListenerOnSpinnerItemSelection() {
		locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
		locationSpinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener(venues, (TextView)findViewById(R.id.latitudeFS), (TextView)findViewById(R.id.longitudeFS), Double.toString(latitude), Double.toString(longitude)));
	}
	
	/*
	 * Classe Interna Privada, responsável por trabalhar com o WebService
	 */
	private class FoursquareAsyncTask extends AsyncTask<Void, Void, String> {

		//VenuesList venues = new VenuesList();

		// pega o texto da httpentity
		// poderia ser usado um BasicResponseHandler, passando como parametro o
		// handler ao inves do localContext
		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();

			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);

				if (n > 0)
					out.append(new String(b, 0, n));
			}

			return out.toString();
		}
		
		
		/*
		 * Método que faz a requisição do WebService em background
		 */
		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();

			// WebService URL
			String URLString = "https://api.foursquare.com/v2/venues/search?ll="
					+ PhotoActivity.latitude
					+ ","
					+ PhotoActivity.longitude
					+ "&oauth_token=3N1YYYFSO2FQVI00TNR1OTYS1C1FFTGBJGU3VWP4QMCCKG5K&v=20140220";

			Log.d("WS", URLString);

			HttpGet httpGet = new HttpGet(URLString);

			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet,	localContext);
				HttpEntity entity = response.getEntity();
				text = getASCIIContentFromEntity(entity);
				
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
			
			return text;
		}
		
		/*
		 * Método executado após a realização do POST do WebService
		 * Quando o WebService termina sua execução, realiza os comandos desejados
		 */
		@Override
		protected void onPostExecute(String results) {
			if (results != null) {
				
				Gson gson = new Gson();
				JsonElement jelement = new JsonParser().parse(results);
				JsonObject jobj = jelement.getAsJsonObject();
				jobj = jobj.getAsJsonObject("response");
				venues = gson.fromJson(jobj.toString(), VenuesList.class);
				
				// Popula o spinner com os valores das venues retornados pelo WS e parseados pelo GSON
				populateLocationSpinner(venues);
				// Escuta o valor selecionado no spinner
				addListenerOnSpinnerItemSelection();
				TextView tv = (TextView) findViewById(R.id.legenda);
				tv.setText("Localização: " + venues.getVenues().get(0).name);
				//PhotoActivity.location = results;
				//GPS LATLNG
				TextView tv1 = (TextView) findViewById(R.id.latitude);
				tv1.setText("Latitude do GPS: " + Double.toString(latitude));
				TextView tv2 = (TextView) findViewById(R.id.longitude);
				tv2.setText("Longitude do GPS: " + Double.toString(longitude));
				//Foursquare LATLNG
				TextView tv3 = (TextView) findViewById(R.id.latitudeFS);
				tv3.setText("Latitude do FS: " + venues.getVenues().get(0).location.lat);
				TextView tv4 = (TextView) findViewById(R.id.longitudeFS);
				tv4.setText("Longitude do FS: " + venues.getVenues().get(0).location.lng);
				button = (Button) findViewById(R.id.save);
				button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						SpinnerOnItemSelectedListener.savePicture(v);
					}
				});
				
				button.setEnabled(true);
			}
		}
	}

}
