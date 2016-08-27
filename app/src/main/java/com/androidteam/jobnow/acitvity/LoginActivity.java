package com.androidteam.jobnow.acitvity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.LoginRequest;
import com.androidteam.jobnow.models.LoginResponse;
import com.androidteam.jobnow.utils.Utils;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    long key_pressed;
    private EditText edtPassword;
    private EditText edtEmail;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
    }

    private void initUI() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.login);
        }

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLogin();
            }
        });
    }

    private void getLogin() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(LoginActivity.this, getString(R.string.pleaseInputEmail), Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, getString(R.string.pleaseInputPassword), Toast.LENGTH_SHORT).show();
        } else if (!Utils.isEmailValid(email)) {
            Toast.makeText(LoginActivity.this, getString(R.string.emailNotValid), Toast.LENGTH_SHORT).show();
        } else {
            APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
            Call<LoginResponse> loginResponseCall =
                    service.loginUser(new LoginRequest(email, password));
            loginResponseCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Response<LoginResponse> response, Retrofit retrofit) {
                    Log.d(TAG, "get login response: " + response.body().toString());
                    int code = response.code();
                    if (code == 200) {
                        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Config.KEY_TOKEN, response.body().result.ApiToken).commit();
                        editor.putInt(Config.KEY_ID, response.body().result.id).commit();
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);
                    }
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d(TAG, "(on failed): " + t.toString());
                }
            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
