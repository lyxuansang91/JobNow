package com.androidteam.jobnow.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.EditSkillActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SkillsFragment extends Fragment {
    private ImageView imgEdit;

    public SkillsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_skills, container, false);
        imgEdit = (ImageView) view.findViewById(R.id.imgEdit);
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditSkillActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
