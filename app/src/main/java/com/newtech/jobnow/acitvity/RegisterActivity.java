package com.newtech.jobnow.acitvity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.models.RegisterRequest;
import com.newtech.jobnow.models.RegisterResponse;
import com.newtech.jobnow.utils.Utils;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnSignup;
    private EditText edtFullName;
    private EditText edtPhoneNumber;
    private EditText edtEmail;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
    }

    private void initUI() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.sign_up);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        btnSignup = (Button) findViewById(R.id.btnSignup);
        edtFullName = (EditText) findViewById(R.id.edtFullName);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                startActivity(intent);
                String fullname = edtFullName.getText().toString();
                String phoneNumber = edtPhoneNumber.getText().toString();
                final String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.pleaseInputEmail), Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.pleaseInputPassword), Toast.LENGTH_SHORT).show();
                } else if (!Utils.isEmailValid(email)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.emailNotValid), Toast.LENGTH_SHORT).show();
                } else if (fullname.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.pleaseInputFullName), Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.pleaseInputPhoneNumber), Toast.LENGTH_SHORT).show();
                } else {
                    APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
                    Call<RegisterResponse> registerFBReponseCall =
                            service.registerUser(new RegisterRequest(fullname, phoneNumber, email, password));
                    registerFBReponseCall.enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(Response<RegisterResponse> response, Retrofit retrofit) {
                            Log.d(TAG, "get login response: " + response.body().toString());
                            int code = response.body().code;
                            if (code == 200) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setTitle("Welcome come JobNow!");
                                builder.setMessage("Please check your email " + email + " and learn how to get started. Good luck!");
                                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                                builder.setCancelable(false);

                            } else {
                                Toast.makeText(getApplicationContext(), response.body().message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d(TAG, "(on failed): " + t.toString());
                        }
                    });
                }

            }
        });
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

    long key_pressed;
}
