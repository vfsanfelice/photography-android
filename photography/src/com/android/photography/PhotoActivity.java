package com.android.photography;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoActivity extends Activity {

	// photo attributes
	private static String photoLabel;
	private static String photoPath;
	private static String latitude;
	private static String longitude;

	// webservice attributes
	private static String URLString = "https://api.foursquare.com/v2/venues/search?ll=%s,%2d"
			+ "&oauth_token=3N1YYYFSO2FQVI00TNR1OTYS1C1FFTGBJGU3VWP4QMCCKG5K&v=20140220";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_layout);

		Intent iin = getIntent();
		Bundle b = iin.getExtras();

		if (b != null) {
			this.photoLabel = (String) b.get("photoLabel");
			this.photoPath = (String) b.get("photoPath");
			// this.latitude = (String) b.get("latitude");
			// this.longitude = (String) b.get("longitude");
		}

		previewCapturedImage(this.photoPath, this.photoLabel);

	}

	private void previewCapturedImage(String fileUri, String label) {
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

			TextView textLabel = (TextView) findViewById(R.id.legenda);
			textLabel.setText(label);

			TextView locationLabel = (TextView) findViewById(R.id.location);
			GPSTracker gpsTracker = new GPSTracker(this);
			String latlong = "lat: " + gpsTracker.getLatitude() + " - long: "
					+ gpsTracker.getLongitude();
			locationLabel.setText(latlong);

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	// chamada do servico
	private class FoursquareAsyncTask extends AsyncTask<Void, Void, String> {

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

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();

			/*
			 * jeito feio de adicionar parametros na url
			 * 
			 * String insertParametersURL = String.format(URLString,
			 * PhotoActivity.latitude, PhotoActivity.longitude);
			 */

			// parametros para enviar no get do servico
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("ll", "lat long");
			parameters.put("oauth_token",
					"3N1YYYFSO2FQVI00TNR1OTYS1C1FFTGBJGU3VWP4QMCCKG5K");
			parameters.put("v", "~~");

			// adiciona parametros na url
			URLString = addParametersToURL(URLString, parameters);

			HttpGet httpGet = new HttpGet(URLString);

			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);

				HttpEntity entity = response.getEntity();

				text = getASCIIContentFromEntity(entity);

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return text;
		}

		@Override
		protected void onPostExecute(String results) {
			if (results != null) {
				TextView tv = (TextView) findViewById(R.id.action_settings);

				tv.setText(results);

			}

			Button b = (Button) findViewById(R.id.action_settings);

			b.setClickable(true);
		}

		// TODO
		public ArrayList<String> getLocationsFromJSON(String json)
				throws JSONException {

			JSONObject jObj = new JSONObject(json);
			JSONObject response = jObj.getJSONObject("response");
			JSONArray venues = response.getJSONArray("venues");

			// JSONObject subObj = jObj.getJSONObject("address");
			// String city = subObj.getString("city");

			return null;
		}

		public String addParametersToURL(String url,
				HashMap<String, String> parameters) {
			String param = "";

			for (Entry<String, String> entry : parameters.entrySet()) {
				if (url.charAt(url.length()) != '&') {
					url.concat("&" + entry.getKey() + "=" + entry.getValue());
				}

				else {
					url.concat(entry.getKey() + "=" + entry.getValue());
				}
			}

			url.concat("&timestamp=" + Calendar.getInstance().getTime());

			return null;
		}
	}

}
