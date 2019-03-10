package com.example.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adapter.ImageAdapterForRecyclerView;
import com.example.entities.Product;

import java.util.ArrayList;

public class ViewOneProductActivity extends AppCompatActivity {

    private Product product;

    private TextView txtProductName;
    private TextView txtPrice;
    private TextView txtStatus;
    private TextView txtDescription;

    private RecyclerView viewProductImgs;


    private RecyclerView.LayoutManager imagesLayoutManager;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_one_product);

        connectView();

        Intent intent = getIntent();
        product = (Product) intent.getExtras().getSerializable("Product");

        txtProductName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtStatus.setText(product.getStatus());
        txtDescription.setText(product.getDescription());
        initImageView();


    }

    private void connectView() {
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtPrice);
        txtStatus = findViewById(R.id.txtStatus);
        txtDescription = findViewById(R.id.txtDescription);
        viewProductImgs = findViewById(R.id.viewProductImgs);
    }

    private void initImageView() {
        viewProductImgs.setHasFixedSize(true);
        ImageAdapterForRecyclerView adapter = new ImageAdapterForRecyclerView(product.getLst_images());
        viewProductImgs.setAdapter(adapter);

        imagesLayoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) imagesLayoutManager).setOrientation(LinearLayout.HORIZONTAL);
        viewProductImgs.setLayoutManager(imagesLayoutManager);

    }
}
