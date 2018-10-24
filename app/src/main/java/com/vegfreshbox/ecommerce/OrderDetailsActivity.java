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

import net.thegreshams.firebase4j.demo.Order;
import net.thegreshams.firebase4j.demo.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    ListView orderhistoryproductslist;
    String id;
    String orderStr;
    Order orderObj;
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
            orderStr=bd.getString("orderStr");
            orderObj = (Order) bd.get("order");
        }
        try {

            orderHistoryProductsDetailsPojoArrayList = new ArrayList<OrderHistoryProductsDetailsPojo>();


            if (orderStr !=null) {
                JSONObject orders = new JSONObject(orderStr);
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


            if(orderObj !=null){

                orderHistoryProductsDetailsPojo.setId(orderObj.getOrderId());
                orderHistoryProductsDetailsPojo.setDeliveryAddress(orderObj.getDeliveryAddress());
                orderHistoryProductsDetailsPojo.setTotal(orderObj.getOrderTotal());
                orderHistoryProductsDetailsPojo.setOrderDate(orderObj.getOrderDate());

                List<Product> products= orderObj.getProducts();

                for (int i = 0, size = products.size(); i < size; i++) {
                    Product objectInArray = products.get(i);
                    ProductPojo pr= new ProductPojo();
                    pr.setId((String)objectInArray.getProductId());
                    pr.setQuantity((String)objectInArray.getProductQuantity());
                    pr.setName((String)objectInArray.getProductName());
                    pr.setPrice((String)objectInArray.getProductPrice());
                    pr.setImage((String)objectInArray.getProductImage());
                    pr.setWgt((String)objectInArray.getWgt());
                    if(objectInArray.getImagelocal() !=null){
                        pr.setImagelocal((String)objectInArray.getImagelocal());
                    }

                    orderHistoryProductsDetailsPojo.getProducts().add(pr);
                    //  System.out.println(objectInArray.get("productName"));
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
