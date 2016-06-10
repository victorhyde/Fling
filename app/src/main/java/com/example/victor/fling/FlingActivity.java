package com.example.victor.fling;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class FlingActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "CL_FireworksActivity";
    public FlingSurfaceView flingSurfaceView;
    boolean firstRun=true;
Intent intent;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Call some material design APIs here
                getWindow().setEnterTransition(new Fade());
                getWindow().setExitTransition(new Fade());
            } else {
                // Implement this feature without material design
            }
            setContentView(R.layout.activity_fling);
            findViewById(R.id.pause).setOnClickListener(this);
            findViewById(R.id.undo).setOnClickListener(this);
            findViewById(R.id.hint).setOnClickListener(this);

            flingSurfaceView = (FlingSurfaceView) (findViewById(R.id.surfaceView1));
        } catch (Exception e) {
            Log.d(TAG, "Failed to create; " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void onPause(){
        super.onPause();
        flingSurfaceView.stop();
    }

    public void banana(int time, int highScore){
// 1. Instantiate an AlertDialog.Builder with its constructor
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String message;
        if (time == highScore) message = String.format("Your time: %d:%02d\nNew high score!", time / 60, time % 60);
        else message = String.format("Your time: %d:%02d\nHigh score: %d:%02d", time / 60, time % 60,highScore / 60, highScore% 60);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.next_level, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = getIntent();
                int numBalls = intent.getIntExtra(DifficultyActivity.DIFFICULTY,8);
                flingSurfaceView.loadGame(numBalls);
            }
        });
        builder.setNegativeButton(R.string.main, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(intent);
            }
        });


        builder.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //go to main menu
                    startActivity(intent);
                }
                return true;
            }
        });


// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(R.string.solved_title);

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (firstRun) {
            firstRun=false;
            Intent intent = getIntent();
            int numBalls = intent.getIntExtra(DifficultyActivity.DIFFICULTY,8);
            flingSurfaceView.loadGame(numBalls);
        }
        if (focus){
        flingSurfaceView.resume();
        }
        else if (!focus) flingSurfaceView.stop();
        // get the imageviews width and height here
    }
    // This snippet hides the system bars.
//    private void hideSystemUI() {
//        // Set the IMMERSIVE flag.
//        // Set the content to appear under the system bars so that the content
//        // doesn't resize when the system bars hide and show.
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
//    }
//
//    // This snippet shows the system bars. It does this by removing all the flags
//// except for the ones that make the content appear under the system bars.
//    private void showSystemUI() {
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pause:
                flingSurfaceView.pause();
                break;
            case R.id.undo:
                flingSurfaceView.undo();
                break;
            case R.id.hint:
                flingSurfaceView.showHint();
                break;
        }
    }
}