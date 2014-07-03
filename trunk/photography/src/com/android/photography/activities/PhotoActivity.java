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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
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
	private EditText editText;
	private Spinner locationSpinner;
	static final String IMAGE_DIRECTORY_NAME = "Photography";
	static VenuesList venues;
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
		setContentView(R.layout.photo_layout);

		button = (Button) findViewById(R.id.save);
		button.setEnabled(false);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();

		if (b != null) {
			photoLabel = (String) b.get("photoLabel");
			photoPath = (String) b.get("photoPath");
			latitude = (Double) b.get("latitude");
			longitude = (Double) b.get("longitude");
		}
		previewCapturedImage(photoPath);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		Intent i = new Intent(PhotoActivity.this, MainActivity.class);
		startActivity(i);
	}

	private void previewCapturedImage(String fileUri) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();

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
		locationSpinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener(venues, (TextView) findViewById(R.id.name), (TextView) findViewById(R.id.actual_date),
				Double.toString(latitude), Double.toString(longitude), (EditText) findViewById(R.id.editTextLocation)));
	}

	/**
	 * Private class responsible for work with WebService
	 */
	private class FoursquareAsyncTask extends AsyncTask<Void, Void, String> {

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

		/**
		 * Method executed in response to DoInBackground that capture the return
		 * of the HTTP request and parse the JSON using GSON library
		 */
		@Override
		protected void onPostExecute(String results) {
			if (!results.contains("Unable to resolve")) {
				progressDialog.dismiss();
				// Mapping the results variable (JSON returned from WebService) with GSON into Java classes
				Gson gson = new Gson();
				JsonElement jelement = new JsonParser().parse(results);
				JsonObject jobj = jelement.getAsJsonObject();
				jobj = jobj.getAsJsonObject("response");
				venues = gson.fromJson(jobj.toString(), VenuesList.class);
				// Populates the spinner with the values returnd by the venues WS and parsed by GSON
				populateLocationSpinner(venues);
				// Listen to the selected value on spinner
				addListenerOnSpinnerItemSelection();
				TextView nome = (TextView) findViewById(R.id.name);
				nome.setText("Nome do arquivo: " + photoLabel);
				editText = (EditText) findViewById(R.id.editTextLocation);
				button = (Button) findViewById(R.id.save);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							// If user typed event on screen
							if (editText.getText().toString().trim().length() != 0) {
								String str = editText.getText().toString();
								SpinnerOnItemSelectedListener.savePicture(v, PhotoActivity.photoLabel, str);
								Intent intent = new Intent(v.getContext(), MainActivity.class);
								intent.putExtra("fromEditText", str);
								startActivity(intent);
								finish();
							// If user selected a value on spinner
							} else {
								String str2 = "@123@";
								SpinnerOnItemSelectedListener.savePicture(v, PhotoActivity.photoLabel, str2);
								Intent intent = new Intent(v.getContext(), MainActivity.class);
								startActivity(intent);
								finish();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

				button.setEnabled(true);

			} else {
				progressDialog.dismiss();
				
				// Create AlertDialog object to notify user about your connection status
				AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);
				builder.setMessage("Problema de Conexão \nPor favor verifique sua conexão!");
				builder.setNeutralButton("Voltar ao Menu Inicial", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						startActivity(intent);
						finish();
					}
				});

				builder.show();
			}
		}

		@Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
		}
	}
}