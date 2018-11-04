package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.flags.impl.DataUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vegfreshbox.ecommerce.adapter.HomeCategoryAdapter;
import com.vegfreshbox.ecommerce.database.DatabaseUtil;
import com.vegfreshbox.ecommerce.products.Categorys;
import com.vegfreshbox.ecommerce.utils.VegUtils;
import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private LayerDrawable mCartMenuIcon;
    private MenuItem mSearchMenu, cart;
    private int mCartCount;
    Button search;
    private RecyclerView recyclerView;
    private ProgressDialog mProgressDialog;
    String categoryFile = null;
    public static long countproductoncart = 0;
    RelativeLayout notificationCount1;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // handleFireBaseData();

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Log.e(key, "getting push notification");
                if (key.equals("AnotherActivity") && getIntent().getExtras().getString(key).equals("true")) {
                    Intent intent = new Intent(this, AnotherActivity.class);
                    intent.putExtra("message", getIntent().getExtras().getString("message"));
                    startActivity(intent);
                    finish();
                }
            }
        }

        final SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        String tok = sharedPreferences.getString("token", null);
        if (VegUtils.isBlank(tok)) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", token);
                    editor.commit();
                    Log.d(TAG, token);
                }
            });
        }

        countproductoncart = DatabaseUtil.getCartCount(HomeActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initViews();
    }


    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        setBadgeCount(this, mCartMenuIcon, String.valueOf(countproductoncart));
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.home, menu);
        getMenuInflater().inflate(R.menu.cart, menu);

        mCartMenuIcon = (LayerDrawable) menu.findItem(R.id.action_cart).getIcon();
        cart = (MenuItem) menu.findItem(R.id.action_cart);
        cart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(i);
                return false;
            }
        });
        setBadgeCount(this, mCartMenuIcon, String.valueOf(countproductoncart));
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.searchnavigationicon) {
            Intent i = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.searchnavigationicon) {
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_order) {

            SharedPreferences sharedPreferences = getSharedPreferences("loginstate", Context.MODE_PRIVATE);
            if (sharedPreferences.getString("islogin", "").equals("1")) {
                Intent i = new Intent(HomeActivity.this, OrderHistory.class);
                startActivity(i);
            } else {
                Intent intent = new Intent(HomeActivity.this, Login.class);
                this.finish();
                startActivity(intent);
            }

        } else if (id == R.id.logout) {

            SharedPreferences sharedPreferences =getSharedPreferences("loginstate", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("islogin", "");
            editor.putString("userid", "");
            editor.putString("userData", null);
            editor.commit();
            Toast.makeText(getApplicationContext(),"Log Out Successfully", Toast.LENGTH_LONG).show();
            NavigationView view=(drawer.findViewById(R.id.nav_view));
            view.getMenu().findItem(R.id.logout).setVisible(false);

        }else if (id == R.id.contact_us) {
            Intent i = new Intent(HomeActivity.this, AboutUs.class);
            startActivity(i);
        }

     //   else if (id == R.id.writetous) {
      //     initChatSDK();
      //      Intent i = new Intent("com.vegfreshbox.ecommerce.chat.SplashActivity");
      //      startActivity(i);
      //  }


        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    // RECYCLE VIEW
    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(
                getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        Categorys cate = new Categorys(HomeActivity.this);
        HomeCategoryAdapter adapter = new HomeCategoryAdapter(
                getApplicationContext(),
                cate.getCategoryList(readCategoryFile()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if(mCartMenuIcon !=null){
            setBadgeCount(this, mCartMenuIcon, String.valueOf(countproductoncart));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view=(drawer.findViewById(R.id.nav_view));

        SharedPreferences sharedPreferences = getSharedPreferences("loginstate", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("islogin", "").equals("1")) {
            view.getMenu().findItem(R.id.logout).setVisible(true);
        }else{
            view.getMenu().findItem(R.id.logout).setVisible(false);
        }
    }



    private String readCategoryFile() {
        String output = null;
        StringBuffer text = null;
        try {
            output=getCatalog();
            if (VegUtils.isBlank(output)) {
                readCategoryFireBaseDatabase();
                Log.e("File not found", "Firebase Storage file not Found");
            }
        } catch (Exception e) {
            Log.e("error parsing ", "category json file", e);
            readCategoryFireBaseDatabase();
        }
        return output;
    }


    private void readCategoryFireBaseDatabase() {
        FireBaseService f = new FireBaseService();
        try {
            f.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FireBaseService extends AsyncTask<String, Void, String> {

        private ProgressDialog mProgressDialog;
        String category = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(HomeActivity.this);
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.loading));
        }

        @Override
        protected String doInBackground(String... params) {
            final UserService userService = new UserService();
            FirebaseResponse output = null;
            try {
                output = userService.getCategory();
                Thread.sleep(5);
                if (output != null) {
                    category = output.getRawBody();
                    try {
                        storeCatalog(category.getBytes());
                        Log.e("FileLoading","loading using database"+category.getBytes());
                    } catch (Exception e) {
                        Log.e("Error writing file", e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.e("error parsing ", "category json file", e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e(TAG, "CAlling category done!");
            mProgressDialog.dismiss();
        }
    }


    private void storeCatalog(byte[] bytes) throws UnsupportedEncodingException {
        String catalog = new String(bytes, "UTF-8");
        SharedPreferences sharedPreferences =getSharedPreferences("loginstate", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("catalog", catalog);
        editor.commit();
    }

    private String getCatalog() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginstate", MODE_PRIVATE);
        String catalog = sharedPreferences.getString("catalog", null);
        return catalog;
    }
}
