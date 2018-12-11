package com.huich.roque.app.recomiendo_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.huich.roque.app.recomiendo_app.R;
import com.huich.roque.app.recomiendo_app.models.Site;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private Context mContext;
    private ArrayList<String> mUrlImageList = new ArrayList<>();

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)mContext).getLayoutInflater()
                .inflate(R.layout.item_info_window, null);

        TextView name_tv = view.findViewById(R.id.txt_infowindow_nombre);
        TextView details_tv = view.findViewById(R.id.txt_infowindow_descripcion);
        CircleImageView img = view.findViewById(R.id.img_infowindow_imagen);


        name_tv.setText(marker.getTitle());
        details_tv.setText(marker.getSnippet());

        Site infoWindowData = (Site) marker.getTag();

        mUrlImageList = infoWindowData.getUrl_imagen();
        String imagen_1 = mUrlImageList.get(0);

        Glide.with(mContext).load(imagen_1).into(img);

        return view;
    }
}
