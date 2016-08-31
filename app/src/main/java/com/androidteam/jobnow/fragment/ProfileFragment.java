package com.androidteam.jobnow.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.MyApplication;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.UploadFileResponse;
import com.androidteam.jobnow.widget.CenteredToolbar;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
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
                R.string.exprience,
                R.string.skills
        };
    }

    private LinearLayout tab1, tab2, tab3;
    private ImageView ic_tab1, ic_tab2, ic_tab3;
    private TextView custom_text1, custom_text2, custom_text3;
    private int tabSelected = 0;
    private CircleImageView img_avatar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, null);
        initUI(v);
        bindData();
        initEvent();
        return v;
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
                changeAvatar();
            }
        });
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
            RequestBody sign = RequestBody.create(MediaType.parse("text/plain"), APICommon.getSign(APICommon.getApiKey(), "api/v1/files/postUploadFile"));
            RequestBody appid = RequestBody.create(MediaType.parse("text/plain"), APICommon.getAppId());
            RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), APICommon.getDeviceType() + "");
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), userid + "");
            RequestBody token = RequestBody.create(MediaType.parse("text/plain"), sharedPreferences.getString(Config.KEY_TOKEN, ""));

            APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
            Log.d(TAG, "sign: " + APICommon.getSign(APICommon.getApiKey(), "api/v1/files/postUploadFile") + " appid: " + APICommon.getAppId() + " device_type: " + APICommon.getDeviceType() + " token: " + sharedPreferences.getString(Config.KEY_TOKEN, "") + " userid: " + userid);
            Call<UploadFileResponse> call = service.postuploadFile(sign, appid,deviceType , token, userId, requestBodyImage);
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
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        pages.add(FragmentPagerItem.of(getString(tabs()[0]), MyProfileFragment.class));
        pages.add(FragmentPagerItem.of(getString(tabs()[1]), ExprienceFragment.class));
        pages.add(FragmentPagerItem.of(getString(tabs()[2]), SkillsFragment.class));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);

        viewPager.setAdapter(adapter);
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

        custom_text1 = (TextView) v.findViewById(R.id.custom_text1);
        custom_text2 = (TextView) v.findViewById(R.id.custom_text2);
        custom_text3 = (TextView) v.findViewById(R.id.custom_text3);
        img_avatar = (CircleImageView) v.findViewById(R.id.img_avatar);
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
