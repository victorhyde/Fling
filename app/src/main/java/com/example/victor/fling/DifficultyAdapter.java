package com.example.victor.fling;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by victor on 2016-05-23.
 */
public class DifficultyAdapter extends ArrayAdapter<MenuOption> {
    Context context;
    int layoutResourceId;
    MenuOption[] options;

    public DifficultyAdapter(Context context, int layoutResourceId, MenuOption[] options) {
        super(context, layoutResourceId, options);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.options = options;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.difficulty_listview, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.difficulty_text);
        Button button = (Button) convertView.findViewById(R.id.difficulty_button);
        tvName.setText(getItem(position).getDescription());
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/Moon Light.otf");
        tvName.setTypeface(font);
        button.setText(getItem(position).getText());
        button.setBackgroundColor(getItem(position).getColor());
        button.setOnClickListener(getItem(position).getOnClick());
        return convertView;
    }
}
