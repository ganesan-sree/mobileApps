package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vegfreshbox.ecommerce.adapter.SubcategoriesAdapter;
import com.vegfreshbox.ecommerce.database.DatabaseUtil;
import com.vegfreshbox.ecommerce.database.MyCart;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;
import com.vegfreshbox.ecommerce.products.Categorys;
import com.vegfreshbox.ecommerce.utils.VegUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    String title;
    String id;
    // public static long countproductoncart=0;
    private RecyclerView horizontal_recycler_view, vertical_recycler_view;
    private ArrayList<String> horizontalList, verticalList;
    private SubcategoriesAdapter horizontalAdapter;
    private ProgressDialog mProgressDialog;
    ListView lv;
    ProductsAdapter productsAdapter;
    ArrayList<ProductPojo> productPojoArrayList;
    private LayerDrawable mCartMenuIcon;
    private MenuItem mSearchMenu, cart;
    ImageView addcart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            title = (String) bd.get("Name");
            id = (String) bd.get("id");
            setTitle(title);
        }


        mProgressDialog = new ProgressDialog(ProductsActivity.this);

        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        vertical_recycler_view = (RecyclerView) findViewById(R.id.vertical_recycler_view);

        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(
                ProductsActivity.this, LinearLayoutManager.VERTICAL, false);
        vertical_recycler_view.setLayoutManager(verticalLayoutmanager);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(
                ProductsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);


        Categorys cate = new Categorys(ProductsActivity.this);
        // SubcategoriesAdapter adapter = new SubcategoriesAdapter(null);
        productsAdapter = new ProductsAdapter(getApplicationContext(), cate.getProductList(id, readCategoryFile()));
        vertical_recycler_view.setAdapter(productsAdapter);

        // horizontal_recycler_view.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.home, menu);
        getMenuInflater().inflate(R.menu.cart, menu);

        mCartMenuIcon = (LayerDrawable) menu.findItem(R.id.action_cart)
                .getIcon();
        // mSearchMenu = (MenuItem) menu.findItem(R.id.action_search);
        cart = (MenuItem) menu.findItem(R.id.action_cart);

        cart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Toast.makeText(ProductsActivity.this,"Cart Click",Toast.LENGTH_LONG).show();
                Intent i = new Intent(ProductsActivity.this, CartActivity.class);
                finish();
                startActivity(i);
                return false;
            }
        });


        MyCart myCartdb = new MyCart(ProductsActivity.this);
        myCartdb.open();
        HomeActivity.countproductoncart = myCartdb.countproduct();

        Log.e("CART Count", String.valueOf(HomeActivity.countproductoncart));
        myCartdb.close();

        setBadgeCount(this, mCartMenuIcon,String.valueOf(HomeActivity.countproductoncart));
        return super.onCreateOptionsMenu(menu);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon,
                                     String count) {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.searchnavigationicon) {
            Intent i = new Intent(ProductsActivity.this, CartActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private String readCategoryFile() {
        String output = null;
        StringBuffer text = null;
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(openFileInput("category.json")));
            String line;
            text = new StringBuffer();
            while ((line = bReader.readLine()) != null) {
                text.append(line + "\n");
            }
            output = text.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;

    }

    public class ProductsAdapter extends
            RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

        private List<ProductPojo> verticalList;
        Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtView, price, wgt, product_quantity;
            ImageView profilePic, addtocart, removefromcart;
            LinearLayout quantityMan;

            public ImageView image, product_quantity_dec, product_quantity_inc;

            public MyViewHolder(View view) {
                super(view);
                txtView = (TextView) view.findViewById(R.id.product_list_name);
                profilePic = (ImageView) view.findViewById(R.id.img_product);
                // profilePic.setBackgroundResource(R.drawable.image_border);
                addtocart = (ImageView) view.findViewById(R.id.addtocart);
                removefromcart = (ImageView) view.findViewById(R.id.removefromcart);
                price = (TextView) view.findViewById(R.id.price);
                wgt = (TextView) view.findViewById(R.id.wgt);
                quantityMan = (LinearLayout) view.findViewById(R.id.qutysec);
                product_quantity = (TextView) view.findViewById(R.id.product_quantity);
                product_quantity_dec = (ImageView) view.findViewById(R.id.product_quantity_dec);
                product_quantity_inc = (ImageView) view.findViewById(R.id.product_quantity_inc);

            }
        }

        public ProductsAdapter(Context context, List<ProductPojo> verticalList) {
            this.verticalList = verticalList;
            this.context = context;

        }

        @Override
        public ProductsAdapter.MyViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.product_table_list_item, parent, false);
            return new ProductsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ProductsAdapter.MyViewHolder holder,
                                     final int position) {

            holder.txtView.setText(verticalList.get(position).getName());

            if (verticalList.get(position).getImagelocal() != null) {
                Picasso.with(context).load("file:///android_asset/"
                                + verticalList.get(position).getImagelocal())
                        .resize(250, 250).into(holder.profilePic);
            } else {
                Picasso.with(context).load(verticalList.get(position).getImage())
                        .resize(250, 250).into(holder.profilePic);
            }

            holder.price.setText(verticalList.get(position).getPrice());
            holder.wgt.setText(verticalList.get(position).getWgt());

            MyCart myCartdb = new MyCart(context);
            myCartdb.open();
            String[] Qty = myCartdb.checkQuantity(verticalList.get(position)
                    .getId());
            if (Qty == null) {
                holder.addtocart.setVisibility(View.VISIBLE);
                holder.quantityMan.setVisibility(View.GONE);
                // holder.removefromcart.setVisibility(View.GONE);
            } else {
                // holder.removefromcart.setVisibility(View.VISIBLE);
                holder.quantityMan.setVisibility(View.VISIBLE);
                holder.addtocart.setVisibility(View.GONE);
                holder.product_quantity.setText(String.valueOf(Qty[0]));
                // holder.wgt.setText(String.valueOf(Qty[1]));
                verticalList.get(position).setQuantity(String.valueOf(Qty[0]));
                verticalList.get(position).setWgt(String.valueOf(Qty[1]));
            }
            myCartdb.close();
            // END PRODUCT CHECK

            holder.addtocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Added to cart successfully.",Toast.LENGTH_SHORT).show();

                    byte[] utf8Bytes;
                    String name = null;

                    try {
                        utf8Bytes = verticalList.get(position).getName().getBytes("UTF8");
                        name = new String(utf8Bytes, "UTF8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("error","",e);
                    }

                    DatabaseUtil.createProduct(context,
                            verticalList.get(position).getId(), name,
                            verticalList.get(position).getImage(), verticalList.get(position).getImagelocal(),
                            verticalList.get(position).getPrice(), verticalList.get(position).getWgt(), 1);

                    // holder.removefromcart.setVisibility(View.VISIBLE);
                    holder.quantityMan.setVisibility(View.VISIBLE);
                    holder.product_quantity.setText(String.valueOf(1));
                    holder.addtocart.setVisibility(View.GONE);
                    HomeActivity.countproductoncart = HomeActivity.countproductoncart + 1;
                    setBadgeCount(context, mCartMenuIcon,String.valueOf(HomeActivity.countproductoncart));

                }
            });

            holder.removefromcart
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Product deleted from your cart.", Toast.LENGTH_SHORT).show();
                            DatabaseUtil.deleteProduct(context, verticalList.get(position).getId());
                            holder.addtocart.setVisibility(View.VISIBLE);
                            holder.removefromcart.setVisibility(View.GONE);
                            HomeActivity.countproductoncart = HomeActivity.countproductoncart - 1;
                            setBadgeCount(context, mCartMenuIcon, String.valueOf(HomeActivity.countproductoncart));
                            // new ProductsActivity().controllcountincrease();
                        }
                    });

            holder.product_quantity_inc
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int qty = 1;
                            String productId = verticalList.get(position).getId();
                            String qtys = verticalList.get(position).getQuantity();
                            String wgt = verticalList.get(position).getWgt();
                            if (qtys != null) {
                                qty = Integer.parseInt(qtys);
                            }
                            Log.e(productId, wgt);
                            Log.e(productId, "" + qty);
                            wgt = VegUtils.getWeight(wgt, qty);

                            qty = qty + 1;

                            verticalList.get(position).setQuantity(String.valueOf(qty));
                            verticalList.get(position).setWgt(String.valueOf(wgt));
                            //Toast.makeText(context, "Qty added" + qty,
                            //		Toast.LENGTH_SHORT).show();
                            holder.product_quantity.setText(String.valueOf(qty));
                            // holder.wgt.setText(String.valueOf(wgt));
                            DatabaseUtil.updateQtyAndWgt(context, verticalList.get(position).getId(), wgt, qty);
                            // CartActivity.updatetotal(100);
                            // context.notifyDataSetChanged();

                        }
                    });

            holder.product_quantity_dec
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int qty = Integer.parseInt(verticalList.get(position).getQuantity());
                            if (qty == 1) {
                                DatabaseUtil.deleteProduct(context, verticalList.get(position).getId());
                                holder.addtocart.setVisibility(View.VISIBLE);
                                holder.quantityMan.setVisibility(View.GONE);
                                HomeActivity.countproductoncart = HomeActivity.countproductoncart - 1;
                                setBadgeCount(context, mCartMenuIcon, String.valueOf(HomeActivity.countproductoncart));
                            } else {

                                String wgt = verticalList.get(position).getWgt();
                                wgt = VegUtils.getWeightDec(wgt, qty);
                                qty = qty - 1;
                                verticalList.get(position).setWgt(String.valueOf(wgt));
                                //holder.wgt.setText(String.valueOf(wgt));
                                verticalList.get(position).setQuantity(String.valueOf(qty));
                                holder.product_quantity.setText(String.valueOf(qty));
                                DatabaseUtil.updateQtyAndWgt(context, verticalList.get(position).getId(), wgt, qty);
                            }
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return verticalList.size();
        }


    }

}
