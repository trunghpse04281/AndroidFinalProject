package com.example.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.entities.Product;
import com.example.finalproject.R;
import com.example.finalproject.ViewOneProductActivity;
import com.example.services.HandlerImageURL;
import com.example.services.ItemClickListener;
import com.example.services.OnLoadMoreListener;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Activity activity;
    private List<Product> lstProduct;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

//    public ProductAdapter(List<Product> lstProduct) {
//        this.lstProduct = lstProduct;
//    }

    public ProductAdapter(RecyclerView recyclerView, List<Product> lstProduct, Activity activity) {
        this.lstProduct = lstProduct;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return lstProduct.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.product_list_item, parent, false);
            return new RecycleViewHolder(view, parent.getContext());
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecycleViewHolder) {
            RecycleViewHolder recycleViewHolder = (RecycleViewHolder) holder;
            recycleViewHolder.tvName.setText(lstProduct.get(position).getName());
            recycleViewHolder.tvDescription.setText(lstProduct.get(position).getDescription());
            recycleViewHolder.tvPrice.setText(String.valueOf(lstProduct.get(position).getPrice()));
            if (lstProduct.get(position).getImage_url() != null && !lstProduct.get(position).getImage_url().equals("")) {
                try {
                    recycleViewHolder.imgProduct.setImageDrawable(HandlerImageURL.LoadImageFromWebOperations(lstProduct.get(position).getLst_images().get(0)));
                } catch (Exception e) {

                }
            } else {
                recycleViewHolder.imgProduct.setImageResource(R.drawable.product_default);
            }

            ((RecycleViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {

                    Intent intent = new Intent(activity, ViewOneProductActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Product", lstProduct.get(position));
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, 1000);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return lstProduct == null ? 0 : lstProduct.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;


        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    private static class RecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private TextView tvDescription;
        private TextView tvPrice;
        private ImageView imgProduct;
        private Context context;

        private ItemClickListener itemClickListener;


        public RecycleViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }
}
