package com.huich.roque.app.recomiendo_app.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.huich.roque.app.recomiendo_app.R;
import com.huich.roque.app.recomiendo_app.adapters.CategoryAdapter;
import com.huich.roque.app.recomiendo_app.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    private RecyclerView mRvCategories;
    private List<Category> mCategoryList;
    private CategoryAdapter mCategoryAdapter;
    private Category mCategory;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private DocumentSnapshot mLastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mCategoryList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvCategories = (RecyclerView) view.findViewById(R.id.rv_categories);
        mCategoryAdapter = new CategoryAdapter( getContext(),mCategoryList);
        mRvCategories.setHasFixedSize(true);
        mRvCategories.setLayoutManager(layoutManager);
        mRvCategories.setAdapter(mCategoryAdapter);

        getDataCategories();

        SitesFragment fragment = new SitesFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.layout_categories_containersites, fragment)
                .addToBackStack(null)
                .commit();

        return view;
    }


    private void getDataCategories(){
        Query firstQuery = mFirestore.collection("Categorias").orderBy("nombre", Query.Direction.DESCENDING).limit(10);
        firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    if (isFirstPageFirstLoad) {
                        mLastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        mCategoryList.clear();
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String categoryId = doc.getDocument().getId();
                            Category category = doc.getDocument().toObject(Category.class).withId(categoryId);

                            if (isFirstPageFirstLoad) {
                                mCategoryList.add(category);
                            } else {
                                mCategoryList.add(0, category);
                            }
                            mCategoryAdapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }
        });

    }


}
