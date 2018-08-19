package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vegfreshbox.ecommerce.adapter.HomeCategoryAdapter;
import com.vegfreshbox.ecommerce.database.DatabaseUtil;
import com.vegfreshbox.ecommerce.products.Categorys;

import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements
		NavigationView.OnNavigationItemSelectedListener
		  {

	private LayerDrawable mCartMenuIcon;
	private MenuItem mSearchMenu, cart;
	private int mCartCount;
	Button search;
	private RecyclerView recyclerView;
	private ProgressDialog mProgressDialog;
	String categoryFile=null;

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
				Log.e(key,"getting push notification");
				//String value = getIntent().getExtras().getString(key);
				if (key.equals("AnotherActivity") && getIntent().getExtras().getString(key).equals("True")) {
					Intent intent = new Intent(this, AnotherActivity.class);
					//intent.putExtra("value", getIntent().getExtras().getString(key));
					startActivity(intent);
					finish();
				}

			}
		}

		FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
			@Override
			public void onComplete(@NonNull Task<InstanceIdResult> task) {
				if (!task.isSuccessful()) {
					Log.w(TAG, "getInstanceId failed", task.getException());
					return;
				}
				// Get new Instance ID token
				String token = task.getResult().getToken();
				// Log and toast
				//String msg = getString(R.string.msg_token_fmt, token);
				Log.d(TAG, token);
				
			}
		});;


		countproductoncart = DatabaseUtil.getCartCount(HomeActivity.this);



		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
				toolbar, R.string.navigation_drawer_open,
				R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		// NavigationView navigationView = (NavigationView)
		// findViewById(R.id.nav_view);

		initViews();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}



	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.home, menu);
		getMenuInflater().inflate(R.menu.cart, menu);

		mCartMenuIcon = (LayerDrawable) menu.findItem(R.id.action_cart)
				.getIcon();
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
			Intent i = new Intent(HomeActivity.this, OrderHistory.class);
			startActivity(i);

		}
		 else if (id == R.id.nav_send) { SharedPreferences sharedPreferences =
		  getSharedPreferences("loginstate", MODE_PRIVATE);
		  SharedPreferences.Editor editor = sharedPreferences.edit();
		  editor.putString("islogin", "");
		  editor.putString("userid","");
			editor.putString("userData",null);

		  editor.commit();
			Toast.makeText(getApplicationContext(),
					"Log Out Successfully",
					Toast.LENGTH_LONG).show();

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	protected void onStop() {
		// To prevent a memory leak on rotation, make sure to call
		// stopAutoCycle() on the slider before activity or fragment is
		// destroyed
		// mDemoSlider.stopAutoCycle();
		super.onStop();
		countproductoncart =DatabaseUtil.getCartCount(HomeActivity.this);

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

		countproductoncart = DatabaseUtil.getCartCount(HomeActivity.this);

		invalidateOptionsMenu();


	}


	void handleFireBaseData() {


		Log.e("CallingFirebase","firebase category.json");

		FirebaseStorage storage = FirebaseStorage.getInstance();
		StorageReference storageRef = storage.getReference();
		StorageReference pathReference = storageRef.child("category.json");

		final long ONE_MEGABYTE = 256 * 256;
		pathReference.getBytes(ONE_MEGABYTE)
				.addOnSuccessListener(new OnSuccessListener<byte[]>() {
					@Override
					public void onSuccess(byte[] bytes) {
						// Data for "images/island.jpg" is returns, use this as
						// needed
						//String str = new String(bytes);
						try {
							FileOutputStream fos = openFileOutput(
									"category.json", Context.MODE_PRIVATE);
							fos.write(bytes);
							fos.close();
						} catch (Exception e) {
							Log.e("Error writing file", e.getMessage());
						}
						//Log.e("date", str);
					}
				}).addOnFailureListener(new OnFailureListener() {

					@Override
					public void onFailure(@NonNull Exception exception) {
					Log.e("Error onfirebase ","expection while retriving ",exception);
					}
				});



	}

	private String readCategoryFile() {
		String output = null;
		StringBuffer text = null;
		try {

			if(fileExists(HomeActivity.this,"category.json")) {
				BufferedReader bReader = new BufferedReader(new InputStreamReader(
						openFileInput("category.json")));
				String line;
				text = new StringBuffer();
				while ((line = bReader.readLine()) != null) {
					text.append(line + "\n");
				}
				output = text.toString();
			}else{
				readCategoryFireBaseDatabase();
				Log.e("File not found","Firebase file not found11111111111111111111111"+output);
			}

		} catch (IOException e) {
			Log.e("error parsing ","category json file",e);
			readCategoryFireBaseDatabase();
		}
		return output;

	}
	
	
	private void readCategoryFireBaseDatabase() {
		FireBaseService f = new FireBaseService();

		try {
			f.execute();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	


			  public boolean fileExists(Context context, String filename) {
				  File file = context.getFileStreamPath(filename);
				  if(file == null || !file.exists()) {
					  return false;
				  }
				  return true;
			  }







			  private class FireBaseService extends AsyncTask<String, Void, String> {

				  private ProgressDialog mProgressDialog;
				  FirebaseResponse res = null;
				  boolean isEmailExist = false;
				  String category=null;

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
						  output=userService.getCategory();
						  Thread.sleep(5);
						  if(output !=null) {
							  category= output.getRawBody();
							  try {
								  FileOutputStream fos = openFileOutput(
										  "category.json", Context.MODE_PRIVATE);
								  fos.write(category.getBytes());
								  fos.close();
							  } catch (Exception e) {
								  Log.e("Error writing file", e.getMessage());
							  }


						  }
						  Log.e("Database","=== "+category);
					  } catch (Exception e) {
						  Log.e("error parsing ","category json file",e);
					  }

					  return "";

				  }

				  @Override
				  protected void onPostExecute(String result) {
					  super.onPostExecute(result);

					  Log.e("Registrated", "Done Registration.==" + result);
					  mProgressDialog.dismiss();


					  mProgressDialog.dismiss();

				  }
			  }

}
