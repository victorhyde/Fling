package com.example.victor.fling;

import android.view.View;

/**
 * Created by victor on 2016-05-14.
 */
public class MenuOption {
    private int color;
    private int image;
    private String text;
    private String description;
    private View.OnClickListener onClick;

    public View.OnClickListener getOnClick() {
        return onClick;
    }

    public MenuOption(int color, int image, String description, View.OnClickListener onClick){
        this.color = color;
        this.image = image;
        this.description = description;
        this.onClick = onClick;
    }
    public MenuOption(int color, String text, String description, View.OnClickListener onClick){
        this.color = color;
        this.text=text;
        this.description = description;
        this.onClick = onClick;
    }

    public int getColor() {
        return color;
    }

    public int getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }
}
