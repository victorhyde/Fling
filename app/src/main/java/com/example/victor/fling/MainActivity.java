package com.example.victor.fling;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
MenuAdapter adapter;
    MenuOption play = new MenuOption(Color.rgb(0,150,136),R.drawable.ic_play_arrow_white_48dp,"Play", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            play();
        }
    });
    MenuOption levelSelect = new MenuOption(Color.rgb(33,150,243),R.drawable.ic_star_white_48dp,"High scores",new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            highScores();
        }
    });
    MenuOption help = new MenuOption(Color.rgb(156,39,176),R.drawable.ic_help_outline_white_48dp,"How to play",new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            instructions();
        }
    });
    MenuOption[]options = {play,levelSelect,help};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView title = (TextView)findViewById(R.id.name);
        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/Moon Light.otf");
        title.setTypeface(font);

        adapter = new MenuAdapter(this, R.layout.menu_listview, options);

        ListView listView = (ListView) findViewById(R.id.menu);
        listView.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade());
        } else {
            // Implement this feature without material design
        }
    }

    public void play(){
        Intent intent = new Intent(this, DifficultyActivity.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//        else
        startActivity(intent);
    }
    public void instructions(){
        Intent intent = new Intent(this, InstructionsActivity.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//        else
            startActivity(intent);
    }

    public void highScores(){
        SharedPreferences sharedPref = this.getSharedPreferences("high scores", Context.MODE_PRIVATE);
        String message;
        int [] scores = new int [3];
        String [] scoreStrings = new String [3];
            scores[0] = sharedPref.getInt("easy high score", -1);
            scores[1] = sharedPref.getInt("medium high score", -1);
            scores[2] = sharedPref.getInt("hard high score", -1);
        for (int i=0;i<3;i++){
            if (scores[i]==-1)scoreStrings[i]="not played yet!";
            else scoreStrings[i]=String.format("%d:%02d", scores[i] / 60, scores[i] % 60);;
        }
        message=String.format("Easy: %s\nMedium: %s\nHard: %s\n", scoreStrings[0],scoreStrings[1],scoreStrings[2]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(R.string.high_scores);

// 3. Get the AlertDialog from create()
        dialog = builder.create();
        dialog.show();
    }
}
