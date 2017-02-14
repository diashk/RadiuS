package com.diana.radius;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.diana.radius.Enums.DATA;
import com.diana.radius.services.Search_service;

/**
 * Created by jbt on 12/11/2016.
 */
public class Search_frag extends Fragment implements View.OnClickListener {

    private EditText search_text;
    private ImageButton search_by_name_btn, search_by_radius_btn;
    private RecyclerView result_list;
    private DB_Helper helper;
    private Location_Item_Adapter adapter;
    private static String MEASUREMENT_TYPE, RADIUS;
    private SharedPreferences sp;
    private Context context;

    // Empty  constructor
    public Search_frag() {
    }

// on create view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.search_fragment, container, false);

        search_text = (EditText) v.findViewById(R.id.editText_search_value);
        search_by_name_btn = (ImageButton) v.findViewById(R.id.imageButton_search_by_value);
        search_by_radius_btn = (ImageButton) v.findViewById(R.id.imageButton_search_radius);
        result_list = (RecyclerView) v.findViewById(R.id.results_list_view);
        search_by_name_btn.setOnClickListener(this);
        search_by_radius_btn.setOnClickListener(this);
        context =getActivity();
        sp =PreferenceManager.getDefaultSharedPreferences(context);


        // create receiver
        Search_Receiver receiver = new Search_Receiver();
        // create filter with first action
        IntentFilter filter = new IntentFilter(Search_service.NAME_ACTION);
        // add the other  action
        filter.addAction(Search_service.RADIUS_ACTION);

        // register the receiver
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);

        helper =new DB_Helper(context, DATA.RESULT);
        adapter = new Location_Item_Adapter(context, helper.getAllLocations(DATA.RESULT),DATA.RESULT);
        result_list.setAdapter(adapter);
        load_list();

        return v;
    }



    // a method to load the list
    public void load_list (){
        if (MainActivity.is_tablet==true && MainActivity.orientation== Configuration.ORIENTATION_LANDSCAPE){
            result_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else{
        //    check phone orientation and change the RecyclerLayout
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            result_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        else
            result_list.setLayoutManager(new GridLayoutManager(getActivity(), 2));}

            adapter.add_all(helper.getAllLocations(DATA.RESULT));

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

    }


    // on buttons click
    @Override
    public void onClick(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);


        Intent intent = new Intent(getContext(), Search_service.class);
        switch (view.getId()){
            case R.id.imageButton_search_by_value: //search by name

                intent.putExtra("search_key", "name");
                String search_param =String.valueOf(search_text.getText());
                if(search_param.equals("")){
                    String search_error= getString(R.string.search_value_error);
                    Toast.makeText(context,search_error , Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("search_param",search_param);
                break;
            case  R.id.imageButton_search_radius: //search by radius
                intent.putExtra("search_key", "radius");
                break;
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
        getContext().startService(intent);
    }


// get broadcast after search service is ended
    class Search_Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            load_list();
        }


    }
}


