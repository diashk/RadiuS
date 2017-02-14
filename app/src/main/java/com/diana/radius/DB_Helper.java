package com.diana.radius;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.diana.radius.Enums.DATA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * This class handles the data base and its methods
 */

public class DB_Helper extends SQLiteOpenHelper {

    // we create final names for the data base table and columns names.
    private  final String TABLE_RESULTS = "results", TABLE_FAVORITES ="favorites", COL_ID="id", COL_NAME="name", COL_ADDRESS ="address",COL_API_ID ="api_id",COL_LAT = "lat",
            COL_LONG="long",COL_PIC="pic", COL_RATING="rating";
    private DATA data;
    
    // constructor
    public DB_Helper(Context context, DATA data) {
        super(context, "locations_DB", null, 1);
        this.data =data;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
// we create results data base
          String  table= String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL, %s BLOB, %s REAL )"
                    , TABLE_RESULTS, COL_ID, COL_NAME, COL_ADDRESS, COL_API_ID, COL_LAT, COL_LONG, COL_PIC, COL_RATING);
        //activate data bases
        sqLiteDatabase.execSQL(table);

// we create favorits data base
            table=String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL, %s BLOB, %s REAL )"
                    ,TABLE_FAVORITES,COL_ID, COL_NAME, COL_ADDRESS, COL_API_ID, COL_LAT,COL_LONG, COL_PIC,COL_RATING);
//activate data bases
        sqLiteDatabase.execSQL(table);
    }



    // The commands methods.

    public ArrayList<Location> getAllLocations(DATA dat) {

        ArrayList<Location> locations_List = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String table=TABLE_RESULTS;
        if (dat.equals(DATA.FAVORITE)){ table=TABLE_FAVORITES;}

        Cursor cursor = db.query(table, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(COL_ID));
            // get values from data base
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            String address = cursor.getString(cursor.getColumnIndex(COL_ADDRESS));
            String api_id = cursor.getString(cursor.getColumnIndex(COL_API_ID));
            double lat =cursor.getDouble(cursor.getColumnIndex(COL_LAT));
            double lng =cursor.getDouble(cursor.getColumnIndex(COL_LONG));
            byte[] pic= cursor.getBlob(cursor.getColumnIndex(COL_PIC));
            Bitmap pic_bitmap = get_Bitmap(pic);
            float rating = cursor.getFloat(cursor.getColumnIndex(COL_RATING));
            locations_List.add(new Location(id,api_id, name, address,lat,lng,pic_bitmap,rating));
        }

        db.close();
        return locations_List;
    }

    public void add_location(Location loc, DATA dat) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, loc.getL_name());
        values.put(COL_ADDRESS, loc.getL_address());
        values.put(COL_API_ID, loc.getL_API_id());
        values.put(COL_LAT, loc.getL_lat());
        values.put(COL_LONG, loc.getL_lng());
        Bitmap bitmap =loc.getL_pic(); // get bitmap  from object
        values.put(COL_PIC,get_blob(bitmap)); // insert byte array using get_blob method
        values.put(COL_RATING, loc.getL_rating());

        SQLiteDatabase db = getWritableDatabase();
        // check to which DB to add
        String table=TABLE_RESULTS;
        if (dat==DATA.FAVORITE){ table=TABLE_FAVORITES;}
        db.insert(table, null, values);
        db.close();

    }

    // delete only from favorites
    public void delete_location (Location loc) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_FAVORITES, COL_ID + "=" + loc.getL_id(), null);
        db.close();
    }

    // delete all from favorites
    public void delete_All(DATA dat) {
        SQLiteDatabase db = getWritableDatabase();
        String table=TABLE_RESULTS;
        if (dat.equals(DATA.FAVORITE)){ table=TABLE_FAVORITES;}
        db.delete(table,null,null);
        db.close();
    }
// bitmap to blob method
    private  byte [] get_blob (Bitmap bitmap){
        if (bitmap==null){return  null;}
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte [] blob = stream.toByteArray();
        return blob;
    }

    // blob to bitmap method
    private Bitmap get_Bitmap (byte [] blob){
        if (blob==null){return null;}
        ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
        return BitmapFactory.decodeStream(inputStream);

    }

}
