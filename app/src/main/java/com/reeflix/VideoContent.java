package com.reeflix;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import com.reeflix.utility.SDUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.reeflix.MainActivity.REQ_TAG;

public class VideoContent extends AppCompatActivity {

    public EditText videotitleText, descriptionText, addDirectorText, producerText, castText, maturityText, genreText;
    public Button btn_savecontent;
    public ProgressDialog progress;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_content);
        videotitleText = findViewById(R.id.videotitleText);
        descriptionText = findViewById(R.id.descriptionText);
        addDirectorText = findViewById(R.id.addDirectorText);
        producerText = findViewById(R.id.producerText);
        castText = findViewById(R.id.castText);
        maturityText = findViewById(R.id.maturityText);
        genreText = findViewById(R.id.genreText);
        btn_savecontent = findViewById(R.id.btn_savecontent);
        requestQueue = RequestQueueSingleton.getInstance(VideoContent.this).getRequestQueue();
        btn_savecontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("rads>>>>>>>>>");
            }
        });
        getDashboard();
    }

    private void getDashboard() {
        //To check internet connection
        if (SDUtility.isNetworkAvailable(VideoContent.this)) {
            try {
                if (SDUtility.isConnected()) {
                    progress = new ProgressDialog(VideoContent.this);
                    progress.setMessage("loading");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    //To get url for create VIDEO content
                    String url = "https://api.viseee.com/categories/getAll";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            (JSONObject response) -> {
                                progress.dismiss();
                                try {
                                    JSONObject serverResponse = new JSONObject(response.toString());
                                    System.out.println("success result: " + serverResponse);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    progress.dismiss();
                                    e.printStackTrace();
                                    //To display exception message
                                    SDUtility.displayExceptionMessage(e.getMessage(), VideoContent.this);
                                }
                            }, error -> {
                        progress.dismiss();
                        System.out.println("Error getting response");
                        SDUtility.displayExceptionMessage(error.getMessage(), VideoContent.this);
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
                //To display exception message
                SDUtility.displayExceptionMessage(e.getMessage(), VideoContent.this);
            }
        } else {
            Toast.makeText(VideoContent.this, "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
        }
    }
}

