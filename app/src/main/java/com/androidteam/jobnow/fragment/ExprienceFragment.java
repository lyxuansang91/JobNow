package com.androidteam.jobnow.fragment;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.AddExperienceActivity;
import com.androidteam.jobnow.acitvity.DetailJobsActivity;
import com.androidteam.jobnow.acitvity.EditExperienceActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExprienceFragment extends Fragment implements View.OnClickListener {


    public ExprienceFragment() {
        // Required empty public constructor
    }

    private ImageView imgEdit1, imgEdit2;
    private LinearLayout lnJob1, lnJob2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exprience, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View rootView) {
        lnJob1 = (LinearLayout) rootView.findViewById(R.id.lnJob1);
        lnJob2 = (LinearLayout) rootView.findViewById(R.id.lnJob2);
        imgEdit1 = (ImageView) rootView.findViewById(R.id.imgEdit1);
        imgEdit2 = (ImageView) rootView.findViewById(R.id.imgEdit2);
        LinearLayout lnAddExp = (LinearLayout) rootView.findViewById(R.id.lnAddExp);
        lnJob1.setOnClickListener(this);
        lnJob2.setOnClickListener(this);
        imgEdit1.setOnClickListener(this);
        imgEdit2.setOnClickListener(this);
        lnAddExp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.lnJob1:
            case R.id.lnJob2:
                intent = new Intent(getActivity(), DetailJobsActivity.class);
                startActivity(intent);
                break;
            case R.id.imgEdit1:
            case R.id.imgEdit2:
                intent = new Intent(getActivity(), EditExperienceActivity.class);
                startActivity(intent);
                break;
            case R.id.lnAddExp:
                intent = new Intent(getActivity(), AddExperienceActivity.class);
                startActivity(intent);
                break;
        }
    }
}
