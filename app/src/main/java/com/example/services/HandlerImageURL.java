package com.example.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class HandlerImageURL implements Serializable {

    public static Drawable LoadImageFromWebOperations(String urlStr) {
        try {
            URL url = new URL(urlStr);
            InputStream is = (InputStream) url.getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            System.out.println("Drawer=>>>>>>>>>>>" + d.toString());
            System.out.println("URL=>>>>>>>>>>>" + url.toString());
            return d;
        } catch (Exception e) {
            return null;
        }
    }

}
