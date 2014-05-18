package com.android.photography;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.photography.webservice.VenuesList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PhotoActivity extends Activity {

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

	// chamada do servico
	private class FoursquareAsyncTask extends AsyncTask<Void, Void, String> {

		VenuesList venues = new VenuesList();

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

			// webservice attributes
			String URLString = "https://api.foursquare.com/v2/venues/search?ll="
					+ PhotoActivity.latitude
					+ ","
					+ PhotoActivity.longitude
					+ "&oauth_token=3N1YYYFSO2FQVI00TNR1OTYS1C1FFTGBJGU3VWP4QMCCKG5K&v=20140220";

			Log.d("WS", URLString);

			HttpGet httpGet = new HttpGet(URLString);

			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);

				HttpEntity entity = response.getEntity();

				text = getASCIIContentFromEntity(entity);

				Gson gson = new Gson();

				JsonElement jelement = new JsonParser().parse(text);
				JsonObject jobj = jelement.getAsJsonObject();
				jobj = jobj.getAsJsonObject("response");

				venues = gson.fromJson(jobj.toString(), VenuesList.class);

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return venues.getVenues().get(0).name;
		}

		@Override
		protected void onPostExecute(String results) {
			if (results != null) {
				TextView tv = (TextView) findViewById(R.id.legenda);
				tv.setText(results);
				PhotoActivity.location = results;
			}
		}
	}

}
