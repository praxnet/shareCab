/**
 * 
 */
package com.olaappathon.sharehack.sharehack.utils;

import java.util.HashMap;
import java.util.Map;

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
	public static final String URL_POST_RIDE_REQUEST = "http://10.20.200.117/github/diwali/web/app_dev.php/ola/ride";

	public static Map<String, String> getKeyValuePair(RideData rideData) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(FROM, rideData.getSourceLati() + "," + rideData.getSourceLong() + " ");
		map.put(TO, rideData.getDestLati() + "," + rideData.getDestLong()+ " ");
		map.put(TIME, rideData.getTime() + " ");
		map.put(SHARE, rideData.getShare() + " ");
		map.put(SOURCE, rideData.getSource()+ " ");
		map.put(DESTINATION, rideData.getDestination()+ " ");

		return map;
	}
}
