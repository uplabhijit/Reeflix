package com.underscoretec.reeflix;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.underscoretec.reeflix.Constant.ApiConstant;
import com.underscoretec.reeflix.fragment.CartFragment;
import com.underscoretec.reeflix.fragment.DashboardFragment;
import com.underscoretec.reeflix.fragment.GiftsFragment;
import com.underscoretec.reeflix.fragment.ProfileFragment;
import com.underscoretec.reeflix.helper.BottomNavigationBehavior;
import com.underscoretec.reeflix.utility.SDUtility;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    public SearchView searchView;
    private ArrayList<String> stringArrayList;
    private ListViewAdapter adapter;
    private ProgressDialog progress;
    private RequestQueue requestQueue;
    public MenuItem myActionMenuItem;
    static final String REQ_TAG = "SEARCHACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadFragment(new DashboardFragment());
        listView = (ListView) findViewById(R.id.list_items);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        // load the store fragment by default

        /* adapter = new ListViewAdapter(this, R.layout.item_listview, stringArrayList);*/
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(HomeActivity.this, (String) parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                listView.setVisibility(View.GONE);
                searchView.setQuery((String) parent.getItemAtPosition(position), true);
            }
        });
        toolbar.setLogo(R.drawable.dashboardreeflix);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void setData() {
        stringArrayList = new ArrayList<>();
       /* stringArrayList.add("Quynh Trang");
        stringArrayList.add("Hoang Bien");
        stringArrayList.add("Quoc Cuong");
        stringArrayList.add("Tran Ha");
        stringArrayList.add("Vu Danh");
        stringArrayList.add("Minh Meo");*/
        //To check internet connection
        if (SDUtility.isNetworkAvailable(HomeActivity.this)) {
            try {
                if (SDUtility.isConnected()) {
                    progress = new ProgressDialog(HomeActivity.this);
                    progress.setMessage("loading");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    //To get url for video
                    String url = ApiConstant.search_api + searchView.getQuery().toString();
                    System.out.println(url);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            (JSONObject response) -> {
                                progress.dismiss();
                                System.out.println("response>>>>>>>>" + response);

                               /* try {
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
                                    progress.dismiss();
                                    e.printStackTrace();
                                    //To display exception message
                                    SDUtility.displayExceptionMessage(e.getMessage(), getContext());
                                }*/
                            }, error -> {
                        progress.dismiss();
                        System.out.println("Error getting response");
                        System.out.println(error.getMessage());
                        SDUtility.displayExceptionMessage(error.getMessage(), HomeActivity.this);
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
                    Toast.makeText(HomeActivity.this, "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                //To display exception message
                SDUtility.displayExceptionMessage(e.getMessage(), HomeActivity.this);
                System.out.println("hhjjkj" + e.getMessage());
            }
        } else {
            Toast.makeText(HomeActivity.this, "Error: " + "in network connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        myActionMenuItem = menu.findItem(R.id.action_searches);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("gverbhjrhbfv" + newText);
                if (TextUtils.isEmpty(newText)) {
                    /* adapter.filter("");*/
                    listView.clearTextFilter();
                } else {
                    listView.setVisibility(View.VISIBLE);
                    /*adapter.filter(newText);*/
                    setData();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            /*doMySearch(query);*/
        }
    }

    Fragment dashboardFragment;
    Fragment giftsfragment;
    Fragment cartfragment;
    ProfileFragment profilefragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shop:
                    dashboardFragment = new DashboardFragment();
                    loadFragment(dashboardFragment);
                    return true;
                case R.id.navigation_gifts:
                    giftsfragment = new GiftsFragment();
                    loadFragment(giftsfragment);
                    return true;
                case R.id.navigation_cart:
                    cartfragment = new CartFragment();
                    loadFragment(cartfragment);
                    return true;
                case R.id.navigation_profile:
                    profilefragment = new ProfileFragment();
                    loadFragment(profilefragment);
                    return true;
            }
            return false;
        }
    };

    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("kn activity result>>>>>>>>>");
        System.out.println("result code>>>>" + requestCode);
        System.out.println("result code>>>>" + resultCode);
        System.out.println("---------------------profilefragment--------------------------" + profilefragment);
        System.out.println("---------------------dashboardFragment--------------------------" + dashboardFragment);
        System.out.println("---------------------giftsfragment--------------------------" + giftsfragment);
        System.out.println("---------------------cartfragment.--------------------------" + cartfragment);
        profilefragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
        System.out.println("on back pressed");
    }
}
