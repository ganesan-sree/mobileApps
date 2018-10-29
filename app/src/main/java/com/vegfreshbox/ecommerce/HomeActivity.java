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
import com.vegfreshbox.ecommerce.chat.SplashActivity;
import com.vegfreshbox.ecommerce.database.DatabaseUtil;
import com.vegfreshbox.ecommerce.products.Categorys;
import com.vegfreshbox.ecommerce.utils.VegUtils;

import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import org.chat21.android.core.ChatManager;
import org.chat21.android.core.users.models.IChatUser;
import org.chat21.android.ui.ChatUI;
import org.chat21.android.ui.contacts.activites.ContactListActivity;
import org.chat21.android.ui.conversations.listeners.OnNewConversationClickListener;
import org.chat21.android.ui.messages.listeners.OnMessageClickListener;
import org.chat21.android.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.chat21.android.core.ChatManager._SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER;

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

        handleFireBaseData();

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

        if (id == R.id.searchnavigationicon) {
            // Handle the camera action
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

        } else if (id == R.id.nav_send) {

            SharedPreferences sharedPreferences =getSharedPreferences("loginstate", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("islogin", "");
            editor.putString("userid", "");
            editor.putString("userData", null);
            editor.commit();
            Toast.makeText(getApplicationContext(),"Log Out Successfully", Toast.LENGTH_LONG).show();

        }else if (id == R.id.contact_us) {
            Intent i = new Intent(HomeActivity.this, AboutUs.class);
            startActivity(i);
        }

     //   else if (id == R.id.writetous) {
      //     initChatSDK();
      //      Intent i = new Intent("com.vegfreshbox.ecommerce.chat.SplashActivity");
      //      startActivity(i);
      //  }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        setBadgeCount(this, mCartMenuIcon, String.valueOf(countproductoncart));
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
       // countproductoncart = DatabaseUtil.getCartCount(HomeActivity.this);
        setBadgeCount(this, mCartMenuIcon, String.valueOf(countproductoncart));
    }


    void handleFireBaseData() {
        Log.e("CallingFirebase", "firebase category.json");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("category.json");

        final long ONE_MEGABYTE = 256 * 256;
        pathReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        try {
                            FileOutputStream fos = openFileOutput(
                                    "category.json", Context.MODE_PRIVATE);
                            fos.write(bytes);
                            fos.close();
                        } catch (Exception e) {
                            Log.e("Error writing file", e.getMessage());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Error onfirebase ", "expection while retriving ", exception);
            }
        });
    }

    private String readCategoryFile() {
        String output = null;
        StringBuffer text = null;
        try {

            if (fileExists(HomeActivity.this, "category.json")) {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(
                        openFileInput("category.json")));
                String line;
                text = new StringBuffer();
                while ((line = bReader.readLine()) != null) {
                    text.append(line + "\n");
                }
                output = text.toString();
            } else {
                readCategoryFireBaseDatabase();
                Log.e("File not found", "Firebase Storage file not Found");
            }

        } catch (IOException e) {
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


    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public void initChatSDK() {

        //enable persistence must be made before any other usage of FirebaseDatabase instance.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // it creates the chat configurations
        ChatManager.Configuration mChatConfiguration =
                new ChatManager.Configuration.Builder(getString(R.string.chat_firebase_appId))
                        .firebaseUrl(getString(R.string.chat_firebase_url))
                        .storageBucket(getString(R.string.chat_firebase_storage_bucket))
                        .build();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // assuming you have a login, check if the logged user (converted to IChatUser) is valid
//        if (currentUser != null) {
        if (currentUser != null) {
            IChatUser iChatUser = (IChatUser) IOUtils.getObjectFromFile(getBaseContext(),
                    _SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER);

//            IChatUser iChatUser = new ChatUser();
//            iChatUser.setId(currentUser.getUid());
//            iChatUser.setEmail(currentUser.getEmail());

            ChatManager.start(this, mChatConfiguration, iChatUser);
            Log.i(TAG, "chat has been initialized with success");

//            ChatManager.getInstance().initContactsSyncronizer();

            ChatUI.getInstance().setContext(getBaseContext());
            ChatUI.getInstance().enableGroups(true);

            ChatUI.getInstance().setOnMessageClickListener(new OnMessageClickListener() {
                @Override
                public void onMessageLinkClick(TextView message, ClickableSpan clickableSpan) {
                    String text = ((URLSpan) clickableSpan).getURL();

                    Uri uri = Uri.parse(text);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);
                }
            });

            // set on new conversation click listener
//            final IChatUser support = new ChatUser("support", "Chat21 Support");
            final IChatUser support = null;
            ChatUI.getInstance().setOnNewConversationClickListener(new OnNewConversationClickListener() {
                @Override
                public void onNewConversationClicked() {
                    if (support != null) {
                        ChatUI.getInstance().openConversationMessagesActivity(support);
                    } else {
                        Intent intent = new Intent(getBaseContext(), ContactListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // start activity from context

                        startActivity(intent);
                    }
                }
            });

//            // on attach button click listener
//            ChatUI.getInstance().setOnAttachClickListener(new OnAttachClickListener() {
//                @Override
//                public void onAttachClicked(Object object) {
//                    Toast.makeText(instance, "onAttachClickListener", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // on create group button click listener
//            ChatUI.getInstance().setOnCreateGroupClickListener(new OnCreateGroupClickListener() {
//                @Override
//                public void onCreateGroupClicked() {
//                    Toast.makeText(instance, "setOnCreateGroupClickListener", Toast.LENGTH_SHORT).show();
//                }
//            });
            Log.i(TAG, "ChatUI has been initialized with success");

        } else {
            Log.w(TAG, "chat can't be initialized because chatUser is null");
        }
    }

    private class FireBaseService extends AsyncTask<String, Void, String> {

        private ProgressDialog mProgressDialog;
        FirebaseResponse res = null;
        boolean isEmailExist = false;
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
                        FileOutputStream fos = openFileOutput("category.json", Context.MODE_PRIVATE);
                        fos.write(category.getBytes());
                        fos.close();
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


}
