package de.gcworld.ibis2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings extends PreferenceActivity {
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences settings_string = PreferenceManager.getDefaultSharedPreferences(this);
		String debug = settings_string.getString("Code", "0");
		
		
		Log.d("IBIS2", "Code ist: " + debug);
		
		if(debug.equals("je-gcworld-02645")) {
		addPreferencesFromResource(R.xml.debug);
		Log.d("IBIS2", "creating debug screen");
		}
		else {
			addPreferencesFromResource(R.xml.preference);
		}
	}
}
