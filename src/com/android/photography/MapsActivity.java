package com.android.photography;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Activity{
	
	protected GoogleMap map;
	protected Marker marker;
	protected LatLng latlng;
	protected MapsLocationSource locationSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps_layout);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		
		MapsLocationSource mls = new MapsLocationSource();
		map.setLocationSource(mls);
	}
	
	public void addMarker(LatLng ll){
		marker = map.addMarker(new MarkerOptions()
								.position(ll)
								.title("Sydney"));	
		
	}
	
	public class GPS extends MapsActivity implements LocationListener {
		
		@Override
		protected void onResume() {
			super.onResume();
			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder.setMessage("O GPS está desligado, deseja ligar agora?")
					.setCancelable(false).setPositiveButton("Sim",
															new DialogInterface.OnClickListener() {
																
																@Override
																public void onClick(DialogInterface dialog, int id) {
																	Intent callGPsSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
																	startActivity(callGPsSettingIntent);
																}
															});
				
			    alertDialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
			    AlertDialog alert = alertDialogBuilder.create();
			    alert.show();
			}
		}
		
		@Override
		protected void onPause() {
			super.onPause();
			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			lm.removeUpdates(this);
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Toast.makeText(this, "Location: " + location, Toast.LENGTH_SHORT).show();
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			map.animateCamera(CameraUpdateFactory.newLatLng(ll));
			this.locationSource.setLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
