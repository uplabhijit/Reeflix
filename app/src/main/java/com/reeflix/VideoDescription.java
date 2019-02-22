package com.reeflix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.reeflix.Constant.ApiConstant;
import com.reeflix.models.Video;
import com.reeflix.utility.SDUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.RequestQueue;

import customfonts.MyTextView_Roboto_Bold;

public class VideoDescription extends AppCompatActivity {

    public Video video;
    public ImageView eventImage;
    public MyTextView_Roboto_Bold eventName;
    public TextView eventDescription;
    public ImageView subscribe, image_movie_poster, arrow;
    private Bitmap bmp;
    private ProgressDialog progress;
    private ProgressBar progressBar;
    public RecyclerView content_recycler_view;
    private VideoListAdapter VideoListAdapter;
    private ArrayList<Video> dummyList = new ArrayList<>();
    private RequestQueue requestQueue;
    static final String REQ_TAG = "LIBRARYACTIVITY";
    public ReadMoreTextView eventDescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_description);

        /*function call to init ui elements*/
        initUielements();
        video = (Video) getIntent().getSerializableExtra("video_obj");
        eventName.setText(video.getTitle());
        eventDescription.setText(video.getDescription());
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        GridLayoutManager layoutManager = new GridLayoutManager(VideoDescription.this, 1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        content_recycler_view.setLayoutManager(layoutManager);
        content_recycler_view.setItemAnimator(new DefaultItemAnimator());
        content_recycler_view.setAdapter(VideoListAdapter);
        if (getIntent().getByteArrayExtra("videoImage") != null) {
            byte[] bytes = getIntent().getByteArrayExtra("videoImage");
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            System.out.println(bmp);
            image_movie_poster.setImageBitmap(bmp);
        } else {
            if (video.getThumbnail2() != null) {
                Glide.with(VideoDescription.this)
                        .load(video.getThumbnail2())
                        .into(image_movie_poster);
            }
        }
        if (video.getThumbnail1() != null) {
            Glide.with(VideoDescription.this)
                    .load(video.getThumbnail1())
                    .into(eventImage);
        }
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fp = new Intent(VideoDescription.this, VideoDetails.class);
                fp.putExtra("video_obj", video);
                startActivity(fp);
            }
        });
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoDescription.this.finish();
            }
        });
    }

    private void initUielements() {
        eventName = findViewById(R.id.eventName);
        eventDescription = findViewById(R.id.eventDescriptions);
        eventImage = findViewById(R.id.eventImage);
        image_movie_poster = findViewById(R.id.image_movie_poster);
        subscribe = findViewById(R.id.subscribe);
        arrow = findViewById(R.id.arrow);
        content_recycler_view = findViewById(R.id.content_recycler_view);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createDummylist();
    }

    private void createDummylist() {
        dummyList = new ArrayList<>();
        //To check internet connection
        if (SDUtility.isNetworkAvailable(VideoDescription.this)) {
            try {
                if (SDUtility.isConnected()) {
                    /*progress = new ProgressDialog(VideoDescription.this);
                    progress.setMessage("loading");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();*/
                    progressBar.setVisibility(View.VISIBLE);
                    //To get url for video
                    String url = ApiConstant.video_dashboard_api;
                    System.out.println(url);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            (JSONObject response) -> {
                                /*  progress.dismiss();*/
                                progressBar.setVisibility(View.GONE);
                                try {
                                    JSONObject serverResp = new JSONObject(response.toString());
                                    System.out.println("success result: " + serverResp.toString());
                                    System.out.println(response.getJSONArray("result"));
                                    JSONArray categoryArray = response.getJSONArray("result");
                                    for (int i = 0; i < categoryArray.length(); i++) {
                                        JSONObject categoryObject = categoryArray.getJSONObject(i);
                                        JSONArray contentArray = categoryObject.getJSONArray("content");
                                        for (int j = 0; j < contentArray.length(); j++) {
                                            JSONObject contentobject = contentArray.getJSONObject(j);
                                            System.out.println("content object" + contentobject);
                                            String thumbnail1 = null;
                                            String thumbnail2 = null;
                                            String sources = null;
                                            if (contentobject.has("thumbnail1")) {
                                                thumbnail1 = contentobject.getString("thumbnail1");
                                            }
                                            if (contentobject.has("thumbnail2")) {
                                                thumbnail2 = contentobject.getString("thumbnail1");
                                            }
                                            if (contentobject.has("sources")) {
                                                sources = contentobject.getJSONObject("sources").getString("hls");
                                            }
                                            Video video = new Video(
                                                    contentobject.getString("title"),
                                                    contentobject.getString("description"),
                                                    thumbnail1,
                                                    thumbnail2,
                                                    contentobject.getString("maturity"),
                                                    sources,
                                                    contentobject.getString("_id")
                                            );
                                            dummyList.add(video);
                                        }
                                        VideoListAdapter = new VideoListAdapter(dummyList, VideoDescription.this, "videoDescription");
                                        content_recycler_view.setAdapter(VideoListAdapter);
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    progressBar.setVisibility(View.GONE);
                                    e.printStackTrace();
                                    //To display exception message
                                    SDUtility.displayExceptionMessage(e.getMessage(), VideoDescription.this);
                                }
                            }, error -> {
                        progressBar.setVisibility(View.GONE);
                        /*mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);*/
                        System.out.println("Error getting response");
                        System.out.println(error.getMessage());
                        SDUtility.displayExceptionMessage(error.getMessage(), VideoDescription.this);
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
                    Toast.makeText(VideoDescription.this, "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                //To display exception message
                SDUtility.displayExceptionMessage(e.getMessage(), VideoDescription.this);
                System.out.println("hhjjkj" + e.getMessage());
            }
        } else {
            Toast.makeText(VideoDescription.this, "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
        }
    }
}
