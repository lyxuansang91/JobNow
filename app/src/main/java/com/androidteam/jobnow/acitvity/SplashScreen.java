package com.androidteam.jobnow.acitvity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidteam.jobnow.BuildConfig;
import com.androidteam.jobnow.R;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.RegisterFBReponse;
import com.androidteam.jobnow.models.RegisterFBRequest;
import com.androidteam.jobnow.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    BuildConfig.APPLICATION_ID,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        setupView();
        initData();
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
//                                    CLog.d(TAG, " register fb: " + object.optString("id") + " " + object.optString("email"));
//                                    editor.putString(Configruation.KEY_IDFB, object.optString("id"));
//                                    editor.putString(Configruation.KEY_NAME, object.optString("name"));
//                                    editor.commit();
//                                    CLog.d(TAG, "token fb: " + loginResult.getAccessToken().getToken());
//                                    EventBus.getDefault().post(new LoginSuccessEvent(LoginSuccessEvent.TYPE_FACEBOOK));
//                                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                                    startActivity(intent);
                                    String email = object.optString("email");
                                    String name = object.optString("name");
                                    String fbid = object.optString("id");
                                    String avatar = Utils.addressAvatarFB(fbid);
                                    APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
                                    Call<RegisterFBReponse> registerFBReponseCall =
                                            service.registerFB(new RegisterFBRequest(name, email, avatar, fbid));
                                    registerFBReponseCall.enqueue(new Callback<RegisterFBReponse>() {
                                        @Override
                                        public void onResponse(Response<RegisterFBReponse> response, Retrofit retrofit) {
                                            Log.d(TAG, "get login response: " + response.body().toString());
                                            int code = response.body().code;
                                            if (code == 200) {
                                                SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(Config.KEY_TOKEN, response.body().result.apiToken).commit();
                                                editor.putInt(Config.KEY_ID, response.body().result.id).commit();
                                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                                startActivity(intent);
                                            }
                                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SplashScreen.this, "Login Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SplashScreen.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupView() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
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
}
