package com.diana.radius.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.diana.radius.DB_Helper;
import com.diana.radius.Enums.DATA;
import com.diana.radius.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Intent serveise that being called every time that internet search is being called
 */
public class Search_service extends IntentService   {
    public static final String NAME_ACTION = "com.diana.radius.name_action";
    public static final String RADIUS_ACTION = "com.diana.radius.radius_action";
    private static final String NAME_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%s&keyword=%s&key=AIzaSyCM7IvgQf_Cal13hT5HceR7xMSBPmN41Uc";
    private static final String RADIUS_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%s&key=AIzaSyCM7IvgQf_Cal13hT5HceR7xMSBPmN41Uc";
    private static final String PICTURE_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=AIzaSyCM7IvgQf_Cal13hT5HceR7xMSBPmN41Uc";
    private String lat, lng;
    String error = null;
    private DB_Helper helper;

// search service constructor
    public Search_service() {
        super("Search_service");
    }


    // on handle intent  method. here we get the type of search (by name or radius) that we desire and, call the web search method and send the broadcast
    @Override
    protected void onHandleIntent(Intent intent1) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        helper = new DB_Helper(this, DATA.RESULT);
        // we get the radius search preference
        String radius = sp.getString("Radius_key", "1000");
        String option = intent1.getStringExtra("search_key");
        Intent intent2 = null;
        lat = sp.getString("lat","0");
        lng = sp.getString("lng","0");
        String search_url = null;

        switch (option) {
            case "name":
// name search
                String search_param = intent1.getStringExtra("search_param");
                search_param = search_param.replace(" ", "%20");
                search_url = String.format(NAME_SEARCH_URL, lat, lng, radius, search_param);
                intent2 = new Intent(NAME_ACTION);
                break;
            case "radius":
                // radius search
                search_url = String.format(RADIUS_SEARCH_URL, lat, lng, radius);
                intent2 = new Intent(RADIUS_ACTION);
                break;
        }
        try {
            search_web(search_url); // call web search
        } catch (IOException e) {
            error = getString(R.string.error);
            e.printStackTrace();
        } catch (JSONException e) {
            error = getString(R.string.error);
            e.printStackTrace();
        }


        //we sent broadcast

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent2);
    }

    // web search method-------------------------------------------------
    public void  search_web(String search_url) throws IOException, JSONException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();


        URL url = new URL(search_url);
        connection = (HttpURLConnection) url.openConnection();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Toast.makeText(this, R.string.conection_error, Toast.LENGTH_SHORT).show();
        }
// get data from api to reader
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        while (line != null) {
            builder.append(line);
            line = reader.readLine();
        }
        // strings for reading data from jason
        String RESULTS = "results", GEO = "geometry", LOC = "location", LAT = "lat", LNG = "lng", ID = "id", NAME = "name", ADDRESS = "vicinity",
                RATING = "rating", PHOTO = "photo_reference";
// create jason object and json array
        JSONObject root = new JSONObject(builder.toString());
        JSONArray list = root.getJSONArray(RESULTS);

        //delete all data before adding new
        helper.delete_All(DATA.RESULT);


 // for every result (every array member) get data relevant to location object and sent new loacation to DB
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);

            // get lat and lng for every object

            JSONObject geometry = object.getJSONObject(GEO);
            JSONObject location = geometry.getJSONObject(LOC);
            double lat = Double.parseDouble(location.getString(LAT));
            double lng = Double.parseDouble(location.getString(LNG));
            // get the rest of the params
            String api_id = object.getString(ID);
            String name = object.getString(NAME);
            String address = object.getString(ADDRESS);
          //  float rating = Float.parseFloat(object.getString(RATING));


            // getting the bitmap
            String photo_reference=null;
            Bitmap pic_bitmap = null;
            if(object.isNull("photos")!=true){
                JSONArray photos_array = object.getJSONArray("photos");
                if (photos_array.isNull(0)!=true){
                    photo_reference = object.getJSONArray("photos").getJSONObject(0).getString(PHOTO);
                    String photo_url = String.format(PICTURE_SEARCH_URL, photo_reference);

                    URL pic_url = new URL(photo_url);
                    connection = (HttpURLConnection) pic_url.openConnection();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, R.string.conection_error, Toast.LENGTH_SHORT).show();
                    }
                    // decode the byte stream from the internet into a Bitmap object
                    pic_bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }
            }

            //insert to data
            //String l_API_id, String l_name, String l_address, double l_lat, double l_lng, Bitmap l_pic, float l_rating
            helper.add_location(new com.diana.radius.Location(api_id, name, address, lat, lng, pic_bitmap, 0), DATA.RESULT);
        }

        return ;

    }



    @Override
    public void onDestroy() {
        // check if we caught error - if true toast error
        super.onDestroy();
        if (error != null)
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }


}
