package edu.ucsd.troll.app;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by shalomabitan on 5/22/14.
 */
public class LoginManager {

    private static String checkLoginUrl = "http://troll.everythingcoed.com/user/login/check";
    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESULT = "result";



    //API Login MAnager

    APILoginHandler loginHandler;
	List<NameValuePair> params = new ArrayList<NameValuePair>();

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
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)

    //Keys for the user interface

    //user id
    public static final String KEY_ID = "id";

    //username
    public static final String KEY_USERNAME = "username";
    //email
    public static final String KEY_EMAIL = "email";
    //first name
    public static final String KEY_FIRSTNAME = "first_name";
    //last name
    public static final String KEY_LASTNAME = "last_name";
    //user_token
    public static final String KEY_USERTOKEN = "user_token";
    //JSON object of user's favorite
    public static final String KEY_FAVORITES = "favorites";
    public static final String KEY_RESPONSE = "response";

    // Constructor
    public LoginManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id, String username, String email, String firstName, String lastName, String userToken){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref

        editor.putString(KEY_ID, id);


        editor.putString(KEY_USERNAME, username);


        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_FIRSTNAME, firstName);
        editor.putString(KEY_LASTNAME, lastName);

        editor.putString(KEY_USERTOKEN, userToken);

        // commit changes
        editor.commit();
    }



    /**
     * Create login session
     * */
    public void createUserFavorites(String favorites){
        // Storing favorites as string for later manipulation
        editor.putString(KEY_FAVORITES, favorites);
        // commit changes
        editor.commit();
    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));


        user.put(KEY_FIRSTNAME, pref.getString(KEY_FIRSTNAME, null));


        user.put(KEY_LASTNAME, pref.getString(KEY_LASTNAME, null));


        user.put(KEY_USERTOKEN, pref.getString(KEY_USERTOKEN, null));



        // return user
        return user;
        
    }




    /**
     * Get stored session's favorites
     * */
    public String getUserFavorites(){
        String favorite = pref.getString(KEY_FAVORITES, null);
        // return user
        return favorite;
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
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
    
    public void refreshInfo() {
    	new RefreshLoginInfo().execute();
    }
    
    /**
     * Refresh Login Information
     * **/
    private class RefreshLoginInfo extends AsyncTask<Void, Void, String>  {
        protected void onPreExecute() {
            super.onPreExecute();
        }
        
        protected String doInBackground(Void... arg0) {
        	// Making a request to url and getting response
			loginHandler = new APILoginHandler();
			Log.d("USER TOKEN", pref.getString(KEY_USERTOKEN, null));
			//Setting up the parameters for the API call
			params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));
			params.add(new BasicNameValuePair(KEY_USERNAME,pref.getString(KEY_USERNAME, null)));
			params.add(new BasicNameValuePair(KEY_USERTOKEN,pref.getString(KEY_USERTOKEN,null)));
		    String jsonStr = loginHandler.checkLoginCall(checkLoginUrl, APILoginHandler.POST, params);
		    
		    //If the user is logged in then put the values given back into the preferences
		    if (jsonStr != null) {
		        try {
		            JSONObject respObj = new JSONObject(jsonStr);
		            Log.d("Response String", "=> " + respObj);
		            
		            String respString = respObj.getString(KEY_RESPONSE);
		            JSONObject respStringObj = new JSONObject(respString);
		            String userKeyString = respStringObj.getString("user");
		            String favKeyString = respStringObj.getString(KEY_FAVORITES);
		            Log.d("FAV: ", "=> " + favKeyString);
		            JSONObject userObj = new JSONObject(userKeyString);
		            Log.d("USER STRING", "=> " + userObj);
		            //JSONObject favObj = new JSONObject(favKeyString);
			            
		            String userId = userObj.getString(KEY_ID);
		            Log.d("id: ", "=> " + userId);
		            //user username
		            String username = userObj.getString(KEY_USERNAME);
		            Log.d("username: ", "=> " + username);
		            //user email
		            String email = userObj.getString(KEY_EMAIL);
		            Log.d("email: ", "=> " + email);
		            //user first name
		            String firstName = userObj.getString(KEY_FIRSTNAME);
		            Log.d("first_name: ", "=> " + firstName);
		            //user last name
		            String lastName = userObj.getString(KEY_LASTNAME);
		            Log.d("last_name: ", "=> " + lastName);
		
		
		           /*String favorites = userObj.getString(KEY_FAVORITES);
		            Log.d("last_name: ", "=> " + favorites);*/
		
		            String presistCode = respStringObj.getString("presist_code");
		            Log.d("User Token: ", "=> " + presistCode);
		            //return just fail otherwise continue
                    String responseResult = respStringObj.getString(TAG_RESULT);
                    Log.d("Response: ", "=> " + responseResult);

                    //return just fail otherwise continue

                    if(responseResult.equals("fail")){
                        return responseResult;
                    }
		
		            // Storing data in preferences
		
		            editor.putString(KEY_ID, userId);
		            editor.putString(KEY_USERNAME, username);
		            editor.putString(KEY_EMAIL, email);
		            editor.putString(KEY_FIRSTNAME, firstName);
		            editor.putString(KEY_LASTNAME, lastName);
		            editor.putString(KEY_FAVORITES, favKeyString);
		            editor.putString(KEY_USERTOKEN, presistCode);
		
		            // commit changes
		            editor.commit();          
		
		        } catch (JSONException e) {
		            e.printStackTrace();
		        }
		    } else {
		        Log.e("ServiceHandler", "Couldn't get any data from the url refreshing");
		    }
		    
		    return null;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
