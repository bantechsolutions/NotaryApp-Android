package com.notaryapp.ui.activities.youtubevideo;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;

public class YoutubeVideoPlayActivity extends YouTubeBaseActivity {

    YouTubePlayerView ytPlayer;
    private String YouTubeURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video_play);

        ytPlayer = (YouTubePlayerView)findViewById(R.id.ytPlayer);

        Bundle extras = getIntent().getExtras();
        YouTubeURL = extras.getString("YOUTUBE_URL");

        ytPlayer.initialize(BuildConfig.google_youtube_api_key,
                new YouTubePlayer.OnInitializedListener() {
                    // Implement two methods by clicking on red
                    // error bulb inside onInitializationSuccess
                    // method add the video link or the playlist
                    // link that you want to play In here we
                    // also handle the play and pause
                    // functionality
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b)
                    {
                        //youTubePlayer.loadVideo("qAHMCZBwYo4");
                        youTubePlayer.loadVideo(YouTubeURL);
                        //youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                        //youTubePlayer.setFullscreen(true);
                        youTubePlayer.play();

                        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                            @Override
                            public void onLoading() {
                            }

                            @Override
                            public void onLoaded(String s) {

                            }

                            @Override
                            public void onAdStarted() {

                            }

                            @Override
                            public void onVideoStarted() {

                            }

                            @Override
                            public void onVideoEnded() {
                                youTubePlayer.seekToMillis(1000);
                                youTubePlayer.pause();
                                finish();


                            }

                            @Override
                            public void onError(YouTubePlayer.ErrorReason errorReason) {

                            }
                        });
                    }

                    // Inside onInitializationFailure
                    // implement the failure functionality
                    // Here we will show toast
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult
                                                                youTubeInitializationResult)
                    {
                        Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
                    }

                });

    }

}