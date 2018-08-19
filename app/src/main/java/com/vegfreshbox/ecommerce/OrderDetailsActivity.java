package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.vegfreshbox.ecommerce.adapter.OrderHistoryDetailsAdapter;
import com.vegfreshbox.ecommerce.pojo.OrderHistoryProductsDetailsPojo;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class OrderDetailsActivity extends AppCompatActivity {

    ListView orderhistoryproductslist;
    String id;
    OrderHistoryDetailsAdapter orderHistoryDetailsAdapter;
    ArrayList<OrderHistoryProductsDetailsPojo> orderHistoryProductsDetailsPojoArrayList;
    private ProgressDialog mProgressDialog;
    public static TextView totalprice,deliveryAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");

        totalprice=(TextView)findViewById(R.id.totalprice);
        deliveryAddress=(TextView)findViewById(R.id.deliveryAddress);

        orderhistoryproductslist=(ListView)findViewById(R.id.orderhistoryproductslist);
        mProgressDialog = new ProgressDialog(OrderDetailsActivity.this);
        OrderHistoryProductsDetailsPojo orderHistoryProductsDetailsPojo = new OrderHistoryProductsDetailsPojo();
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            id = (String) bd.get("id");
        }
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("loginstate", MODE_PRIVATE);
            String userData = sharedPreferences.getString("userData", null);
            orderHistoryProductsDetailsPojoArrayList = new ArrayList<OrderHistoryProductsDetailsPojo>();
            JSONObject resp = new JSONObject(userData);

            if (resp.has("orders")) {
                JSONObject orders = (JSONObject) resp.get("orders");
                Iterator<String> keys = orders.keys();

                while (keys.hasNext()) {
                    //System.out.println(keys.next());
                    String orderId = keys.next();
                    Log.e("OrderId++++++",orderId);
                    JSONObject order = (JSONObject) orders.get(orderId);

                    if(orderId.equals(id)) {




                        orderHistoryProductsDetailsPojo.setId(orderId);
                        orderHistoryProductsDetailsPojo.setDeliveryAddress(order.getString("deliveryAddress"));
                        orderHistoryProductsDetailsPojo.setTotal(order.getString("orderAmt"));
                        orderHistoryProductsDetailsPojo.setOrderDate(order.getString("orderDate"));

                        JSONArray products= order.getJSONArray("products");

                        for (int i = 0, size = products.length(); i < size; i++) {
                            JSONObject objectInArray = products.getJSONObject(i);
                            ProductPojo pr= new ProductPojo();
                            pr.setId((String)objectInArray.get("productId"));
                            pr.setQuantity((String)objectInArray.get("productQuantity"));
                            pr.setName((String)objectInArray.get("productName"));
                            pr.setPrice((String)objectInArray.get("productPrice"));
                            pr.setImage((String)objectInArray.get("productImage"));
                            pr.setWgt((String)objectInArray.get("wgt"));
                            if(objectInArray.has("imagelocal")){
                                pr.setImagelocal((String)objectInArray.get("imagelocal"));
                            }

                            orderHistoryProductsDetailsPojo.getProducts().add(pr);
                          //  System.out.println(objectInArray.get("productName"));
                        }

                        
                        break;
                    }
                }
            }


            //Log.e("JSON TEST",jsonObject.getString("id"));


            orderHistoryDetailsAdapter = new OrderHistoryDetailsAdapter(OrderDetailsActivity.this, R.layout.table_item_order_history_details,
                    orderHistoryProductsDetailsPojo.getProducts());
            orderhistoryproductslist.setAdapter(orderHistoryDetailsAdapter);

            totalprice.setText("INR "+String.valueOf(orderHistoryProductsDetailsPojo.getTotal()));
            deliveryAddress.setText(orderHistoryProductsDetailsPojo.getDeliveryAddress());

        }
        catch (Exception e){
            e.printStackTrace();
        }
          

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
