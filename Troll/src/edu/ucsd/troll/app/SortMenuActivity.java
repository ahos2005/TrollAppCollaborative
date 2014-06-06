package edu.ucsd.troll.app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import edu.ucsd.troll.app.R;

/**
 * Created by shalomabitan on 5/22/14.
 */
public class SortMenuActivity extends Activity {


    // JSON Node names
    private static final String TAG_CONTACTS = "menu";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_RATING = "rating";
    private static final String TAG_SIZES = "sizes";
    private static final String TAG_SIZE = "size";
    private static final String TAG_PRICE = "price";
    private static final String TAG_MENUID = "menus_id";
//private static final String TAG_PHONE_OFFICE = "office";
    private static final String TAG_SORT = "sort_by";
    private static final String TAG_SORT_ORDER = "order_by";



    List<NameValuePair> params = new ArrayList<NameValuePair>();

    String sortBy = "simple";
    String orderBy = "desc";
    String menu_id = null;
    	

    //Edittext
    EditText usernameTextBox, passwordTextBox;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
 
            setContentView(R.layout.sort_menu_layout);
            
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	menu_id = extras.getString(TAG_MENUID);
            }

            
            final Button switchToRating = (Button) findViewById(R.id.ratingBtn);

            final Button switchToPriceAsc = (Button) findViewById(R.id.priceBtnAsc);
            final Button switchToPriceDesc = (Button) findViewById(R.id.priceBtnDesc);
            final Button switchToCategoryAsc = (Button) findViewById(R.id.categoryBtnAsc);
            final Button switchToCategoryDesc = (Button) findViewById(R.id.categoryBtnDesc);
            final Button switchToTitleAsc = (Button) findViewById(R.id.titleBtnAsc);
            final Button switchToTitleDesc = (Button) findViewById(R.id.titleBtnDesc);
            final Button switchToClear = (Button) findViewById(R.id.clearBtn);


            switchToRating.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	Intent i = new Intent(getApplicationContext(), LocationMenuActivity.class);
                	i.putExtra(TAG_MENUID, menu_id);
                    i.putExtra(TAG_SORT,"rating");
                    i.putExtra(TAG_SORT_ORDER,"asc");
                    startActivity(i);
                    finish();
                }
            });
            
            switchToPriceAsc.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	Intent i = new Intent(getApplicationContext(), LocationMenuActivity.class);
                	i.putExtra(TAG_MENUID, menu_id);
                    i.putExtra(TAG_SORT,"price");
                    i.putExtra(TAG_SORT_ORDER,"asc");
                    startActivity(i);
                    finish();
                }
            });
            
            switchToPriceDesc.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	Intent i = new Intent(getApplicationContext(), LocationMenuActivity.class);
                	i.putExtra(TAG_MENUID, menu_id);
                    i.putExtra(TAG_SORT,"price");
                    i.putExtra(TAG_SORT_ORDER,"desc");
                    startActivity(i);
                    finish();
                }
            });
            
            
            switchToCategoryDesc.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	Intent i = new Intent(getApplicationContext(), LocationMenuActivity.class);
                	i.putExtra(TAG_MENUID, menu_id);
                    i.putExtra(TAG_SORT,"category");
                    i.putExtra(TAG_SORT_ORDER,"desc");
                    startActivity(i);
                    finish();
                }
            });
            
            switchToCategoryAsc.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	Intent i = new Intent(getApplicationContext(), LocationMenuActivity.class);
                	i.putExtra(TAG_MENUID, menu_id);
                    i.putExtra(TAG_SORT,"category");
                    i.putExtra(TAG_SORT_ORDER,"asc");
                    startActivity(i);
                    finish();
                }
            });
            
            switchToTitleAsc.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	Intent i = new Intent(getApplicationContext(), LocationMenuActivity.class);
                	i.putExtra(TAG_MENUID, menu_id);
                    i.putExtra(TAG_SORT,"title");
                    i.putExtra(TAG_SORT_ORDER,"asc");
                    startActivity(i);
                    finish();
                }
            });
            
            switchToTitleDesc.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	Intent i = new Intent(getApplicationContext(), LocationMenuActivity.class);
                	i.putExtra(TAG_MENUID, menu_id);
                    i.putExtra(TAG_SORT,"title");
                    i.putExtra(TAG_SORT_ORDER,"desc ");
                    startActivity(i);
                    finish();
                }
            });
            
            switchToClear.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	Intent i = new Intent(getApplicationContext(), LocationMenuActivity.class);
                	i.putExtra(TAG_MENUID, menu_id);
                    i.putExtra(TAG_SORT,"simple");
                    i.putExtra(TAG_SORT_ORDER,"desc ");
                    startActivity(i);
                    finish();
                }
            });
            
         


   


    }

    
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


}