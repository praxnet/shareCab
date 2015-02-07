package com.olaappathon.sharehack.sharehack;

import com.olaappathon.sharehack.sharehack.utils.Constant;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FragmentSettings extends Fragment {

	EditText mEditTextAddress;
	Button mButtonSave;

	public FragmentSettings() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

		init(rootView);
		return rootView;
	}

	private void init(View rootView) {
		mEditTextAddress = (EditText) rootView.findViewById(R.id.ip_address);
		mButtonSave = (Button) rootView.findViewById(R.id.save);
		mButtonSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				preferences.edit().putString(Constant.PREF_KEY_IP_ADDRESS, mEditTextAddress.getText().toString()).commit();
			}
		});

	}
}
