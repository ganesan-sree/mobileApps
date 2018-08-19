package com.vegfreshbox.ecommerce.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VegUtils {

	private  static List<String> parseWgt(String toParse) {
		List<String> chunks = new LinkedList<String>();
		Pattern VALID_PATTERN1 = Pattern.compile("[0-9]+|[A-Za-z]+");
		Matcher matcher = VALID_PATTERN1.matcher(toParse);
		while (matcher.find()) {
			chunks.add(matcher.group());
		}

		return chunks;
	}

	public static String getWeight(String weightText, int qty) {
		int weigth = 1;
		String mesure = "g";

		try {
			List<String> li = parseWgt(weightText);
			Log.e("Parsing wgt +", "" + li);
			weigth = Integer.parseInt(li.get(0));
			mesure = li.get(1);
			weigth = weigth / qty;
			qty = qty + 1;
			weigth = weigth * qty;

		} catch (Exception e) {
			Log.i("parsing wgt", "failed");
		}
		return "" + weigth + "" + mesure;
	}

	public  static String getWeightDec(String weightText, int qty) {
		int weigth = 1;
		String mesure = "g";

		try {
			List<String> li = parseWgt(weightText);
			Log.e("Parsing wgt -", "" + li);
			weigth = Integer.parseInt(li.get(0));
			mesure = li.get(1);
			weigth = weigth / qty;
			qty = qty - 1;
			weigth = weigth * qty;
		} catch (Exception e) {
			Log.i("parsing wgt", "failed");
		}
		return "" + weigth + "" + mesure;
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}


	public static String getSubTotal(String price, String qty){

		if(price !=null && qty!=null) {
			return ""+(Integer.parseInt(price)) * (Integer.parseInt(qty));
		}
		return "";
	}
}
