/**
 * 
 */
package com.olaappathon.sharehack.sharehack;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author sharanu
 * 
 */
public class MapActivity extends Activity {

	// Google Map
	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_holder);

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		try {
	        System.out.println("mass: " + getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode);
        } catch (NameNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}

			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			final LatLng CIU = new LatLng(35.21843892856462, 33.41662287712097);
			Marker ciu = googleMap.addMarker(new MarkerOptions().position(CIU).title("My Office"));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}
}
