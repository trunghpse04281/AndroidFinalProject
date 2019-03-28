package com.example.finalproject;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.android.MediaManager;
import com.example.adapter.CategoryAdapterForSpinner;
import com.example.adapter.ImageAdapter;
import com.example.entities.Category;
import com.example.services.Constants;
import com.example.services.Entity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private MaterialButton btnAddProduct;
    private MaterialButton btnUploadImage;
    private TextInputEditText editProductName;
    private TextInputEditText editProductDes;
    private TextInputEditText editProductPrice;
    private Spinner spinnerCategory;
    RequestQueue mRequestQueue;
    private ArrayList<Category> lstCategory;

    private final int PICK_IMAGE_MULTIPLE = 1;
    private ImageView imageView;

    private Uri filePath;


    //variable to choose images
    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private ImageAdapter galleryAdapter;

    private Intent uploadImgIntent;


    ArrayList<Bitmap> lstBitmapImg;
    ArrayList<Uri> mArrayUri;
    private ImageView testImg;

    private Integer currentID = -1;
    private String curentCategory = "Đồ ăn";


    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mRequestQueue = Volley.newRequestQueue(AddProductsActivity.this);
        uploadImgIntent = new Intent();
        getCategory();
        connectVIew();
        initDrawer(R.menu.logged_in_drawer_view);
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
                            Intent intent = new Intent(AddProductsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("logout")) {
                            Entity.deleteCurrentUser(AddProductsActivity.this);
                            Intent intent = new Intent(AddProductsActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (menuItem.toString().equalsIgnoreCase("register")) {
                            Entity.deleteCurrentUser(AddProductsActivity.this);
                            Intent intent = new Intent(AddProductsActivity.this, RegisterActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("home")) {
                            Intent intent = new Intent(AddProductsActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("My Product")) {
                            Intent intent = new Intent(AddProductsActivity.this, ViewMyProductActivity.class);
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

    private void connectVIew() {
        editProductName = findViewById(R.id.editProductName);
        editProductDes = findViewById(R.id.editProductDes);
        editProductPrice = findViewById(R.id.editProductPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imageView = findViewById(R.id.imgProduct);
        gvGallery = (GridView) findViewById(R.id.gv);

        btnAddProduct.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);

        testImg = findViewById(R.id.testImg);


        spinnerCategory.setOnItemSelectedListener(this);

    }

    private void initSpinner() {
        CategoryAdapterForSpinner adapter = new CategoryAdapterForSpinner(getApplicationContext(), lstCategory);
        spinnerCategory.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUploadImage:
                chooseImages();
                break;
            case R.id.btnAddProduct:
                addProduct();
                break;
        }
    }

    private void chooseImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        lstBitmapImg = new ArrayList<>();
        mArrayUri = new ArrayList<Uri>();
        try {
            this.uploadImgIntent = data;
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
//                when user pick 1 image
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri.add(mImageUri);
                    galleryAdapter = new ImageAdapter(getApplicationContext(), mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                } else {
//                    when user pick many images
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new ImageAdapter(getApplicationContext(), mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }

                for (int i = 0; i < mArrayUri.size(); i++) {
                    lstBitmapImg.add(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mArrayUri.get(i)));
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong" + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage() {
        new UploadImage().execute();
    }

    private void addProduct() {
        boolean error = false;
        if (TextUtils.isEmpty(editProductName.getText().toString())) {
            editProductName.requestFocus();
            editProductName.setError(AddProductsActivity.this.getResources().getString(R.string.error_field_required));
            error = true;
        } else if (TextUtils.isEmpty(editProductPrice.getText().toString())) {
            editProductPrice.requestFocus();
            editProductPrice.setError(AddProductsActivity.this.getResources().getString(R.string.error_field_required));
            error = true;
        } else {
            try {
                Double.parseDouble(editProductPrice.getText().toString());
            } catch (Exception ex) {
                editProductPrice.requestFocus();
                editProductPrice.setError(AddProductsActivity.this.getResources().getString(R.string.error_number_input));
                error = true;
            }
        }
        if (error == false) {
            insertProductToDB();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        curentCategory = lstCategory.get(position).getName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String imgeString = "";

    private class UploadImage extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Map config = new HashMap();
                config.put("cloud_name", "dzuo5nllw");
                config.put("api_key", "635484271368143");
                config.put("api_secret", "sw6Q1hWLPQlSBxbuWxsmpRpMvGo");
                MediaManager.init(AddProductsActivity.this, config);
            } catch (Exception e) {

            } finally {
                for (int i = 0; i < mArrayUri.size(); i++) {
                    String requestId = MediaManager.get().upload(mArrayUri.get(i)).option("public_id", currentID + "image" + i).dispatch();
                    String url = MediaManager.get().url().generate(currentID + "image" + i);
                    System.out.println("Request=>>>>>>>>>>>>>>>>>" + url);
//                    String url = "sdfdsfsdfsdfdsfsdfsdfdsfsd";
                    imgeString += url + ",";
                }
                Toast.makeText(AddProductsActivity.this, "Add Successfully", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    private String insertProductToDBUrl;

    private void insertProductToDB() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("product")
                .appendPath("insert");
        insertProductToDBUrl = builder.build().toString();

        StringRequest strRequest = new StringRequest(Request.Method.POST, insertProductToDBUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(insertProductToDBUrl);
                    Gson gson = new Gson();
                    System.out.println(response);
                    currentID = Integer.parseInt(response);
                    uploadImage();
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
                params.put("name", editProductName.getText().toString());
                params.put("price", editProductPrice.getText().toString());
                params.put("description", editProductDes.getText().toString());
                params.put("image_url", imgeString);
                params.put("category", curentCategory);
                params.put("owner", Entity.getCurrentUser(AddProductsActivity.this));
                params.put("status", "Còn hàng");
                return params;
            }
        };
        mRequestQueue.add(strRequest);
    }

    private String categoryUrl;

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
                        lstCategory.remove(0);
                        initSpinner();
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

    private String updateImageProductUrl;

    public void updateImageProduct() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTP_PROTOCOL)
                .encodedAuthority(Constants.HOST)
                .appendPath("product")
                .appendPath("updateImgProduct");
        updateImageProductUrl = builder.build().toString();

        StringRequest strRequest = new StringRequest(Request.Method.POST, insertProductToDBUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(updateImageProductUrl);
                    System.out.println(response);
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
                params.put("id", String.valueOf(currentID - 1));
                params.put("imageString", imgeString);
                return params;
            }
        };
        mRequestQueue.add(strRequest);
        System.out.println("thanh cong");
        System.out.println(imgeString);

    }
}
