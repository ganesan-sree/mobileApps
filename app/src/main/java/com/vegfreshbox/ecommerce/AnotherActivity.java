package com.vegfreshbox.ecommerce;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Ganesh on 20-Aug-16.
 */
public class AnotherActivity extends AppCompatActivity {

    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        date = (TextView) findViewById(R.id.informationTextView);
        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                Log.e(key,"getting push notification");
                if (key.equals("message")) {
                    Log.e("Message", getIntent().getExtras().getString(key));
                    date.setText(getIntent().getExtras().getString(key));
                }

            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}

