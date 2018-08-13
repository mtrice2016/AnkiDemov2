package com.roastedlikeever.ankidemov2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class InteractiveDrivingActivity extends AppCompatActivity {

    private RequestQueue queue;

    private boolean isAuto;

    private String childCar;
    private String autoCar;

    private int childSpeed = 0;
    private int autoSpeed = 0;

    private int curLane;
    private int lightMode = 0;

    Timer timer;

    Button gasButton;
    Button brakeButton;
    ProgressBar progressBar;

    private final int SPEED_DEGRADATION = 3;
    private final int ACCELERATION = 10;
    private final int MAX_SPEED = 1300;
    private final int BASE_SPEED = 500;

    private boolean isStarted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        Intent intent = getIntent();
        childCar = intent.getStringExtra(SetupDrivingActivity.INTERACTIVE_DRIVING_CHILD_CAR);
        autoCar = intent.getStringExtra(SetupDrivingActivity.INTERACTIVE_DRIVING_AUTO_CAR);
        isAuto = !autoCar.isEmpty();

        gasButton = findViewById(R.id.gasButton);
        brakeButton = findViewById(R.id.brakeButton);
        progressBar = findViewById(R.id.speedBar);

        gasButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startDemo(v);
                    v.setPressed(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                    v.setPressed(false);
                }
                return true;
            }
        });

        isStarted = false;

        queue = Volley.newRequestQueue(this);
    }


    public void startDemo(View view) {
        if(isStarted) {
            return;
        }

        if(isAuto) {
            queue.add(AnkiRequests.setLights(autoCar, 14, 9, 0));
        }
        queue.add(AnkiRequests.setLights(childCar, 0, 15,0));

        childSpeed = 600;
        autoSpeed = 500;

        createTimer();
        timer.scheduleAtFixedRate(new DemoTickTask(), 0, 50);

        curLane = 50 * (isAuto ? -1 : 1);

        isStarted = true;
    }

    @Override
    public void onDestroy() {
        queue.add(AnkiRequests.setSpeed(childCar, 0));
        if(isAuto) {
            queue.add(AnkiRequests.setSpeed(autoCar, 0));
        }
        closeTimer();
        super.onDestroy();
    }

    public void steerLeft(View view) {
        curLane -= 40;
        curLane = Utils.clamp(curLane, -68, 68);
        queue.add(AnkiRequests.changeLane(childCar, curLane));
    }

    public void steerRight(View view) {
        curLane += 40;
        curLane = Utils.clamp(curLane, -68, 68);
        queue.add(AnkiRequests.changeLane(childCar, curLane));
    }

    public void lights(View view) {
        ++lightMode;
        lightMode %= 3;
        switch(lightMode) {
            case 0:
                queue.add(AnkiRequests.setLights(childCar, 0, 15, 0));
                queue.add(AnkiRequests.setHeadlights(childCar, false));
                queue.add(AnkiRequests.setTaillights(childCar, 0));
                break;
            case 1:
                queue.add(AnkiRequests.setLights(childCar, 15, 0, 15));
                queue.add(AnkiRequests.setHeadlights(childCar, true));
                break;
            case 2:
                queue.add(AnkiRequests.setLights(childCar, 14, 4, 5));
                queue.add(AnkiRequests.setTaillights(childCar, 2));
                break;
        }
    }



    private void createTimer() {
        closeTimer();
        timer = new Timer();
    }

    private void closeTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    class DemoTickTask extends TimerTask {
        int count = 0 ;

        public void run() {
            // Constant slowdown to 500
            if(childSpeed > BASE_SPEED) {
                childSpeed -= SPEED_DEGRADATION;
                childSpeed = Utils.clamp(childSpeed, BASE_SPEED, MAX_SPEED);
            }

            // Gas pedal
            if(gasButton.isPressed()) {
                childSpeed += ACCELERATION;
            }

            // Speedup if below 500
            if(childSpeed < BASE_SPEED) {
                childSpeed += 50;
                childSpeed = Utils.clamp(childSpeed, 0, BASE_SPEED);
            }

            // Brake pedal
            if(brakeButton.isPressed()) {
                childSpeed = 0;
            }


            // Autonomous car control: switches from 500 to 1200 and back every 5 seconds
            if(count % 100 == 0) {
                autoSpeed = 1700 - autoSpeed;
            }



            childSpeed = Utils.clamp(childSpeed, 0, MAX_SPEED);
            autoSpeed = Utils.clamp(autoSpeed, 0, MAX_SPEED);


            updateSpeedBar();

//            System.out.println("-------------------------------5-----" + speed5g);
//            System.out.println("-------------------------------4-----" + speed4g);
            if(count++ % 5 == 0) {
                queue.add(AnkiRequests.setSpeed(childCar, childSpeed));
                if(isAuto) {
                    queue.add(AnkiRequests.setSpeed(autoCar, autoSpeed));
                }
            }

        }
    }

    private void updateSpeedBar() {
        progressBar.setProgress(Utils.scale(childSpeed, 100f/MAX_SPEED));
    }


}
