package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.vegfreshbox.ecommerce.app.CustomRequest;

import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import org.json.JSONObject;

import java.util.Map;

public class AddressAdd extends AppCompatActivity {

	EditText chk_fullname, chk_mobile, chk_pincode, chk_flat, chk_colony,chk_city;
	Button btnadd;
	private ProgressDialog mProgressDialog;
	String userId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_add);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mProgressDialog = new ProgressDialog(AddressAdd.this);
		chk_fullname = (EditText) findViewById(R.id.chk_fullname);
		chk_mobile = (EditText) findViewById(R.id.chk_mobile);
		chk_pincode = (EditText) findViewById(R.id.chk_pincode);
		chk_flat = (EditText) findViewById(R.id.chk_flat);
		chk_colony = (EditText) findViewById(R.id.chk_colony);
		chk_city = (EditText) findViewById(R.id.chk_city);
		btnadd = (Button) findViewById(R.id.btnadd);
		btnadd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (chk_fullname.getText().toString().trim().length() == 0
						|| chk_mobile.getText().toString().trim().length() == 0
						|| chk_pincode.getText().toString().trim().length() == 0
						|| chk_flat.getText().toString().trim().length() == 0
						|| chk_colony.getText().toString().trim().length() == 0) {
					Toast.makeText(AddressAdd.this, "Please fill all fields.",
							Toast.LENGTH_SHORT).show();
				} else {

					new FireBaseService().execute("");

				}
			}
		});

	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	private class FireBaseService extends AsyncTask<String, Void, String> {

		boolean isAddressCreated = false;
		String userId = null;
		String userData = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgressDialog.show();
			mProgressDialog.setCancelable(false);
			mProgressDialog.setMessage(getString(R.string.loading));

			SharedPreferences sharedPreferences = getSharedPreferences(
					"loginstate", MODE_PRIVATE);

			userId = sharedPreferences.getString("userid", null);

			Log.e("Hi", "Download Commencing");

		}

		@Override
		protected String doInBackground(String... params) {

			final UserService userService = new UserService();
			Log.e("344344", "registration started1 ++ " + userId);

			try {
				if (userId != null) {
					isAddressCreated = userService.createNewAddress(userId,
							chk_fullname.getText().toString().trim(),
							chk_mobile.getText().toString().trim(), chk_flat
									.getText().toString().trim(), chk_colony
									.getText().toString().trim(), chk_city
									.getText().toString().trim(), chk_pincode
									.getText().toString().trim());
				}

				if (isAddressCreated) {

					SharedPreferences sharedPreferences = getSharedPreferences(
							"loginstate", MODE_PRIVATE);
					userId = sharedPreferences.getString("userid", null);

					if (userId != null) {
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						FirebaseResponse res = userService.getUserById(userId);
						userData = res.getRawBody();
						if (userData != null) {
							editor.putString("userData", userData);
						}

						editor.commit();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return "Executed!";

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Log.e("Hi", "Done Downloading.==" + result);
			mProgressDialog.dismiss();
			try {
				if (isAddressCreated) {
					Toast.makeText(AddressAdd.this, "Address Added!",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(AddressAdd.this,
							CheckoutActivity.class);
					finish();
					startActivity(intent);
				} else {
					Toast.makeText(AddressAdd.this,
							"Something went wrong please try again!",
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

		}
	}

}
