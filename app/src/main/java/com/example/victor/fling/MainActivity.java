package com.example.victor.fling;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
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
    MenuOption levelSelect = new MenuOption(Color.rgb(33,150,243),R.drawable.ic_view_module_white_48dp,"Select level",null);
    MenuOption help = new MenuOption(Color.rgb(156,39,176),R.drawable.ic_help_outline_white_48dp,"How to play",null);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else
        startActivity(intent);
    }
}
