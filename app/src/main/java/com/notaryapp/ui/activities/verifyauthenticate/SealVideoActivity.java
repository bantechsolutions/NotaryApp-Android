package com.notaryapp.ui.activities.verifyauthenticate;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;

public class SealVideoActivity extends BaseActivity {

    //private static final String VIDEO_SAMPLE = AppUrl.VIDEO_STREAM;

    private VideoView mVideoView;
    private TextView mBufferingTextView;

    // Current playback position (in milliseconds).
    private int mCurrentPosition = 0;

    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";
    private String from;
    private String uriPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seal_video);

        /*time out*/
        listenerBinding = Foreground.get(getApplication()).addListener(this);
        counttimerInactivity = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                Intent myIntent = new Intent(getApplicationContext(), notaryappSplashActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finishAffinity();
                finish();

            }
        }.start();
        setTimer();
        /*time out*/

        from = getIntent().getStringExtra("from");

        mVideoView = findViewById(R.id.videoview);
        mBufferingTextView = findViewById(R.id.buffering_textview);

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }

        // Set up the media controller widget and attach it to the video view.
        MediaController controller = new MediaController(this);
        controller.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(controller);

        if (from != null) {
            if (from.equalsIgnoreCase("lad")) {
                uriPath = "android.resource://" + getPackageName() + "/raw/lad";
            } else {
                uriPath = "android.resource://" + getPackageName() + "/raw/stamp_hint";
            }

        } else {
            uriPath = "android.resource://" + getPackageName() + "/raw/notaryapp_stamp_help_video";
        }

        initializePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releasePlayer();
    }

    private void initializePlayer() {
        // Show the "Buffering..." message while the video loads.
        mBufferingTextView.setVisibility(VideoView.INVISIBLE);

        // Buffer and decode the video sample.


//        Uri videoUri = getMedia(VIDEO_SAMPLE);
        Uri videoUri = Uri.parse(uriPath);
        mVideoView.setVideoURI(videoUri);
//        mVideoView.requestFocus();
//        mVideoView.start();
        // Listener for onPrepared() event (runs after the media is prepared).
        mVideoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        // Hide buffering message.
                        mBufferingTextView.setVisibility(VideoView.INVISIBLE);
                        // Restore saved position, if available.
                        if (mCurrentPosition > 0) {
                            mVideoView.seekTo(mCurrentPosition);
                        } else {
                            // Skipping to 1 shows the first frame of the video.
                            mVideoView.seekTo(1);
                        }

                        // Start playing!
                        mVideoView.start();
                    }
                });

        // Listener for onCompletion() event (runs after media has finished
        // playing).
        mVideoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        // Return the video position to the start.
                        mVideoView.seekTo(0);
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                        finish();

                    }
                });

    }


    // Release all media-related resources. In a more complicated app this
    // might involve unregistering listeners or releasing audio focus.
    private void releasePlayer() {
        mVideoView.stopPlayback();
    }

    // Get a Uri for the media sample regardless of whether that sample is
    // embedded in the app resources or available on the internet.
    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            // Media name is an external URL.
            return Uri.parse(mediaName);
        } else {
            // Media name is a raw resource embedded in the app.
            return Uri.parse("android.resource://" + getPackageName() +
                    "/raw/" + mediaName);
        }
    }
}
