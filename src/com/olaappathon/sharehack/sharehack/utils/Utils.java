/**
 * 
 */
package com.olaappathon.sharehack.sharehack.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.internal.ma;
import com.olaappathon.sharehack.sharehack.data.RideData;

/**
 * @author sharanu
 * 
 */
public class Utils {

	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String NUM_MEM = "num_members";
	public static final String TIME = "time";
	public static final String SOURCE = "source";
	public static final String DESTINATION = "destination";
	public static final String CREATED_BY_ID = "created_by";
	public static final String SHARE = "share";
	public static final String URL_POST_RIDE_REQUEST = "http://%s/github/diwali/web/app_dev.php/ola/ride";
	public static final String URL_POST_RIDE_SHARE = "http://%s/github/diwali/web/app_dev.php/ola/ride/share";

	public static Map<String, String> getKeyValuePair(RideData rideData) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(FROM, rideData.getSourceLati() + "," + rideData.getSourceLong());
		map.put(TO, rideData.getDestLati() + "," + rideData.getDestLong());
		map.put(TIME, rideData.getTime() + " ");
		map.put(SHARE, rideData.getShare() + " ");
		map.put(SOURCE, rideData.getSource() + " ");
		map.put(DESTINATION, rideData.getDestination() + " ");
		map.put(CREATED_BY_ID, rideData.getId() + " ");
		map.put(NUM_MEM, rideData.getNoOfMembers());

		return map;
	}

	public static String getStringFromPref(Context context, String key, String defaultValue) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString(key, defaultValue);
	}

	public static String getGetParams(RideData rideData) {
		String request = FROM + "=" + rideData.getSourceLati() + "," + rideData.getSourceLong();
		request += "&" + TO + "=" + rideData.getDestLati() + "," + rideData.getDestLong();
		request += "&" + NUM_MEM + rideData.getNoOfMembers();
		return request;
	}

	public static ArrayList<RideData> getRideDataArray(String response) {
		ArrayList<RideData> arrayList = new ArrayList<RideData>();
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray jsonArray = jsonObject.getJSONArray("shared_rides");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);
				RideData rideData = new RideData();
				try {
	                rideData.setId(object.getInt("id"));
                } catch (Exception e) {
	                rideData.setId(-1);
	                e.printStackTrace();
                }
				rideData.setSource(object.getString("from"));
				rideData.setDestination(object.getString("to"));
				rideData.setNoOfMembers(object.getString("num_members"));
				arrayList.add(rideData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return arrayList;
	}

}
