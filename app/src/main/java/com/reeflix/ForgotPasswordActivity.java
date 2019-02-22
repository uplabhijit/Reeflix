package com.reeflix;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.reeflix.Constant.ApiConstant;
import com.reeflix.Constant.CustomViewPager;
import com.reeflix.utility.SDUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    public CustomViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    public View view;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    public EditText emailText, otpText, newpassword, confirm_password;
    private ProgressDialog progress;
    public ProgressBar progressBar;
    RequestQueue requestQueue;
    public Button btn_verifyEmail, btn_verifyOtp, btn_resetPassword;
    static final String REQ_TAG = "VERIFYEMAILACTIVITY";
    public int mCurrentFragmentPosition;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_forgot_password);
        initUIElements();
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.email_for_forgotpasss,
                R.layout.otp_for_forgotpass_view_pagger,
                R.layout.confirm_pass_layout_for_view_pagger
        };
        // adding bottom dots
        addBottomDots(0);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        //To set paging enable or not
        viewPager.setPagingEnabled(false);
        btnSkip.setOnClickListener(v -> {
            int current = getItem(-1);
            if (current < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current);
            }
        });
        btnNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current);
            } else {
                System.out.println("Not set");
            }
        });
    }

    //To init UI elements
    private void initUIElements() {
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
    }

    //To add bottom dots
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            mCurrentFragmentPosition = position;
            //To add bottom dots
            addBottomDots(position);
            view.setTag("myview" + position);
            View views = viewPager.findViewWithTag("myview" + viewPager.getCurrentItem());
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                /* btnNext.setText(getString(R.string.start));*/
                btnSkip.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                newpassword = views.findViewById(R.id.newpassword);
                confirm_password = views.findViewById(R.id.confirm_password);
                btn_resetPassword = views.findViewById(R.id.btn_resetPassword);
                progressBar = views.findViewById(R.id.progressBar);
                //keyboard listener
                confirm_password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            System.out.println("trace for resetpassword");
                            resetPassword(null);
                            return true;
                        }
                        return false;
                    }
                });
                btn_resetPassword.setOnClickListener(view -> {
                    //function call to reset password
                    resetPassword(null);
                });
            } else if (position == 0) {
                btnSkip.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
            } else {
                // still pages are left
                /*btnNext.setText(getString(R.string.next));*/
                btnNext.setVisibility(View.GONE);
                btnSkip.setVisibility(View.VISIBLE);
                otpText = views.findViewById(R.id.otpText);
                //keyboard listener
                otpText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            System.out.println("trace for verify otp");
                            verifyOtp(null);
                            return true;
                        }
                        return false;
                    }
                });
                btn_verifyOtp = views.findViewById(R.id.btn_verifyOtp);
                progressBar = views.findViewById(R.id.progressBar);
                btn_verifyOtp.setOnClickListener(view -> {
                    //function call to verify otp
                    verifyOtp(null);
                });
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        private MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            System.out.println("position" + position);
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (position == 0) {
                btnSkip.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
            }
            view = layoutInflater != null ? layoutInflater.inflate(layouts[position], container, false) : null;
            container.addView(view);
            view.setTag("myview" + position);
            View views = viewPager.findViewWithTag("myview" + viewPager.getCurrentItem());
            if (position == 0) {
                btnSkip.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                emailText = views.findViewById(R.id.emailText);
                progressBar = views.findViewById(R.id.progressBar);
                //keyboard listener
                emailText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            System.out.println("trace for verify phone");
                            verifyEmail(null);
                            return true;
                        }
                        return false;
                    }
                });
                btn_verifyEmail = views.findViewById(R.id.btn_verifyEmail);
                btn_verifyEmail.setOnClickListener(view -> {
                    //Function call to verify email
                    verifyEmail(null);
                });
            }
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    /*function call to verify email or phonenumber*/
    private void verifyEmail(View view) {
        //To check internet connection
        if (SDUtility.isNetworkAvailable(ForgotPasswordActivity.this)) {
            try {
                if (SDUtility.isConnected()) {
                    if (SDUtility.isValidphoneNumber(emailText.getText().toString())) {
                        /*progress = new ProgressDialog(this);
                        //TODO: replace with string
                        progress.setMessage(getResources().getString(R.string.verify_email_loader));
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();*/
                        progressBar.setVisibility(View.VISIBLE);
                        JSONObject json = new JSONObject();
                        try {
                            json.put("phoneNumber", emailText.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            SDUtility.displayExceptionMessage(e.getMessage(), ForgotPasswordActivity.this);
                        }
                        //To get url to verify email
                        String url = ApiConstant.api_verifyemail_url;
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, json,
                                response -> {
                                    /* progress.dismiss();*/
                                    progressBar.setVisibility(View.GONE);
                                    System.out.println(response.toString());
                                    try {
                                        JSONObject serverResp = new JSONObject(response.toString());
                                        System.out.println("success result: " + serverResp);
                                        String errorStatus = serverResp.getString("error");
                                        if (errorStatus.equals("true")) {
                                            String errorMessage = serverResp.getString("message");
                                            Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        } else {
                                            //To set view pager to item
                                            viewPager.setCurrentItem(1);
                                        }
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        System.out.println("Error getting response || Error msg:-" + e.getMessage());
                                        SDUtility.displayExceptionMessage(e.getMessage(), ForgotPasswordActivity.this);
                                    }
                                }, error -> {
                            /*progress.dismiss();*/
                            progressBar.setVisibility(View.GONE);
                            System.out.println("Error getting response");
                            System.out.println("Error getting response || Error msg:-" + error.getMessage());
                            SDUtility.displayExceptionMessage(error.getMessage(), ForgotPasswordActivity.this);
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
                        emailText.setError(getString(R.string.invalidphonenumber_toastmsg));
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                SDUtility.displayExceptionMessage(e.getMessage(), ForgotPasswordActivity.this);
            }
        }
    }

    //Function call to verify otp
    public void verifyOtp(View view) {
        //To check internet connection
        if (SDUtility.isNetworkAvailable(ForgotPasswordActivity.this)) {
            try {
                if (SDUtility.isConnected()) {
                    if (otpText.getText().toString().length() > 0) {
                        /*progress = new ProgressDialog(this);
                        //TODO: replace with string
                        progress.setMessage(getResources().getString(R.string.verify_otp_loader));
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();*/
                        progressBar.setVisibility(View.VISIBLE);
                        JSONObject json = new JSONObject();
                        try {
                            json.put("phoneNumber", emailText.getText().toString());
                            json.put("otp", otpText.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //To get verify otp url
                        String url = ApiConstant.api_verifyotp_url;
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, json,
                                response -> {
                                    /* progress.dismiss();*/
                                    progressBar.setVisibility(View.GONE);
                                    System.out.println(response.toString());
                                    try {
                                        JSONObject serverResp = new JSONObject(response.toString());
                                        System.out.println("success result: " + serverResp);
                                        String errorStatus = serverResp.getString("error");
                                        if (errorStatus.equals("true")) {
                                            String errorMessage = serverResp.getString("message");
                                            Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        } else {
                                            token = serverResp.getString("token");
                                            viewPager.setCurrentItem(2);
                                        }
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        /* progress.dismiss();*/
                                        e.printStackTrace();
                                        System.out.println("Error getting response || Error msg:-" + e.getMessage());
                                        SDUtility.displayExceptionMessage(e.getMessage(), ForgotPasswordActivity.this);
                                    }
                                }, error -> {
                            progressBar.setVisibility(View.GONE);
                            System.out.println("Error getting response");
                            System.out.println("Error getting response || Error msg:-" + error.getMessage());
                            SDUtility.displayExceptionMessage(error.getMessage(), ForgotPasswordActivity.this);
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
                SDUtility.displayExceptionMessage(e.getMessage(), ForgotPasswordActivity.this);
            }
        }
    }

    //function call to reset password
    public void resetPassword(View view) {
        System.out.println("token" + token);
        //To check inyternet connection
        if (SDUtility.isNetworkAvailable(ForgotPasswordActivity.this)) {
            try {
                if (SDUtility.isConnected()) {
                    if (SDUtility.isValidPassword(newpassword.getText().toString()) && SDUtility.isValidPassword(confirm_password.getText().toString())) {
                        if (newpassword.getText().toString().equals(confirm_password.getText().toString())) {
                            /*progress = new ProgressDialog(this);
                            //TODO: replace with string
                            progress.setMessage(getResources().getString(R.string.reset_password_loader));
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();*/
                            progressBar.setVisibility(View.VISIBLE);
                            JSONObject json = new JSONObject();
                            try {
                                json.put("phoneNumber", emailText.getText().toString());
                                json.put("password", newpassword.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                SDUtility.displayExceptionMessage(e.getMessage(), ForgotPasswordActivity.this);
                            }
                            //To get password reset url
                            String url = ApiConstant.api_resetpassword_url;
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
                                                Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                            } else {
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                            System.out.println("Error getting response || Error msg:-" + e.getMessage());
                                            SDUtility.displayExceptionMessage(e.getMessage(), ForgotPasswordActivity.this);
                                        }
                                    }, error -> {
                                progressBar.setVisibility(View.GONE);
                                System.out.println("Error getting response");
                                System.out.println("Error getting response || Error msg:-" + error.getMessage());
                                SDUtility.displayExceptionMessage(error.getMessage(), ForgotPasswordActivity.this);
                            }) {    //this is the part, that adds the header to the request
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("key", "c815a866efbe9cebfb842062cc85e46f");
                                    params.put("Authorization", "Bearer " + token);
                                    params.put("content-type", "application/json");
                                    return params;
                                }
                            };
                            jsonObjectRequest.setTag(REQ_TAG);
                            requestQueue.add(jsonObjectRequest);
                        } else {
                            Toast.makeText(this, R.string.bothpasswordsame_errormsg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        newpassword.setError(getString(R.string.invalidpassword_errormsg));
                        confirm_password.setError(getString(R.string.invalidpassword_errormsg));
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                SDUtility.displayExceptionMessage(e.getMessage(), ForgotPasswordActivity.this);
            }
        }
    }
}
