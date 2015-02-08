package com.olaappathon.sharehack.sharehack;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.olaappathon.sharehack.sharehack.data.RideData;
import com.olaappathon.sharehack.sharehack.utils.Constant;
import com.olaappathon.sharehack.sharehack.utils.Utils;

public class FragmentSearchRide extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private static Context mContext;
	private static RideData mRideData;

	private Button mButtonRideNow;
	private Button mButtonRidelater;
	private EditText mEditTextDestination;
	private EditText mEditTextSource;
	static private MainActivity activity;

	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);

	public FragmentSearchRide() {
	}

	private static ProgressDialog pDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = (MainActivity) getActivity();
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		View rootView = inflater.inflate(R.layout.fragment_search_kride, container, false);
		mContext = getActivity().getApplicationContext();
		init(rootView);

		return rootView;
	}

	View.OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_ride_now:
				processRideNow();
				break;
			case R.id.button_ride_later:
				processRideLater();
			default:
				break;
			}
		}

	};

	private void processRideLater() {
		showDialogue(AlertDialogFragment.newInstance(true));
	}

	private void processRideNow() {
		showDialogue(AlertDialogFragment.newInstance(false));
	}

	private void showDialogue(DialogFragment newFragment) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		newFragment.show(ft, "dialog");
	}

	private void init(View view) {

		mRideData = new RideData();
		mButtonRidelater = (Button) view.findViewById(R.id.button_ride_later);
		mButtonRideNow = (Button) view.findViewById(R.id.button_ride_now);

		mButtonRidelater.setOnClickListener(mClickListener);
		mButtonRideNow.setOnClickListener(mClickListener);

		mEditTextDestination = (EditText) view.findViewById(R.id.edittext_destination);
		// mEditTextSource = (EditText) view.findViewById(R.id.edittext_source);
		onCreateInit();

	}

	private static void showpDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private static void hidepDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	private static void launchList(ArrayList<RideData> rideDatas) {
		activity.attachList(rideDatas);
	}

	private static void sendDataToServer() {

		showpDialog();
		String url = String.format(Utils.URL_POST_RIDE_SHARE, Utils.getStringFromPref(mContext, Constant.PREF_KEY_IP_ADDRESS, "192.168.1.31"));
		url += "?" + Utils.getGetParams(mRideData);
		System.out.println("url" + url);

		Map<String, String> params = Utils.getKeyValuePair(mRideData);

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					hidepDialog();
					launchList(Utils.getRideDataArray(response.toString()));
					VolleyLog.v("Response:%n %s", response.toString(4));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				hidepDialog();
				VolleyLog.e("Error: ", error.getMessage());
			}
		});
		AppController.getInstance().getRequestQueue().add(req);
	}

	public static class AlertDialogFragment extends DialogFragment {
		static private boolean mShowTimer;
		RadioGroup mRadioGroupShare;
		CheckBox mCheckBoxShare;
		TimePicker mTimePicker;
		Button mRideOK, mRideCancel;
		static AlertDialogFragment mFrag;
		EditText mEditTextNoOfMembers;

		private static String pad(int c) {
			if (c >= 10)
				return String.valueOf(c);
			else
				return "0" + String.valueOf(c);
		}

		View.OnClickListener mClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.button_ok:
					mRideData.setId(1);
					mRideData.setNoOfMembers(mEditTextNoOfMembers.getText().toString());
					sendDataToServer();
					mFrag.dismiss();
					break;
				case R.id.button_cancel:
					mFrag.dismiss();
				default:
					break;
				}
			}

		};

		private void setTime() {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			mTimePicker.setCurrentHour(hour);
			mTimePicker.setCurrentMinute(minute);
		}

		public static AlertDialogFragment newInstance(boolean showTime) {
			mFrag = new AlertDialogFragment();
			mShowTimer = showTime;
			return mFrag;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			View v = inflater.inflate(R.layout.fragment_ridenow, container, false);

			getDialog().setTitle("Ride!!");

			mEditTextNoOfMembers = (EditText) v.findViewById(R.id.no_of_members);
			mRideOK = (Button) v.findViewById(R.id.button_ok);
			mRideCancel = (Button) v.findViewById(R.id.button_cancel);
			mRideOK.setOnClickListener(mClickListener);
			mRideCancel.setOnClickListener(mClickListener);
			mCheckBoxShare = (CheckBox) v.findViewById(R.id.checkbox_share_ride);
			mCheckBoxShare.setVisibility(View.GONE);
			mTimePicker = (TimePicker) v.findViewById(R.id.timepicker);
			mTimePicker.setVisibility(mShowTimer ? View.VISIBLE : View.GONE);
			mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
					mRideData.setTime((new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute))).toString());

					mTimePicker.setCurrentHour(hourOfDay);
					mTimePicker.setCurrentMinute(minute);
				}
			});
			setTime();

			mRadioGroupShare = (RadioGroup) v.findViewById(R.id.radio_group_share);
			mCheckBoxShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mRadioGroupShare.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					mRideData.setShare(isChecked ? "" : Constant.SHARE_NOT);
				}
			});
			mRadioGroupShare.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
					case R.id.radio_public:
						mRideData.setShare(Constant.SHARE_PUBLIC);
						break;
					case R.id.radio_only_contact:
						mRideData.setShare(Constant.SHARE_CONTACT);
					default:
						break;
					}
				}
			});

			return v;
		}
	}
	
	
	
	
	
	
	
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates;

    final int RQS_GooglePlayServices = 1;
    boolean markerClicked;
    TextView tvLocInfo;
    PolygonOptions polygonOptions;
    Polygon polygon;
    Location myLocation;
    private GoogleMap map;
    protected String mLastUpdateTime;
    private Marker currentLocMarker;
    private Marker destLocMarker;
    private LatLng currentLatLng;
    private int markerCount = 0;
    List<Polyline> polylines = new ArrayList<Polyline>();

    
    protected void onCreateInit() {

        buildGoogleApiClient();
        mRequestingLocationUpdates = true;
        try {
	        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view))
	                .getMap();
        } catch (Exception e) {
	        e.printStackTrace();
        }
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.setOnMapClickListener(this);
            map.setOnMapLongClickListener(this);
            map.setOnMarkerDragListener(this);

            markerClicked = false;
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGoogleApiClient.connect();
    }
    
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            placeCurrentMarker(currentLatLng, true);
            markerCount++;

            tvLocInfo.setText(String.valueOf(mCurrentLocation.getLatitude()) + " " + String.valueOf(mCurrentLocation.getLongitude()));
        } else {
            dealWithNoLocation(getActivity());
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void placeCurrentMarker(LatLng currentLatLng, boolean moveCamera) {
        if(currentLocMarker == null){
            //currentLocMarker.remove();
            currentLocMarker = map.addMarker(new MarkerOptions().position(currentLatLng).draggable(true)
                    .title("Your location").snippet("you are cool"));
        }

        if(moveCamera){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        mGoogleApiClient.reconnect();
        map.clear();
        markerCount = 0;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
        map.clear();
        markerCount = 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //Toast.makeText(this, "on location changed", Toast.LENGTH_SHORT).show();
        //placeCurrentMarker(currentLatLng, false);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapClick(LatLng point) {
        tvLocInfo.setText(point.toString());
        map.animateCamera(CameraUpdateFactory.newLatLng(point));

        markerClicked = false;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        markerCount++;
        if(markerCount <=2){
            tvLocInfo.setText("New marker added@" + point.toString());
            destLocMarker = map.addMarker(new MarkerOptions()
                    .position(point)
                    .draggable(true));
        }

        if(markerCount == 2){
            redrawRoute();
        }

        markerClicked = false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        tvLocInfo.setText("Marker " + marker.getId() + " Drag@" + marker.getPosition());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        tvLocInfo.setText("Marker " + marker.getId() + " DragEnd");
        redrawRoute();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        tvLocInfo.setText("Marker " + marker.getId() + " DragStart");

    }

    private void dealWithNoLocation(final Context context){
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled = false;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled && !network_enabled){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("no location");
            dialog.setPositiveButton("switch on", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("nah", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();

        }else{
            Toast.makeText(context, "shit happens", Toast.LENGTH_LONG).show();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=false");

        return urlString.toString();
    }

    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline route = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                        .width(2)
                        .color(Color.BLUE).geodesic(true));

                polylines.add(route);
            }
        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;
        connectAsyncTask(String urlPass){
            url = urlPass;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if(result!=null){
                clearLinesOnMap();
                drawPath(result);
            }
        }
    }

    private void redrawRoute(){
        if(currentLatLng != null && destLocMarker != null){
            String url = makeURL(currentLatLng.latitude, currentLatLng.longitude, destLocMarker.getPosition().latitude, destLocMarker.getPosition().longitude);
            new connectAsyncTask(url).execute();
        }
    }

    private void clearLinesOnMap(){
        for(Polyline line : polylines)
        {
            line.remove();
        }

        polylines.clear();
    }

	
	

}
