package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.vegfreshbox.ecommerce.adapter.AddressAdapter;
import com.vegfreshbox.ecommerce.app.CustomRequest;
import com.vegfreshbox.ecommerce.pojo.AddressPojo;

import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    Button btnpayment;
    private ProgressDialog mProgressDialog;

    AddressAdapter adapter;
    ListView lv;
    LinearLayout addnewaddress,payment;
    public static int chooseaddr=0;
    public static String addressId="";
    String userId=null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	SharedPreferences sharedPreferences = getSharedPreferences("loginstate", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("islogin", "").equals("1")) {

        } else {
            Intent intent=new Intent(CheckoutActivity.this,Login.class);
            this.finish();
            startActivity(intent);
      }
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");
        getSupportActionBar().setSubtitle("One Step to complete your order.");
        chooseaddr=0;
        

//        btnpayment=(Button)findViewById(R.id.btnpayment);
//        btnpayment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        addnewaddress=(LinearLayout)findViewById(R.id.addnewaddress);
        addnewaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CheckoutActivity.this,AddressAdd.class);
                chooseaddr=0;
                finish();
                startActivity(intent);
            }
        });
        payment=(LinearLayout)findViewById(R.id.payment);

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chooseaddr!=0){
                    Intent intent=new Intent(CheckoutActivity.this,PaymentActivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putString("addressId", addressId);
					intent.putExtras(mBundle);
                    finish();


                    Log.e("Addressid=====",addressId);
                    startActivity(intent);
                }else{
                    Toast.makeText(CheckoutActivity.this,"Please Choose address first!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        lv = (ListView)findViewById(R.id.addresslist);
        mProgressDialog = new ProgressDialog(CheckoutActivity.this);
        SharedPreferences sharedPreferences1 = getSharedPreferences("loginstate", MODE_PRIVATE);
       // sharedPreferences1.getString("userid",null);

        userId=sharedPreferences1.getString("userid",null);
        Map<String, String> parameterData = new HashMap<>();
        if(userId !=null) {
			new FireBaseService().execute("");
		}
       // jsonObjectRequest2("http://ecommerceadmin.learnrip.com/Json/useraddress/", parameterData);

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

//    public void jsonObjectRequest2(String url, final Map<String, String> params) {
//        try {
//            mProgressDialog.show();
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setMessage(getString(R.string.loading));
//            Response.Listener<JSONObject> reponseListener = new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject jsonObject) {
//                    processJsonObject2(jsonObject);
//                    if(mProgressDialog!=null) {
//                        mProgressDialog.dismiss();
//                    }
//                }
//            };
//            Response.ErrorListener errorListener = new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    //Toast.makeText(getActivity(), "error==>  " + volleyError.toString(), Toast.LENGTH_LONG).show();
//                    if(mProgressDialog!=null) {
//                        mProgressDialog.dismiss();
//                    }
//                }
//            };
//            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, reponseListener, errorListener);
//            RequestQueue requestQueue = Volley.newRequestQueue(CheckoutActivity.this);
//            requestQueue.add(jsObjRequest);
//        } catch (Exception e) {
//            VolleyLog.d("RESPONSE ERROR", e.toString());
//            if(mProgressDialog!=null) {
//                mProgressDialog.dismiss();
//            }
//        }
//    }
//    private void processJsonObject2(JSONObject response) {
//        if (response != null) {
//            Log.e("==RESPONSE ERROR=", response.toString());
//            try {
//                JSONArray jsonArray2 = response.getJSONArray("address");
//                productPojoArrayList=new ArrayList<AddressPojo>();
//                for (int i=0; i<jsonArray2.length(); i++){
//                    JSONObject jsonObject=jsonArray2.getJSONObject(i);
//
//                    AddressPojo addressPojo=new AddressPojo();
//                    addressPojo.setId(jsonObject.getString("id"));
//                    addressPojo.setName(jsonObject.getString("name"));
//                    addressPojo.setMobile(jsonObject.getString("mobile"));
//                    addressPojo.setPincode(jsonObject.getString("pincode"));
//                    addressPojo.setFlat(jsonObject.getString("flat"));
//                    addressPojo.setColony(jsonObject.getString("colony"));
//
//
//
//
//                    Log.e("JSON TEST",jsonObject.getString("id"));
//
//                    productPojoArrayList.add(addressPojo);
//                }
//
//                
//            }catch (Exception e){
//
//            }
//
//        }
//    }
    
    @Override
	protected void onStop() {
		// To prevent a memory leak on rotation, make sure to call
		// stopAutoCycle() on the slider before activity or fragment is
		// destroyed
		// mDemoSlider.stopAutoCycle();
		super.onStop();
		
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
        	mProgressDialog.dismiss();
        	mProgressDialog = null;
        }
    }



    
    private class FireBaseService extends AsyncTask<String, Void, String> {
    	FirebaseResponse res = null;
		ArrayList<AddressPojo> productPojoArrayList=new ArrayList<AddressPojo>();
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgressDialog.show();
			mProgressDialog.setCancelable(false);
			mProgressDialog.setMessage(getString(R.string.loading));

			Log.e("Hi", "Download Commencing");

		}

		@Override
		protected String doInBackground(String... params) {

			// mProgressDialog = new ProgressDialog(RegisterActivity.this);
			//final UserService userService = new UserService();
			Log.e("344344", "registration started1");
			
				try {
				//res = userService.getUserById(userId);

					SharedPreferences sharedPreferences = getSharedPreferences("loginstate", MODE_PRIVATE);
					String userData=sharedPreferences.getString("userData",null);
				
				JSONObject resp = new JSONObject(userData);

				if (resp.has("address")) {
					JSONObject address = (JSONObject) resp.get("address");
					Iterator<String> keys = address.keys();

					while (keys.hasNext()) {
						//System.out.println(keys.next());
						String addressId=keys.next();
						JSONObject addr = (JSONObject) address.get(addressId);
						System.out.println("address1=" + addr.getString("address1"));
						System.out.println("address2=" + addr.getString("address2"));
						System.out.println("city=" + addr.getString("city"));
						System.out.println("pincode=" + addr.getString("pincode"));
						
						 AddressPojo addressPojo=new AddressPojo();
		                    addressPojo.setId(addressId);
		                    addressPojo.setName(addr.getString("fullName"));
		                    addressPojo.setMobile(addr.getString("mobileNumber"));
		                    addressPojo.setPincode(addr.getString("pincode"));
		                    addressPojo.setFlat(addr.getString("address1"));
		                    addressPojo.setColony(addr.getString("address2"));
		                    addressPojo.setCity(addr.getString("city"));
		                    productPojoArrayList.add(addressPojo);

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
			if(mProgressDialog !=null){
				mProgressDialog.dismiss();
			}
			
			try {
				if (productPojoArrayList.size() > 0) {
					
					adapter = new AddressAdapter(CheckoutActivity.this, R.layout.address_table_item, productPojoArrayList);
	                lv.setAdapter(adapter);
					
				}else{
					Toast.makeText(CheckoutActivity.this,
							"No Saved address !",
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
				
			}

			

		}
	}


}

