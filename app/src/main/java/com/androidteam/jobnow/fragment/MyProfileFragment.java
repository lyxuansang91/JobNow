package com.androidteam.jobnow.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.eventbus.BindProfile1Event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MyProfileFragment extends Fragment {
    private TextView tvEmail, tvPhoneNumber, tvGender, tvBirthday, tvCountry, tvPostalCode;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvPhoneNumber = (TextView) view.findViewById(R.id.tvPhoneNumber);
        tvGender = (TextView) view.findViewById(R.id.tvGender);
        tvBirthday = (TextView) view.findViewById(R.id.tvBirthday);
        tvCountry = (TextView) view.findViewById(R.id.tvCountry);
        tvPostalCode = (TextView) view.findViewById(R.id.tvPostalCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
//    }

    @Override
    public void onDetach() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Subscribe
    public void onEvent(BindProfile1Event event) {
        tvEmail.setText(event.userModel.email);
        tvPhoneNumber.setText(event.userModel.phoneNumber);
//        tvGender.setText(event.userModel.email);
        tvBirthday.setText(event.userModel.birthDay);
//        tvCountry.setText(event.userModel.email);
//        tvPostalCode.setText(event.userModel.email);
    }
}
