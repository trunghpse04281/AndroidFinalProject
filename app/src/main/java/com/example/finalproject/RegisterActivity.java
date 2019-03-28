package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.button.MaterialButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.entities.Category;
import com.example.services.Constants;
import com.example.services.Entity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private String currentUser;
    private DrawerLayout drawerLayout;

    private TextInputEditText editUserName;
    private TextInputEditText editPassword;
    private TextInputEditText editRePassword;
    private TextInputEditText editPhoneNumb;
    private MaterialButton btnRegister;


    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        currentUser = Entity.getCurrentUser(RegisterActivity.this);
        if (currentUser.equalsIgnoreCase("")) {
            initDrawer(R.menu.not_login_drawer_view);
        } else {
            initDrawer(R.menu.logged_in_drawer_view);
        }
        connectView();
    }

    public void initDrawer(int menuId) {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.inflateMenu(menuId);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);

                        if (menuItem.toString().equalsIgnoreCase("login")) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("logout")) {
                            Entity.deleteCurrentUser(RegisterActivity.this);
                            Intent intent = new Intent(RegisterActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (menuItem.toString().equalsIgnoreCase("register")) {
                            Entity.deleteCurrentUser(RegisterActivity.this);
                            Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("home")) {
                            Intent intent = new Intent(RegisterActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                        }else if (menuItem.toString().equalsIgnoreCase("My Product")) {
                            Intent intent = new Intent(RegisterActivity.this, ViewMyProductActivity.class);
                            startActivity(intent);
                        }
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void connectView() {
        mRequestQueue = Volley.newRequestQueue(RegisterActivity.this);
        editUserName = findViewById(R.id.editUserName);
        editPassword = findViewById(R.id.editPassword);
        editRePassword = findViewById(R.id.editRePassword);
        editPhoneNumb = findViewById(R.id.editPhoneNumb);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                register();
                break;
        }
    }

    private void register() {
        boolean error = false;

        if (TextUtils.isEmpty(editUserName.getText().toString())) {
            editUserName.requestFocus();
            editUserName.setError("Please input this field");
            error = true;
        } else if (TextUtils.isEmpty(editPassword.getText().toString())) {
            editPassword.requestFocus();
            editPassword.setError("Please input this field");
            error = true;
        } else if (TextUtils.isEmpty(editRePassword.getText().toString())) {
            editRePassword.requestFocus();
            editRePassword.setError("Please input this field");
            error = true;
        } else if (TextUtils.isEmpty(editPhoneNumb.getText().toString())) {
            editPhoneNumb.requestFocus();
            editPhoneNumb.setError("Please input this field");
            error = true;
        } else {
            if (!editPassword.getText().toString().equals(editRePassword.getText().toString())) {
                editRePassword.requestFocus();
                editRePassword.setError("Password not match");
                error = true;
            }
        }

        if (error == false) {
            checkRegister();
        }
    }

    private String checkRegisterUrl;

    public void checkRegister() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("account")
                .appendPath("checkDuplicateUserName");
        checkRegisterUrl = builder.build().toString();
        new CheckRegister().execute();
    }

    private class CheckRegister extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            StringRequest strRequest = new StringRequest(Request.Method.POST, checkRegisterUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        if (response.equalsIgnoreCase("[]")) {
                            insertUser();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Account has already used, plase choose other one", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_name", editUserName.getText().toString());
                    return params;
                }
            };
            mRequestQueue.add(strRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private String isertUserUrl;

    public void insertUser() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("account")
                .appendPath("register");
        isertUserUrl = builder.build().toString();
        new InsertUser().execute();
    }

    private class InsertUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            StringRequest strRequest = new StringRequest(Request.Method.POST, isertUserUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println(response);
                        if (!response.equals("-1")) {
                            Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_name", editUserName.getText().toString());
                    params.put("password", editPassword.getText().toString());
                    params.put("phone_number", editPhoneNumb.getText().toString());
                    return params;
                }
            };
            mRequestQueue.add(strRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
