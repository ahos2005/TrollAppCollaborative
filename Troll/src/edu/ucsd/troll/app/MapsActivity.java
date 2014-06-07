package edu.ucsd.troll.app;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

import org.apache.http.NameValuePair;

import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class MapsActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        OnMarkerClickListener{

    private static String locationUrl = "http://troll.everythingcoed.com/get/locations";
    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESULT = "result";
    private static final String TAG_LOCATIONS = "locations";
    private static final String TAG_ID = "id";
    private static final String TAG_LOCATIONSID = "id";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";
    private static final String TAG_TITLE = "location_name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_LASTNAME = "last_name";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_USERTOKEN = "presist_code";
    
    private static final String TAG_MENUID = "menus_id";

    private static final String TAG_SORT = "sort_by";
    private static final String TAG_SORT_ORDER = "order_by";
    
    //for saving data through instances
    static final String CURRENT_ZOOM = "currentZoom";
    static final String CURRENT_LAT = "currentLat";
    static final String CURRENT_TILT = "currentTilt";
    static final String CURRENT_LONG = "currentLong";
    static final String DEST_LAT = "destinationLat";
    static final String DEST_LONG = "destinationLong";
    static final String CURRENT_NAV_STATUS = "currentNavStatus";
    
	protected GoogleMap map;
    protected GMapDirection mapDirections;
	private LatLng myLatLng, destinationLatLng;
	private Location lastKnownLocation;
	private boolean mapMarkerClicked = false, atStartOfMap = true, navigationStatus;
	private Button mNearestLocationButton;
	private String locationAPIResult;
	private String locationString;
	private String locationStringId;
	private int    location_pic;
	private float currentMapZoom;
	private float currentMapTilt;
	private Marker selectedMarker;
    
    LocationAPIManager locationsStorage;
    JSONArray locations = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> locationList;
    
    //paramete list for api calls
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    protected LocationManager locationManager;
    LatLng warrenLatLng;
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //providing up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        //add the api key for the call
        params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));
        locationList = new ArrayList<HashMap<String, String>>();
        setContentView(R.layout.maps_layout);
        
        currentMapZoom = 15;
        currentMapTilt = 30;
        navigationStatus = false;
        myLatLng = new LatLng(32.881271, -117.2389000);
        destinationLatLng = new LatLng(32.881271, -117.2389000);
        if (savedInstanceState != null) {
        	System.gc();
        	// Restore value of members from saved state
            currentMapTilt = savedInstanceState.getFloat(CURRENT_TILT);
        	currentMapZoom = savedInstanceState.getFloat(CURRENT_ZOOM);
            myLatLng = new LatLng(savedInstanceState.getDouble(CURRENT_LAT),
		              savedInstanceState.getDouble(CURRENT_LONG));
            destinationLatLng = new LatLng(savedInstanceState.getDouble(DEST_LAT),
		              savedInstanceState.getDouble(DEST_LONG));
            navigationStatus = savedInstanceState.getBoolean(CURRENT_NAV_STATUS);
        }
        //providing up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        //add the api key for the call
        params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));
        
        locationsStorage = new LocationAPIManager(getApplicationContext());
        locationAPIResult = locationsStorage.getLocations();
        
        locationList = new ArrayList<HashMap<String, String>>();
        
        setUpLocations(locationAPIResult);
        
        Log.d("setUpMapIfNeeded", "activated");
        
        setUpMapIfNeeded();
        
        Log.d("setUpMapIfNeeded", "passed");
        
        Log.d("setOnMarkerClickListener", "activated");
        
        map.setOnMarkerClickListener(this);
        
        Log.d("setOnMarkerClickListener", "passed");
        
		/* ADDED IN THIS REWRITE */
		//Also, probably need to disable button when invisible
		mNearestLocationButton = (Button)findViewById(R.id.nearest_loc_butt);
		mNearestLocationButton.setVisibility(View.VISIBLE);
		mNearestLocationButton.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				mNearestLocationButton.setVisibility(View.GONE);
		    	Toast.makeText(getApplicationContext(), "You've just been trolled", Toast.LENGTH_SHORT).show();
		    	
				setUpLocations(locationAPIResult);
			 }
			 });

		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy); 
        }

    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            	Log.d("onLocationChanged", "Joel - activated");

            	/* Brute force map cleanup.  */
   				map.clear();			// clear map of polylines but consequentially markers
		    	lastKnownLocation = (Location) locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		    	if(lastKnownLocation == null)
   	   		    	lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
   		    	if(lastKnownLocation != null)//**AHMED
   		    	{
   		    		Log.d("Returned LOC Is", "=>" + lastKnownLocation);
   		    		Log.d("Lat", "=>" + lastKnownLocation.getLatitude());
   		    		Log.d("Lng", "=>" + lastKnownLocation.getLongitude());
   		    		myLatLng = new LatLng((float) lastKnownLocation.getLatitude(), (float) lastKnownLocation.getLongitude());
   		    	}
   		    	else
   		    		Toast.makeText(getApplicationContext(), "GPS/Network is off", Toast.LENGTH_SHORT).show();
		    	myLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
   				setUpMapIfNeeded();		// markers.
            }
            @Override
            public void onProviderDisabled(String provider) {
            	Log.d("onProviderDisabled", "Joel - disabled");
            }
            @Override
            public void onProviderEnabled(String provider) {
            	Log.d("onProviderEnabled", "Joel - enabled");
            }
            @Override
            public void onStatusChanged(String provider, int status,
                    Bundle extras) {
            	Log.d("onStatusChanged", "Joel - changed");
            }           
        });
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putFloat(CURRENT_ZOOM, currentMapZoom);
        savedInstanceState.putFloat(CURRENT_TILT, currentMapTilt);
        savedInstanceState.putDouble(CURRENT_LAT, myLatLng.latitude);
        savedInstanceState.putDouble(CURRENT_LONG, myLatLng.longitude);
        savedInstanceState.putDouble(DEST_LAT, destinationLatLng.latitude);
        savedInstanceState.putDouble(DEST_LONG, destinationLatLng.longitude);
        savedInstanceState.putBoolean(CURRENT_NAV_STATUS, navigationStatus);
    }

    /**
     * All screens in your app that are not the main entrance 
     * to your app (the "home" screen)should offer the user 
     * a way to navigate to the logical parent screen in the 
     * app's hierarchy by pressing the Up button in the action bar.  
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   	private void setUpLocations(String locationsArray) {
    	
    	Log.d("Location String", locationsArray);
    	try {
            JSONObject jsonObj = new JSONObject(locationsArray);
            // Getting JSON Array node
            locations = jsonObj.getJSONArray(TAG_LOCATIONS);
            Log.d("individual location: ", "=> " + locations);
            // looping through All Contacts
            for (int i = 0; i < locations.length(); i++) {
            	JSONObject c = locations.getJSONObject(i);
                Log.d("individual location: ", "=> " + c);
                String id = c.getString(TAG_LOCATIONSID);
                Log.d("id: ", "=> " + id);
                String lat = c.getString(TAG_LAT);
                Log.d("lat: ", "=> " + lat);
                String lng = c.getString(TAG_LNG);
                Log.d("lng: ", "=> " + lng);
                String address = c.getString(TAG_ADDRESS);
                Log.d("address: ", "=> " + address);
                String title = c.getString(TAG_TITLE);
                Log.d("title: ", "=> " + title);


                // tmp hashmap for single contact
                HashMap<String, String> locationHash = new HashMap<String, String>();
                
                Log.d("hash map: ", "=> " + "become active");

                // adding each child node to HashMap key => value
                locationHash.put(TAG_LOCATIONSID, id);
                Log.d("hash map: ", "=> " + "put id");
                
                locationHash.put(TAG_LAT, lat);
                Log.d("hash map: ", "=> " + "put lat");

                locationHash.put(TAG_LNG, lng);
                Log.d("hash map: ", "=> " + "put lng");

                locationHash.put(TAG_TITLE, title);
                Log.d("hash map: ", "=> " + "put title");

                // adding locations to locations list
                locationList.add(locationHash);
                
                Log.d("list: ", "=> " + "added");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }  	
    }
   	
    /* FOR INDIVIDUAL SELECTIONS */
   	@SuppressLint("NewApi")
	@Override
   	public boolean onMarkerClick(Marker marker){
   		
   		
   		Log.d("onMarkerClick", "activated");
		selectedMarker = marker;
		locationString = marker.getTitle();
		locationStringId = marker.getSnippet();
		
		Log.d("the marker ID", marker.getSnippet());
		location_pic = R.drawable.fairbanks_center1;
	
   		/* CREATING DIALOGUE BOX TO APPEAR ON CLICK OF BUTTON */
           final Dialog dialog = new Dialog(this);
   		dialog.setContentView(R.layout.dialogue_menu_navigate);
   		dialog.setTitle(locationString);
   		/*	SETTING IMAGE , IN THIS INSTANCE FAIRBANKS NEAR CENTER */
   		// set the custom dialog components - text, image and button
   		ImageView image = (ImageView) dialog.findViewById(R.id.image);
   		image.setImageResource(location_pic);
   		/*	SETTING BUTTON WITHING DIALOGUE */
   		Button dialogButtonNavigate = (Button) dialog.findViewById(R.id.navigateToButton);
   		Button dialogButtonMenu = (Button) dialog.findViewById(R.id.seeMenuButton);
   		
   		Typeface btn_font = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
   		dialogButtonNavigate.setTypeface(btn_font);
   		dialogButtonMenu.setTypeface(btn_font);
   		Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);
   		
   		int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        	dialogButtonNavigate.setBackgroundDrawable(round_btn); 
        	dialogButtonMenu.setBackgroundDrawable(round_btn);
        } else {
        	dialogButtonNavigate.setBackground(round_btn); 
        	 dialogButtonMenu.setBackground(round_btn); 
        }
   		
   		// if button is clicked, close the custom dialog
   		dialogButtonMenu.setOnClickListener(new View.OnClickListener() {
   			@Override
   			public void onClick(View v) {
                Intent act8 = new Intent(v.getContext(), LocationMenuActivity.class);
                act8.putExtra(TAG_MENUID, locationStringId);
                act8.putExtra(TAG_SORT,"simple");
                act8.putExtra(TAG_SORT_ORDER,"asc");
                startActivity(act8);
  				dialog.dismiss();
   			}
   		});
   		
   		dialogButtonNavigate.setOnClickListener(new View.OnClickListener() {
   			@Override
   			public void onClick(View v) {
   				map.clear();
   		    	lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
   		    	if(lastKnownLocation == null)
   	   		    	lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
   		    	if(lastKnownLocation != null)//**AHMED
   		    	{
   	   		   		navigationStatus = true;
   	   				if(selectedMarker != null)
   	   					destinationLatLng = selectedMarker.getPosition();
   	 				mapMarkerClicked = true;
   		    		Log.d("Returned LOC Is", "=>" + lastKnownLocation);
   		    		Log.d("Lat", "=>" + lastKnownLocation.getLatitude());
   		    		Log.d("Lng", "=>" + lastKnownLocation.getLongitude());
   		    		myLatLng = new LatLng((float) lastKnownLocation.getLatitude(), (float) lastKnownLocation.getLongitude());
   		    	}
   		    	else
   		    		Toast.makeText(getApplicationContext(), "GPS/Network is off", Toast.LENGTH_SHORT).show();
		    	setUpMapIfNeeded();
        		mNearestLocationButton.setVisibility(View.VISIBLE);            	
  				dialog.dismiss();
   			}
   		});

   		dialog.show();
   		Log.d("onMarkerClick", "passed");
   		return true;
   	}

   	@SuppressLint("NewApi")
	private void setUpMapIfNeeded() {
	 	// Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    }
	    mapDirections = new GMapDirection();
        if(map != null)
        {
	        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        if(atStartOfMap == true){
	        	map.setMyLocationEnabled(true);
	        	atStartOfMap = false;
		        Log.d("The Location List Array:", "=>" + locationList);
		        LatLng ucsdLatLng = new LatLng(32.881271, -117.2389000);//879271,2289000
		        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsdLatLng, currentMapZoom));
				if(map.getUiSettings().isMyLocationButtonEnabled()){
        	CameraPosition cameraPosition = new CameraPosition.Builder()
        			.target(ucsdLatLng) // Sets the center of the map to
        	        .zoom(currentMapZoom)                   // Sets the zoom
        	        .bearing(0)//(float) myBearing) // Sets the orientation of the camera to east
        	        .tilt(currentMapTilt)//(float)myAngle)    // Sets the tilt of the camera to 30 degrees
        	        .build();    // Creates a CameraPosition from the builder
        	    	map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
        }

	      //this prints out the markers on the map
	      for (HashMap<String, String> hashMap : locationList) {
	    	  Log.d("The Location List Array:", "=>" + hashMap.get(TAG_ID));
	          map.addMarker(new MarkerOptions()
	              .position(new LatLng(Double.parseDouble(hashMap.get(TAG_LAT)) ,        
	                  Double.parseDouble(hashMap.get(TAG_LNG))))
	              .title(hashMap.get(TAG_TITLE))
	              .snippet(hashMap.get(TAG_ID))
	              .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_coffee_icon)));
	      }
	    }
        if(navigationStatus){
        	System.gc();
			/*for directions again*/
			/* WE REDRAW THE LINE FOR DIRECTIONS */
		    	Document doc = mapDirections.getDocument(myLatLng, destinationLatLng, mapDirections.MODE_WALKING);
				ArrayList<LatLng> directionPoint = mapDirections.getDirection(doc);
				PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
				for(int i=0, _i=directionPoint.size();i<_i;++i)
					rectLine.add(directionPoint.get(i));
				map.addPolyline(rectLine);
	    }
	}

    //Called when the Activity is no longer visible at all.
    @Override
    public void onStop() {
        // After disconnect() is called, the client is considered "dead".
        super.onStop();
        Log.d("onStop()", "in onStop() method");
    }
    /*
     * Called when the Activity is going into the background.
     * Parts of the UI may be visible, but the Activity is inactive.
     */
    @Override
    public void onPause() {
        super.onPause();
        currentMapZoom = map.getCameraPosition().zoom;
        currentMapTilt = map.getCameraPosition().tilt;
        Log.d("onPause()", "in onPause() method");
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart()", "in onStart() method");
    }
    /*
     * Called when the system detects that this Activity is now visible.
     */
    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Log.d("onResume()", "in onResume() method");
    }

    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.resolved));
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));
                    break;
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(LocationUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));
               break;
        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
