package edu.ucsd.troll.app;

/**
 * Created by shalomabitan on 5/22/14.
 */

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;



public class MenuActivity extends ListActivity {
	
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
    
    LocationAPIManager locationsStorage;
    JSONArray locations = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> locationList;
    
    ArrayList<HashMap<String, String>> menuList;

    
    String locationAPIResult;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
  
      
        
       locationsStorage = new LocationAPIManager(getApplicationContext());
        
       locationAPIResult = locationsStorage.getLocations();
        
       locationList = new ArrayList<HashMap<String, String>>();
        
       setUpLocations(locationAPIResult);
        
        
       ListView lv = getListView();

        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String title = ((TextView) view.findViewById(R.id.title))
                        .getText().toString();
                String rating = ((TextView) view.findViewById(R.id.rating))
                        .getText().toString();
                String category = ((TextView) view.findViewById(R.id.category))
                        .getText().toString();
                
                String menuID = ((TextView) view.findViewById(R.id.menu_id))
                        .getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        LocationMenuActivity.class);
                in.putExtra(TAG_TITLE, title);
                in.putExtra(TAG_ADDRESS, rating);
                in.putExtra(TAG_TITLE, category);
                in.putExtra(TAG_MENUID, menuID);
                in.putExtra(TAG_SORT,"simple");
                in.putExtra(TAG_SORT_ORDER,"asc");
                startActivity(in);

            }
        });
        
        
        ListAdapter adapter = new SimpleAdapter(
                MenuActivity.this, locationList,
                R.layout.menu_list, new String[] { TAG_TITLE, TAG_ADDRESS,
                TAG_LAT, TAG_MENUID }, new int[] { R.id.title,
                R.id.category, R.id.rating, R.id.menu_id });

        	setListAdapter(adapter);
    }
		
    private void setUpLocations(String locationsArray) {
	   
	   Log.d("Location String", locationsArray);

		try{
			JSONObject jsonObj= new JSONObject(locationsArray);
    
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
                
                String menuID = c.getString(TAG_MENUID);
                Log.d("menu id: ", "=> " + menuID);

//                // Phone node is JSON Object
//                JSONObject phone = c.getJSONObject(TAG_PHONE);
//                String mobile = phone.getString(TAG_PHONE_MOBILE);
//                String home = phone.getString(TAG_PHONE_HOME);
//                String office = phone.getString(TAG_PHONE_OFFICE);

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
                
                locationHash.put(TAG_MENUID, menuID);
                Log.d("hash map: ", "=> " + "put menu");


                // adding locations to locations list
                locationList.add(locationHash);
                
                Log.d("list: ", "=> " + "added");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    	
    }
   	
}