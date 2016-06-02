package com.example.victor.fling;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class DifficultyActivity extends AppCompatActivity {
    public static final String DIFFICULTY = "fling.DIFFICULTY";
    DifficultyAdapter adapter;
    MenuOption beginner = new MenuOption(Color.rgb(33,150,243), "4", "beginner (4 balls)", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            play(4);
        }
    });
    MenuOption easy = new MenuOption(Color.rgb(46,175,80), "6", "easy (6 balls)", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            play(6);
        }
    });
    MenuOption medium = new MenuOption(Color.rgb(255,193,7), "8", "medium (8 balls)", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            play(8);
        }
    });
    MenuOption hard = new MenuOption(Color.rgb(244,67,54), "10", "hard (10 balls)", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            play(10);
        }
    });
    MenuOption[] options = {beginner, easy, medium, hard};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fling);if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
            getWindow().setExitTransition(new Slide(Gravity.LEFT));
        } else {
            // Implement this feature without material design
        }
        setContentView(R.layout.activity_main);
        TextView title = (TextView) findViewById(R.id.name);
        title.setText("Select difficulty:");
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Moon Light.otf");
        title.setTypeface(font);

        adapter = new DifficultyAdapter(this, R.layout.difficulty_listview, options);

        ListView listView = (ListView) findViewById(R.id.menu);
        listView.setAdapter(adapter);
    }

    public void play(int numBalls) {
        Intent intent = new Intent(this, FlingActivity.class);
        intent.putExtra(DIFFICULTY,numBalls);
        startActivity(intent);
        finish();
    }
}