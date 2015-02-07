/**
 * 
 */
package com.olaappathon.sharehack.sharehack;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.olaappathon.sharehack.sharehack.data.RideData;

/**
 * @author sharanu
 * 
 */
public class FragmentShareResult extends Fragment {
	ListView ridesList;
	ArrayList<RideData> rideDatas = new ArrayList<RideData>();

	public FragmentShareResult(ArrayList<RideData> rides) {
		rideDatas = rides;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_search_ride, container, false);
		ridesList = (ListView) rootView.findViewById(R.id.listView1);
		RidesAdapter adapter = new RidesAdapter(getActivity(), rideDatas);
		ridesList.setAdapter(adapter);
		ridesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

				// set title
				alertDialogBuilder.setTitle("Your Title");

				// set dialog message
				alertDialogBuilder.setMessage("Your Ride Confirmed! Enjoy Ride").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						((MainActivity) getActivity()).displayView(0);
					}
				});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		});
		return rootView;
	}
}
