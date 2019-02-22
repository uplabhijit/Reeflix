package com.reeflix;

import android.app.ProgressDialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import com.reeflix.Constant.ApiConstant;
import com.reeflix.models.Video;
import com.reeflix.utility.SDUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public RecyclerView recycler_view, recycler_view_reminder, recycler_view_alert;
    private ArrayList<Video> videoList = new ArrayList<>();
    private VideoListAdapter VideoListAdapter;
    public ProgressDialog progress;
    static final String REQ_TAG = "LIBRARYACTIVITY";
    public LinearLayout typeLayout;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        typeLayout = findViewById(R.id.typeLayout);
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        //Calender recycler view
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view_reminder = findViewById(R.id.recycler_view_reminder);
        recycler_view_alert = findViewById(R.id.recycler_view_alert);
        /* VideoListAdapter = new VideoListAdapter(videoList, MainActivity.this);*/
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(VideoListAdapter);
        GridLayoutManager reminderManager = new GridLayoutManager(this, 1);
        reminderManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_view_reminder.setLayoutManager(reminderManager);
        recycler_view_reminder.setItemAnimator(new DefaultItemAnimator());
        recycler_view_reminder.setAdapter(VideoListAdapter);
        GridLayoutManager alertManager = new GridLayoutManager(this, 1);
        alertManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_view_alert.setLayoutManager(alertManager);
        recycler_view_alert.setItemAnimator(new DefaultItemAnimator());
        recycler_view_alert.setAdapter(VideoListAdapter);

        /*createDummyVideoList();*/
    }

    private void createDummyVideoList() {
        //To check internet connection
        if (SDUtility.isNetworkAvailable(this)) {
            try {
                if (SDUtility.isConnected()) {
                    progress = new ProgressDialog(this);
                    progress.setMessage("loading");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    //To get url for create event
                    String url = ApiConstant.video_dashboard_api;
                    JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                            response -> {
                                progress.dismiss();
                                typeLayout.setVisibility(View.VISIBLE);
                                try {
                                    JSONArray jsonArray = new JSONArray(response.toString());
                                    System.out.println("success result: " + jsonArray.length());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject videoJsonObject = jsonArray.getJSONObject(i);
                                        Video video = new Video(
                                                videoJsonObject.getString("title"),
                                                videoJsonObject.getString("description"),
                                                videoJsonObject.getString("thumbnail1"),
                                                videoJsonObject.getString("thumbnail2"),
                                                videoJsonObject.getString("maturity"),
                                                videoJsonObject.getJSONObject("sources").getString("hls"),
                                                videoJsonObject.getString("id")
                                        );
                                        videoList.add(video);
                                    }
                                    VideoListAdapter = new VideoListAdapter(videoList, MainActivity.this, "");
                                    recycler_view.setAdapter(VideoListAdapter);
                                    recycler_view_reminder.setAdapter(VideoListAdapter);
                                    recycler_view_alert.setAdapter(VideoListAdapter);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    progress.dismiss();
                                    e.printStackTrace();
                                    //To display exception message
                                    SDUtility.displayExceptionMessage(e.getMessage(), MainActivity.this);
                                }
                            }, error -> {
                        progress.dismiss();
                        System.out.println("Error getting response");
                        SDUtility.displayExceptionMessage(error.getMessage(), MainActivity.this);
                    });
                    jsonObjectRequest.setTag(REQ_TAG);
                    requestQueue.add(jsonObjectRequest);
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                //To display exception message
                SDUtility.displayExceptionMessage(e.getMessage(), MainActivity.this);
            }
        }
    }
}
