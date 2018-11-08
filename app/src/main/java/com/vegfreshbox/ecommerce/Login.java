package com.vegfreshbox.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.vegfreshbox.ecommerce.app.CustomRequest;
import com.vegfreshbox.ecommerce.pojo.SubCategoryPojo;

import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button btn_register,btn_login;
    private ProgressDialog mProgressDialog;
    EditText email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        mProgressDialog = new ProgressDialog(Login.this);
        btn_register=(Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });


        getSupportActionBar().hide();

        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FireBaseService().execute("");
            }
        });
    }





    private class FireBaseService extends AsyncTask<String, Void, String> {

        String userId=null;
        String userData=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.loading));
        }

        @Override
        protected String doInBackground(String... params) {

            final UserService userService = new UserService();
           // Log.e("Login", "login started1");
            userId=userService.isCredentialValid(email.getText().toString().trim(),password.getText().toString().trim());
            //Log.e("Login", "Login userid"+userId);
			if (userId != null) {
				FirebaseResponse res = userService.getUserById(userId);
				userData=res.getRawBody();
			}
            
            return "Executed!";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

           // Log.e("Login", "Login process done.==" + result);

            if(userId!=null){
                SharedPreferences sharedPreferences = getSharedPreferences("loginstate", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("islogin", "1");
                editor.putString("userid",userId);
                if(userData!=null){
                	editor.putString("userData",userData);
                }
                editor.commit();
                Intent intent=new Intent(Login.this,CartActivity.class);
                finish();
                startActivity(intent);
            }else{
                Toast.makeText(Login.this,"Login Failed",Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();

        }
    }
}
