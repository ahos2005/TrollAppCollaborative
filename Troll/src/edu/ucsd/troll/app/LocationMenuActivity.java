package edu.ucsd.troll.app;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class will display the menu for
 * a specific location. it will use
 * ListView and AsyncTask to make the appropriate
 * API calls
 */
public class LocationMenuActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get menu JSON
    private static String urlSimple = "http://troll.everythingcoed.com/get/menu";
    private static String urlRating = "http://troll.everythingcoed.com/get/menu/1/sort/rating";
    private static String urlPriceAsc = "http://troll.everythingcoed.com/get/menu/1/sort/price/asc";
    private static String urlPriceDesc = "http://troll.everythingcoed.com/get/menu/1/sort/price/desc";

    // JSON Node names
    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_MENU = "menu";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_RATING = "rating";
    private static final String TAG_VOTES = "total_votes";
    private static final String TAG_SIZES = "sizes";
    private static final String TAG_SIZE = "size_titles";
    private static final String TAG_PRICE = "prices";
    private static final String TAG_MENUID = "menus_id";

    
    private static final String TAG_SORT = "sort_by";
    private static final String TAG_SORT_ORDER = "order_by";
    
    String sortBy = "simple";
    String orderBy = "desc";
    String menu_id = null;
    String finalUrl = null;
    
    String prices = null;
    String sizes = null;

    
    // menu JSONArray
    JSONArray menu = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> menuList;
    
    List<NameValuePair> params = new ArrayList<NameValuePair>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_menu_layout);
                
        params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));
        
        //sets the sorting parameters for the menu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            
        	sortBy = extras.getString(TAG_SORT);
        	orderBy = extras.getString(TAG_SORT_ORDER);
        	menu_id = extras.getString(TAG_MENUID);

            Log.d("Sort_by", sortBy);

            Log.d("menu_id", menu_id);

        }
        
        finalUrl = getFinalUrl(sortBy, orderBy, menu_id);
        
        Log.d("Final URL", finalUrl);

        menuList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
            	String itemId = ((TextView) view.findViewById(R.id.itemId))
                        .getText().toString();
            	String title = ((TextView) view.findViewById(R.id.title))
                        .getText().toString();
                String rating = ((TextView) view.findViewById(R.id.rating))
                        .getText().toString();
                String category = ((TextView) view.findViewById(R.id.category))
                        .getText().toString();
                String description = ((TextView) view.findViewById(R.id.description))
                		.getText().toString();
                String votes = ((TextView) view.findViewById(R.id.total_votes))
                        .getText().toString();
                
                String sizes = ((TextView) view.findViewById(R.id.sizes))
                        .getText().toString();
                
                String price = ((TextView) view.findViewById(R.id.price))
                        .getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        SingleMenuItemActivity.class);
                in.putExtra(TAG_ID, itemId);
                in.putExtra(TAG_TITLE, title);
                in.putExtra(TAG_DESCRIPTION, description);
                in.putExtra(TAG_RATING, rating);
                in.putExtra(TAG_SIZE, category);
                in.putExtra(TAG_VOTES, votes);
                in.putExtra(TAG_SIZE, sizes);
                in.putExtra(TAG_PRICE, price);

                
                startActivity(in);

            }
        });

        // Calling async task to get json
        new GetMenu().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_menu_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
            	 Intent in = new Intent(getApplicationContext(), SortMenuActivity.class);
            	 in.putExtra(TAG_MENUID, menu_id);
                 startActivity(in);
                 finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    

    public String getFinalUrl(String sort, String order, String menu) {
        
    	if(sort.equals("simple")){
    		return urlSimple + "/" + menu ;
    	}else if(sort.equals("rating")){
    		return urlSimple + "/" + menu + "/sort/rating";
    	}else if(sort.equals("price")){
    		return urlSimple + "/" + menu + "/sort/price/" + order;
    	}else if(sort.equals("title")){
    		return urlSimple + "/" + menu + "/sort/title/" + order;
    	}else if(sort.equals("category")){
    		return urlSimple + "/" + menu + "/sort/category/" + order;
    	}
    	
		return null;
    }

	  
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetMenu extends AsyncTask<Void, Void, Void> {

        HashMap<String, String> singleMenu = new HashMap<String, String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LocationMenuActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            APIServiceHandler sh = new APIServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(finalUrl, APIServiceHandler.GET, params);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    Log.d("Response: ", "=> " + jsonObj);

                    // Getting JSON Array node
                    menu = jsonObj.getJSONArray(TAG_MENU);

                    // looping through All Contacts
                    for (int i = 0; i < menu.length(); i++) {
                        JSONObject c = menu.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        Log.d("ID: ", "=> " + id);
                        String title = c.getString(TAG_TITLE);
                        Log.d("TITLE: ", "=> " + title);
                        String desc = c.getString(TAG_DESCRIPTION);
                        Log.d("DESCRIPTION: ", "=> " + desc);
                        String category = c.getString(TAG_CATEGORY);
                        Log.d("CATEGORY: ", "=> " + category);
                        String rating = c.getString(TAG_RATING);
                        Log.d("RATING: ", "=> " + rating);
                        
                        String votes = c.getString(TAG_VOTES);
                        Log.d("RATING: ", "=> " + votes);


                        String size = c.getString(TAG_SIZE);
                         Log.d("SIZE: ", "=> " + size);
                        String price = c.getString(TAG_PRICE);
                        Log.d("PRICE: ", "=> " + price);
                        //String office = phone.getString(TAG_PHONE_OFFICE);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        //contact.put(TAG_ID, id);
                        contact.put(TAG_TITLE, title);
                        contact.put(TAG_CATEGORY, category);
                        contact.put(TAG_RATING, rating);
                        contact.put(TAG_DESCRIPTION, desc);
                        contact.put(TAG_ID, id);
                        contact.put(TAG_SIZE, size);
                        contact.put(TAG_PRICE, price);
                        contact.put(TAG_VOTES, votes);

                        // adding contact to contact list
                        menuList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    LocationMenuActivity.this, menuList,
                    R.layout.location_menu_list, new String[] {TAG_ID, TAG_TITLE,TAG_DESCRIPTION, TAG_CATEGORY,
                    TAG_RATING, TAG_VOTES}, new int[] {R.id.itemId, R.id.title,R.id.description,
                    R.id.category, R.id.rating, R.id.total_votes});
            setListAdapter(adapter);
        }

    }

}