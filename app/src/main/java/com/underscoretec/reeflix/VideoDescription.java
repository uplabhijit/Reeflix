package com.underscoretec.reeflix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.underscoretec.reeflix.models.Video;

public class VideoDescription extends AppCompatActivity {

    public Video video;
    public ImageView eventImage;
    public TextView eventName, eventDescription;
    public ImageButton subscribe;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_description);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        eventName = findViewById(R.id.eventName);
        eventDescription = findViewById(R.id.eventDescription);
        eventImage = findViewById(R.id.eventImage);
        subscribe = findViewById(R.id.subscribe);
        video = (Video) getIntent().getSerializableExtra("video_obj");
        eventName.setText(video.getTitle());
        eventDescription.setText(video.getDescription());
        byte[] bytes = getIntent().getByteArrayExtra("videoImage");
        bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        System.out.println(bmp);
        eventImage.setImageBitmap(bmp);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fp = new Intent(VideoDescription.this, VideoDetails.class);
                fp.putExtra("video_obj", video);
                startActivity(fp);
            }
        });
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
}
