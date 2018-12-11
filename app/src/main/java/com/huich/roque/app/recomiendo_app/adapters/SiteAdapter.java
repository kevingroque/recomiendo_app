package com.huich.roque.app.recomiendo_app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huich.roque.app.recomiendo_app.R;
import com.huich.roque.app.recomiendo_app.models.Site;

import java.util.List;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.ViewHolder>{

    private Context mContext;
    private List<Site> mSiteList;

    public SiteAdapter(Context mContext, List<Site> mSiteList) {
        this.mContext = mContext;
        this.mSiteList = mSiteList;
    }

    @Override
    public SiteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sites, parent, false);
        SiteAdapter.ViewHolder siteHolder = new SiteAdapter.ViewHolder(v);
        mContext = parent.getContext();
        return siteHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SiteAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final String siteId = mSiteList.get(position).SiteId;

        String title = mSiteList.get(position).getNombre();
        String district = mSiteList.get(position).getDistrito();
        String comments = "0";
        String recommends = "0";
        float rating = mSiteList.get(position).getRating();
        String url = mSiteList.get(position).getUrl_imagen().get(0);

        holder.setSitesData(title,district,comments,recommends,rating,url);
    }

    @Override
    public int getItemCount() {
        return mSiteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView mTitle, mDistrict, mComments, mRecommends, mRating;
        private ImageView mImageSite;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setSitesData(String title, String district, String comments, String recomends, float rating, String url_imagen){
            mTitle = mView.findViewById(R.id.txt_itemsites_titulo);
            mDistrict = mView.findViewById(R.id.txt_itemsites_distrito);
            mComments = mView.findViewById(R.id.txt_itemsites_comments);
            mRecommends = mView.findViewById(R.id.txt_itemsites_recommendations);
            mRating = mView.findViewById(R.id.txt_itemsites_rating);
            mImageSite = mView.findViewById(R.id.img_itemsites_huarique);

            mTitle.setText(title);
            mDistrict.setText(district);
            mComments.setText(comments);
            mRecommends.setText(recomends);
            mRating.setText((String.valueOf(rating)));
            Glide.with(mContext).load(url_imagen).into(mImageSite);
        }
    }
}
