package com.androidteam.jobnow.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.MyApplication;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.UserDetailResponse;
import com.squareup.picasso.Picasso;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

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
        bindData();
        return view;
    }

    private void bindData() {
        loadUserDetail();
    }

    private void loadUserDetail() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        int user_id = sharedPreferences.getInt(Config.KEY_ID, 0);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<UserDetailResponse> call =
                service.getUserDetail(APICommon.getSign(APICommon.getApiKey(),
                                "api/v1/users/getUserDetail"),
                        APICommon.getAppId(),
                        APICommon.getDeviceType(), user_id, token, user_id);
        call.enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Response<UserDetailResponse> response, Retrofit retrofit) {
                if (response.body() != null && response.body().code == 200) {
                    if (response.body().result.avatar != null) {
                        Picasso.with(getActivity()).load(response.body().result.avatar).into(ProfileFragment.img_avatar);
                        ProfileFragment.tvName.setText(response.body().result.fullname == null || response.body().result.fullname.isEmpty() ? response.body().result.email : response.body().result.fullname);
                        tvEmail.setText(response.body().result.email);
                        tvPhoneNumber.setText(response.body().result.phoneNumber);
//        tvGender.setText(event.userModel.email);
                        tvBirthday.setText(response.body().result.birthDay);
//        tvCountry.setText(event.userModel.email);
//        tvPostalCode.setText(event.userModel.email);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void initUI(View view) {
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvPhoneNumber = (TextView) view.findViewById(R.id.tvPhoneNumber);
        tvGender = (TextView) view.findViewById(R.id.tvGender);
        tvBirthday = (TextView) view.findViewById(R.id.tvBirthday);
        tvCountry = (TextView) view.findViewById(R.id.tvCountry);
        tvPostalCode = (TextView) view.findViewById(R.id.tvPostalCode);
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDetach() {
//        if (EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
//        super.onDetach();
//    }
//
//    @Subscribe
//    public void onEvent(BindProfile1Event event) {
//        tvEmail.setText(event.userModel.email);
//        tvPhoneNumber.setText(event.userModel.phoneNumber);
////        tvGender.setText(event.userModel.email);
//        tvBirthday.setText(event.userModel.birthDay);
////        tvCountry.setText(event.userModel.email);
////        tvPostalCode.setText(event.userModel.email);
//    }
}
