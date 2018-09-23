package com.vegfreshbox.ecommerce.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ganesh on 02/01/17.
 */

public class MyCart {
    final static String DATABASE_NAME = "mycart";
    final static int DATABASE_VERSION = 1;
    final static String DATABASE_CREATE = "create table product_list(id integer primary key autoincrement,"
            + "productid text,productname text,productimage text,imagelocal text,productprice text,productwgt text,productquantity text)";
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    final Context context;

    public MyCart(Context con) {
        this.context = con;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        if (dbHelper != null) {
            dbHelper = new DatabaseHelper(context);
            db = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        db.close();
    }

    public long insertData(String productid, String productname,
                           String productimage, String imagelocal, String productprice, String productwgt,
                           String productquantity) {
        ContentValues values = new ContentValues();

        values.put("productid", productid);
        values.put("productname", productname);
        values.put("productimage", productimage);
        values.put("imagelocal", imagelocal);
        values.put("productprice", productprice);
        values.put("productwgt", productwgt);
        values.put("productquantity", productquantity);

        return db.insert("product_list", null, values);

    }

    public void updateQuantity(String productid, String quantity) {
        ContentValues cv = new ContentValues();
        cv.put("productquantity", quantity); // These Fields should be your
        // String values of actual
        // column names
        db.update("product_list", cv, "productid=" + productid, null);
    }

    public void updateWgt(String productid, String wgt) {
        ContentValues cv = new ContentValues();
        cv.put("productwgt", wgt); // These Fields should be your
        // String values of actual
        // column names
        db.update("product_list", cv, "productid=" + productid, null);
    }

    public Cursor getAllData() {
        return db.query(true, "product_list", new String[]{"id", "productid",
                        "productname", "productimage", "imagelocal", "productprice",
                        "productwgt", "productquantity"}, null, null, null, null,
                "productname" + " ASC", null);
    }

    public void deleteAllData() {
        // SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("product_list", null, null);
        db.close();
    }

    public int checkAvailable(String product_id) {
        Cursor cursor = null;
        String sql = "SELECT * FROM product_list WHERE productid='"
                + product_id + "'";
        cursor = db.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            // PID Found
            cursor.close();
            return 1;
        } else {
            // PID Not Found
            cursor.close();
            return 0;
        }
    }

    public String[] checkQuantity(String product_id) {
        Cursor cursor = null;
        String qty[] = null;
        String sql = "SELECT productquantity,productwgt FROM product_list WHERE productid='"
                + product_id + "'";
        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            // PID Found
            qty = new String[2];
            qty[0] = cursor.getString(0);
            qty[1] = cursor.getString(1);
            cursor.close();
            return qty;
        } else {
            // PID Not Found
            cursor.close();
            return null;
        }
    }

    public boolean deleteproduct(String product_id) {
        return db.delete("product_list", "productid" + "='" + product_id + "'",
                null) > 0;
    }

    public long countproduct() {

        long count = DatabaseUtils.queryNumEntries(db, "product_list");

        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            try {
                database.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS mycart");

            onCreate(db);
        }
    }
}
