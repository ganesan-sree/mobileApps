package com.vegfreshbox.ecommerce.products;

import android.content.Context;
import android.util.Log;

import com.vegfreshbox.ecommerce.pojo.MasterCategory;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Categorys {

	final Context context;

	public Categorys(Context con) {
		this.context = con;

	}

	public String loadJSONFromAsset() {
		String json = null;
		try {
			InputStream is = context.getAssets().open("category.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");

			//System.out.println(json);
		} catch (IOException ex) {
			Log.e("==RESPONSE ERROR=111111111111", ex.toString());
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	public ArrayList<MasterCategory> getCategoryList(String categoryString) {

		//Log.e("Categoryfilefirebase",""+categoryString);
		String jsonString = null;
		if (categoryString != null) {
			jsonString = categoryString;

		} else {
			jsonString = loadJSONFromAsset();
			Log.e("Categoryfileassets","lading from assets folder");
		}

		//Log.e("Categoryfilegoto",jsonString);

		JSONObject jsonObject = null;
		
		if(parseJsonError(jsonString)){
			jsonString = loadJSONFromAsset();
		}
		
		ArrayList<MasterCategory> arrayList = new ArrayList<MasterCategory>();
		try {

			jsonObject = new JSONObject(jsonString);

			JSONArray msg = (JSONArray) jsonObject.get("category");
			for (int i = 0; i < msg.length(); i++) {

				//System.out.println(msg.getJSONObject(i));
				MasterCategory masterCategory = new MasterCategory();
				masterCategory.setId((String) msg.getJSONObject(i).get("id"));
				masterCategory.setName((String) msg.getJSONObject(i)
						.get("name"));
				masterCategory.setImage((String) msg.getJSONObject(i).get(
						"image"));
				if( msg.getJSONObject(i).has("imageLocal")) {
				masterCategory.setImageLocal((String) msg.getJSONObject(i).get(
						"imageLocal"));
				}
				
				arrayList.add(masterCategory);
			}

		} catch (JSONException e) {
			Log.e("JSON Error", "Errorrororrrrrrrrrrrrrrrrrrr");
		}



		return arrayList;

	}

	public ArrayList<ProductPojo> getProductList(String categoryid,String categoryList) {
		
		String jsonString = null;
		if (categoryList != null) {
			jsonString = categoryList;

		} else {
			jsonString = loadJSONFromAsset();
		}
		JSONObject jsonObject = null;
		
		if(parseJsonError(jsonString)){
			jsonString = loadJSONFromAsset();
		}
		
		
		ArrayList<ProductPojo> productPojoArrayList = new ArrayList<ProductPojo>();

		try {
			 jsonObject = new JSONObject(jsonString);

			JSONArray msg = (JSONArray) jsonObject.get("category");
			for (int i = 0; i < msg.length(); i++) {

				String jsonCatid = (String) msg.getJSONObject(i).get("id");

				if (categoryid != null && categoryid.equals(jsonCatid)) {
					productPojoArrayList = formProduct((JSONArray) msg
							.getJSONObject(i).get("products"),
							productPojoArrayList);
				}

			}
		} catch (JSONException e) {
			Log.e("JSON Error", "Errro while parsing category list");
		}

		return productPojoArrayList;

	}

	private ArrayList<ProductPojo> formProduct(JSONArray products,
			ArrayList<ProductPojo> productPojoArrayList) throws JSONException {

		JSONArray jsonArray2 = products;

		for (int i = 0; i < jsonArray2.length(); i++) {
			JSONObject jsonObject = jsonArray2.getJSONObject(i);

			ProductPojo productPojo = new ProductPojo();
			productPojo.setId(jsonObject.getString("id"));
			productPojo.setName(jsonObject.getString("name"));
			productPojo.setImage(jsonObject.getString("image"));
			productPojo.setPrice(jsonObject.getString("price"));
			productPojo.setQuantity("1");
			//productPojo.setOurprice(jsonObject.getString("our_price"));
			//productPojo.setOurprice(jsonObject.getString("our_price"));
			productPojo.setWgt(jsonObject.getString("weight"));

			if(jsonObject.has("imageLocal")) {
				productPojo.setImagelocal(jsonObject.getString("imageLocal"));
			}
			// Log.e("JSON TEST", jsonObject.getString("id"));

			productPojoArrayList.add(productPojo);

		}
		return productPojoArrayList;

	}

	boolean parseJsonError(String jsonString) {
		boolean status = false;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
			JSONArray msg = (JSONArray) jsonObject.get("category");

		} catch (JSONException e) {
			Log.e("JSON Error", "Json Parse Error");

			status = true;
		}
		return status;
	}

}
