package com.reeflix;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.reeflix.models.Category;
import com.reeflix.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import customfonts.MyTextView_Roboto_Bold;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<Category> categoryList;
    private Context context;
    public RecyclerView content_recycler_view;
    private VideoListAdapter VideoListAdapter;
    private ArrayList<Video> videoList = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyTextView_Roboto_Bold heading_text_category_list;
        public Button view_more_video;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            heading_text_category_list = itemView.findViewById(R.id.heading_text_category_list);
            view_more_video = itemView.findViewById(R.id.view_more_video);
            content_recycler_view = itemView.findViewById(R.id.content_recycler_view);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            content_recycler_view.setLayoutManager(layoutManager);
            content_recycler_view.setItemAnimator(new DefaultItemAnimator());
            content_recycler_view.setAdapter(VideoListAdapter);
            content_recycler_view.setNestedScrollingEnabled(false);
        }
    }

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_dashboard_card, viewGroup, false);
        return new CategoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        videoList = new ArrayList<>();
        final Category category = categoryList.get(i);
        myViewHolder.heading_text_category_list.setText(category.getName());
        JSONArray contentArray = category.getContentArray();
        for (int j = 0; j < contentArray.length(); j++) {
            try {
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
                videoList.add(video);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            VideoListAdapter = new VideoListAdapter(videoList, context, "category");
            content_recycler_view.setAdapter(VideoListAdapter);
        }
        myViewHolder.view_more_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*function call to view all video*/
                openViewAllVideo(categoryList.get(i), myViewHolder);
            }
        });
    }

    /*function call to open view all video*/
    private void openViewAllVideo(Category category, MyViewHolder myViewHolder) {
        Intent fp = new Intent(context, ViewAllVideoActivity.class);
        fp.putExtra("category_id", category.getCategoryId());
        context.startActivity(fp);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}


