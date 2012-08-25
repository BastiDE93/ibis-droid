package de.gcworld.ibis2;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class debug extends PreferenceActivity {
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.debug);
	}
}
