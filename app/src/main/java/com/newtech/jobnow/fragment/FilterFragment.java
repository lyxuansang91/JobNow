package com.newtech.jobnow.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.newtech.jobnow.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment {
    private Toolbar toolbar;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filter, container, false);
        InitUI(v);
        return v;
    }

    private void InitUI(View v) {
        ImageView imgBack = (ImageView) v.findViewById(R.id.imgRing);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
//        toolbar.setTitle("");
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ring);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_filter, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//
////        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
////
////        SearchView searchView = null;
////        if (searchItem != null) {
////            searchView = (SearchView) searchItem.getActionView();
//////            searchItem.expandActionView();
////        }
////        if (searchView != null) {
////            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
////        }
//
//        MenuItem item = menu.findItem(R.id.action_search);
//        SearchView searchView = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
//        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//        MenuItemCompat.setActionView(item, searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//        searchView.setOnClickListener(new View.OnClickListener() {
//                                          @Override
//                                          public void onClick(View v) {
//
//                                          }
//                                      }
//        );
//        super.onCreateOptionsMenu(menu, inflater);
//    }
}
