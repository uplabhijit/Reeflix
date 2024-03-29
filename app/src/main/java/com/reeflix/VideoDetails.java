package com.reeflix;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.offline.TrackKey;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadHelper;
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistParserFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import com.google.android.exoplayer2.util.Util;

import com.reeflix.models.Video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoDetails extends AppCompatActivity implements AdsMediaSource.MediaSourceFactory, DownloadTracker.Listener {

    private SimpleExoPlayer player;
    private boolean useExtensionRenderers;
    public PlayerView simpleExoPlayerView;
    public Video video;
    private boolean playWhenReady = true;
    private ProgressBar progressBar;
    private PowerManager.WakeLock wl;
    private ImageButton bt;
    private int currentWindow;
    private long playbackPosition;
    private SeekBar volumeSeekBar;
    private int currentOrientation;
    private AudioAttributes audioAttributes;
    private ArrayList<String> stringArrayList;
    private Handler mainHandler;
    private ImaAdsLoader adsLoader;
    private DataSource.Factory dataSourceFactory;
    private ConcatenatingMediaSource concatenatingMediaSource;
    private AdsMediaSource mediaSourceWithAds;
    public DownloadTracker downloadTracker;
    private MediaSource hlsMediaSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //No title bar is set for the activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        dataSourceFactory = buildDataSourceFactory();
        setContentView(R.layout.activity_video_details);

        DemoApplication application = (DemoApplication) getApplication();
        useExtensionRenderers = application.useExtensionRenderers();
        downloadTracker = application.getDownloadTracker();

        String adTag = VideoDetails.this.getString(R.string.ad_tag_url);
        adsLoader = new ImaAdsLoader(VideoDetails.this, Uri.parse(adTag));
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        getSupportActionBar().hide();
        bt = (ImageButton) findViewById(R.id.exo_close);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*To close video deatils page*/
                VideoDetails.this.finish();
            }
        });
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(player.getAudioStreamType(), i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /*Temporary function to data in concatenating video list*/
        setdata();
    }

    /*function call to add hls url in video list to play*/
    private void setdata() {
        stringArrayList = new ArrayList<>();
        stringArrayList.add("https://d2ve01upp8znde.cloudfront.net/file-1542955317188/hls/main.m3u8");
        /*stringArrayList.add("https://d2ve01upp8znde.cloudfront.net/file-1542955657508/hls/main.m3u8");*/
        /*stringArrayList.add("https://d2ve01upp8znde.cloudfront.net/file-1542955317188/hls/main.m3u8");
        stringArrayList.add("https://d2ve01upp8znde.cloudfront.net/file-1542955657508/hls/main.m3u8");*/
        /* stringArrayList.add("https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");*/
        /*stringArrayList.add("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8");*/
       /* stringArrayList.add("http://184.72.239.149/vod/smil:BigBuckBunny.smil/playlist.m3u8");
        stringArrayList.add("http://184.72.239.149/vod/smil:BigBuckBunny.smil/playlist.m3u8");
        stringArrayList.add("http://184.72.239.149/vod/smil:BigBuckBunny.smil/playlist.m3u8");*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        currentOrientation = VideoDetails.this.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUiFullScreen();
        } else {
            hideSystemUi();
        }
    }

    private void hideSystemUiFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        downloadTracker.addListener(VideoDetails.this);
        video = (Video) getIntent().getSerializableExtra("video_obj");
        System.out.println("video obj>>>>>>" + video.getSources());

        /*function call to initialize player*/
        initializePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        wl.acquire();
    }

    /*function call to initialize player*/
    private void initializePlayer() {
        // Create a default TrackSelector
        mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        //Initialize the player
        //Initialize simpleExoPlayerView
        simpleExoPlayerView = findViewById(R.id.exoplayer);
        // This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse(video.getSources());
        // This is the MediaSource representing the media to be played.
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        /*To track download status*/
        downloadTracker.toggleDownload(this, video.getTitle(), videoUri, null);

      /*  HlsDownloadHelper downloadHelper =
                new HlsDownloadHelper(videoUri, dataSourceFactory);
        downloadHelper.prepare(
                new HlsDownloadHelper.Callback() {

                    private boolean shouldDownload(Format track) {
                        System.out.println("track>>>>>>>>>>>>>>>>>>>" + track);
                        if (track != null) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onPrepared(DownloadHelper helper) {
                        // Preparation completes. Now other DownloadHelper methods can be called.
                        List<TrackKey> trackKeys = new ArrayList<>();
                        for (int i = 0; i < downloadHelper.getPeriodCount(); i++) {
                            TrackGroupArray trackGroups = downloadHelper.getTrackGroups(i);
                            for (int j = 0; j < trackGroups.length; j++) {
                                TrackGroup trackGroup = trackGroups.get(j);
                                for (int k = 0; k < trackGroup.length; k++) {
                                    Format track = trackGroup.getFormat(k);
                                    if (shouldDownload(track)) {
                                        trackKeys.add(new TrackKey(i, j, k));
                                    }
                                }
                            }
                        }
                        DownloadAction downloadAction = downloadHelper.getDownloadAction(null, trackKeys);
                        System.out.println("download Action>>>>>>>>>>>>" + downloadAction);
                        DownloadService
                                .startWithAction(getApplicationContext(), MyDownloadService.class, downloadAction, true);
                    }

                    @Override
                    public void onPrepareError(DownloadHelper helper, IOException e) {
                        System.out.println("helper>>>>>>>>" + helper);
                    }
                });*/
        // Produces Extractor instances for parsing the media data.
       /* concatenatingMediaSource = new ConcatenatingMediaSource();
        MediaSource[] mediaSources = new HlsMediaSource[stringArrayList.size()];
        for (int i = 0; i < stringArrayList.size(); i++) {
            mediaSources[i] = buildMediaSource(Uri.parse(stringArrayList.get(i)));
            concatenatingMediaSource.addMediaSource(mediaSources[i]);
        }*/

        /*HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(
                new DefaultDashChunkSource.Factory(dataSourceFactory),
                buildDataSourceFactory(false))
                .setManifestParser(
                        new FilteringManifestParser<>(new DashManifestParser(), downloadAction.getKeys()))
                .createMediaSource(uri);*/
        simpleExoPlayerView.setPlayer(player);
        mediaSourceWithAds = buildMediaSource(videoUri);
        /*MediaSource hlsMediaSource = new HlsMediaSource(videoUri, dataSourceFactory, mainHandler, new AdaptiveMediaSourceEventListener() {

            @Override
            public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            }

            @Override
            public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            }

            @Override
            public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            }

            @Override
            public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            }

            @Override
            public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            }

            @Override
            public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            }

            @Override
            public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            }

            @Override
            public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
            }

            @Override
            public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
            }
        });
*/
        // Compose the content media source into a new AdsMediaSource with both ads and content.
        // Prepare the player with the source.
        player.prepare(mediaSourceWithAds);
        player.setPlayWhenReady(playWhenReady);
        player.setAudioAttributes(audioAttributes, true);
        player.addListener(new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        //You can use progress dialog to show user that video is preparing or buffering so please wait
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_IDLE:
                        //idle state
                        break;
                    case Player.STATE_READY:
                        // dismiss your dialog here because our video is ready to play now
                        progressBar.setVisibility(View.GONE);
                        break;
                    case Player.STATE_ENDED:
                        // do your processing after ending of video
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            @Override
            public void onSeekProcessed() {
            }
        });
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSourceWithAds, true, false);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        simpleExoPlayerView.setControllerShowTimeoutMs(10000);
    }

    /**
     * Returns a new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory() {
        return ((DemoApplication) getApplication()).buildDataSourceFactory();
    }

    /*function call to build hls media source*/
    private AdsMediaSource buildMediaSource(Uri uri) {
        /*DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, "playlist"));*/
        hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setPlaylistParserFactory(
                new DefaultHlsPlaylistParserFactory(getOfflineStreamKeys(uri)))
                .createMediaSource(uri);
        ;
        return new AdsMediaSource(hlsMediaSource,
                dataSourceFactory,
                adsLoader,
                simpleExoPlayerView.getOverlayFrameLayout());
    }

    private List<StreamKey> getOfflineStreamKeys(Uri uri) {
        return ((DemoApplication) getApplication()).getDownloadTracker().getOfflineStreamKeys(uri);
    }

    /*function call to release exo player*/
    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUi();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        wl.release();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onStop() {
        downloadTracker.removeListener(this);
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        adsLoader.release();
        super.onDestroy();
    }

    @Override
    public AdsMediaSource createMediaSource(Uri uri) {
        return buildMediaSource(uri);
    }

    @Override
    public int[] getSupportedTypes() {
        return new int[]{C.TYPE_HLS};
    }

    @Override
    public void onDownloadsChanged() {
    }
}


/*my dump code*/



