package com.underscoretec.reeflix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.underscoretec.reeflix.Constant.ApiConstant;
import com.underscoretec.reeflix.utility.SDUtility;
import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    public TextView createaccount;
    public EditText emailText, passText;
    /* private AlertDialog dialog;*/
    public ProgressBar progressbar;
    private RequestQueue requestQueue;
    static final String REQ_TAG = "LOGINACTIVITY";
    public boolean status;
    public static AppCompatActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        initUiElement();
        mainActivity = this;
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        //To check Login Status
        checkLoginStatus();
    }

    //To init UI elements
    private void initUiElement() {
        emailText = findViewById(R.id.emailText);
        passText = findViewById(R.id.passText);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        passText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // Do you job here which you want to done through event
                    System.out.println("trace for login");
                    login(null);
                    return true;
                }
                return false;
            }
        });
    }

    //To check login status
    private void checkLoginStatus() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        status = pref.getBoolean("loginStatus", false);
        if (status) {
            Intent fp;
            fp = new Intent(Login.this, HomeActivity.class);
            startActivity(fp);
            finish();
        } else {
            System.out.println("stay in login page");
        }
    }

    //To create new account
    public void createAccount(View view) {
        Intent fp;
        fp = new Intent(Login.this, Registration.class);
        startActivity(fp);
    }

    /*function call to login*/
    public void login(View v) {

        /*To check internet connection*/
        if (SDUtility.isNetworkAvailable(Login.this))
            try {
                if (SDUtility.isConnected()) {
                    if (SDUtility.isValidEmail(emailText.getText().toString())) {
                        if (SDUtility.isValidPassword(passText.getText().toString())) {
                            /*progress = new ProgressDialog(this);
                            //TODO: replace with string
                            progress.setMessage(getResources().getString(R.string.login_loader));
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();*/
                            progressbar.setVisibility(View.VISIBLE);
                            JSONObject json = new JSONObject();
                            try {
                                json.put("phoneNumber", emailText.getText().toString());
                                json.put("password", passText.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(json);
                            //To get login url
                            String url = ApiConstant.api_login_url;
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                                    response -> {
                                        /*dialog.dismiss();*/
                                        progressbar.setVisibility(View.GONE);
                                        System.out.println(response.toString());
                                        try {
                                            JSONObject serverResp = new JSONObject(response.toString());
                                            System.out.println("success result: " + serverResp);
                                            String errorStatus = serverResp.getString("error");
                                            if (errorStatus.equals("true")) {
                                                String errorMessage = serverResp.getString("message");
                                                Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                                            } else {
                                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putString("store", response.toString());
                                                editor.putBoolean("loginStatus", true);
                                                editor.commit();
                                                status = pref.getBoolean("loginStatus", false);
                                                if (status) {
                                                    Intent fp;
                                                    //To go to dashboard page
                                                    fp = new Intent(Login.this, HomeActivity.class);
                                                    startActivity(fp);
                                                    finish();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                            /* dialog.dismiss();*/
                                            progressbar.setVisibility(View.GONE);
                                            System.out.println("Error getting response || Error msg:-" + e.getMessage());
                                            SDUtility.displayExceptionMessage(e.getMessage(), Login.this);
                                        }
                                    }, error -> {
                                /* dialog.dismiss();*/
                                progressbar.setVisibility(View.GONE);
                                System.out.println("Error getting response");
                                System.out.println("Error getting response || Error msg:-" + error.getMessage());
                                SDUtility.displayExceptionMessage(error.getMessage(), Login.this);
                            }) {    //this is the part, that adds the header to the request
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("key", "c815a866efbe9cebfb842062cc85e46f");
                                    params.put("content-type", "application/json");
                                    return params;
                                }
                            };
                            jsonObjectRequest.setTag(REQ_TAG);
                            requestQueue.add(jsonObjectRequest);
                        } else {
                            passText.setError(getString(R.string.invalidpassword_errormsg));
                        }
                    } else {
                        emailText.setError(getString(R.string.invalidemail_errormsg));
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                SDUtility.displayExceptionMessage(e.getMessage(), Login.this);
            }
    }

    public void forgotPassword(View view) {
        Intent fp;
        fp = new Intent(Login.this, ForgotPasswordActivity.class);
        startActivity(fp);
    }
}
