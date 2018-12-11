package com.huich.roque.app.recomiendo_app.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.huich.roque.app.recomiendo_app.R;
import com.huich.roque.app.recomiendo_app.adapters.CustomInfoWindowAdapter;
import com.huich.roque.app.recomiendo_app.models.Site;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;

    private FirebaseFirestore mFirestore;
    private List<Site> mSiteList;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mSiteList = new ArrayList<>();

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map_main_huariques);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setMinZoomPreference(11);

        mFirestore = FirebaseFirestore.getInstance();
        mSiteList = new ArrayList<>();

        mFirestore.collection("Sitios").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String siteId = documentChange.getDocument().getId();
                        Site site = documentChange.getDocument().toObject(Site.class).withId(siteId);

                        Site infoSite = new Site();
                        infoSite.setNombre(site.getNombre());
                        infoSite.setUrl_imagen(site.getUrl_imagen());
                        infoSite.setDescripcion(site.getDescripcion());

                        CustomInfoWindowAdapter infoWindowAdapter = new CustomInfoWindowAdapter(getActivity());
                        mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);

                        LatLng latLng = new LatLng(site.getLatitud(), site.getLongitud());

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng)
                                .title(site.getNombre())
                                .snippet(site.getDescripcion())
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                        Marker marker = mGoogleMap.addMarker(markerOptions);
                        marker.setTag(infoSite);
                        marker.showInfoWindow();

                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        float zoon = 8;
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoon));
                    }
                }
            }
        });
    }
}
