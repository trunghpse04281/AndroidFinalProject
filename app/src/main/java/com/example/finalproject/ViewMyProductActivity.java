package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adapter.CategoryAdapter;
import com.example.adapter.ProductAdapter;
import com.example.entities.Category;
import com.example.entities.Product;
import com.example.services.Constants;
import com.example.services.Entity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ViewMyProductActivity extends AppCompatActivity implements View.OnClickListener {

    private String currentUser;
    private DrawerLayout drawerLayout;
    private RecyclerView productList;
    private ArrayList<Product> lstProduct;
    private RecyclerView.LayoutManager productLayoutManager;
    private ProductAdapter productAdapter;

    private String categoryUrl;
    private String productUrl;
    private String getProdByCatUrl;
    RequestQueue mRequestQueue;


    private RecyclerView categoryList;
    private ArrayList<Category> lstCategory;
    private CategoryAdapter categoryAdapter;
    private RecyclerView.LayoutManager categoryLayoutManager;
    private FloatingActionButton flBtnAddProduct;

    private String searchString = "";
    private EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_product);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        currentUser = Entity.getCurrentUser(ViewMyProductActivity.this);
        if (currentUser.equalsIgnoreCase("")) {
            initDrawer(R.menu.not_login_drawer_view);
        } else {
            initDrawer(R.menu.logged_in_drawer_view);
        }
        connectView();
        getProductByCategory("Tất cả");
    }

    private void connectView() {
        lstProduct = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(ViewMyProductActivity.this);
        productList = findViewById(R.id.productList);
        categoryList = findViewById(R.id.categoryList);
        getCategory();
//        getProduct();

        flBtnAddProduct = findViewById(R.id.flBtnAddProduct);
        flBtnAddProduct.setOnClickListener(this);
        editSearch = findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editSearch.getText().toString().trim().length() >= 3) {
                    searchString = editSearch.getText().toString();
                    searchProduct();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadProductList() {
        productList.setHasFixedSize(true);

        productLayoutManager = new LinearLayoutManager(this);
        productList.setLayoutManager(productLayoutManager);

        productAdapter = new ProductAdapter(productList, lstProduct, this);
        productList.setAdapter(productAdapter);

//        productAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                if (lstProduct.size() <= (10 * DATA_CURRENT_PAGE)) {
//                    lstProduct.add(null);
//                    productAdapter.notifyItemInserted(lstProduct.size() - 1);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            int index = lstProduct.size();
//                            int end = index + 10;
//                            lstProduct.remove(lstProduct.size() - 1);
//                            productAdapter.notifyItemRemoved(lstProduct.size());
//                            getProduct();
//                            productAdapter.notifyDataSetChanged();
//                            productAdapter.setLoaded();
//                        }
//                    }, 5000);
//                }
//            }
//        });
    }

    private void loadCategoryList() {
        categoryList.setHasFixedSize(true);

        categoryLayoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) categoryLayoutManager).setOrientation(LinearLayout.HORIZONTAL);
        categoryList.setLayoutManager(categoryLayoutManager);

        categoryAdapter = new CategoryAdapter(lstCategory, ViewMyProductActivity.this);
        categoryList.setAdapter(categoryAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flBtnAddProduct:
                if (currentUser.equalsIgnoreCase("")) {
                    Toast.makeText(ViewMyProductActivity.this, "You must login to use this function", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent(this, AddProductsActivity.class);
                    startActivityForResult(intent, 0);
                }
                break;
        }
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
                            Intent intent = new Intent(ViewMyProductActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("logout")) {
                            Entity.deleteCurrentUser(ViewMyProductActivity.this);
                            Intent intent = new Intent(ViewMyProductActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (menuItem.toString().equalsIgnoreCase("register")) {
                            Entity.deleteCurrentUser(ViewMyProductActivity.this);
                            Intent intent = new Intent(ViewMyProductActivity.this, RegisterActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("home")) {
                            Intent intent = new Intent(ViewMyProductActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("My Product")) {

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

    public void getCategory() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("category")
                .appendPath("getAll");
        categoryUrl = builder.build().toString();
        new getCategory().execute();
    }

    private class getCategory extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            StringRequest strRequest = new StringRequest(Request.Method.GET, categoryUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println(response);
                        lstCategory = new ArrayList<>();
                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<ArrayList<Category>>() {
                        }.getType();
                        lstCategory = gson.fromJson(response, collectionType);
                        loadCategoryList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            mRequestQueue.add(strRequest);
            return null;
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
        }
    }

    public void getProductByCategory(String categoryName) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("product")
                .appendPath("getProductByUser");
        getProdByCatUrl = builder.build().toString();
        new GetProductByCategory().execute(categoryName);
    }

    private class GetProductByCategory extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... categoryName) {
            StringRequest strRequest = new StringRequest(Request.Method.POST, getProdByCatUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println(getProdByCatUrl);
                        System.out.println("getProductByCategory  " + response);
                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<ArrayList<Product>>() {
                        }.getType();
                        lstProduct = gson.fromJson(response, collectionType);
                        String[] lstImage;
                        for (int i = 0; i < lstProduct.size(); i++) {
                            if (lstProduct.get(i).getImage_url() != null) {
                                lstImage = lstProduct.get(i).getImage_url().split(",");
                                lstProduct.get(i).setLst_images(Arrays.asList(lstImage));
                            }
                        }
                        System.out.println("SIze=>>>>>>>>>>>" + lstProduct.size());
                        loadProductList();
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
                    params.put("user", Entity.getCurrentUser(ViewMyProductActivity.this));
                    params.put("catName", categoryName[0]);
                    return params;
                }
            };
            mRequestQueue.add(strRequest);
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 200) {
            if (requestCode == 1000) {
                if (currentUser.equalsIgnoreCase("")) {
                    initDrawer(R.menu.not_login_drawer_view);
                } else {
                    initDrawer(R.menu.logged_in_drawer_view);
                }
            }
        }
    }

    public void onCategoryClick(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
        getProductByCategory(value);
    }

    private String searchProductUrl = "";
    private void searchProduct() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("product")
                .appendPath("getProductByName");
        searchProductUrl = builder.build().toString();
        new SearchProduct().execute();
    }

    private class SearchProduct extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            StringRequest strRequest = new StringRequest(Request.Method.POST, searchProductUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println(searchProductUrl);
                        System.out.println("getProductByCategory  " + response);
                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<ArrayList<Product>>() {
                        }.getType();
                        lstProduct = gson.fromJson(response, collectionType);
                        loadProductList();
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
                    params.put("productName", searchString);
                    return params;
                }
            };
            mRequestQueue.add(strRequest);
            return null;
        }
    }
}
