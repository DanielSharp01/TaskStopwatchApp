package com.danielsharp01.taskstopwatch.model;

import android.content.res.Resources;

import androidx.annotation.ColorInt;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.R;

public class Tag {
    private String name;
    private String color;
    public static String[] allColors = new String[] { "red", "green", "blue", "black", "white", "yellow", "brown", "ocean", "orange", "pink", "purple" };

    public Tag(String name) {
        this.name = name;
        this.color = allColors[DI.getRandom().nextInt(allColors.length)];
    }

    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public @ColorInt int getColorResource(Resources resources) {
        switch (color)
        {
            case "red":
                return resources.getColor(R.color.niceRed);
            case "green":
                return resources.getColor(R.color.niceGreen);
            case "blue":
                return resources.getColor(R.color.niceBlue);
            case "black":
                return resources.getColor(R.color.niceBlack);
            case "gray":
                return resources.getColor(R.color.niceGray);
            case "white":
                return resources.getColor(R.color.niceWhite);
            case "yellow":
                return resources.getColor(R.color.niceYellow);
            case "brown":
                return resources.getColor(R.color.niceBrown);
            case "ocean":
                return resources.getColor(R.color.niceOcean);
            case "orange":
                return resources.getColor(R.color.niceOrange);
            case "pink":
                return resources.getColor(R.color.nicePink);
            case "purple":
                return resources.getColor(R.color.nicePurple);
        }

        return 0;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
