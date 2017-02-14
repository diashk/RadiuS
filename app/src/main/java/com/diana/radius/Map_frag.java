package com.diana.radius;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.diana.radius.services.Location_service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

/**
 * map fragment
 */
public class Map_frag extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private SharedPreferences sp;
    private Context context;
    private double myLat, myLng, locLat, locLnf;
    public static GoogleMap map ;
    private Marker marker_my_loc, marker_picked_loc;
    private View v;
    private  Location dest_location;
    private LatLng zoom_latlng;
    private boolean ismapready;
    private final String FIRST_MARKER_ML="my", MY_LOC_MARKER="my_new",PICKED_LOC_MARKER="picked";

    // Empty  constructor
    public  Map_frag() {
    }

// on creat method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        v = inflater.inflate(R.layout.map_fragment, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment2);
        supportMapFragment.getMapAsync(this);

        // create receiver
        Location_Receiver receiver = new Location_Receiver();
        // create filter with first action
        IntentFilter filter = new IntentFilter(Location_service.LOCATION_ACTION);

        // register the receiver
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
        return v;
    }


// on map ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //save map
        map = googleMap;
        //a
        add_location_marker(FIRST_MARKER_ML);
        ismapready =true;
        if (dest_location!=null){ add_location_marker(PICKED_LOC_MARKER);}
    }

    public  void set_dest_loc (Location loc){
        dest_location=loc;
        add_location_marker(PICKED_LOC_MARKER);

    }


    public void add_location_marker(String loc_type) {
        LatLng latLng = null;
        String title= null;

        switch (loc_type){
            case FIRST_MARKER_ML :
                if (marker_my_loc!=null) {
                    marker_my_loc.remove();
                }
                myLat = Double.parseDouble(sp.getString("lat", "0"));
                myLng = Double.parseDouble(sp.getString("lng", "0"));
                latLng = new LatLng(myLat, myLng);
                zoom_latlng = latLng;
                title = getString(R.string.you_are_here);
                marker_my_loc = map.addMarker(new MarkerOptions().position(latLng).title(title));

                map.animateCamera(CameraUpdateFactory.
                        newLatLngZoom(latLng, 16));

                break;
            case MY_LOC_MARKER :
                if (marker_my_loc!=null) {
                    marker_my_loc.remove();
                }

                myLat = Double.parseDouble(sp.getString("lat", "0"));
                myLng = Double.parseDouble(sp.getString("lng", "0"));
                latLng = new LatLng(myLat, myLng);
                title = getString(R.string.you_are_here);
                marker_my_loc = map.addMarker(new MarkerOptions().position(latLng).title(title));

                break;
            case PICKED_LOC_MARKER:
                if (marker_picked_loc != null) {
                   marker_picked_loc.remove();}
                if (ismapready=true) {
                    locLat = dest_location.getL_lat();
                    locLnf = dest_location.getL_lng();
                    LatLng latLng_dest = new LatLng(locLat, locLnf);
                    zoom_latlng =latLng_dest;
                    title = dest_location.getL_name();
                    marker_picked_loc= map.addMarker(new MarkerOptions().position(latLng_dest).title(title));

                }

                break;
        }
        map.animateCamera(CameraUpdateFactory.
                newLatLngZoom(zoom_latlng, 16));

        return;
    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

// location change receiver - calls to change
    private class Location_Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //TODO delete toast
            Toast.makeText(context, "you got new loc", Toast.LENGTH_SHORT).show();
            add_location_marker(MY_LOC_MARKER);
        }
    }



}

