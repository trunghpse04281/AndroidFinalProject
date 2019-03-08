package com.example.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.finalproject.R;
import com.example.services.HandlerImageURL;

import java.util.ArrayList;

public class ImageAdapterForRecyclerView extends RecyclerView.Adapter<ImageAdapterForRecyclerView.ViewHolder> {

    private ArrayList<String> mArrayUri;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gv_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.ivGallery.setImageDrawable(HandlerImageURL.LoadImageFromWebOperations(mArrayUri.get(i)));
    }

    @Override
    public int getItemCount() {
        return mArrayUri != null ? mArrayUri.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivGallery;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGallery = itemView.findViewById(R.id.ivGallery);
        }
    }
}
