package com.vegfreshbox.ecommerce;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.gms.flags.impl.DataUtils;
import com.vegfreshbox.ecommerce.app.CustomRequest;

import net.thegreshams.firebase4j.demo.UserService;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText first_name, last_name, email, password;
    Button btnlogin, btnregister;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        mProgressDialog = new ProgressDialog(RegisterActivity.this);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnregister = (Button) findViewById(R.id.btnregister);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, Login.class);
                finish();
                startActivity(intent);
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = dataValidate();

                if (message != null) {
                    Toast.makeText(getApplicationContext(),
                            message,
                            Toast.LENGTH_SHORT).show();
                } else {
                    new FireBaseService().execute("");
                }


            }


        });
    }

    boolean isEmpty(EditText text) {
        CharSequence ch = text.getText().toString();
        return TextUtils.isEmpty(ch);
    }

    boolean isEmailValid(EditText text) {
        CharSequence ch = text.getText().toString();
        return (!TextUtils.isEmpty(ch) && Patterns.EMAIL_ADDRESS.matcher(ch).matches());
    }

    String dataValidate() {
        String message = null;
        if (isEmpty(first_name)) {
            message = "First Name is required";
        } else if (isEmpty(last_name)) {
            message = "Last Name is Required";
        } else if (!isEmailValid(email)) {
            message = "Email Address is not Valid";
        } else if (isEmpty(password)) {
            message = "Password is Required";
        } else if (!isEmpty(password) && (password.getText().toString()).length() < 4) {
            message = "Password length should be min 4 char";
        }
        return message;
    }

    private class FireBaseService extends AsyncTask<String, Void, String> {

        FirebaseResponse res = null;
        boolean isEmailExist = false;

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
            Log.e("Registration", "registration Started" + params[0]);
            if (!userService.isEmailAvailble(email.getText()
                    .toString().trim())) {
                Log.e("Registration", "registration started going to create new user" + email.getText());

                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
                    String token = sharedPreferences.getString("token", null);
                    Log.e("token", token);
                    res = userService.createUser(first_name.getText()
                                    .toString().trim(), last_name.getText().toString()
                                    .trim(), email.getText().toString().trim(),
                            password.getText().toString().trim(), token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                isEmailExist = true;
            }

            return "Executed!";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.e("Registrated", "Done Registration.==" + result);
            mProgressDialog.dismiss();
            if (res == null) {

                if (isEmailExist) {
                    Toast.makeText(RegisterActivity.this,
                            "Email Address is Already Available!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Register Failed, Please try again",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                mProgressDialog.dismiss();
                Log.e("Status of registration", "" + res.getCode());
                Log.e("Response of registation", res.getRawBody());
                Toast.makeText(RegisterActivity.this,
                        "Register successfully, Please Login",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this, Login.class);
                finish();
                startActivity(intent);
            }
            mProgressDialog.dismiss();

        }
    }

}
