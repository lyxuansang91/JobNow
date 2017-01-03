package com.newtech.jobnow.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.acitvity.MyApplication;
import com.newtech.jobnow.acitvity.SplashScreen;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.models.BaseResponse;
import com.newtech.jobnow.models.UploadFileResponse;
import com.newtech.jobnow.widget.CenteredToolbar;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private String TAG = ProfileFragment.class.getSimpleName();
    private ViewPager viewPager;

    public int[] tabs() {
        return new int[]{
                R.string.myProfile,
                R.string.experience,
                R.string.skills
        };
    }

    private LinearLayout tab1, tab2, tab3;
    private ImageView ic_tab1, ic_tab2, ic_tab3, imgLogout, ivEditProfile;
    private TextView custom_text1, custom_text2, custom_text3;
    private int tabSelected = 0;
    public static CircleImageView img_avatar;
    public static TextView tvName, tvLocation;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(v);
        bindData();
        initEvent();
        return v;
    }

    private void logout() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, true);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        int userID = sharedPreferences.getInt(Config.KEY_ID, 0);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");

        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.getLogout(APICommon.getSign(APICommon.getApiKey(), "api/v1/users/getLogout"), APICommon.getAppId(), APICommon.getDeviceType(), userID, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    if (response.body().code == 200) {
                        Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(getActivity(), SplashScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish(); // call this to f
                    } else if (response.body().code == 503) {
                        MyApplication.getInstance().getApiToken(new MyApplication.TokenCallback() {
                            @Override
                            public void onTokenSuccess() {
                                logout();
                            }
                        });
                        Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initEvent() {
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected = 0;
                setPager();
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected = 1;
                setPager();
            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected = 2;
                setPager();
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabSelected = position;
                setTabSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getActivity())) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
                    } else {
                        // continue with your code
                        changeAvatar();
                    }
                } else {
                    // continue with your code
                    changeAvatar();
                }


            }
        });

        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar();
            }
        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    changeAvatar();
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }

    private void changeAvatar() {
        final CharSequence[] items = {getString(R.string.takephoto), getString(R.string.gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.addPhoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.takephoto))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(intent, 11);
                } else if (items[item].equals(getString(R.string.gallery))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    getActivity().startActivityForResult(
                            Intent.createChooser(intent, getString(R.string.selectFile)),
                            12);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 11) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img_avatar.setImageBitmap(thumbnail);
                uploadAvatar(destination.getPath());
            } else if (requestCode == 12) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                img_avatar.setImageBitmap(bm);
                uploadAvatar(selectedImagePath);
            }
        }
    }

    private void uploadAvatar(final String path) {
        final File file = new File(path);
        if (file.exists()) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
//            String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
            int userid = sharedPreferences.getInt(Config.KEY_ID, 0);
            RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody sign = RequestBody.create(MediaType.parse("text/plain"), APICommon.getSign(APICommon.getApiKey(), "api/v1/users/postAvatarUploadFile"));
            RequestBody appid = RequestBody.create(MediaType.parse("text/plain"), APICommon.getAppId());
            RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), APICommon.getDeviceType() + "");
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), userid + "");
            RequestBody token = RequestBody.create(MediaType.parse("text/plain"), sharedPreferences.getString(Config.KEY_TOKEN, ""));

            APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
            Log.d(TAG, "sign: " + APICommon.getSign(APICommon.getApiKey(), "api/v1/users/postAvatarUploadFile") + " appid: " + APICommon.getAppId() + " device_type: " + APICommon.getDeviceType() + " token: " + sharedPreferences.getString(Config.KEY_TOKEN, "") + " userid: " + userid);
            Call<UploadFileResponse> call = service.postuploadAvatar(sign, appid, deviceType, token, userId, requestBodyImage);
            call.enqueue(new Callback<UploadFileResponse>() {
                @Override
                public void onResponse(Response<UploadFileResponse> response, Retrofit retrofit) {
                    if (response.body() != null) {
                        Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getActivity(), getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setPager() {
        viewPager.setCurrentItem(tabSelected);
        setTabSelected();
    }

    private void setTabSelected() {
        switch (tabSelected) {
            case 0:
                tab1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                tab2.setBackgroundResource(R.drawable.bg_tab_profile);
                tab3.setBackgroundResource(R.drawable.bg_tab_profile);

                ic_tab1.setImageResource(R.mipmap.ic_profile_tab_selected);
                custom_text1.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                ic_tab2.setImageResource(R.mipmap.ic_exprience);
                custom_text2.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab3.setImageResource(R.mipmap.ic_skill);
                custom_text3.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                break;
            case 1:
                tab2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                tab1.setBackgroundResource(R.drawable.bg_tab_profile);
                tab3.setBackgroundResource(R.drawable.bg_tab_profile);

                ic_tab1.setImageResource(R.mipmap.ic_profile_tab);
                custom_text1.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab2.setImageResource(R.mipmap.ic_exprience_selected);
                custom_text2.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                ic_tab3.setImageResource(R.mipmap.ic_skill);
                custom_text3.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                break;
            case 2:
                tab3.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                tab1.setBackgroundResource(R.drawable.bg_tab_profile);
                tab2.setBackgroundResource(R.drawable.bg_tab_profile);

                ic_tab1.setImageResource(R.mipmap.ic_profile_tab);
                custom_text1.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab2.setImageResource(R.mipmap.ic_exprience);
                custom_text2.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab3.setImageResource(R.mipmap.ic_skill_selected);
                custom_text3.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                break;
            default:
                tab1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                tab2.setBackgroundResource(R.drawable.bg_tab_profile);
                tab3.setBackgroundResource(R.drawable.bg_tab_profile);

                ic_tab1.setImageResource(R.mipmap.ic_profile_tab_selected);
                custom_text1.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                ic_tab2.setImageResource(R.mipmap.ic_exprience);
                custom_text2.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab3.setImageResource(R.mipmap.ic_skill);
                custom_text3.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                break;
        }
    }

    private void bindData() {
//        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
//        pages.add(FragmentPagerItem.of(getString(tabs()[0]), MyProfileFragment.class));
//        pages.add(FragmentPagerItem.of(getString(tabs()[1]), ExperienceFragment.class));
//        pages.add(FragmentPagerItem.of(getString(tabs()[2]), SkillsFragment.class));
//        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
//                getChildFragmentManager(), pages);
        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOffscreenPageLimit(3);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MyProfileFragment();
                case 1:
                    return new ExperienceFragment();
                case 2:
                    return new SkillsFragment();
                default:
                    return new MyProfileFragment();
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return getString(tabs()[position]);
        }

    }


    private void initUI(View v) {
        CenteredToolbar toolbar = (CenteredToolbar) v.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);

//        viewPagerTab.setViewPager(viewPager);
        tab1 = (LinearLayout) v.findViewById(R.id.tab1);
        tab2 = (LinearLayout) v.findViewById(R.id.tab2);
        tab3 = (LinearLayout) v.findViewById(R.id.tab3);

        ic_tab1 = (ImageView) v.findViewById(R.id.ic_tab1);
        ic_tab2 = (ImageView) v.findViewById(R.id.ic_tab2);
        ic_tab3 = (ImageView) v.findViewById(R.id.ic_tab3);

        imgLogout = (ImageView) v.findViewById(R.id.imgLogout);

        custom_text1 = (TextView) v.findViewById(R.id.custom_text1);
        custom_text2 = (TextView) v.findViewById(R.id.custom_text2);
        custom_text3 = (TextView) v.findViewById(R.id.custom_text3);

        tvName = (TextView) v.findViewById(R.id.tvName);
        tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        img_avatar = (CircleImageView) v.findViewById(R.id.img_avatar);
        ivEditProfile = (ImageView) v.findViewById(R.id.ivEditProfile);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("d", "exit");
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
