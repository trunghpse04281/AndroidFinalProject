package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.entities.Category;
import com.example.finalproject.R;

import java.util.ArrayList;

public class CategoryAdapterForSpinner extends BaseAdapter {

    private Context context;
    private ArrayList<Category> lstCategory;
    LayoutInflater inflter;

    public CategoryAdapterForSpinner(Context context, ArrayList<Category> lstCategory) {
        this.context = context;
        this.lstCategory = lstCategory;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return lstCategory == null ? 0 : lstCategory.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.category_list_item_spinner, null);
        TextView tvCatName = convertView.findViewById(R.id.tvCatName);
        tvCatName.setText(lstCategory.get(position).getName());
        return convertView;
    }
}
