package com.androidteam.jobnow.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.MyApplication;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.BaseResponse;
import com.androidteam.jobnow.models.UpdateProfileRequest;
import com.androidteam.jobnow.models.UserDetailResponse;
import com.androidteam.jobnow.models.UserModel;
import com.squareup.picasso.Picasso;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MyProfileFragment extends Fragment implements View.OnClickListener {
    private TextView tvEmail, tvPhoneNumber, tvGender, tvBirthday, tvCountry, tvPostalCode;
    private ProgressDialog progressDialog;
    private ImageView imgEditPhoneNumber, imgEditGender, imgEditBirthday, imgCountry, imgEditPostalCode, imgEditDescription;
    private UserModel userModel;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initUI(view);
        bindData();
        event();
        return view;
    }

    private void event() {
        imgEditPhoneNumber.setOnClickListener(this);
        imgEditGender.setOnClickListener(this);
        imgEditBirthday.setOnClickListener(this);
        imgCountry.setOnClickListener(this);
        imgEditPostalCode.setOnClickListener(this);
        imgEditDescription.setOnClickListener(this);
    }

    private void bindData() {
        loadUserDetail();
    }

    private void loadUserDetail() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, true);
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
                progressDialog.dismiss();
                if (response.body() != null && response.body().code == 200) {
                    userModel = response.body().result;
                    if (response.body().result.avatar != null) {
                        Picasso.with(getActivity()).load(response.body().result.avatar).
                                placeholder(R.mipmap.default_avatar).
                                error(R.mipmap.default_avatar).
                                into(ProfileFragment.img_avatar);
                    }
                    ProfileFragment.tvName.setText(
                            response.body().result.fullname == null || response.body().result.fullname.isEmpty() ? response.body().result.email : response.body().result.fullname);
                    ProfileFragment.tvLocation.setText(response.body().result.countryName);
                    tvEmail.setText(response.body().result.email);
                    tvPhoneNumber.setText(response.body().result.phoneNumber);
                    if (response.body().result.gender == 1) {
                        tvGender.setText(getString(R.string.male));
                    } else if (response.body().result.gender == 2) {
                        tvGender.setText(getString(R.string.female));
                    } else {
                        tvGender.setText(getString(R.string.other));
                    }
                    tvBirthday.setText(response.body().result.birthDay);
                    tvCountry.setText(response.body().result.countryName);
                    tvPostalCode.setText(response.body().result.postalCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
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


        imgEditPhoneNumber = (ImageView) view.findViewById(R.id.imgEditPhoneNumber);
        imgEditGender = (ImageView) view.findViewById(R.id.imgEditGender);
        imgEditBirthday = (ImageView) view.findViewById(R.id.imgEditBirthday);
        imgCountry = (ImageView) view.findViewById(R.id.imgCountry);
        imgEditPostalCode = (ImageView) view.findViewById(R.id.imgEditPostalCode);
        imgEditDescription = (ImageView) view.findViewById(R.id.imgEditDescription);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgEditPhoneNumber:
                updateProfile(Config.TYPE_EDIT_PHONE_NUMBER);
                break;
        }
    }

    private void updateProfile(final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.phoneNumber));
        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        if (type == Config.TYPE_EDIT_PHONE_NUMBER) {
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            input.setHint(getString(R.string.phone_number));
        }
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
                String phonenumber = userModel.phoneNumber;
                String fullname = userModel.fullname;
                String email = userModel.email;
                String fb_id = userModel.fb_id;
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
                String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
                int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
                if (type == Config.TYPE_EDIT_PHONE_NUMBER) {
                    phonenumber = input.getText().toString();
                }
                Call<BaseResponse> call =
                        service.postUpdateDetail(new UpdateProfileRequest(fullname, email, fb_id, token, userId));
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {

                        if (response.body() != null) {
                            if (response.code() == 200) {

                            }
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

}
