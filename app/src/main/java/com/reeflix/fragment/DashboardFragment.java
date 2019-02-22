package com.reeflix.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import com.reeflix.CategoryAdapter;
import com.reeflix.Constant.ApiConstant;

import com.reeflix.Constant.Constant;
import com.reeflix.MainSliderAdapter;
import com.reeflix.PicassoImageLoadingService;
import com.reeflix.R;
import com.reeflix.RequestQueueSingleton;
import com.reeflix.models.Category;
import com.reeflix.utility.SDUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ss.com.bannerslider.Slider;

public class DashboardFragment extends Fragment {

    public LinearLayout typeLayout;
    private RequestQueue requestQueue;
    public RecyclerView recycler_view, recycler_view_reminder, recycler_view_alert;
    public RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    public ProgressBar progressbar;
    private ArrayList<Category> categoryList = new ArrayList<>();
    static final String REQ_TAG = "LIBRARYACTIVITY";
    private Slider slider;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_searches:
                return false;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Slider.init(new PicassoImageLoadingService(getContext()));

        /*To init ui elements*/
        typeLayout = view.findViewById(R.id.typeLayout);
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        slider = view.findViewById(R.id.banner_slider1);
        progressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        slider.setAdapter(new MainSliderAdapter());
        requestQueue = RequestQueueSingleton.getInstance(getActivity()).getRequestQueue();
        categoryAdapter = new CategoryAdapter(categoryList, getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setNestedScrollingEnabled(false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*function call get dummy video list*/
        createDummyVideoList();
    }

    /*function call to get video list*/
    private void createDummyVideoList() {
        categoryList = new ArrayList<>();
        //To check internet connection
        if (SDUtility.isNetworkAvailable(getContext())) {
            try {
                if (SDUtility.isConnected()) {
                    progressbar.setVisibility(View.VISIBLE);
                    //To get url for video
                    String url = ApiConstant.video_dashboard_api;
                    System.out.println(url);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            (JSONObject response) -> {
                                /* progress.dismiss();*/
                                progressbar.setVisibility(View.GONE);
                                typeLayout.setVisibility(View.VISIBLE);
                                try {
                                    JSONObject serverResp = new JSONObject(response.toString());
                                    System.out.println("success result: " + serverResp.toString());
                                    System.out.println(response.getJSONArray("result"));
                                    JSONArray categoryArray = response.getJSONArray("result");
                                    for (int i = 0; i < categoryArray.length(); i++) {
                                        JSONObject categoryObject = categoryArray.getJSONObject(i);
                                        Category category = new Category(
                                                categoryObject.getString("name"),
                                                categoryObject.getJSONArray("content"),
                                                categoryObject.getString("_id")
                                        );
                                        categoryList.add(category);
                                    }
                                    categoryAdapter = new CategoryAdapter(categoryList, getContext());
                                    categoryRecyclerView.setAdapter(categoryAdapter);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    /* progress.dismiss();*/
                                    progressbar.setVisibility(View.GONE);
                                    e.printStackTrace();
                                    //To display exception message
                                    SDUtility.displayExceptionMessage(e.getMessage(), getContext());
                                }
                            }, error -> {
                        /*progress.dismiss();*/
                        progressbar.setVisibility(View.GONE);
                        System.out.println("Error getting response");
                        System.out.println(error.getMessage());
                        SDUtility.displayExceptionMessage(error.getMessage(), getContext());
                    }) {    //this is the part, that adds the header to the request
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("key", Constant.reeflix_App_key);
                            params.put("content-type", "application/json");
                            return params;
                        }
                    };
                    jsonObjectRequest.setTag(REQ_TAG);
                    requestQueue.add(jsonObjectRequest);
                } else {
                    Toast.makeText(getActivity(), "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                //To display exception message
                SDUtility.displayExceptionMessage(e.getMessage(), getContext());
                System.out.println("hhjjkj" + e.getMessage());
            }
        } else {
            Toast.makeText(getActivity(), "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
        }
    }
}


