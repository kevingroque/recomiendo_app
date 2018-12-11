package com.huich.roque.app.recomiendo_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huich.roque.app.recomiendo_app.R;
import com.huich.roque.app.recomiendo_app.commons.Common;
import com.huich.roque.app.recomiendo_app.fragments.SitesByIdFragment;
import com.huich.roque.app.recomiendo_app.fragments.SitesFragment;
import com.huich.roque.app.recomiendo_app.interfaces.ItemClickListener;
import com.huich.roque.app.recomiendo_app.models.Category;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> mCategoryList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int row_index = -1;

    public CategoryAdapter(Context mContext, List<Category> mCategoryList) {
        this.mContext = mContext;
        this.mCategoryList = mCategoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent, false);
        CategoryAdapter.ViewHolder categoryHolder = new CategoryAdapter.ViewHolder(v);
        mContext = parent.getContext();
        return  categoryHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        final String categoryId = mCategoryList.get(position).CategoryId;

        String name = mCategoryList.get(position).getNombre();
        String url = mCategoryList.get(position).getUrl_imagen();

        holder.setCategoryData(name,url);

        if(!mCategoryList.get(position).isSelected()){
            holder.mTitulo.setTextColor(Color.BLACK);
            holder.mViewSelectable.setVisibility(View.GONE);
        }else {
            holder.mTitulo.setTextColor(Color.WHITE);
            holder.mViewSelectable.setVisibility(View.VISIBLE);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onclick(View view, int position) {
                row_index = position;
                Common.currentCategory = mCategoryList.get(position);
                notifyDataSetChanged();
            }
        });

        if (row_index == position){
            holder.mTitulo.setTextColor(Color.BLACK);
            holder.mViewSelectable.setVisibility(View.GONE);
        }else {
            holder.mTitulo.setTextColor(Color.WHITE);
            holder.mViewSelectable.setVisibility(View.VISIBLE);
        }


        holder.coordinatorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoryId", categoryId);

                if (categoryId.equals("bMCQp0LJbmDhZOos4Lfw")){
                    SitesFragment sitesFragment = new SitesFragment();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_categories_containersites, sitesFragment)
                            .addToBackStack(null)
                            .commit();

                }else {
                    SitesByIdFragment sitesByIdFragment = new SitesByIdFragment();
                    sitesByIdFragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_categories_containersites, sitesByIdFragment)
                            .addToBackStack(null)
                            .commit();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mView;
        private ImageView mImagenCategory;
        private CoordinatorLayout coordinatorLayout;
        private View mViewSelectable;
        private TextView mTitulo;
        ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mViewSelectable = mView.findViewById(R.id.view_itemcategory);
            mTitulo = mView.findViewById(R.id.txt_itemcategory_nombre);
            mImagenCategory = mView.findViewById(R.id.img_itemcategory_imagen);
            coordinatorLayout = mView.findViewById(R.id.layout_categoryitem_item);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        public void setCategoryData(String titulo, String url_imagen){

            mTitulo.setText(titulo);
            Glide.with(mContext).load(url_imagen).into(mImagenCategory);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onclick(v, getAdapterPosition());
        }
    }
}
