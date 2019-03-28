package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adapter.ImageAdapterForRecyclerView;
import com.example.entities.Product;
import com.example.services.Entity;

import java.util.ArrayList;

public class ViewOneProductActivity extends AppCompatActivity implements View.OnClickListener {

    private Product product;
    private String currentUser;

    private TextView txtProductName;
    private TextView txtPrice;
    private TextView txtStatus;
    private TextView txtDescription;
    private TextView txtPhoneNumb;

    private RecyclerView viewProductImgs;
    private DrawerLayout drawerLayout;


    private RecyclerView.LayoutManager imagesLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_one_product);
        //Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        1000);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        connectView();

        Intent intent = getIntent();
        product = (Product) intent.getExtras().getSerializable("Product");

        txtProductName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtStatus.setText(product.getStatus());
        txtDescription.setText(product.getDescription());
        SpannableString content = new SpannableString(product.getOwnerPhone());
        content.setSpan(new UnderlineSpan(), 0, product.getOwnerPhone().length(), 0);
        txtPhoneNumb.setText(content);
        txtPhoneNumb.setOnClickListener(this);
        initImageView();
        currentUser = Entity.getCurrentUser(ViewOneProductActivity.this);
        if (currentUser.equalsIgnoreCase("")) {
            initDrawer(R.menu.not_login_drawer_view);
        } else {
            initDrawer(R.menu.logged_in_drawer_view);
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
                            Intent intent = new Intent(ViewOneProductActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("logout")) {
                            Entity.deleteCurrentUser(ViewOneProductActivity.this);
                            Intent intent = new Intent(ViewOneProductActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (menuItem.toString().equalsIgnoreCase("register")) {
                            Entity.deleteCurrentUser(ViewOneProductActivity.this);
                            Intent intent = new Intent(ViewOneProductActivity.this, RegisterActivity.class);
                            startActivity(intent);
                        } else if (menuItem.toString().equalsIgnoreCase("home")) {
                            Intent intent = new Intent(ViewOneProductActivity.this, ViewProductsActivity.class);
                            startActivity(intent);
                        }else if (menuItem.toString().equalsIgnoreCase("My Product")) {
                            Intent intent = new Intent(ViewOneProductActivity.this, ViewMyProductActivity.class);
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
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtPrice);
        txtStatus = findViewById(R.id.txtStatus);
        txtDescription = findViewById(R.id.txtDescription);
        viewProductImgs = findViewById(R.id.viewProductImgs);
        txtPhoneNumb = findViewById(R.id.txtPhoneNumb);
    }

    private void initImageView() {
        viewProductImgs.setHasFixedSize(true);
        ImageAdapterForRecyclerView adapter = new ImageAdapterForRecyclerView(product.getLst_images());
        viewProductImgs.setAdapter(adapter);

        imagesLayoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) imagesLayoutManager).setOrientation(LinearLayout.HORIZONTAL);
        viewProductImgs.setLayoutManager(imagesLayoutManager);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPhoneNumb:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", product.getOwnerPhone(), null));
                startActivity(intent);
                break;
        }
    }
}
