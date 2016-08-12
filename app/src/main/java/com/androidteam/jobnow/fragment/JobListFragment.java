package com.androidteam.jobnow.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.DetailJobsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobListFragment extends Fragment {

    public static String KEY_SEACH = "hasSearch";

    public JobListFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(boolean hasSearch) {
        Fragment fragment = new JobListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_SEACH, hasSearch);
        fragment.setArguments(bundle);
        return fragment;
    }

    private LinearLayout lnJob1, lnJob2, lnJob3;
    private RelativeLayout rlSearchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_job_list, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View view) {
        lnJob1 = (LinearLayout) view.findViewById(R.id.lnJob1);
        lnJob2 = (LinearLayout) view.findViewById(R.id.lnJob2);
        lnJob3 = (LinearLayout) view.findViewById(R.id.lnJob3);
        rlSearchBar = (RelativeLayout) view.findViewById(R.id.rlSearchBar);
        rlSearchBar.setVisibility(getArguments().getBoolean(KEY_SEACH) ? View.VISIBLE : View.GONE);

        lnJob1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailJobsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        lnJob2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailJobsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        lnJob3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailJobsActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

}
