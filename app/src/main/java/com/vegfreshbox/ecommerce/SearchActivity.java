package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.vegfreshbox.ecommerce.adapter.SubcategoriesAdapter;
import com.vegfreshbox.ecommerce.app.CustomRequest;
import com.vegfreshbox.ecommerce.database.MyCart;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;
import com.vegfreshbox.ecommerce.pojo.SubCategoryPojo;
import com.vegfreshbox.ecommerce.utils.BitmapLruCache;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private LayerDrawable mCartMenuIcon;
    private MenuItem mSearchMenu,cart;
    private Button search;
    private EditText searchtext;
    private ProgressDialog mProgressDialog;
    ProductsAdapter productsAdapter;
    ArrayList<ProductPojo> productPojoArrayList;
    RecyclerView searchrecycle;

    public static long countproductoncart=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mProgressDialog = new ProgressDialog(SearchActivity.this);

        searchrecycle= (RecyclerView) findViewById(R.id.searchrecycle);

        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
        searchrecycle.setLayoutManager(verticalLayoutmanager);

        search=(Button)findViewById(R.id.search);
        searchtext=(EditText)findViewById(R.id.searchtext);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=searchtext.getText().toString().trim();
                if(text.length()<=0){
                    Toast.makeText(SearchActivity.this,"Please type some search text!",Toast.LENGTH_SHORT).show();
                }else{
                    Map<String, String> parameterData = new HashMap<>();
                    parameterData.put("text",searchtext.getText().toString().trim());
                    jsonObjectRequest2("http://ecommerceadmin.learnrip.com/Json/search/", parameterData);
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        getMenuInflater().inflate(R.menu.cart, menu);

        mCartMenuIcon = (LayerDrawable) menu.findItem(R.id.action_cart).getIcon();
        //mSearchMenu = (MenuItem) menu.findItem(R.id.action_search);
        mSearchMenu.setVisible(false);
        cart=(MenuItem) menu.findItem(R.id.action_cart);
//        mSearchMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(SearchActivity.this,"Search Click",Toast.LENGTH_LONG).show();
//                Intent i=new Intent(SearchActivity.this,SearchActivity.class);
//                startActivity(i);
//                return false;
//            }
//        });

        cart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(SearchActivity.this,"Cart Click",Toast.LENGTH_LONG).show();
                Intent i=new Intent(SearchActivity.this,CartActivity.class);
                startActivity(i);
                return false;
            }
        });

        MyCart myCartdb=new MyCart(SearchActivity.this);
        myCartdb.open();
        countproductoncart=myCartdb.countproduct();
        myCartdb.close();
        setBadgeCount(this, mCartMenuIcon, String.valueOf(countproductoncart));
        return super.onCreateOptionsMenu(menu);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void jsonObjectRequest2(String url, final Map<String, String> params) {

        //Log.e("GUNJAN",url);

        //Toast.makeText(SearchActivity.this,"JsonObj",Toast.LENGTH_SHORT).show();
        try {
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.loading));
            Response.Listener<JSONObject> reponseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    processJsonObject2(jsonObject);
                    mProgressDialog.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    Toast.makeText(SearchActivity.this, "error==>  " + volleyError.toString(), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            };
            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, reponseListener, errorListener);
            RequestQueue requestQueue = Volley.newRequestQueue(SearchActivity.this);
            requestQueue.add(jsObjRequest);
        } catch (Exception e) {
            VolleyLog.d("RESPONSE ERROR", e.toString());
            mProgressDialog.dismiss();
        }
    }
    private void processJsonObject2(JSONObject response) {
        Toast.makeText(SearchActivity.this,"JsonObj",Toast.LENGTH_SHORT).show();
        if (response != null) {
            Log.e("==RESPONSE ERROR=", response.toString());
            try {

                JSONArray jsonArray2 = response.getJSONArray("products");
                productPojoArrayList = new ArrayList<ProductPojo>();
                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject jsonObject = jsonArray2.getJSONObject(i);

                    ProductPojo productPojo = new ProductPojo();
                    productPojo.setId(jsonObject.getString("id"));
                    productPojo.setName(jsonObject.getString("name"));
                    productPojo.setImage(jsonObject.getString("image"));
                    productPojo.setPrice(jsonObject.getString("price"));



                    Log.e("JSON TEST", jsonObject.getString("id"));

                    productPojoArrayList.add(productPojo);
                }


                productsAdapter = new ProductsAdapter(getApplicationContext(), productPojoArrayList);
                searchrecycle.setAdapter(productsAdapter);

            } catch (Exception e) {

                e.printStackTrace();

            }

        }
    }

        public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

            private List<ProductPojo> verticalList;
            ImageLoader imageLoader;
            Context context;
            //  private LayerDrawable mCartMenuIcon;
            //  private Menu mMenu;

            public class MyViewHolder extends RecyclerView.ViewHolder {
                public TextView txtView,price,ourprice;
                ImageView profilePic,addtocart,removefromcart;




                public MyViewHolder(View view) {
                    super(view);
                    txtView = (TextView) view.findViewById(R.id.product_list_name);
                    profilePic = (ImageView)view.findViewById(R.id.img_product);
                    addtocart=(ImageView)view.findViewById(R.id.addtocart);
                    removefromcart=(ImageView)view.findViewById(R.id.removefromcart);
                    price=(TextView)view.findViewById(R.id.price);
                    //ourprice=(TextView)view.findViewById(R.id.ourprice);



                }
            }


            public ProductsAdapter(Context context,List<ProductPojo> verticalList) {
                this.verticalList = verticalList;
                this.context=context;
//        imageLoader = AppController.getInstance().getImageLoader();

            }

            @Override
            public ProductsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_table_list_item, parent, false);
                return new ProductsAdapter.MyViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(final ProductsAdapter.MyViewHolder holder, final int position) {

                ImageLoader.ImageCache imageCache = new BitmapLruCache();
                ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(context), imageCache);

                holder.txtView.setText(verticalList.get(position).getName());
                Picasso.with(context).load(verticalList.get(position).getImage()).resize(250, 250).into(holder.profilePic);
                holder.price.setText(verticalList.get(position).getPrice());

