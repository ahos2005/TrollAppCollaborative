package edu.ucsd.troll.app;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity will be the single menu item's display page. it will recieve
 * data from the calling activity. it needs the item to be able to make the
 * appropriate additions, such as "add to favorites", "rating", etc
 */
public class SingleMenuItemActivity extends Activity {
	private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
	private static final String TAG_APIKEYNAME = "api_key";

	private static final String TAG_RESPONSE = "response";
	private static final String TAG_RESULT = "result";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_MENU = "menu";
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_RATING = "rating";
	private static final String TAG_VOTES = "total_votes";
	private static final String TAG_SIZE = "size_titles";
	private static final String TAG_PRICE = "prices";
	private static final String TAG_ITEMID = "items_id";

	private RatingBar ratingBar;
	private TextView txtRatingValue;
	private Button btnSubmit;
	private Button btnFavorite;
	private Button btnUnfavorite;
	private boolean foundFavorite = false;

	String ratingUrl = null;
	String favoriteUrl = null;

	String itemId = null;
	String orig_itemId = null;
	String favId = null;

	LoginManager login;

	List<NameValuePair> params = new ArrayList<NameValuePair>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.menu_single_item);
		login = new LoginManager(getApplicationContext());
		params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));

		TextView itemID = (TextView) findViewById(R.id.lblItemID);
		itemID.setText(getIntent().getStringExtra(TAG_ID));

		// setting up the pricing string
		String prices = getIntent().getStringExtra(TAG_PRICE);

		Log.d("PRICES", prices);
		// setting up the sizes string
		String sizeTitles = getIntent().getStringExtra(TAG_SIZE);

		Log.d("SIZES", sizeTitles);

		TextView sizes = (TextView) findViewById(R.id.lblPrice);
		// sizes.setText(Html.fromHtml(setUpPricing(sizeTitles, prices)));

		// Log.d("The list", setUpPricing(sizeTitles, prices));

		// setting the title name
		TextView itemTitle = (TextView) findViewById(R.id.lblTitle);
		itemTitle.setText(getIntent().getStringExtra(TAG_TITLE));
		// setting the Description
		TextView itemDescription = (TextView) findViewById(R.id.lblDescription);
		itemDescription.setText(getIntent().getStringExtra(TAG_DESCRIPTION));

		TextView itemCategory = (TextView) findViewById(R.id.lblTotalVotes);
		itemCategory.setText("Total Votes: "
				+ getIntent().getStringExtra(TAG_VOTES));
		addListenerOnRatingBar();
		addListenerOnButton();
		View submitView = findViewById(R.id.btnSubmit);
		View favoriteView = findViewById(R.id.btnFavorite);
		View unfavoriteView = findViewById(R.id.btnUnfavorite);

		if (!login.isLoggedIn()) {
			submitView.setVisibility(View.GONE);
			favoriteView.setVisibility(View.GONE);
			unfavoriteView.setVisibility(View.GONE);
			ratingBar.setEnabled(false);
		} else {
			checkFavorite();
			if(foundFavorite) {
				favoriteView.setVisibility(View.GONE);
				unfavoriteView.setVisibility(View.VISIBLE);
			}
			else {
				favoriteView.setVisibility(View.VISIBLE);
				unfavoriteView.setVisibility(View.GONE);
			}
		}
	}

	public void checkFavorite() {
		Log.d("TESTCALL:", "Call to CheckFavorites");
		HashMap<String, String> userDetails = login.getUserDetails();
		String userFavorites = login.getUserFavorites();
		orig_itemId = getIntent().getStringExtra(TAG_ID);
		String username = userDetails.get(TAG_USERNAME);

		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		Log.d("RESULT:", "=>" + username);

		if (userFavorites != null) {
			try {
				// JSONObject jsonObj = new JSONObject(userFavorites);

				// Log.d("Response: ", "=> " + jsonObj);

				// Getting JSON Array node

				JSONArray favorites = new JSONArray(userFavorites);
				Log.d("FAVLENGTH: ", "=>" + favorites.length());
				// looping through All Contacts
				for (int i = 0; i < favorites.length(); i++) {
					JSONObject c = favorites.getJSONObject(i);
					foundFavorite = false;
					Log.d("FAV CHECK", String.valueOf(foundFavorite));
					favId = c.getString(TAG_ID);
					Log.d("ID: ", "=> " + favId);
					String favTitle = c.getString("title");
					Log.d("Title: ", "=> " + favTitle);
					String favUserId = c.getString("users_id");
					Log.d("User ID: ", "=> " + favUserId);
					String favMenuId = c.getString("menus_id");
					Log.d("MENU ID: ", "=> " + favMenuId);
					String favItemId = c.getString(TAG_ITEMID);
					Log.d("ITEM ID: ", "=> " + favItemId);
					Log.d("ITEM ID ORIG", "=> " + orig_itemId);
					if (favItemId.equals(orig_itemId)) {
						foundFavorite = true;
						Log.d("FAV FOUND", String.valueOf(foundFavorite));
//						favoriteView.setVisibility(View.GONE);
//						unfavoriteView.setVisibility(View.VISIBLE);
						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler",
					"Couldn't get any data from prefs (checkFavorite)");
		}

//		if (!foundFavorite) {
//			favoriteView.setVisibility(View.VISIBLE);
//			unfavoriteView.setVisibility(View.GONE);
//		}
	}

	/**
	 * Adds listener to the rating bar to be able to set rating
	 */
	public void addListenerOnRatingBar() {

		String ratingValue = getIntent().getStringExtra(TAG_RATING);

		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		ratingBar.setRating(Float.parseFloat(ratingValue));
		txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);

		// if rating value is changed,
		// display the current rating value in the result (textview)
		// automatically
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {

				txtRatingValue.setText(String.valueOf(rating));

			}
		});
	}

	// create the look of the prices
	public String setUpPricing(String sizes, String prices) {

		String delims = "[;]";
		String[] sepetarePrices = prices.split(delims);
		String[] sepetareSizes = sizes.split(delims);

		String returnHtml = null;

		returnHtml = "<ul>";
		returnHtml += "<li>Prices:</li><br />";

		for (int i = 0; i < sepetarePrices.length; i++) {
			String priceString = sepetarePrices[i];
			Float priceFloat = Float.parseFloat(priceString);
			DecimalFormat df = new DecimalFormat("0.00");
			df.setMaximumFractionDigits(2);
			priceString = df.format(priceFloat);
			returnHtml += "<li>" + sepetareSizes[i] + " : " + "$" + priceString
					+ "</li>";
			returnHtml += "<br />";
		}
		returnHtml += "</ul>";
		return returnHtml;
	}

	/**
	 * Adds listeners to the favorites,submit, and unfavorite buttons
	 */
	@SuppressLint("NewApi")
	public void addListenerOnButton() {

		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnFavorite = (Button) findViewById(R.id.btnFavorite);
		btnUnfavorite = (Button) findViewById(R.id.btnUnfavorite);

		//set the font of the buttons
		Typeface btn_font = Typeface.createFromAsset(getAssets(),
				"KaushanScript-Regular.ttf");
		btnSubmit.setTypeface(btn_font);
		btnFavorite.setTypeface(btn_font);
		btnUnfavorite.setTypeface(btn_font);
		Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);

		//creates the background of the buttons
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btnSubmit.setBackgroundDrawable(round_btn);
			btnFavorite.setBackgroundDrawable(round_btn);
			btnUnfavorite.setBackgroundDrawable(round_btn);
		} else {
			btnSubmit.setBackground(round_btn);
			btnFavorite.setBackground(round_btn);
			btnUnfavorite.setBackground(round_btn);
		}

		// if click on me, then display the current rating value.
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ratingUrl = "http://troll.everythingcoed.com/ratings/"
						+ getIntent().getStringExtra(TAG_ID) + "/"
						+ ratingBar.getRating();

				Log.d("The URL will be", ratingUrl);

				new SetRating().execute();
				// remove rating button after clicked
				v.setVisibility(View.INVISIBLE);
				ratingBar.setEnabled(false);

			}

		});

		/**
		 * Favorite Button is clicked
		 */
		btnFavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//set the url to be sent to the API to be the one for favorite
				favoriteUrl = "http://troll.everythingcoed.com/user/create/favorites/"
						+ getIntent().getStringExtra(TAG_ID);
				Log.d("The URL will be", favoriteUrl);

				new SetFavorite().execute();
				// v.setVisibility(View.GONE);
				// View unfavoriteView = findViewById(R.id.btnUnfavorite);
				
				// need to run login check to refresh favorite information
				login.refreshInfo();
				login = new LoginManager(getApplicationContext());
				checkFavorite();

				//hide the favorite and unhide unfavorite
				View favoriteView = findViewById(R.id.btnFavorite);
				View unfavoriteView = findViewById(R.id.btnUnfavorite);
				favoriteView.setVisibility(View.GONE);
				unfavoriteView.setVisibility(View.VISIBLE);
			}

		});

		/**
		 * Unfavorite Button is clicked
		 */
		btnUnfavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//set up the API call url to be the one to deleta favorite
				favoriteUrl = "http://troll.everythingcoed.com/user/delete/favorites/"
						+ favId;
				Log.d("The URL will be", favoriteUrl);

				new SetFavorite().execute();
				
				// need to run login check
				login.refreshInfo();
				login = new LoginManager(getApplicationContext());
				checkFavorite();
				
				//hide the unfavorite button and bring back favorite button
				View favoriteView = findViewById(R.id.btnFavorite);
				View unfavoriteView = findViewById(R.id.btnUnfavorite);			
				unfavoriteView.setVisibility(View.GONE);
				favoriteView.setVisibility(View.VISIBLE);
			}

		});

	}

	/**
	 * This method sets the rating for the Rating bar
	 * @author Joel
	 *
	 */
	private class SetRating extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// Creating service handler class instance
			APIServiceHandler sh = new APIServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(ratingUrl,
					APIServiceHandler.GET, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					Log.d("Response: ", "=> " + jsonObj);

					// Getting JSON Object nodes
					String responseReturn = jsonObj.getString(TAG_RESPONSE);
					Log.d("Response: ", "=> " + responseReturn);

					// get the result JSON response result
					JSONObject resultObj = new JSONObject(responseReturn);

					// get the value (success or fail)
					String responseResult = resultObj.getString(TAG_RESULT);
					Log.d("Response: ", "=> " + responseResult);

					String responseMessage = resultObj.getString(TAG_MESSAGE);
					Log.d("Response: ", "=> " + responseMessage);

					// return just fail otherwise continue

					if (responseResult.equals("success")) {
						return responseMessage;
					} else {
						return "Something Went Wrong";
					}
					// adding contact to contact list

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
			Toast.makeText(SingleMenuItemActivity.this, String.valueOf(result),
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * This method sets favorite or unfavorite depending on the URL recieved
	 * @author Kevin
	 *
	 */
	private class SetFavorite extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// Creating service handler class instance
			APIServiceHandler sh = new APIServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(favoriteUrl,
					APIServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					Log.d("Response: ", "=> " + jsonObj);

					// Getting JSON Object node
					String responseReturn = jsonObj.getString(TAG_RESPONSE);
					Log.d("Response: ", "=> " + responseReturn);

					// get the result JSON response result
					JSONObject resultObj = new JSONObject(responseReturn);

					// get the value (success or fail)
					String responseResult = resultObj.getString(TAG_RESULT);
					Log.d("Response: ", "=> " + responseResult);

					String responseMessage = resultObj.getString(TAG_MESSAGE);
					Log.d("Response: ", "=> " + responseMessage);

					// return just fail otherwise continue

					if (responseResult.equals("success")) {
						return responseMessage;
					} else {
						return "Something Went Wrong";
					}
					// adding contact to contact list

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
			Toast.makeText(SingleMenuItemActivity.this, String.valueOf(result),
					Toast.LENGTH_SHORT).show();
		}

	}

}
