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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vegfreshbox.ecommerce.adapter.OrderHistoryAdapter;
import com.vegfreshbox.ecommerce.adapter.OrderHistoryDetailsAdapter;
import com.vegfreshbox.ecommerce.pojo.OrderDateComparator;
import com.vegfreshbox.ecommerce.pojo.OrderHistoryPojo;
import com.vegfreshbox.ecommerce.pojo.OrderHistoryProductsDetailsPojo;
import com.vegfreshbox.ecommerce.utils.VegUtils;

import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

public class OrderHistory extends AppCompatActivity {

    OrderHistoryAdapter adapter;
    ListView lv;
    private ProgressDialog mProgressDialog;
    ArrayList<OrderHistoryPojo> orderHistoryPojoArrayList;
    String orderStr =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order History");
        getSupportActionBar().setSubtitle("Order is placed by you.");

        mProgressDialog = new ProgressDialog(OrderHistory.this);
        lv = (ListView) findViewById(R.id.orderhistorylist);
        orderHistoryPojoArrayList = new ArrayList<OrderHistoryPojo>();
        try {



            adapter = new OrderHistoryAdapter(OrderHistory.this,
                    R.layout.table_item_order_history,
                    orderHistoryPojoArrayList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,
                                        int i, long l) {
                    OrderHistoryPojo details = (OrderHistoryPojo) adapterView
                            .getItemAtPosition(i);
                    Toast.makeText(getApplicationContext(),
                            "Showing Order Details for " + details.getId(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(OrderHistory.this,
                            OrderDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", details.getId());
                    intent.putExtra("orderStr", orderStr);

                    startActivity(intent);

                }
            });

            if (VegUtils.isOnline(OrderHistory.this)) {
                new FireBaseService().execute("");
            } else {
                Toast.makeText(OrderHistory.this, "You are not connected to Internet please go online", Toast.LENGTH_SHORT).show();
            }






        } catch (Exception e) {
            Log.e("error", e.getMessage(), e);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private class FireBaseService extends AsyncTask<String, Void, String> {

        private ProgressDialog mProgressDialog;
        FirebaseResponse res = null;
        boolean isEmailExist = false;
        String category = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(OrderHistory.this);
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.loading));

        }

        @Override
        protected String doInBackground(String... params) {
            final UserService userService = new UserService();
            FirebaseResponse output = null;

            try {
                SharedPreferences sharedPreferences = getSharedPreferences(
                        "loginstate", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userid", null);
                orderStr = userService.getUserOrder(userId);

                Log.e("orderstr",orderStr);
                if (!VegUtils.isBlank(orderStr) && !orderStr.equals("null")) {
                    JSONObject orders = new JSONObject(orderStr);

                    Iterator<String> keys = orders.keys();

                    while (keys.hasNext()) {
                        // System.out.println(keys.next());
                        String orderId = keys.next();
                        Log.e("OrderId =", orderId);
                        JSONObject order = (JSONObject) orders.get(orderId);
                        OrderHistoryPojo orderHistoryPojo = new OrderHistoryPojo();
                        orderHistoryPojo.setId(orderId);
                        orderHistoryPojo.setOrderdate(order.getString("orderDate"));
                        orderHistoryPojo
                                .setOrderamount(order.getString("orderAmt"));
                        orderHistoryPojo.setOrderstatus(order.getString("status"));

                        orderHistoryPojoArrayList.add(orderHistoryPojo);
                        Collections.sort(orderHistoryPojoArrayList, new OrderDateComparator());
                    }
                }



            } catch (Exception e) {
                Log.e("error parsing ", "users orders", e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("order history", "Calling users orders!");
            adapter.setOrderlist(orderHistoryPojoArrayList);
            adapter.notifyDataSetChanged();
            mProgressDialog.dismiss();
        }
    }
}