//        CHECK PRODUCT AVAILABE IN DATABASE OR NOT
                MyCart myCartdb=new MyCart(context);
                myCartdb.open();
                int i=myCartdb.checkAvailable(verticalList.get(position).getId());
                if(i==0){
                    holder.addtocart.setVisibility(View.VISIBLE);
                    holder.removefromcart.setVisibility(View.GONE);
                }else{
                    holder.removefromcart.setVisibility(View.VISIBLE);
                    holder.addtocart.setVisibility(View.GONE);

                }
                myCartdb.close();
//        END PRODUCT CHECK


                holder.addtocart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"Added to cart successfully.",Toast.LENGTH_SHORT).show();
                        MyCart myCartdb=new MyCart(context);
                        myCartdb.open();
                        myCartdb.insertData(verticalList.get(position).getId(),
                        		verticalList.get(position).getName(), verticalList.get(position).getImage(),
                        		verticalList.get(position).getImagelocal(),
                        		verticalList.get(position).getPrice(), verticalList.get(position).getWgt(), "1");
                        myCartdb.close();
                        holder.removefromcart.setVisibility(View.VISIBLE);
                        holder.addtocart.setVisibility(View.GONE);
                        HomeActivity.countproductoncart=HomeActivity.countproductoncart+1;
                        setBadgeCount(context, mCartMenuIcon, String.valueOf(HomeActivity.countproductoncart));
//                ProductsActivity productsActivity=new ProductsActivity();
//                productsActivity.setBadgeCount(context,mCartMenuIcon,String.valueOf(20));

                    }
                });

                holder.removefromcart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"Product deleted from your cart.",Toast.LENGTH_SHORT).show();
                        MyCart myCartdb=new MyCart(context);
                        myCartdb.open();
                        myCartdb.deleteproduct(verticalList.get(position).getId());
                        myCartdb.close();
                        holder.addtocart.setVisibility(View.VISIBLE);
                        holder.removefromcart.setVisibility(View.GONE);
                        HomeActivity.countproductoncart=HomeActivity.countproductoncart-1;
                        setBadgeCount(context, mCartMenuIcon, String.valueOf(HomeActivity.countproductoncart));
//                new ProductsActivity().controllcountincrease();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return verticalList.size();
            }


        }
    }

