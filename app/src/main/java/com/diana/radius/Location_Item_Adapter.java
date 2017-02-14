package com.diana.radius;

/**
 * Created by jbt on 12/12/2016.
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.diana.radius.Enums.DATA;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
     * An adapter for the  Movie layout, for the web_search_movie_layout view list.
     */


public class Location_Item_Adapter extends RecyclerView.Adapter<Location_Item_Adapter.LocationHolder> {

    private Context context;
    private ArrayList<Location> locations = new ArrayList<>();
    private double my_lat, my_lng;
    private String measurement_type;
    private SharedPreferences sp;
    private On_location_picked listener;
    private DATA LOAD_TO_FRAG;

    // constructor
    public Location_Item_Adapter(Context context, ArrayList<Location> locations,DATA data) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        this.locations = locations;
        this.listener = (On_location_picked) context;
        LOAD_TO_FRAG = data;
    }

    // on create view holder
    @Override
    public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // notice in inflate method we type parent as the second parameter and false as the third parameter.
        // false means do not attach views to root if exists!
        View v = null;
        if (viewType == 1)
            v = LayoutInflater.from(context).inflate(R.layout.location_item, parent, false);
        else
            v = LayoutInflater.from(context).inflate(R.layout.location_item, parent, false);
        return new LocationHolder(v);
    }

    // add all method - called to reload lists
    public void add_all(ArrayList<Location> locations) {
        this.locations = locations;
    }

    @Override
    public void onBindViewHolder(LocationHolder holder, int position) {
        holder.bind(locations.get(position));
    }

    // get item count
    @Override
    public int getItemCount() {
        return locations.size();
    }


    /* Holder  class*/
    public class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView l_name, l_address, l_distance;
        private ImageView l_image;
        private Location location;
        private double distance;
        private boolean is_long_click;

// location holder method
        public LocationHolder(View itemView) {
            super(itemView);

            l_name = (TextView) itemView.findViewById(R.id.loc_item_name);
            l_address = (TextView) itemView.findViewById(R.id.loc_item_adress);
            l_distance = (TextView) itemView.findViewById(R.id.loc_item_distance);
            l_image = (ImageView) itemView.findViewById(R.id.loc_item_image);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


        }
// bind method
        public void bind(Location location) {

            my_lat = Double.parseDouble(sp.getString("lat", "0"));
            my_lng = Double.parseDouble(sp.getString("lng", "0"));
            measurement_type = sp.getString("Measurement_key", "K");
            distance = distance(location.getL_lat(), my_lat, location.getL_lng(), my_lng);
            String dist_string = null;
            if (measurement_type.equals("K")) {
                dist_string =context.getString(R.string.meters_from_you);
            } else {
                dist_string = context.getString(R.string.yards_from_you);
                distance = (distance / 1.6);
            }
            String is = context.getString(R.string.is);
            DecimalFormat formatter = new DecimalFormat("#.#");
            String dist = location.getL_name() + is +" "+ formatter.format(distance) + dist_string;
            this.location = location;
            l_name.setText(location.getL_name());
            l_address.setText(location.getL_address());
            l_distance.setText(dist);
            Bitmap bitmap = location.getL_pic();
            if (bitmap == null) l_image.setImageResource(R.drawable.noimage);
            else l_image.setImageBitmap(bitmap);
        }

        // method to calculate distance between two locations
        private double distance(double lat1, double lat2, double lon1,
                                double lon2) {

            final int R = 6371; // Radius of the earth

            Double latDistance = Math.toRadians(lat2 - lat1);
            Double lonDistance = Math.toRadians(lon2 - lon1);
            Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c * 1000; // convert to meters
            return distance;
        }


        @Override
        public void onClick(View v) {
            // work only if there was no long click
            if (is_long_click == true) {
                is_long_click = false;
                return;}

            String pick = context.getString(R.string.picked);
          int position = getLayoutPosition();
            Location picked_loc =locations.get(position);

            Toast.makeText(context,  pick+ picked_loc.getL_name().toString(), Toast.LENGTH_SHORT).show();
            listener.onClick_locPick(picked_loc, LOAD_TO_FRAG);

        }

// on long click implementation
        @Override
        public boolean onLongClick(View v) {
            // work only if called from resuld frag
            if (LOAD_TO_FRAG.equals(DATA.FAVORITE)){
                return false;
            }
            is_long_click = true;
            String pick = context.getString(R.string.picked);
            int position = getLayoutPosition();
            Location picked_loc =locations.get(position);
            Toast.makeText(context, pick + picked_loc.getL_name().toString(), Toast.LENGTH_SHORT).show();
            listener.onLongClick_locPick(picked_loc);
            return false;
        }
    }

// on click methods interface
    public interface On_location_picked {

        public Location onClick_locPick(
                Location location, DATA data);

        public Location onLongClick_locPick(Location location);

    }



}






