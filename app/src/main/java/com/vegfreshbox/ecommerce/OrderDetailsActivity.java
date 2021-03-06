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
import com.vegfreshbox.ecommerce.pojo.AddressPojo;
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
    public static TextView totalprice, deliveryAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Order Details");

        totalprice = (TextView) findViewById(R.id.totalprice);
        deliveryAddress = (TextView) findViewById(R.id.deliveryAddress);

        orderhistoryproductslist = (ListView) findViewById(R.id.orderhistoryproductslist);
        mProgressDialog = new ProgressDialog(OrderDetailsActivity.this);
        OrderHistoryProductsDetailsPojo orderHistoryProductsDetailsPojo = new OrderHistoryProductsDetailsPojo();
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            id = (String) bd.get("id");
            orderStr = bd.getString("orderStr");
            orderObj = (Order) bd.get("order");
        }
        try {

            orderHistoryProductsDetailsPojoArrayList = new ArrayList<OrderHistoryProductsDetailsPojo>();

           // Log.e("OrderStrDetail","O="+orderStr);
          //  Log.e("OrderOrderDetail",""+orderObj);
           // Log.e("orderId",""+id);
            if (orderStr != null) {
                JSONObject orders = new JSONObject(orderStr);
                Iterator<String> keys = orders.keys();
                String deliveryAddrress = "";
                while (keys.hasNext()) {
                    //System.out.println(keys.next());
                    String orderId = keys.next();
                    //Log.e("OrderId++++++", orderId);
                    JSONObject order = (JSONObject) orders.get(orderId);
                    if (order.has("deliveryAddress")) {
                        JSONObject deliveryAddress = order.getJSONObject("deliveryAddress");
                        //Log.e("delivery address ==", deliveryAddress.toString());
                        AddressPojo addr = getAddressData(deliveryAddress);
                        deliveryAddrress = addr.toString();
                    }
                    if (orderId.equals(id)) {

                        orderHistoryProductsDetailsPojo.setId(orderId);
                        orderHistoryProductsDetailsPojo.setDeliveryAddress(deliveryAddrress);
                        orderHistoryProductsDetailsPojo.setTotal(order.getString("orderAmt"));
                        orderHistoryProductsDetailsPojo.setOrderDate(order.getString("orderDate"));

                        JSONArray products = order.getJSONArray("products");

                        for (int i = 0, size = products.length(); i < size; i++) {
                            JSONObject objectInArray = products.getJSONObject(i);
                            ProductPojo pr = new ProductPojo();
                            pr.setId((String) objectInArray.get("productId"));
                            pr.setQuantity((String) objectInArray.get("productQuantity"));
                            pr.setName((String) objectInArray.get("productName"));
                            pr.setPrice((String) objectInArray.get("productPrice"));
                            pr.setWgt((String) objectInArray.get("wgt"));
                            if (objectInArray.has("productImage")) {
                                pr.setImage((String) objectInArray.get("productImage"));
                            }
                            if (objectInArray.has("imagelocal")) {
                                pr.setImagelocal((String) objectInArray.get("imagelocal"));
                            }
                            orderHistoryProductsDetailsPojo.getProducts().add(pr);
                        }


                        break;
                    }


                }
            }


            if (orderObj != null) {

                orderHistoryProductsDetailsPojo.setId(orderObj.getOrderId());
                orderHistoryProductsDetailsPojo.setDeliveryAddress(orderObj.getDeliveryAddress());
                orderHistoryProductsDetailsPojo.setTotal(orderObj.getOrderTotal());
                orderHistoryProductsDetailsPojo.setOrderDate(orderObj.getOrderDate());

                List<Product> products = orderObj.getProducts();

                for (int i = 0, size = products.size(); i < size; i++) {
                    Product prObj = products.get(i);
                    ProductPojo pr = new ProductPojo();
                    pr.setId((String) prObj.getProductId());
                    pr.setQuantity((String) prObj.getProductQuantity());
                    pr.setName((String) prObj.getProductName());
                    pr.setPrice((String) prObj.getProductPrice());
                    pr.setImage((String) prObj.getProductImage());
                    pr.setWgt((String) prObj.getWgt());
                    if (prObj.getImagelocal() != null) {
                        pr.setImagelocal((String) prObj.getImagelocal());
                    }

                    orderHistoryProductsDetailsPojo.getProducts().add(pr);
                }

            }

            orderHistoryDetailsAdapter = new OrderHistoryDetailsAdapter(OrderDetailsActivity.this, R.layout.table_item_order_history_details,
                    orderHistoryProductsDetailsPojo.getProducts());
            orderhistoryproductslist.setAdapter(orderHistoryDetailsAdapter);

            totalprice.setText("INR " + String.valueOf(orderHistoryProductsDetailsPojo.getTotal()));
            deliveryAddress.setText(orderHistoryProductsDetailsPojo.getDeliveryAddress());

        } catch (Exception e) {
           Log.e("error","Orderdetails",e);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private AddressPojo getAddressData(JSONObject addr) {
        AddressPojo addressData = new AddressPojo();
        try {
            addressData.setName(addr.getString("name"));
            addressData.setFlat(addr.getString("flat"));
            addressData.setColony(addr.getString("colony"));
            addressData.setCity(addr.getString("city"));
            addressData.setPincode(addr.getString("pincode"));
            addressData.setMobile(addr.getString("mobile"));
        } catch (Exception e) {
            Log.e("err", e.getMessage(), e);
        }
        return addressData;
    }
}
