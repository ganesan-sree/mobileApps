package com.vegfreshbox.ecommerce;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vegfreshbox.ecommerce.adapter.CartListAdapter;
import com.vegfreshbox.ecommerce.database.MyCart;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;
import com.vegfreshbox.ecommerce.utils.VegUtils;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ArrayList<ProductPojo> productPojoArrayList;
    ListView lv;
    CartListAdapter adapter;
    public static double total=0.000;
    public static TextView totalprice;
    LinearLayout continueshopping,checkout;
    ImageView product_quantity_dec,product_quantity_inc;
    public static MyCart mycartdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mycartdb=new MyCart(CartActivity.this);


        lv = (ListView)findViewById(R.id.cartlist);
        productPojoArrayList = new ArrayList<ProductPojo>();
        totalprice=(TextView)findViewById(R.id.totalprice);
        continueshopping=(LinearLayout)findViewById(R.id.continueshopping);
        checkout=(LinearLayout)findViewById(R.id.checkout);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VegUtils.isOnline(CartActivity.this)){
                    Intent intent=new Intent(CartActivity.this,CheckoutActivity.class);
                    finish();
                    startActivity(intent);
                }else{
                    Toast.makeText(CartActivity.this, "You are not connected to Internet please go online", Toast.LENGTH_SHORT).show();
                }

            }
        });

        continueshopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();

            }
        });



//        DATABASE
        //MyCart mycartdb=new MyCart(CartActivity.this);

        mycartdb.open();
        Cursor c = mycartdb.getAllData();
        if (c.moveToFirst()) {
            do {

                ProductPojo item = new ProductPojo();
                item.setId(c.getString(1));
                item.setName(c.getString(2));
                item.setImage(c.getString(3));
                item.setImagelocal(c.getString(4));
                item.setPrice(c.getString(5));
                item.setWgt(c.getString(6));
                item.setQuantity(c.getString(7));
                //total+= Double.parseDouble(c.getString(5));
                productPojoArrayList.add(item);
            } while (c.moveToNext());
        }

        updatetotal(100);
        mycartdb.close();
        adapter = new CartListAdapter(CartActivity.this, R.layout.cart_table_list_item, productPojoArrayList);
        lv.setAdapter(adapter);
    }

    public static void updatetotal(long val){

        mycartdb.open();
        Cursor c = mycartdb.getAllData();
        total=0;
        if (c.moveToFirst()) {
            do {

//                int i=Integer.parseInt(c.getString(6));

                total+= (Double.parseDouble(c.getString(5)))*Integer.parseInt(c.getString(7));

            } while (c.moveToNext());
        }
        totalprice.setText("INR "+String.valueOf(total));


        mycartdb.close();
    }

}
