package com.notaryapp.ejournal.vejdrawview;

import android.graphics.Path;

public class Stroke {

    //color of the stroke
    int color;
    //width of the stroke
    int strokeWidth;
    //a Path object to represent the path drawn
    Path path;

    //constructor to initialise the attributes
    public Stroke(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}