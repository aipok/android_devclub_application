package com.divapps.aipok.devclub.activities;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.divapps.aipok.devclub.R;

/**
 * Created by Vitali Nartov on 17/06/15.
 * Media player activity
 */
public class Player extends FragmentActivity  implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public static final String URL_TAG = "movie_url";
    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.player);

        String url = getIntent().getExtras().getString(URL_TAG, null);
        if(TextUtils.isEmpty(url)){
            Log.d("Player", "url is empty");
            finish();
        }

        if (mediaControls == null) {
            mediaControls = new MediaController(Player.this);
        }

        //initialize the VideoView
        myVideoView = (VideoView) findViewById(R.id.video_view);

        // create a progress bar while the video file is loading
        progressDialog = new ProgressDialog(Player.this);
        // set a message for the progress bar
        progressDialog.setMessage("Loading...");
        //set the progress bar not cancelable on users' touch
        progressDialog.setCancelable(true);
        // show the progress bar
        progressDialog.show();

        try {
            //set the media controller in the VideoView
            myVideoView.setMediaController(mediaControls);

            //set the uri of the video to be played
            myVideoView.setVideoURI(Uri.parse(url));
            myVideoView.setOnPreparedListener(Player.this);
            myVideoView.setOnErrorListener(Player.this);
            myVideoView.setOnCompletionListener(Player.this);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // close the progress bar and play the video
        progressDialog.dismiss();
        //if we have a position on savedInstanceState, the video playback should start from here
        myVideoView.seekTo(position);
        if (position == 0) {
            myVideoView.start();
        } else {
            //if we come from a resumed activity, video playback will be paused
            myVideoView.pause();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        finish();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // close the progress bar and play the video
        progressDialog.dismiss();
        finish();
        return false;
    }
}
