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
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.services.ImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductsActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialButton btnAddProduct;
    private MaterialButton btnUploadImage;
    private TextInputEditText editProductName;
    private TextInputEditText editProductDes;
    private TextInputEditText editProductPrice;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        uploadImgIntent = new Intent();
        connectVIew();

    }

    private void connectVIew() {
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imageView = findViewById(R.id.imgProduct);
        gvGallery = (GridView) findViewById(R.id.gv);

        btnAddProduct.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);

        testImg = findViewById(R.id.testImg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUploadImage:
                chooseImages();
                break;
            case R.id.btnAddProduct:
                uploadImage();
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

    public class UploadImage extends AsyncTask<Void, Void, String> {

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
                    String requestId = MediaManager.get().upload(mArrayUri.get(i)).option("public_id", "image" + i).dispatch();
                    String url = MediaManager.get().url().generate("image" + i);
                    System.out.println("Request=>>>>>>>>>>>>>>>>>" + url);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
