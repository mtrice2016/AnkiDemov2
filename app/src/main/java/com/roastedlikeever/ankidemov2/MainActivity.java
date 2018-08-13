package com.roastedlikeever.ankidemov2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity {

    public static final String MAIN_ACTIVITY_TO_OPEN = "com.att.attankidemo.Main_Activity_To_Open";

    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);
    }


    public void demo1(View view) {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
    }

    public void demo2(View view) {
        Intent intent = new Intent(this, Setup2Activity.class);
        intent.putExtra(MAIN_ACTIVITY_TO_OPEN, "2");
        startActivity(intent);
    }

    public void demo3(View view) {
        Intent intent = new Intent(this, Setup2Activity.class);
        intent.putExtra(MAIN_ACTIVITY_TO_OPEN, "3");
        startActivity(intent);
    }

    public void demoDriving(View view) {
        Intent intent = new Intent(this, SetupDrivingActivity.class);
        startActivity(intent);
    }

    public void connect(View view) {
        for(String car : getResources().getStringArray(R.array.car_names)) {
            queue.add(AnkiRequests.connect(car));
        }
    }


}


