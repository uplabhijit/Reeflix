package com.underscoretec.reeflix;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.underscoretec.reeflix.Constant.ApiConstant;
import com.underscoretec.reeflix.utility.SDUtility;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import in.mayanknagwanshi.imagepicker.imageCompression.ImageCompressionListener;
import in.mayanknagwanshi.imagepicker.imagePicker.ImagePicker;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Registration extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate {

    public ImageView camerafab;
    public TextView termsandcondition;
    private ProgressDialog progressDialog;
    static final String REQ_TAG = "VACTIVITY";
    public String profileImageId;
    ImagePicker imagePicker;
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private SingleUploadBroadcastReceiver uploadReceiver;
    Spanned Text;
    private CircleImageView croppedImageView;
    private ProgressBar profileImageProgress;
    private Bitmap selectedImage;
    public EditText input_name, input_email, input_phonenumber, input_address, input_zipcode, input_password;
    private RequestQueue requestQueue;
    public static AppCompatActivity registrationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);

        registrationActivity = this;

        /*function call to init ui elements*/
        initUIelements();

        /*To initialize image picker*/
        imagePicker = new ImagePicker();

        //To call request queue for volley
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        //To initialize receiver
        uploadReceiver = new SingleUploadBroadcastReceiver();

        //Function call to choose image
        camerafab.setOnClickListener(view ->

                /*function call to open image picker*/
                openImagePicker());
    }

    /*function call to open image picker*/
    @AfterPermissionGranted(101)
    private void openImagePicker() {
        if (EasyPermissions.hasPermissions(Registration.this, galleryPermissions)) {
            imagePicker.withActivity(Registration.this) //calling from activity
                    .chooseFromGallery(true) //default is true
                    .chooseFromCamera(true) //default is true
                    .withCompression(false) //default is true
                    .start();
        } else {
            EasyPermissions.requestPermissions(Registration.this, getString(R.string.accessstorage_toastmsg), 101, galleryPermissions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    //function call to init ui elements
    private void initUIelements() {
        croppedImageView = findViewById(R.id.personimage);
        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_address = findViewById(R.id.input_address);
        input_phonenumber = findViewById(R.id.input_phonenumber);
        input_zipcode = findViewById(R.id.input_zipcode);
        input_password = findViewById(R.id.input_password);
        profileImageProgress = findViewById(R.id.profile_imageview_progressbar);
        termsandcondition = findViewById(R.id.termsandcondition);
        Text = Html.fromHtml("By continuing, you agree to our " +
                "<a href='https://underscoretec.com/'>Terms of use and Privacy policies </a>");
        termsandcondition.setMovementMethod(LinkMovementMethod.getInstance());
        termsandcondition.setText(Text);
        camerafab = findViewById(R.id.camerafab);
        //To get deviceId
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            //Add compression listener if withCompression is set to true
            imagePicker.addOnCompressListener(new ImageCompressionListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompressed(String filePath) {//filePath of the compressed image
                    //convert to bitmap easily
                    selectedImage = BitmapFactory.decodeFile(filePath);
                    croppedImageView.setImageBitmap(selectedImage);
                }
            });
        }
        //call the method 'getImageFilePath(Intent data)' even if compression is set to false
        String filePath = imagePicker.getImageFilePath(data);
        System.out.println("filepath_--------------------" + filePath);
        if (filePath != null) {//filePath will return null if compression is set to true
            selectedImage = BitmapFactory.decodeFile(filePath);
            profileImageProgress.setVisibility(View.VISIBLE);
            uploadMultipart(filePath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //To upload multi part data
    private void uploadMultipart(String filePath) {
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            String uploadUrl = ApiConstant.api_imageupload_url;
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, uploadUrl)
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("key", "c815a866efbe9cebfb842062cc85e46f")
                    .addFileToUpload(filePath, "file") //Adding file
                    .addParameter("name", "profileImage") //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            profileImageProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProgress(int progress) {
        Log.d("PROGRESS", "progress = " + progress);
        profileImageProgress.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgress(progress);
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        // your code here
        Log.d("On Progress", String.valueOf(uploadedBytes));
    }

    @Override
    public void onError(Exception exception) {
        // your code here
        Log.d("On error", String.valueOf(exception));
        profileImageProgress.setVisibility(View.GONE);
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        progressDialog.dismiss();
        profileImageProgress.setVisibility(View.GONE);
        String str = new String(serverResponseBody);
        try {
            JSONObject serverResp = new JSONObject(str);
            System.out.println("profile respone>>>>>>>>>>>>>>>>>>>>>>" + serverResp);
            if (serverResp.getString("error").equals("true")) {
                String errorMessage = serverResp.getString("message");
                Toast.makeText(Registration.this, errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                croppedImageView.setImageBitmap(selectedImage);
                profileImageProgress.setVisibility(View.GONE);
                profileImageId = serverResp.getJSONObject("upload").getString("_id");
                //register(null);
                Toast.makeText(Registration.this, getString(R.string.proupdate_toastmsg), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            SDUtility.displayExceptionMessage(e.getMessage(), Registration.this);
        }
    }

    @Override
    public void onCancelled() {
        System.out.println(("On cancelled"));
        profileImageProgress.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] galleryPermissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, galleryPermissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, galleryPermissions, grantResults, this);
    }

    //To go to login
    public void login(View view) {
        finish();
    }

    /*function call to register*/
    public void register(View view) {
        // To check internet connection
        if (SDUtility.isNetworkAvailable(Registration.this)) {
            try {
                if (SDUtility.isConnected()) {
                    if (SDUtility.isValidEmail(input_email.getText().toString())) {
                        if (SDUtility.isValidPassword(input_password.getText().toString())) {
                            if (SDUtility.isValidphoneNumber(input_phonenumber.getText().toString())) {
                                progressDialog = new ProgressDialog(this);
                                progressDialog.setMessage(getString(R.string.registeringwaitloader_msg));
                                progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
                                progressDialog.show();
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("name", input_name.getText().toString());
                                    json.put("email", input_email.getText().toString());
                                    json.put("phoneNumber", input_phonenumber.getText().toString());
                                    json.put("password", input_password.getText().toString());
                                    json.put("address", input_address.getText().toString());
                                    json.put("zipcode", input_zipcode.getText().toString());
                                    if (profileImageId != null) {
                                        json.put("imageId", profileImageId);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(json);
                                String url = ApiConstant.api_registration_url;
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                                        response -> {
                                            progressDialog.dismiss();
                                            //serverResp.setText("String Response : "+ response.toString());
                                            System.out.println(response.toString());
                                            try {
                                                JSONObject serverResp = new JSONObject(response.toString());
                                                System.out.println("success result: " + serverResp);
                                                String errorStatus = serverResp.getString("error");
                                                if (errorStatus.equals("true")) {
                                                    String errorMessage = serverResp.getString("message");
                                                    Toast.makeText(Registration.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                } else {
                                                   /* SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putString("store", response.toString());
                                                    editor.putBoolean("loginStatus", true);
                                                    editor.commit();
                                                    boolean status = pref.getBoolean("loginStatus", false);
                                                    if (status) {*/
                                                        Intent fp = new Intent(Registration.this, otpActivity.class);
                                                        fp.putExtra("phoneNumber",response.getJSONObject("result").getString("phoneNumber"));
                                                        fp.putExtra("otp",response.getJSONObject("result").getString("otp"));
                                                        startActivity(fp);
                                                        /*finish();
                                                        Login.mainActivity.finish();*/
                                                  /*  }*/
                                                }
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch block
                                                progressDialog.dismiss();
                                                e.printStackTrace();
                                                SDUtility.displayExceptionMessage(e.getMessage(), Registration.this);
                                            }
                                        }, error -> {
                                    progressDialog.dismiss();
                                    System.out.println("Error getting response");
                                    Toast.makeText(Registration.this, R.string.failedregistration_stringmsg, Toast.LENGTH_SHORT).show();
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
                                input_phonenumber.setError(getString(R.string.invalidphonenumber_toastmsg));
                            }
                        } else {
                            input_password.setError(getString(R.string.invalidpassword_errormsg));
                        }
                    } else {
                        input_email.setError(getString(R.string.invalidemail_errormsg));
                    }
                } else {
                    Toast.makeText(Registration.this, getString(R.string.internetconnection_textmsg), Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                //function call display error message
                SDUtility.displayExceptionMessage(e.getMessage(), Registration.this);
            }
        } else {
            Toast.makeText(Registration.this, getString(R.string.internetconnection_textmsg), Toast.LENGTH_SHORT).show();
        }
    }
}
