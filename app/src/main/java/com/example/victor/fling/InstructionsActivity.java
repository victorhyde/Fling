package com.example.victor.fling;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        TextView title = (TextView)findViewById(R.id.how_to_play);
        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/Moon Light.otf");
        title.setTypeface(font);
    }
}
