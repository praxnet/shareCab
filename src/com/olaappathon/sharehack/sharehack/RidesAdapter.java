package com.olaappathon.sharehack.sharehack;

import java.util.ArrayList;

import com.olaappathon.sharehack.sharehack.data.RideData;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RidesAdapter extends BaseAdapter {
	Context context;
	ArrayList<RideData> rides;

	public RidesAdapter(Context context, ArrayList<RideData> rides) {
		this.context = context;
		this.rides = rides;
	}

	@Override
	public int getCount() {
		return rides.size();
	}

	@Override
	public Object getItem(int arg0) {
		return rides.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.listview_ride_item, null);
		}

		RideData data = rides.get(pos);

		TextView fromTo = (TextView) convertView.findViewById(R.id.from);
		TextView to = (TextView) convertView.findViewById(R.id.to);
		TextView numberOfSeats = (TextView) convertView.findViewById(R.id.number);

		numberOfSeats.setText(data.getNoOfMembers() + "");
		fromTo.setText(data.getSource());
		to.setText(data.getDestination());
		return convertView;
	}

}
