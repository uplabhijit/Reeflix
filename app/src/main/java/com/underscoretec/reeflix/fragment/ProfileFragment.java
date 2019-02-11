package com.underscoretec.reeflix.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.underscoretec.reeflix.Constant.ApiConstant;
import com.underscoretec.reeflix.Login;
import com.underscoretec.reeflix.R;
import com.underscoretec.reeflix.RequestQueueSingleton;
import com.underscoretec.reeflix.SingleUploadBroadcastReceiver;
import com.underscoretec.reeflix.utility.SDUtility;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import customfonts.MyTextView_Roboto_Medium;
import de.hdodenhof.circleimageview.CircleImageView;
import in.mayanknagwanshi.imagepicker.imageCompression.ImageCompressionListener;
import in.mayanknagwanshi.imagepicker.imagePicker.ImagePicker;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfileFragment extends Fragment implements SingleUploadBroadcastReceiver.Delegate {

    public Button btn_update;
    CircleImageView editImage, personimage;
    public EditText input_email, input_phonenumber, input_address, input_zipcode;
    public ProgressDialog progress;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;
    static final String REQ_TAG = " PROFILEUPDATEACTIVITY";
    ImagePicker imagePicker;
    private SingleUploadBroadcastReceiver uploadReceiver;
    public String uploadId;
    public TextView input_name;
    private String filePath;
    private Bitmap selectedImage;
    private ProgressBar profileImageProgress;
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private JSONObject serverResp;
    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_searches).setVisible(false);
        inflater.inflate(R.menu.profile_popup, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*To work on item selected*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_edit:
                btn_update.setVisibility(View.VISIBLE);
                editImage.setVisibility(View.VISIBLE);
                input_name.setEnabled(true);
                input_address.setEnabled(true);
                input_zipcode.setEnabled(true);
                setHasOptionsMenu(false);
                return true;
            case R.id.action_logout:
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.show();
                MyTextView_Roboto_Medium acceptButton = (MyTextView_Roboto_Medium) dialog.findViewById(R.id.btn_yes);
                // if yes  button is clicked, open the custom dialog
                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("store", "");
                        editor.putBoolean("loginStatus", false);
                        editor.commit();
                        Intent intent = new Intent(getActivity(), Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                MyTextView_Roboto_Medium rejectButton = (MyTextView_Roboto_Medium) dialog.findViewById(R.id.btn_no);
                // if yes  button is clicked, close the custom dialog
                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        System.out.println("on Resume");
        super.onResume();
        uploadReceiver.register(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("on pause");
        uploadReceiver.unregister(getContext());
    }

    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        //To init ui element
        initUiElement();
        return view;
    }

    private void initUiElement() {
        btn_update = view.findViewById(R.id.btn_update);
        editImage = view.findViewById(R.id.editimage);
        personimage = view.findViewById(R.id.personimage);
        input_name = view.findViewById(R.id.input_name);
        input_phonenumber = view.findViewById(R.id.input_phonenumber);
        input_email = view.findViewById(R.id.input_email);
        input_address = view.findViewById(R.id.input_address);
        input_zipcode = view.findViewById(R.id.input_zipcode);//keyboard Listener
        profileImageProgress = view.findViewById(R.id.pro_imageview_progressbar);
        progressBar = view.findViewById(R.id.progressBar);
        //add listener to update button
        btn_update.setOnClickListener(v -> {
            //function call to update profile
            updateProfile();
        });
        //add listener to update profile image
        editImage.setOnClickListener(v -> {
            //function call update profile image
            updateProfileImage();
        });
        input_zipcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    System.out.println("trace for update profile");
                    updateProfile();
                    return true;
                }
                return false;
            }
        });
        //To get profile data
        getProfileData();
        //To initialize image picker
        imagePicker = new ImagePicker();
        //To initialize receiver
        uploadReceiver = new SingleUploadBroadcastReceiver();
        //To get request queue
        requestQueue = RequestQueueSingleton.getInstance(getActivity()).getRequestQueue();
    }

    //function call to get profile data
    private void getProfileData() {
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0);
        try {
            serverResp = new JSONObject(pref.getString("store", ""));
            System.out.println(serverResp);
            if (serverResp.getJSONObject("result").has("name")) {
                input_name.setText(serverResp.getJSONObject("result").getString("name"));
            }
            if (serverResp.getJSONObject("result").has("email")) {
                input_email.setText(serverResp.getJSONObject("result").getString("email"));
            }
            if (serverResp.getJSONObject("result").has("phoneNumber")) {
                input_phonenumber.setText(serverResp.getJSONObject("result").getString("phoneNumber"));
            }
            if (serverResp.getJSONObject("result").has("address")) {
                input_address.setText(serverResp.getJSONObject("result").getString("address"));
            }
            if (serverResp.getJSONObject("result").has("zipcode")) {
                input_zipcode.setText(serverResp.getJSONObject("result").getString("zipcode"));
            }
            if (serverResp.getJSONObject("result").has("imageId")) {
                Boolean value = !serverResp.getJSONObject("result").getString("imageId").equals("");
                if (value) {
                   /* progress = new ProgressDialog(getContext());
                    progress.setMessage("Loading...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();*/
                    profileImageProgress.setVisibility(View.VISIBLE);
                    String getImageUrl = ApiConstant.getimage_url + serverResp.getJSONObject("result").getString("imageId");
                    Glide.with(this)
                            .asBitmap()
                            .load(getImageUrl)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    personimage.setImageBitmap(resource);
                                    profileImageProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    super.onLoadFailed(errorDrawable);
                                    profileImageProgress.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Image load fails", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

            /*To display toast message*/
            SDUtility.displayExceptionMessage(e.getMessage(), getActivity());
        }
        //To make all fiels disable
        input_name.setEnabled(false);
        input_email.setEnabled(false);
        input_phonenumber.setEnabled(false);
        input_phonenumber.setEnabled(false);
        input_address.setEnabled(false);
        input_zipcode.setEnabled(false);
    }

    //function call to update profile
    private void updateProfile() {
        //To check internet connection
        if (SDUtility.isNetworkAvailable(getContext())) {
            try {

                /*To check internet is connected or not*/
                if (SDUtility.isConnected()) {
                 /*   progress = new ProgressDialog (getContext ());
                    progress.setMessage (getString (R.string.pleasewait_text));
                    progress.setCancelable (false); // disable dismiss by tapping outside of the dialog
                    progress.show ();*/
                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject profilejson = new JSONObject();
                    try {
                        profilejson.put("name", input_name.getText().toString());
                        profilejson.put("address", input_address.getText().toString());
                        profilejson.put("zipcode", input_zipcode.getText().toString());
                        if (uploadId != null) {
                            profilejson.put("imageId", uploadId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SDUtility.displayExceptionMessage(e.getMessage(), getActivity());
                    }
                    //To get url for create event
                    String url = ApiConstant.api_updateuser_url + serverResp.getJSONObject("result").getString("_id");
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, profilejson,
                            response -> {
                                /* progress.dismiss ();*/
                                progressBar.setVisibility(View.GONE);
                                try {
                                    JSONObject serverResp = new JSONObject(response.toString());
                                    System.out.println("success result: " + serverResp);
                                    String errorStatus = serverResp.getString("error");
                                    if (errorStatus.equals("true")) {
                                        String errorMessage = serverResp.getString("message");
                                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                    } else {
                                        System.out.println("successfully>>>>>>>");
                                        SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("store", response.toString());
                                        editor.commit();
                                        //To get updated profile data
                                        getProfileData();
                                        btn_update.setVisibility(View.INVISIBLE);
                                        editImage.setVisibility(View.INVISIBLE);
                                        setHasOptionsMenu(true);
                                        Toast.makeText(getActivity(), R.string.profileupdate_toastmsg, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    /*progress.dismiss ();*/
                                    progressBar.setVisibility(View.GONE);
                                    e.printStackTrace();
                                }
                            }, error -> {
                        /*progress.dismiss ();*/
                        progressBar.setVisibility(View.GONE);
                        System.out.println("Error getting response");
                        SDUtility.displayExceptionMessage(error.getMessage(), getActivity());
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
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                SDUtility.displayExceptionMessage(e.getMessage(), getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Function call to upload profile image
    private void updateProfileImage() {
        if (EasyPermissions.hasPermissions(getActivity(), galleryPermissions)) {
            imagePicker.withActivity(getActivity()) //calling from activity
                    .chooseFromGallery(true) //default is true
                    .chooseFromCamera(true) //default is true
                    .withCompression(false) //default is true
                    .start();
        } else {
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.accessstorage_toastmsg), 101, galleryPermissions);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            //Add compression listener if withCompression is set to true
            imagePicker.addOnCompressListener(new ImageCompressionListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompressed(String filePath) {//filePath of the compressed image
                    //convert to bitmap easily
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    //To set person image
                    selectedImage = BitmapFactory.decodeFile(filePath);
                    personimage.setImageBitmap(selectedImage);
                }
            });
        }
        //call the method 'getImageFilePath(Intent data)' even if compression is set to false
        filePath = imagePicker.getImageFilePath(data);
        System.out.println("filepath_--------------------" + filePath);
        if (filePath != null) {//filePath will return null if compression is set to true
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            selectedImage = BitmapFactory.decodeFile(filePath, options);
            profileImageProgress.setVisibility(View.VISIBLE);
            uploadMultipart(filePath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //To upload multi part image
    private void uploadMultipart(String filePath) {
        //Uploading code
        System.out.println(" bhhnfnjjnfv");
        try {
            String uploadId = UUID.randomUUID().toString();
            String uploadUrl = ApiConstant.api_imageupload_url;
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);
            //Creating a multi part request
            new MultipartUploadRequest(getContext(), uploadId, uploadUrl)
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("key", "c815a866efbe9cebfb842062cc85e46f")
                    .addFileToUpload(filePath, "file") //Adding file
                    .addParameter("name", "profileImage") //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            profileImageProgress.setVisibility(View.GONE);
        }
    }

    /*To show progress using upload receiver*/
    @Override
    public void onProgress(int progr) {
        System.out.println("PROGRESS progress = " + progr);
        profileImageProgress.setVisibility(View.VISIBLE);
    }

    /*On progress status*/
    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        // your code here
        System.out.println("On Progress" + String.valueOf(uploadedBytes));
    }

    /*To show error*/
    @Override
    public void onError(Exception exception) {
        System.out.println("");
        // your code here
        System.out.println("On error" + String.valueOf(exception));
        profileImageProgress.setVisibility(View.GONE);
    }

    /*To complete upload file*/
    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        System.out.println("on completed");
        String str = new String(serverResponseBody);
        System.out.println(str);
        try {
            JSONObject serverResp = new JSONObject(str);
            if (serverResp.getString("error").equals("true")) {
                String errorMessage = serverResp.getString("message");
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                personimage.setImageBitmap(selectedImage);
                profileImageProgress.setVisibility(View.GONE);
                uploadId = serverResp.getJSONObject("upload").getString("_id");
                Toast.makeText(getActivity(), R.string.profileimageuploaded, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            SDUtility.displayExceptionMessage(e.getMessage(), getActivity());
        }
    }

    /*TO Cancel upload*/
    @Override
    public void onCancelled() {
        System.out.println(("On cancelled"));
        profileImageProgress.setVisibility(View.GONE);
    }
}


