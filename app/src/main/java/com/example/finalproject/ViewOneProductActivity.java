package com.example.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.entities.Product;

public class ViewOneProductActivity extends AppCompatActivity {

    private Product product;

    private TextView txtProductName;
    private TextView txtPrice;
    private TextView txtStatus;
    private TextView txtDescription;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_one_product);

        connectView();

        Intent intent = getIntent();
        product = (Product) intent.getExtras().getSerializable("Product");

        txtProductName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));
//        txtStatus.setText(product.get());
        txtDescription.setText(product.getDescription());


    }

    private void connectView() {
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtPrice);
        txtStatus = findViewById(R.id.txtStatus);
        txtDescription = findViewById(R.id.txtDescription);
    }
}
