package com.diana.radius.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.diana.radius.Fav_frag;
import com.diana.radius.MainActivity;
import com.diana.radius.R;

/**
 * settings fragment
 */
public class Settings_Fragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

//onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        ListPreference measurement_list = (ListPreference) findPreference("Measurement_key");
        ListPreference radius_list =(ListPreference) findPreference("Radius_key");
        // add listener for changed in the distance measurement preference
        measurement_list.setOnPreferenceChangeListener(this);
        radius_list.setOnPreferenceChangeListener(this);
        }


    // on preference change method
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
            String d_m = getString(R.string.d_m);


        // check which preference changed
        String distance_measurement="Kilometer";
        switch (preference.getKey()){
            case "Measurement_key":

                if (newValue.equals("K")){
                    distance_measurement=getString(R.string.KM);
                }else  distance_measurement=getString(R.string.MILE);
                preference.setSummary(d_m +distance_measurement);
                break;

            case "Radius_key":
                String radius_measurement=null;
                String r_m =getString(R.string.r_m);
                if (newValue.equals("1000")){radius_measurement="1";}else if (newValue.equals("3000")) {radius_measurement="3";}else {radius_measurement="10000";}
                preference.setSummary("Your search radius is :" +radius_measurement);
                break;

        }

        Toast.makeText(getActivity(), getString(R.string.setting_change), Toast.LENGTH_SHORT).show();
        return true;
    }
}
