package com.diana.radius;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;


import com.diana.radius.Enums.DATA;
import com.diana.radius.services.Search_service;

/**
 * favorites fragment
 */
public class Fav_frag extends Fragment {
    private DB_Helper helper;
    private Location_Item_Adapter adapter;
    private RecyclerView fav_list;
    private Context context;

    // Empty  constructor
    public Fav_frag() {
    }

// on create method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fav_fragment, container, false);
        fav_list = (RecyclerView) v.findViewById(R.id.fav_list_view);
        context =getContext();

        helper =new DB_Helper(context, DATA.FAVORITE);
        adapter = new Location_Item_Adapter(context, helper.getAllLocations(DATA.FAVORITE),DATA.FAVORITE);
        fav_list.setAdapter(adapter);
        load_list();

        return v;
    }
// load list method
    public void load_list (){

        //    check phone orientation and change the RecyclerLayout
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {fav_list.setLayoutManager(new LinearLayoutManager(context));}
        else{
            fav_list.setLayoutManager(new GridLayoutManager(context, 2));}

            adapter.add_all(helper.getAllLocations(DATA.FAVORITE));
    }

}

