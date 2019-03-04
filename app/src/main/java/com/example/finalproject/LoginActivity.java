package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.services.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText editUserName;
    private TextInputEditText editPassword;
    private MaterialButton btnCancel;
    private MaterialButton btnLogin;

    private String tag = LoginActivity.class.getSimpleName();

    private String url = "";
    private String user_name = "";
    private String password = "";
    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mRequestQueue = Volley.newRequestQueue(LoginActivity.this);
        connectView();
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

    public void saveData(String user_name) {
        SharedPreferences pre = getSharedPreferences(Constants.FILE_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString("user_name", user_name);
        edit.commit();
        Toast.makeText(LoginActivity.this, "Login successfully for account: " + user_name, Toast.LENGTH_LONG).show();
    }

    public void logIn() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("account")
                .appendPath("login");
        url = builder.build().toString();
        new HandleRequest().execute();
    }

    private class HandleRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(tag, "onResponse: " + response);
                    try {
                        if (!response.trim().equals("")) {
                            JSONObject jsonObject = new JSONObject(response);
                            user_name = jsonObject.getString("user_name");
                            password = jsonObject.getString("password");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    saveData(user_name);
                                }
                            });
                            Log.e(tag, "User =>>>>" + jsonObject.getString("user_name"));
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
            System.out.println("onPost");
        }
    }

}
