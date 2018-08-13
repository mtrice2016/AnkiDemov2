package com.roastedlikeever.ankidemov2;

import android.widget.Button;

public class Utils {

    public static void enableButton(Button b) {
        b.setAlpha(1f);
        b.setClickable(true);
    }

    public static void disableButton(Button b) {
        b.setAlpha(.5f);
        b.setClickable(false);
    }

    public static boolean sleep(int sec) {
        return millSleep(sec * 1000);
    }

    public static boolean millSleep(int mill) {
        try {
            Thread.sleep(mill);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static int clamp(int x, int l, int u) {
        if(x < l)
            return l;
        if(x > u)
            return u;
        return x;
    }

    public static int scale(int num, float scale) {
        return (int)(num * scale);
    }

}
