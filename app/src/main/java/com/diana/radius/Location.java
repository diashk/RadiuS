package com.diana.radius;

import android.graphics.Bitmap;

/**
 * location method
 */
public class Location {

    private long l_id;
    private String l_name, l_address, l_API_id;
    private double  l_lat, l_lng;
    private Bitmap l_pic;
    private float l_rating;


    //constructors

// Location  with id
    public Location(long l_id,String l_API_id, String l_name, String l_address, double l_lat, double l_lng, Bitmap l_pic, float l_rating) {
        this.l_id = l_id;
        this.l_API_id = l_API_id;
        this.l_name = l_name;
        this.l_address = l_address;
        this.l_lat = l_lat;
        this.l_lng = l_lng;
        this.l_pic = l_pic;
        this.l_rating = l_rating;

    }

// Location no id

    public Location(String l_API_id, String l_name, String l_address, double l_lat, double l_lng, Bitmap l_pic, float l_rating) {
        this.l_API_id = l_API_id;
        this.l_name = l_name;
        this.l_address = l_address;
        this.l_lat = l_lat;
        this.l_lng = l_lng;
        this.l_pic = l_pic;
        this.l_rating = l_rating;
    }


    //getters and setters


    public long getL_id() {
        return l_id;
    }

    public void setL_id(long l_id) {
        this.l_id = l_id;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getL_address() {
        return l_address;
    }

    public void setL_address(String l_address) {
        this.l_address = l_address;
    }

    public String getL_API_id() {
        return l_API_id;
    }

    public void setL_API_id(String l_API_id) {
        this.l_API_id = l_API_id;
    }

    public double getL_lat() {
        return l_lat;
    }

    public void setL_lat(double l_lat) {
        this.l_lat = l_lat;
    }

    public double getL_lng() {
        return l_lng;
    }

    public void setL_lng(double l_lng) {
        this.l_lng = l_lng;
    }

    public Bitmap getL_pic() {
        return l_pic;
    }

    public void setL_pic(Bitmap l_pic) {
        this.l_pic = l_pic;
    }

    public float getL_rating() {
        return l_rating;
    }

    public void setL_rating(float l_rating) {
        this.l_rating = l_rating;
    }

    //to string
    @Override
    public String toString() {
        return
                "Establishment's name :" + l_name + '\'' +
                "Address: " + l_address + '\'' ;
    }
}
