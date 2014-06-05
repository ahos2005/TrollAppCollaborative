package edu.ucsd.troll.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by shalomabitan on 5/22/14.
 */
public class SignUpActivity extends Activity {

    //init the progress bar
    private ProgressDialog pDialog;

    private static String createUrl = "http://troll.everythingcoed.com/user/create";
    private static String checkLoginUrl = "http://troll.everythingcoed.com/user/login/check";

    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESULT = "result";
    private static final String TAG_USER = "user";
    private static final String TAG_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_FIRSTNAME = "first_name";
    private static final String TAG_LASTNAME = "last_name";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_USERTOKEN = "presist_code";


    // menu JSONArray
    JSONArray menu = null;

    //api response string
    String responseString = null;

    //login manager
    LoginManager login;


    // Hashmap for ListView
    ArrayList<HashMap<String, String>> userList;

    List<NameValuePair> params = new ArrayList<NameValuePair>();

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;
    //login button
    Button signUpButton;
    
    //Edit text
    
    EditText firstNameTextBox,
    		 lastNameTextBox,
    		 emailTextBox,
    		 usernameTextBox,
    		 passwordTextBox;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        userList = new ArrayList<HashMap<String, String>>();


        // Session Manager
        login = new LoginManager(getApplicationContext());

        // Username, Password input text
         firstNameTextBox = (EditText) findViewById(R.id.SignupFirstName);
         lastNameTextBox = (EditText) findViewById(R.id.SignupLastName);
         emailTextBox = (EditText) findViewById(R.id.SignupEmail);
         usernameTextBox = (EditText) findViewById(R.id.SignupUserName);
         passwordTextBox = (EditText) findViewById(R.id.SignupPassword);

        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        signUpButton = (Button) findViewById(R.id.signupBtn);
        //Set Button Font
        Typeface btn_font = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
        signUpButton.setTypeface(btn_font);
        
        // Set Button Shape
        Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);
        signUpButton.setBackgroundDrawable(round_btn);

        // Login button click event
        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
            	String firstName = firstNameTextBox.getText().toString();
            	String lastName = lastNameTextBox.getText().toString();
            	String email = emailTextBox.getText().toString();
            	String username = usernameTextBox.getText().toString();
            	String password = passwordTextBox.getText().toString();

                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0 && email.trim().length() > 0){


                    params.add(new BasicNameValuePair(TAG_USERNAME, username));
                    params.add(new BasicNameValuePair(TAG_PASSWORD, password));
                    params.add(new BasicNameValuePair(TAG_EMAIL, email));
                    params.add(new BasicNameValuePair(TAG_FIRSTNAME, firstName));
                    params.add(new BasicNameValuePair(TAG_LASTNAME, lastName));
                    params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));

                    new CreateUser().execute();

                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(SignUpActivity.this, "Sign Up failed..", "Please enter username, password & email", false);
                }

            }
        });
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class CreateUser extends AsyncTask<Void, Void, String> {

        HashMap<String, String> user = new HashMap<String, String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            APIServiceHandler sh = new APIServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(createUrl, APIServiceHandler.POST, params);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                  JSONObject jsonObj = new JSONObject(jsonStr);

                      Log.d("Response: ", "=> " + jsonObj);
                    //the overall return object
                    String responseReturn = jsonObj.getString(TAG_RESPONSE);
                    Log.d("Response: ", "=> " + responseReturn);


                    //get the result JSON response result
                    JSONObject resultObj = new JSONObject(responseReturn);

                    //get the value (success or fail)
                    String responseResult = resultObj.getString(TAG_RESULT);
                    Log.d("Response: ", "=> " + responseResult);

                    //return just fail otherwise continue

                    if(responseResult.equals("fail")){
                        return responseResult;
                    }

                    //the entire user info object
                    
                    String responseUser = jsonObj.getString(TAG_USER);
                    Log.d("user: ", "=> " + responseUser);

                    JSONObject userObj = new JSONObject(responseUser);
                    //user id
                    String responseUserId = userObj.getString(TAG_ID);
                    Log.d("id: ", "=> " + responseUserId);
                    //user username
                    String responseUserName = userObj.getString(TAG_USERNAME);
                    Log.d("username: ", "=> " + responseUserName);
                    //user email
                    String responseUserEmail = userObj.getString(TAG_EMAIL);
                    Log.d("email: ", "=> " + responseUserEmail);
                    //user first name
                    String responseUserFirstName = userObj.getString(TAG_FIRSTNAME);
                    Log.d("first_name: ", "=> " + responseUserFirstName);
                    //user last name
                    String responseUserLastName = userObj.getString(TAG_LASTNAME);
                    Log.d("last_name: ", "=> " + responseUserLastName);


                    // adding each child node to HashMap key => value
                    //contact.put(TAG_ID, id);
                    user.put(TAG_ID, responseUserId);
                    user.put(TAG_USERNAME, responseUserName);
                    user.put(TAG_EMAIL, responseUserEmail);
                    user.put(TAG_FIRSTNAME, responseUserFirstName);
                    user.put(TAG_LASTNAME, responseUserLastName);


                    return responseResult;

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
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.d("RESULT: ", "=> " + result);

            if(result.equals("success")){

                // Creating user login session
                // For testing i am string name, email as follow
                // Use user real data
                login.createLoginSession(
                        user.get(TAG_ID),
                        user.get(TAG_USERNAME),
                        user.get(TAG_EMAIL),
                        user.get(TAG_FIRSTNAME),
                        user.get(TAG_LASTNAME),
                        null
                );

                // Starting MainActivity
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();

            }else{
                // username / password doesn't match
                alert.showAlertDialog(SignUpActivity.this, "Login failed..", "Username/Password is incorrect", false);
            }
            /**
             * Updating parsed JSON data into ListView
             * */
//            ListAdapter adapter = new SimpleAdapter(
//                    ProfileActivity.this, menuList,
//                    R.layout.list_item, new String[] {TAG_TITLE, TAG_DESCRIPTION,
//                    TAG_SIZE}, new int[] { R.id.title,
//                    R.id.rating, R.id.category });
//
//            setListAdapter(adapter);
        }

    }


}
