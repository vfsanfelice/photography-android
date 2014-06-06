package com.android.photography.activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.photography.R;
import com.android.photography.listener.SpinnerOnItemSelectedListener;
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
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void populateLocationSpinner(VenuesList vl) {
		locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
		// for no json, pegando os nomes das locations e populando o spinner
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
		locationSpinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener(venues, (TextView) findViewById(R.id.latitudeFS), (TextView) findViewById(R.id.longitudeFS),
				(TextView) findViewById(R.id.legenda), Double.toString(latitude), Double.toString(longitude)));
	}

	/**
	 * Private class responsible for work with WebService
	 */
	private class FoursquareAsyncTask extends AsyncTask<Void, Void, String> {

		// pega o texto da httpentity
		// poderia ser usado um BasicResponseHandler, passando como parametro o
		// handler ao inves do localContext
		protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
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

		/**
		 * Method that calls the WebService in background and returns the
		 * response of the HTTP request
		 */
		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();

			// WebService URL
			String URLString = "https://api.foursquare.com/v2/venues/search?ll=" + PhotoActivity.latitude + "," + PhotoActivity.longitude
					+ "&oauth_token=3N1YYYFSO2FQVI00TNR1OTYS1C1FFTGBJGU3VWP4QMCCKG5K&v=20140220";

			Log.d("WS", URLString);

			HttpGet httpGet = new HttpGet(URLString);

			String text = null;
			try {
				synchronized (this) {
					int counter = 0;

					while (counter <= 2) {

						this.wait(200);
						counter++;
						setProgress(counter * 50);
						HttpResponse response = httpClient.execute(httpGet, localContext);
						HttpEntity entity = response.getEntity();
						text = getASCIIContentFromEntity(entity);
					}
				}

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return text;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(PhotoActivity.this, "Carregando...", "Aguarde enquanto é carregada a lista de lugares...", false, false);
		}

		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress(values[0]);
		}

		/*
		 * Método executado após a realização do POST do WebService Quando o
		 * WebService termina sua execução, realiza os comandos desejados
		 */

		/**
		 * Method executed in response to DoInBackground that capture the return
		 * of the HTTP request and parse the JSON using GSON library
		 */
		@Override
		protected void onPostExecute(String results) {
			if (results != null) {
				progressDialog.dismiss();
				// Mapeamento da variável results (json retornado do WebService)
				// com o GSON em classes java
				Gson gson = new Gson();
				JsonElement jelement = new JsonParser().parse(results);
				JsonObject jobj = jelement.getAsJsonObject();
				jobj = jobj.getAsJsonObject("response");
				venues = gson.fromJson(jobj.toString(), VenuesList.class);

				// Popula o spinner com os valores das venues retornados pelo WS
				// e parseados pelo GSON
				populateLocationSpinner(venues);

				// Escuta o valor selecionado no spinner
				addListenerOnSpinnerItemSelection();

				TextView legenda = (TextView) findViewById(R.id.legenda);
				legenda.setText("Localização: " + venues.getVenues().get(0).name);
				TextView nome = (TextView) findViewById(R.id.nome);
				nome.setText("Nome do arquivo: " + photoLabel);
				// GPS LATLNG
				TextView tv1 = (TextView) findViewById(R.id.latitude);
				tv1.setText("Latitude do GPS: " + Double.toString(latitude));
				TextView tv2 = (TextView) findViewById(R.id.longitude);
				tv2.setText("Longitude do GPS: " + Double.toString(longitude));
				// Foursquare LATLNG
				TextView tv3 = (TextView) findViewById(R.id.latitudeFS);
				tv3.setText("Latitude do FS: " + venues.getVenues().get(0).location.lat);
				TextView tv4 = (TextView) findViewById(R.id.longitudeFS);
				tv4.setText("Longitude do FS: " + venues.getVenues().get(0).location.lng);
				button = (Button) findViewById(R.id.save);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							SpinnerOnItemSelectedListener.savePicture(v, PhotoActivity.photoLabel);
							Intent intent = new Intent(v.getContext(), MainActivity.class);
							startActivity(intent);

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

				button.setEnabled(true);
			}
		}
	}

}
