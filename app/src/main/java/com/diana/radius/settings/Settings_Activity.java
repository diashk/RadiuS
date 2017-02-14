package com.diana.radius.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.diana.radius.R;

/**
 * settings activity - connect layout to content view
 */
public class Settings_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
    }

}
