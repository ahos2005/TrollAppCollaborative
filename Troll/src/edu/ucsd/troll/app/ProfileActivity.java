package edu.ucsd.troll.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
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

public class ProfileActivity extends Activity {

    //init the progress bar
    private ProgressDialog pDialog;

    // URL to get menu JSON
    private static String url = "http://troll.everythingcoed.com/user/login";

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
    //private static final String TAG_PHONE_OFFICE = "office";

    // menu JSONArray
    JSONArray menu = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> favoriteList;

    List<NameValuePair> params = new ArrayList<NameValuePair>();

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    LoginManager login;
    //login button
    //Button logoutButton;

    //Edittext
    EditText usernameTextBox, passwordTextBox;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //login manager
        login = new LoginManager(getApplicationContext());
        
		CharSequence[] forgot_pass = {"Forgot Password?"};
		if(!login.isLoggedIn()){

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			//			LayoutInflater inflater = this.getLayoutInflater();
			builder.setTitle("Please Sign Up or Sign In")
						.setItems(forgot_pass, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent j = new Intent(getApplicationContext(), PasswordRetrievalActivity.class);
								startActivity(j);
								finish();
							}
						})
//			.setSingleChoiceItems(forgot_pass, 0, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					if(which != -1){
//						Intent j = new Intent(getApplicationContext(), PasswordRetrievalActivity.class);
//						startActivity(j);
//						finish();
//					}
//				}
//			})
            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
            	@Override
            	public void onClick(DialogInterface dialogInterface, int i) {
            		Intent j = new Intent(getApplicationContext(), LoginActivity.class);
    	    		startActivity(j);
    	    		finish();
            	}
            })
            .setNeutralButton("Sign Up", new DialogInterface.OnClickListener() {
            	@Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Sign up 
                    Intent j = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(j);
                    finish();

                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Go back to main
                    Intent j = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(j);
                    finish();
                }
            });
            AlertDialog loginDialog = builder.create();
            loginDialog.show();


        }else{
            setContentView(R.layout.profile_layout);
            //this is just for testing and debugging
            //login.logoutUser();
         
            
            final Button logoutButton = (Button) findViewById(R.id.logoutBtn);
            final Button diaryButton = (Button) findViewById(R.id.diaryBtn);
            final Button favorites = (Button) findViewById(R.id.favBtn);
          
            //Set Button Font
            Typeface btn_font = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
            logoutButton.setTypeface(btn_font);
            diaryButton.setTypeface(btn_font);
            favorites.setTypeface(btn_font);
            
            // Set Button Shape
            Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);
            logoutButton.setBackgroundDrawable(round_btn);
            diaryButton.setBackgroundDrawable(round_btn);
            favorites.setBackgroundDrawable(round_btn);
            
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override

            public void onClick(View view) {
                	login.logoutUser();
                    Intent act2 = new Intent(view.getContext(), MainActivity.class);
                    Toast.makeText(ProfileActivity.this, "You Have Been Logged out", Toast.LENGTH_LONG).show();
                    startActivity(act2);
                    finish();
                }
            });

            diaryButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					Intent act3 = new Intent(view.getContext(), DiaryActivity.class);
					Toast.makeText(ProfileActivity.this, "Diary", Toast.LENGTH_SHORT).show();
					startActivity(act3);

				}
			});
            
            
            favorites.setOnClickListener(new View.OnClickListener() {
            	                 @Override
            	                 
            	             public void onClick(View view) {
            	                     Intent i = new Intent(getApplicationContext(), FavoritesActivity.class);
            	                     startActivity(i);
//            	                     finish();
            	                 }
            	             });

            TextView usernameTextView = (TextView) findViewById(R.id.user_username);

            TextView emailTextView = (TextView) findViewById(R.id.user_email);

            TextView firstNameTextView = (TextView) findViewById(R.id.user_first_name);

            TextView lastNameTextView = (TextView) findViewById(R.id.user_last_name);


            HashMap<String, String> user = login.getUserDetails();

            // name
            String username = user.get(SessionManager.KEY_USERNAME);
            // email
            String email = user.get(SessionManager.KEY_EMAIL);
            //first name
            String lastname = user.get(SessionManager.KEY_LASTNAME);
            //last name
            String firstname = user.get(SessionManager.KEY_FIRSTNAME);


            // displaying user data
            usernameTextView.setText(Html.fromHtml("Username: <b>" + username + "</b>"));

            emailTextView.setText(Html.fromHtml("Email: <b>" + email + "</b>"));

            firstNameTextView.setText(Html.fromHtml("First Name: <b>" + firstname + "</b>"));

            lastNameTextView.setText(Html.fromHtml("Last Name: <b>" + lastname + "</b>"));


//            HashMap<String, String> favoritesMap = login.getUserFavorites();
//
//            String favorites = favoritesMap.get(SessionManager.KEY_FAVORITES);
//
//            JSONArray jObject = new JSONArray(favorites);
//            for (int i = 0; i < jObject.length(); i++,start++) {
//                JSONObject menuObject = jObject.getJSONObject(i);
//
//                String name= menuObject.getString("name");
//                String email= menuObject.getString("email");
//                String image= menuObject.getString("image");
//            }


        }


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
  //class getFavorites() moved to FavoritesActivity.java


}