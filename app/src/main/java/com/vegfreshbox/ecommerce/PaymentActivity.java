package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vegfreshbox.ecommerce.database.MyCart;
import com.vegfreshbox.ecommerce.mail.GMailSender;
import com.vegfreshbox.ecommerce.pojo.AddressPojo;
import com.vegfreshbox.ecommerce.utils.VegUtils;

import net.thegreshams.firebase4j.demo.Order;
import net.thegreshams.firebase4j.demo.Product;
import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    public static double totalpay = 0.000;
    public static MyCart mycartdb;
    Button btnpayment;
    private ProgressDialog mProgressDialog;
    TextView paymentamount, paymentcardholdername, paymentcardnumber, paymentcardexpiremonth, paymentcardexpireyear, paymentcardcvv;

    ArrayList<Product> products;
    ArrayList<String> productid;

    ArrayList<String> productname;
    ArrayList<String> productprice;
    ArrayList<String> productquantity;
    double shippingcharge;
    String userId = null;
    String userData = null;
    RadioButton paymentcreditradio, codradio;
    String addressid = "1";
    LinearLayout carddetails, codpaymentbutton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {

            addressid = (String) bd.get("addressId");
            Log.e("Addres Id selected ", addressid);
        }


        sharedPreferences = getSharedPreferences(
                "loginstate", MODE_PRIVATE);

        mProgressDialog = new ProgressDialog(PaymentActivity.this);

        paymentamount = (TextView) findViewById(R.id.paymentamount);
        btnpayment = (Button) findViewById(R.id.btnpayment);


        mycartdb = new MyCart(PaymentActivity.this);
        mycartdb.open();
        Cursor c = mycartdb.getAllData();

        totalpay = 0;
        if (c.moveToFirst()) {
            do {

                totalpay += (Double.parseDouble(c.getString(5))) * Integer.parseInt(c.getString(7));

            } while (c.moveToNext());
        }
        paymentamount.setText("Total Order amount  , INR " + String.valueOf(totalpay) + "\n\n ");


        mycartdb.close();


        // GET ALL PRODUCTS AND SEND TO SERVER

        products = new ArrayList<Product>();

        mycartdb.open();
        Cursor cursor = mycartdb.getAllData();
        if (cursor.moveToFirst()) {
            do {
                // SET ALL PRODUCTS IN ARRAY
                Product p = new Product(cursor.getString(1), String.valueOf(cursor.getString(2)),
                        String.valueOf(cursor.getString(5)), String.valueOf(cursor.getString(6)), String.valueOf(cursor.getString(7)));

                p.setImagelocal(cursor.getString(4));
                p.setProductImage(cursor.getString(3));
                products.add(p);
            } while (cursor.moveToNext());
        }

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy); 


        btnpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (VegUtils.isOnline(PaymentActivity.this)) {
                    userId = sharedPreferences.getString("userid", null);
                    userData = sharedPreferences.getString("userData", null);
                    new FireBaseService().execute("");
                } else {
                    Toast.makeText(PaymentActivity.this, "You are not connected to Internet please go online", Toast.LENGTH_SHORT).show();
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

        FirebaseResponse res = null;
        boolean isOrderCreated = false;
        Order order = null;
        AddressPojo addressData = null;
        String userEmailAddress=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.loading));
            Log.e("Hi", "Connecting firebase for order create");
            addressData = getAddressData(addressid);
        }

        @Override
        protected String doInBackground(String... params) {
            final UserService userService = new UserService();
            Log.e("Payment", "Creating Order Started");


            try {

                final Map<String, Object> config = userService.getConfig();

                if (userId != null) {

                   // order = userService.createUserOrder(userId,
                    //        String.valueOf(totalpay), products, addressData.toString());

                   // SharedPreferences.Editor editor = sharedPreferences.edit();
                    //FirebaseResponse res = userService.getUserById(userId);
                  //  final String userData = res.getRawBody();
                  //  if (userData != null) {
                 //       editor.putString("userData", userData);
                 //   }
                 //   editor.commit();

                   // if (config.get("cancreateorder") != null && (Boolean) config.get("cancreateorder")) {

                    order=  userService.createNewOrder(userId,
                                String.valueOf(totalpay), products, addressData.toString());

                  //  }
                    Log.e("can Send mail", ""+ config.get("cansendmail"));
                    if ((Boolean) config.get("cansendmail")) {
                        Thread thread = new Thread() {
                            public void run() {
                                Log.e("Email Address to sent", userService.getEmailAddress(userData));
                                Log.e("Email Address to email", "" + config.get("gmailEmail") + "  " + config.get("gmailPassword") + " " + config.get("cansendmail"));
                                sendMail(addressData, order, userService.getEmailAddress(userData), config);
                        }
                    } ;
                    thread.start();
                }
            }

            } catch (Exception e) {
                Log.e("order", "while placing order", e);
            }

            return "Executed!";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.e("Hi", "Done Downloading.==" + result);
            try {

                mycartdb.open();
                mycartdb.deleteAllData();
                mycartdb.close();


                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_container));

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText("Congrats, Your Order Placed Successfully, Confirmation Sent to mail address!");

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                //Toast.makeText(getApplicationContext(),
                //		"Congrats, Your Order Placed Successfully!",
                //		Toast.LENGTH_SHORT).show();



              //  Intent intent = new Intent(getApplicationContext(),
               //        HomeActivity.class);
               // startActivity(intent);


                Intent intent = new Intent(PaymentActivity.this,
                        OrderDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("order", order);
                startActivity(intent);

                finish();
            } catch (Exception e) {
                Log.e("order", "order detials", e);
                Toast.makeText(getApplicationContext(),
                        "Something went wrong please try again!",
                        Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void sendMail(AddressPojo addressData, Order order,String userEmailAddress, Map<String, Object> config) {


        try {
            Log.e("eee", "start sending mail");

            GMailSender sender = new GMailSender((String) config.get("gmailEmail"),(String) config.get("gmailPassword"));
            sender.sendMail(
                    "Your Order Detail",
                    getEmailBody(addressData, order),
                    "freshvegbox@gmail.com",
                    userEmailAddress);

        } catch (Exception e) {
            Log.e("MailApp", "Could not send email", e);
        }

    }

    String getEmailBody(AddressPojo addressData, Order order) {

        String pr = "";
        String prdHtml = loadProductFromAsset();
        for (Product p : products) {
            String productHtml = prdHtml;

            productHtml = productHtml.replace("#productname#", "" + p.getProductName());
            productHtml = productHtml.replace("#qty#", "" + p.getProductQuantity());
            productHtml = productHtml.replace("#prdprice#", "" + VegUtils.getSubTotal(p.getProductPrice(),
                    p.getProductQuantity()));
            productHtml = productHtml.replace("#wgt#", "" + p.getWgt());
            pr = pr + productHtml;
        }
        String body = "";

        body = loadOrderConfirmationFromAsset();

        body = body.replace("#orderno#", "" + order.getOrderId());
        body = body.replace("#orderdate#", "" + order.getOrderDate());
        body = body.replaceAll("#total#", "" + order.getOrderTotal());

        body = body.replace("#fullname#", "" + addressData.getName());
        body = body.replace("#address1#", "" + addressData.getFlat());
        body = body.replace("#address2#", "" + addressData.getColony());
        body = body.replace("#city#",
                "" + addressData.getCity() + " " + addressData.getPincode());
        body = body.replace("#phone#", "" + addressData.getMobile());
        body = body.replace("#productlist#", pr);

        return body;

    }


    AddressPojo getAddressData(String addressId) {
        AddressPojo addressData = new AddressPojo();
        String userData = sharedPreferences.getString("userData", null);
        try {
            JSONObject resp = new JSONObject(userData);

            if (resp.has("address")) {
                JSONObject address = (JSONObject) resp.get("address");
                Iterator<String> keys = address.keys();

                while (keys.hasNext()) {
                    // System.out.println(keys.next());
                    String addId = keys.next();

                    if (addId.equals(addressId)) {
                        JSONObject addr = (JSONObject) address.get(addressId);
                        addressData.setName(addr.getString("fullName"));
                        addressData.setFlat(addr.getString("address1"));
                        addressData.setColony(addr.getString("address2"));
                        addressData.setCity(addr.getString("city"));
                        addressData.setPincode(addr.getString("pincode"));
                        addressData.setMobile(addr.getString("mobileNumber"));

                    }

                }
            }
        } catch (Exception e) {
            Log.e("err", e.getMessage(), e);
        }
        return addressData;
    }


    public String loadOrderConfirmationFromAsset() {
        String json = null;
        try {
            InputStream is = PaymentActivity.this.getAssets().open("orderConfirmation.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            Log.e("OrderFromAsset ", ex.toString());
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public String loadProductFromAsset() {
        String json = null;
        try {
            InputStream is = PaymentActivity.this.getAssets().open("product.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e("productFromAssets", ex.toString());
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
