package com.underscoretec.reeflix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.underscoretec.reeflix.Constant.ApiConstant;
import com.underscoretec.reeflix.utility.SDUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import customfonts.EditText_Roboto_Regular;

public class otpActivity extends AppCompatActivity {

    public Button btn_verifyactivationotp;
    public EditText_Roboto_Regular otp;
    private ProgressDialog progress;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;
    static final String REQ_TAG = "VERIFYOTPACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_otp);

        /*function call to init ui element*/
        initUiElement();
    }

    /*function call to init ui element*/
    private void initUiElement() {
        btn_verifyactivationotp = findViewById(R.id.btn_verifyactivationotp);
        progressBar = findViewById(R.id.progressBar);
        otp = findViewById(R.id.otp);
        //keyboard listener
        otp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    System.out.println("trace for login");
                    verifyOtp(null);
                    return true;
                }
                return false;
            }
        });
        btn_verifyactivationotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*function call to verify otp*/
                verifyOtp(null);
            }
        });
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        /*To set text in otp field*/
        otp.setText(getIntent().getExtras().getString("otp"));
    }

    /*function call to verify otp*/
    private void verifyOtp(View v) {

        /*to check internet connection*/
        if (SDUtility.isNetworkAvailable(otpActivity.this)) {
            try {
                if (SDUtility.isConnected()) {
                    if (otp.getText().toString().length() > 0) {
                       /* progress = new ProgressDialog(this);
                        //TODO: replace with string
                        progress.setMessage(getResources().getString(R.string.verify_otp_loader));
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();*/
                        progressBar.setVisibility(View.VISIBLE);
                        JSONObject json = new JSONObject();
                        try {
                            json.put("phoneNumber", getIntent().getExtras().getString("phoneNumber"));
                            json.put("otp", otp.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //To get verify otp url
                        String url = ApiConstant.api_verifyotpduringregistration_url;
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, json,
                                response -> {
                                    progressBar.setVisibility(View.GONE);
                                    System.out.println(response.toString());
                                    try {
                                        JSONObject serverResp = new JSONObject(response.toString());
                                        System.out.println("success result: " + serverResp);
                                        String errorStatus = serverResp.getString("error");
                                        if (errorStatus.equals("true")) {
                                            String errorMessage = serverResp.getString("message");
                                            Toast.makeText(otpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        } else {
                                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("store", response.toString());
                                            editor.putBoolean("loginStatus", true);
                                            editor.commit();
                                            boolean status = pref.getBoolean("loginStatus", false);
                                            if (status) {
                                                Intent fp = new Intent(otpActivity.this, HomeActivity.class);
                                                startActivity(fp);
                                                finish();
                                                Registration.registrationActivity.finish();
                                                Login.mainActivity.finish();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        progressBar.setVisibility(View.GONE);
                                        e.printStackTrace();
                                        System.out.println("Error getting response || Error msg:-" + e.getMessage());
                                        SDUtility.displayExceptionMessage(e.getMessage(), otpActivity.this);
                                    }
                                }, error -> {
                            progressBar.setVisibility(View.GONE);
                            System.out.println("Error getting response");
                            System.out.println("Error getting response || Error msg:-" + error.getMessage());
                            SDUtility.displayExceptionMessage(error.getMessage(), otpActivity.this);
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
                        Toast.makeText(this, R.string.enterotp_msg, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                SDUtility.displayExceptionMessage(e.getMessage(), otpActivity.this);
            }
        }
    }
}
