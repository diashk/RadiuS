package com.diana.radius;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.diana.radius.Enums.DATA;
import com.diana.radius.Enums.DIALOG;
import com.diana.radius.services.Location_service;
import com.diana.radius.settings.Settings_Activity;

public class MainActivity extends AppCompatActivity  implements AlertDialog.OnClickListener, Location_Item_Adapter.On_location_picked  {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionPagerAdapter mSectionsPagerAdapter;
    private  double LAT, LNG;
    public  Intent service_intent;
    public Location location;
    public DB_Helper helper;
    public DIALOG DIALOG_TYPE;
    public Search_frag fragment_s;
    public Fav_frag fragment_f;
    public Map_frag fragment_m;
    public static boolean is_tablet;
    public static int orientation;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

// on create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        orientation=this.getResources().getConfiguration().orientation;
        if(orientation== Configuration.ORIENTATION_LANDSCAPE){
         if(getResources().getBoolean(R.bool.is_tablet))
        {
            is_tablet=true;
        }}


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);





        // check if we have permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION },888);}
            // start location listener service
            service_intent = new Intent(this,Location_service.class);
            startService(service_intent);
        } else {
            // start location listener service
            service_intent = new Intent(this,Location_service.class);
            startService(service_intent);}

        // start location listener service
        service_intent = new Intent(this,Location_service.class);
        startService(service_intent);

        helper = new DB_Helper(this, DATA.RESULT);

        return;
    }

    public boolean is_tablet() {
        return is_tablet;
    }

    public void setIs_tablet(boolean is_tablet) {
        this.is_tablet = is_tablet;
    }

    // menu methods -------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent in = new Intent(this, Settings_Activity.class);
            startActivity(in);
            return true;
        }
        if (id == R.id.action_delete_all) {
            DIALOG_TYPE =DIALOG.DELETE_ALL;
            dialogBuilder();
            return true;
        }

        if (id == R.id.action_exit) {
            DIALOG_TYPE=DIALOG.EXIT_APP;
            dialogBuilder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//pager adapter methods --------------------------------------------------------------

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionPagerAdapter extends FragmentPagerAdapter {
         private FragmentManager fm;


        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (fragment_f==null){
                fragment_f =  new Fav_frag();
            }
            if (fragment_m==null){
                fragment_m=new Map_frag();
            }
            if(fragment_s==null){
                fragment_s =  new Search_frag();
            }

            switch (position) {
                case 0:
                    if (is_tablet==true){ // check if tablet or not
                        return new Tablet_Frag();
                    }else {
                    return fragment_s;}
                case 1:
                    return fragment_f;
                    case 2:
                        return fragment_m;
                default:
                    return null;
            }
        }

        @Override


        public int getCount() {
            if( is_tablet==true){  // check if tablet or not
                return 2;
            }else {
            return 3;}
        }

        @Override
        public CharSequence getPageTitle(int position) {


            switch (position) {
                case 0:
                    return getString(R.string.search);

                case 1:
                    return  getString( R.string.favorites) ;

                case 2:
                    return  getString( R.string.on_map );


            }
            return null;
        }
    }
// on click implementation

    @Override
    public Location onClick_locPick(Location location,DATA data) {
        if (fragment_m==null){ // if fragment wasnt't created- create
            fragment_m = (Map_frag) mSectionsPagerAdapter.getItem(2);
        }
        fragment_m.set_dest_loc(location);

        if (is_tablet==false){ mViewPager.setCurrentItem(2);}else {  // check if tablet or not and
// if tablet and calles from favorites  - go to first tab
            if (data.equals(DATA.FAVORITE)){
                mViewPager.setCurrentItem(0);
            }
        }

        return null;
    }
// on long click implementation
    @Override
    public Location onLongClick_locPick(Location location) {
        this.location = location;
        DIALOG_TYPE= DIALOG.SAVE_OR_SHARE;
        dialogBuilder();
        return null;
    }


// dialogs
    private AlertDialog dialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (DIALOG_TYPE){

            case SAVE_OR_SHARE:
                builder.setPositiveButton(R.string.save_or_share_positive, this);
                builder.setNegativeButton(R.string.save_or_share_negative, this);

                builder.setMessage(R.string.save_or_share_msg);
                builder.setTitle(R.string.save_or_share_title);
                break;

            case DELETE_ALL:
                builder.setPositiveButton(R.string.delete_all_positive, this);

                builder.setMessage(R.string.delete_all_msg);
                builder.setTitle(R.string.delete_all_title);

                break;


            case EXIT_APP:
                builder.setPositiveButton(R.string.exit_app_positive, this);

                builder.setMessage(R.string.exit_app_msg);
                builder.setTitle(R.string.exit_app_title);
                break;
        }

        builder.setNeutralButton(R.string.string_cancel,this);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int choice) {

        switch (choice) {
            case (DialogInterface.BUTTON_POSITIVE):
                switch (DIALOG_TYPE){
                    case DELETE_ALL:
                        helper.delete_All(DATA.FAVORITE);
                        fragment_f.load_list();
                        return;

                    case EXIT_APP:
                        stopService(service_intent);
                        finish();
                        return;
                    case SAVE_OR_SHARE:
                      share_location();
                    return;
                }

            case (DialogInterface.BUTTON_NEGATIVE):// save

                helper.add_location(location, DATA.FAVORITE);
                if (fragment_f==null){
                    fragment_f = (Fav_frag) mSectionsPagerAdapter.getItem(1);
                }
                fragment_f.load_list();
                mViewPager.setCurrentItem(1);
                return;

            case (DialogInterface.BUTTON_NEUTRAL): //cancel
                Toast.makeText(this, R.string.string_canceled, Toast.LENGTH_SHORT).show();
                return;
        }
    }


// share location methods
    private void share_location() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,location.toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
