package com.roastedlikeever.ankidemov2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Demo2Activity extends AppCompatActivity {

    private RequestQueue queue;
    private String car4g;
    private String car5g;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);

        Intent intent = getIntent();
        car5g = intent.getStringExtra(Setup2Activity.DEMO_2_5G_CAR);
        car4g = intent.getStringExtra(Setup2Activity.DEMO_2_4G_CAR);

        queue = Volley.newRequestQueue(this);
    }


    public void startDemo(View view) {
        queue.add(AnkiRequests.setLights(car4g,14,9,0));
        queue.add(AnkiRequests.setLights(car5g, 0, 15,0));
        queue.add(AnkiRequests.setSpeed(car5g, 1000));
        queue.add(AnkiRequests.setSpeed(car4g, 1000));
        switchToStopScreen();
    }

    public void switchToStopScreen(View view) {
        switchToStopScreen();
    }

    private void switchToStopScreen() {
        setContentView(R.layout.activity_demo2_stopscreen);
    }


    public void stopDemo(View view) {
        queue.add(AnkiRequests.setSpeed(car5g, 0));
        setContentView(R.layout.activity_demo2);
        Utils.millSleep(400);       // Sleeps the UI thread - very dangerous!
        queue.add(AnkiRequests.setSpeed(car4g, 0));
    }

}
