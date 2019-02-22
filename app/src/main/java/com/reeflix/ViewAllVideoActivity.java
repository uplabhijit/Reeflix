package com.reeflix;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.reeflix.Constant.ApiConstant;
import com.reeflix.models.Video;
import com.reeflix.utility.SDUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import customfonts.MyTextView_Roboto_Medium;

public class ViewAllVideoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Video> movieList;
    private StoreAdapter mAdapter;
    private ProgressBar progressBar;
    private ShimmerFrameLayout mShimmerViewContainer;
    public String categoryId;
    static final String REQ_TAG = "VIEWALLVIDEOACTIVITY";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_video);
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();
        mAdapter = new StoreAdapter(ViewAllVideoActivity.this, movieList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ViewAllVideoActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new ViewAllVideoActivity.GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        categoryId = getIntent().getStringExtra("category_id");
        System.out.println(categoryId);

        /*function call to show view all video list*/
        fetchStoreItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon action bar is clicked; go to parent activity
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*function call to show view all video list*/
    private void fetchStoreItems() {
        if (SDUtility.isNetworkAvailable(ViewAllVideoActivity.this)) {
            try {
                if (SDUtility.isConnected()) {
                    progressBar.setVisibility(View.VISIBLE);
                    String URL = ApiConstant.video_view_all_api + categoryId;
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressBar.setVisibility(View.GONE);
                                    JSONArray jsonArray = null;
                                    if (response == null) {
                                        Toast.makeText(ViewAllVideoActivity.this, "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    try {
                                        jsonArray = response.getJSONArray("result").getJSONObject(0).getJSONArray("content");
                                        String thumbnail1 = null;
                                        String thumbnail2 = null;
                                        String sources = null;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject contentObject = jsonArray.getJSONObject(i);
                                            if (contentObject.has("thumbnail1")) {
                                                thumbnail1 = contentObject.getString("thumbnail1");
                                            }
                                            if (contentObject.has("thumbnail2")) {
                                                thumbnail2 = contentObject.getString("thumbnail1");
                                            }
                                            if (contentObject.has("sources")) {
                                                sources = contentObject.getJSONObject("sources").getString("hls");
                                            }
                                            Video video = new Video(
                                                    contentObject.getString("title"),
                                                    contentObject.getString("description"),
                                                    thumbnail1,
                                                    thumbnail2,
                                                    contentObject.getString("maturity"),
                                                    sources,
                                                    contentObject.getString("_id")
                                            );
                                            movieList.add(video);
                                        }
                                        mAdapter = new StoreAdapter(ViewAllVideoActivity.this, movieList);
                                        recyclerView.setAdapter(mAdapter);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressBar.setVisibility(View.GONE);
                                        SDUtility.displayExceptionMessage(e.getMessage(), ViewAllVideoActivity.this);
                                    }
                                    // stop animating Shimmer and hide the layout
                                    mShimmerViewContainer.stopShimmerAnimation();
                                    mShimmerViewContainer.setVisibility(View.GONE);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error in getting json
                            Log.e(String.valueOf(ViewAllVideoActivity.this), "Error: " + error.getMessage());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ViewAllVideoActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {    //this is the part, that adds the header to the request
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("key", "c815a866efbe9cebfb842062cc85e46f");
                            params.put("content-type", "application/json");
                            return params;
                        }
                    };
                    /*  MyApplication.getInstance().addToRequestQueue(request);*/
                    request.setTag(REQ_TAG);
                    requestQueue.add(request);
                } else {
                    Toast.makeText(ViewAllVideoActivity.this, "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                //To display exception message
                SDUtility.displayExceptionMessage(e.getMessage(), ViewAllVideoActivity.this);
                System.out.println("hhjjkj" + e.getMessage());
            }
        } else {
            Toast.makeText(ViewAllVideoActivity.this, "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            System.out.println("span count>>>>>" + spanCount);
            int column = position % spanCount; // item column
            System.out.println("position>>>>>>>>>>>>>" + position);
            System.out.println("column>>>>>>>>>>>>" + column);
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
        private Context context;
        private List<Video> movieList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public MyTextView_Roboto_Medium name;
            public ImageView thumbnail;
            public CardView card_view;
            public LinearLayout mainlayout;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.title);
                /*price = view.findViewById(R.id.price);*/
                thumbnail = view.findViewById(R.id.thumbnail);
                card_view = view.findViewById(R.id.card_view);
                mainlayout = view.findViewById(R.id.mainlayout);
            }
        }

        public StoreAdapter(Context context, List<Video> movieList) {
            this.context = context;
            this.movieList = movieList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Video video = movieList.get(position);
            holder.name.setText(video.getTitle());
            /*holder.price.setText(video.getMaturity());*/
            if (video.getThumbnail1() != null) {
                Glide.with(context)
                        .load(video.getThumbnail1())
                        .into(holder.thumbnail);
            }
            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent fp = new Intent(ViewAllVideoActivity.this, VideoDescription.class);
                    fp.putExtra("video_obj", video);
                    context.startActivity(fp);
                }
            });
        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmerAnimation();
    }
}
