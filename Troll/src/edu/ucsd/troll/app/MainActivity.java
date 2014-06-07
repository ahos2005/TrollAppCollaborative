package edu.ucsd.troll.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    //Api varaibles
    private static String url = "http://troll.everythingcoed.com/get/fact";
    private static final String TAG_FACTID = "id";
    private static final String TAG_FACT = "fact";
    public String factString = null;
    
    private static String locationUrl = "http://troll.everythingcoed.com/get/locations";

    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESULT = "result";
    private static final String TAG_LOCATIONS = "locations";
    private static final String TAG_LOCATIONID = "id";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";
    private static final String TAG_TITLE = "location_name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_LASTNAME = "last_name";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_USERTOKEN = "presist_code";

    private static final String TAG_SORT = "sort_by";
    private static final String TAG_SORT_ORDER = "order_by";

    //just added a comment

    
    JSONArray locations = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> locationList;

    JSONArray fact = null;

    //store the get values
    List<NameValuePair> params = new ArrayList<NameValuePair>();

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    
    

    // Session Manager Class
    SessionManager session;
    
    LocationAPIManager locationsStorage;


    APILoginHandler loginManager;

    TextView factTextView;

    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        if (haveNetworkConnection()== false) { 
//        	
//        	MainActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
//        	
//        	}
        
//        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
//        // Check if Internet present
//        if (!cd.isConnectingToInternet()) {
//            // Internet Connection is not present
//            alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
//                "Please connect to working Internet connection", false);
//            // stop executing code by return
//            return;
//        }
        
        locationsStorage = new LocationAPIManager(getApplicationContext());

        factTextView = (TextView) findViewById(R.id.factText);

        params.add(new BasicNameValuePair("api_key", "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N"));

        //get random fact
        new GetRandomFact().execute();

        //if(! locationsStorage.locationsLoaded())
        new GetLocations().execute();


        //factTextView.setText(factString);

       // factTextView.setText(factString);

        final Button switchToMaps = (Button) findViewById(R.id.mapsBtn);
        final Button switchToMenu = (Button) findViewById(R.id.menuBtn);
        final Button switchToProfile = (Button) findViewById(R.id.profileBtn);

        // Set Text/Fonts
        Typeface btn_font = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
        switchToMaps.setTypeface(btn_font);
        switchToMenu.setTypeface(btn_font);
        switchToProfile.setTypeface(btn_font);
        
        // Set Button Shape
        Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        	 switchToMaps.setBackgroundDrawable(round_btn); 
             switchToMenu.setBackgroundDrawable(round_btn); 
             switchToProfile.setBackgroundDrawable(round_btn);
        } else {
        	 switchToMaps.setBackground(round_btn); 
             switchToMenu.setBackground(round_btn); 
             switchToProfile.setBackground(round_btn);
        }


        switchToMaps.setOnClickListener(new View.OnClickListener() {
            @Override

        public void onClick(View view) {
                Intent act2 = new Intent(view.getContext(), MapsActivity.class);
                startActivity(act2);
            }
        });

        switchToMenu.setOnClickListener(new View.OnClickListener() {
           @Override

           public void onClick(View view) {
                Intent act3 = new Intent(view.getContext(), MenuActivity.class);
                startActivity(act3);
           }
        });

        switchToProfile.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent act4 = new Intent(view.getContext(), ProfileActivity.class);
                startActivity(act4);
            }
        });
    }
    
    
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    

    private class GetRandomFact extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            APIServiceHandler sh = new APIServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, APIServiceHandler.GET, params);

            Log.d("Response: ", "=> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Log.d("Response: ", "=> " + jsonStr);
                    String id = jsonObj.getString(TAG_FACTID);
                    //Log.d("ID: ", "=> " + id);
                    String theFact = jsonObj.getString(TAG_FACT);
                    //Log.d("Fact: ", "=> " + theFact);
                    return theFact;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            
            for(int i=0;i<result.length();++i)
            {
            	if(result.charAt(i) == '&' || result.charAt(i) == ';')
            	{
            		factTextView.setText("We all know that Gary is the best!");
            		return;
            	}
            }
            factTextView.setText(result);
        }
    }
    
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetLocations extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

        }

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            APIServiceHandler sh = new APIServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(locationUrl, APIServiceHandler.GET, params);
            Log.d("Response: ", "=> " + jsonStr);
            
 
            if (jsonStr != null) {
               return jsonStr;
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
 
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            
            Log.d("Result: ", "=> " + result);
            
            locationsStorage.createLocationsSession(result);    
        }

    }
}