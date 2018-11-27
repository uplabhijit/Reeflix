package com.underscoretec.reeflix;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.underscoretec.reeflix.models.Video;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyViewHolder> {

    private List<Video> videoList;
    private Context context;
    public ByteArrayOutputStream bos;
    public byte [] bitmapdata;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title, description;
        public CardView eventDetails;
        public ImageView event_imageview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            event_imageview =  itemView.findViewById(R.id.event_imageview);
            eventDetails =  itemView.findViewById(R.id.eventDetails);
        }
    }
    @NonNull
    @Override
    public VideoListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_video_list_adapter, parent, false);
        return new VideoListAdapter.MyViewHolder(itemView);
    }

    public VideoListAdapter(List<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListAdapter.MyViewHolder holder, int position) {

        final Video video = videoList.get(position);
        holder.title.setText(video.getTitle());
        holder.description.setText(video.getDescription());
        Glide.with(context)
                .asBitmap()
                .load(video.getThumbnail1())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.event_imageview.setImageBitmap(resource);
                    }
                });

        holder.eventDetails.setOnClickListener(view -> {
            //function call to open event
            openVideoDetails(videoList.get(position),holder);
        });

    }

    private void openVideoDetails(Video video, MyViewHolder holder) {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.event_imageview.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
        bitmapdata=bos.toByteArray();

        Intent fp = new Intent(context, VideoDescription.class);
        fp.putExtra("video_obj",video);
        fp.putExtra("videoImage",bitmapdata);
        context.startActivity(fp);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
}
