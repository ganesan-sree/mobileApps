package com.vegfreshbox.ecommerce.database;

import android.content.Context;

public class DatabaseUtil {

	public static void deleteProduct(Context context, String productId) {
		MyCart myCartdb = new MyCart(context);
		myCartdb.open();
		myCartdb.deleteproduct(productId);
		myCartdb.close();
	}

	public static void updateQtyAndWgt(Context context, String productId,
			String wgt, int qty) {
		MyCart myCartdb = new MyCart(context);
		myCartdb.open();
		myCartdb.updateQuantity(productId, String.valueOf(qty));
		myCartdb.updateWgt(productId, String.valueOf(wgt));
		myCartdb.close();
	}

	public static void createProduct(Context context, String productId,
			String name, String img, String imgLocal, String price, String wgt,
			int qty) {
		MyCart myCartdb = new MyCart(context);
		myCartdb.open();
		myCartdb.insertData(productId, name, img, imgLocal, price, wgt,
				String.valueOf(qty));
		myCartdb.close();
	}


	public static long getCartCount(Context context) {
		long countproductoncart=0;
		MyCart myCartdb = new MyCart(context);
		myCartdb.open();
		countproductoncart = myCartdb.countproduct();
		myCartdb.close();
		return countproductoncart;
	}

}
