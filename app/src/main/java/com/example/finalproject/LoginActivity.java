package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.entities.User;
import com.example.services.Constants;
import com.example.services.Entity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText editUserName;
    private TextInputEditText editPassword;
    private MaterialButton btnCancel;
    private MaterialButton btnLogin;

    private String tag = LoginActivity.class.getSimpleName();

    private String loginUrl = "";
    RequestQueue mRequestQueue;

    private DrawerLayout drawerLayout;
    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mRequestQueue = Volley.newRequestQueue(LoginActivity.this);
        connectView();
        initDrawer(R.menu.not_login_drawer_view);
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
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("logout")) {
                            Entity.deleteCurrentUser(LoginActivity.this);
                            Intent intent = new Intent(LoginActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (menuItem.toString().equalsIgnoreCase("register")) {
                            Entity.deleteCurrentUser(LoginActivity.this);
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("home")) {
                            Intent intent = new Intent(LoginActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("My Product")) {
                            Intent intent = new Intent(LoginActivity.this, ViewMyProductActivity.class);
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

    public void connectView() {
        editUserName = findViewById(R.id.editUserName);
        editPassword = findViewById(R.id.editPassword);
        btnCancel = findViewById(R.id.btnCancel);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                logIn();
                break;
            case R.id.btnCancel:
                break;
        }
    }

    public void logIn() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("account")
                .appendPath("login");
        loginUrl = builder.build().toString();
        new HandleRequest().execute();
    }

    private class HandleRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(tag, "onResponse: " + response);
                    Log.e(tag, "Url: " + loginUrl);
                    try {
                        if (response.trim().equals("")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Server fail", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            current_user = new User();
                            Gson gson = new Gson();
                            current_user = gson.fromJson(response, User.class);

                            if (current_user != null) {
                                Entity.saveCurrentUser(LoginActivity.this, current_user);
                                Intent intent = new Intent(LoginActivity.this, ViewProductsActivity.class);
                                startActivityForResult(intent, 1000);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(tag, "onErrorResponse: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_name", editUserName.getText().toString());
                    params.put("password", editPassword.getText().toString());
                    return params;
                }
            };
            mRequestQueue.add(stringRequest);
            System.out.println("onDoing");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
