package edu.ucsd.troll.app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


/**
 * Created by shalomabitan on 5/22/14.
 */
public class LocationAPIManager {

    private static String checkLoginUrl = "http://troll.everythingcoed.com/user/login/check";


    private static String locationUrl = "http://troll.everythingcoed.com/get/locations";

    private static final String KEY_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String KEY_APIKEYNAME= "api_key";
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESULT = "result";
    private static final String KEY_LOCATIONS = "locations";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_TITLE = "location_name";
    private static final String KEY_ADDRESS = "address";



    //API Login MAnager

    APILoginHandler loginHandler;

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserPref";

    // All Shared Preferences Keys
    private static final String IS_LOADED = "LocationsAreLoaded";

    // User name (make variable public to access from outside)

    //Keys for the user interface

    //user id
    public static final String KEY_ID = "id";

    // Constructor
    public LocationAPIManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLocationsSession(String locations){
    	
    	editor.putBoolean(IS_LOADED, true);
        editor.putString(KEY_LOCATIONS, locations);
        // commit changes
        editor.commit();
    }







    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user name
//        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
//
//        // user email id
//        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
//
//
//        user.put(KEY_FIRSTNAME, pref.getString(KEY_FIRSTNAME, null));
//
//
//        user.put(KEY_LASTNAME, pref.getString(KEY_LASTNAME, null));
//
//
//        user.put(KEY_USERTOKEN, pref.getString(KEY_USERTOKEN, null));



        // return user
        return user;
    }




    /**
     * Get stored session data
     * */
    public String getLocations(){
        return pref.getString(KEY_LOCATIONS, null);
    }



    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, ProfileActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean locationsLoaded(){
        return pref.getBoolean(IS_LOADED, false);
    }
}

