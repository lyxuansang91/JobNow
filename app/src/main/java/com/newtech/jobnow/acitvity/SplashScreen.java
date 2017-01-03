package com.newtech.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.models.CountJobResponse;
import com.newtech.jobnow.models.RegisterFBReponse;
import com.newtech.jobnow.models.RegisterFBRequest;
import com.newtech.jobnow.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = SplashScreen.class.getSimpleName();
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnLoginFacebook;
    private TextView tvNumberJob;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    BuildConfig.APPLICATION_ID,
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }



        InitProject initProject = new InitProject();
        initProject.execute();

    }

    private void initData() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(final JSONObject object,
                                                    GraphResponse response) {
                                if (object != null) {
                                    String email = object.optString("email");
                                    String name = object.optString("name");
                                    String fbid = object.optString("id");
                                    String avatar = Utils.addressAvatarFB(fbid);
                                    APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
                                    Call<RegisterFBReponse> registerFBReponseCall =
                                            service.registerFB(new RegisterFBRequest(name, email, avatar, fbid));
                                    registerFBReponseCall.enqueue(new Callback<RegisterFBReponse>() {
                                        @Override
                                        public void onResponse(final Response<RegisterFBReponse> response, Retrofit retrofit) {
                                            Log.d(TAG, "get login response: " + response.body().toString());
                                            int code = response.body().code;
                                            if (code == 200) {
                                                SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(Config.KEY_TOKEN, response.body().result.apiToken).commit();
                                                editor.putInt(Config.KEY_ID, response.body().result.id).commit();
                                                editor.putString(Config.KEY_EMAIL, response.body().result.email).commit();
                                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(),
                                                                response.body().message, Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                });

                                            }
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Log.d(TAG, "(on failed): " + t.toString());
                                        }
                                    });
                                } else {

                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashScreen.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onError(final FacebookException error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashScreen.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void getCountJob() {
        final ProgressDialog progressDialog = ProgressDialog.show(SplashScreen.this, "", getString(R.string.loading), true, true);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<CountJobResponse> call = service.getCountJob(
                APICommon.getSign(APICommon.getApiKey(), "api/v1/jobs/getCountJob"),
                APICommon.getAppId(), APICommon.getDeviceType(), 0);
        call.enqueue(new Callback<CountJobResponse>() {
            @Override
            public void onResponse(final Response<CountJobResponse> response, Retrofit retrofit) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Log.d(TAG, "response:" + response.body());
                            if (response.body() != null && response.body().code == 200) {
                                tvNumberJob.setText(getString(R.string.number_job, response.body().result));
                            } else {
                                Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                });


            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.error_connect), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }
        });
    }

    private void setupView() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
        tvNumberJob = (TextView) findViewById(R.id.tvNumberJob);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFacebook();
            }
        });
    }

    long key_pressed;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - key_pressed < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Back again to exit", Toast.LENGTH_SHORT).show();
        }
        key_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loginFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
    }


    private class InitProject extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setupView();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    Config.Pref, Context.MODE_PRIVATE);
            String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
            Boolean result = false;
            if (token != null && !token.isEmpty()) {
              result = true;
            }
            initData();

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if(!aVoid) {
                getCountJob();
            } else {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }


    }
}
