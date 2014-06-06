package edu.ucsd.troll.app;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class PasswordRetrievalActivity extends Activity {

    //init the progress bar
    private ProgressDialog pDialog;

    private static String recoverUrl = "http://troll.everythingcoed.com/user/recover";
    private static String checkLoginUrl = "http://troll.everythingcoed.com/user/login/check";

    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_USER = "user";
    private static final String TAG_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESULT = "result";
    private static final String TAG_RESETCODE = "reset_code";
    private static final String TAG_EMAIL = "email";



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
    
    //Submit button
    Button passwordRetrieveSubmitBtn;
    
    //Edit text
    EditText usernameTextBox;

    
    
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_retrieval);

        //userList = new ArrayList<HashMap<String, String>>();

        // Session Manager
        login = new LoginManager(getApplicationContext());

        // Username, Password input text
        usernameTextBox = (EditText) findViewById(R.id.edit_passretrieve_username);

        // submit username button
        passwordRetrieveSubmitBtn = (Button) findViewById(R.id.passwordRetrieveBtn);
        
   		Typeface btn_font = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
   		passwordRetrieveSubmitBtn.setTypeface(btn_font);
   		Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);
   		
   		int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        	passwordRetrieveSubmitBtn.setBackgroundDrawable(round_btn); 
        } else {
        	passwordRetrieveSubmitBtn.setBackground(round_btn); 
        }

        
        // Submit username button activity
        passwordRetrieveSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username from edit text box
            	String username = usernameTextBox.getText().toString();

                // Check if username textbox is filled
                if(username.trim().length() > 0){

                    params.add(new BasicNameValuePair(TAG_USERNAME, username));
                    params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));

                    new RecoverPassword().execute();

                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(PasswordRetrievalActivity.this, "Password retrieval failed..",
                    		"Please enter username", false);
                }
            }
        });
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class RecoverPassword extends AsyncTask<Void, Void, String> {

        HashMap<String, String> user = new HashMap<String, String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PasswordRetrievalActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            APIServiceHandler sh = new APIServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(recoverUrl, APIServiceHandler.POST, params);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
            	//Look for a response which means failure
            	try{
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
            	//If no response section was found, It was a success and it's code.
            	} catch (JSONException e) {
                    String resetCode = jsonStr.replace("\"", "");
                    Log.d("reset code: ", "=> " + resetCode);
                                                
                    user.put(TAG_RESETCODE, resetCode);
                    
                    return "success";
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

                // Starting ChangePasswordActivity
                Intent i = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                i.putExtra(TAG_RESETCODE, (String)user.get(TAG_RESETCODE));
                i.putExtra(TAG_USERNAME, (String)user.get(TAG_USERNAME));
                startActivity(i);
                finish();

            }else{
                // username / password doesn't match
                alert.showAlertDialog(PasswordRetrievalActivity.this, "Password retrieval failed..",
                		"Username is invalid", false);
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
