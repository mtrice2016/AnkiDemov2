package com.roastedlikeever.ankidemov2;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Demo1Activity extends AppCompatActivity {

    private RequestQueue queue;
    private String innerCar;
    private String middleCar;
    private String outerCar;

    Button startButton;
    Button stopButton;

    RunDemoTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        Intent intent = getIntent();
        innerCar = intent.getStringExtra(Setup1Activity.DEMO_1_INNER_CAR);
        middleCar = intent.getStringExtra(Setup1Activity.DEMO_1_MIDDLE_CAR);
        outerCar = intent.getStringExtra(Setup1Activity.DEMO_1_OUTER_CAR);

        queue = Volley.newRequestQueue(this);

        Utils.disableButton(stopButton);
    }


    public void startDemo(View view) {
        cleanTask();

        task = new RunDemoTask();
        getLifecycle().addObserver(task);
        task.execute();
    }

    private void cleanTask() {
        if(task != null) {
            task.cancel(true);
            this.getLifecycle().removeObserver(task);
        }
    }


    public void endDemo(View view) {    endDemo();  }

    private void endDemo() {
        queue.add(AnkiRequests.setSpeed(0));

        Utils.disableButton(stopButton);
        Utils.enableButton(startButton);
    }

    public void forceStop(View view) {
        if(task != null && task.getStatus() != AsyncTask.Status.FINISHED){
            cleanTask();
        }

        queue.add(AnkiRequests.setSpeed(0));
        Utils.disableButton(stopButton);
        Utils.enableButton(startButton);
    }



    protected class RunDemoTask extends AsyncTask<Void, Void, Void> implements LifecycleObserver {
        private boolean autoStop;

        protected RunDemoTask() {
            autoStop = false;
        }

        @Override
        protected void onPreExecute() {
            if(!this.isCancelled()) {
                Utils.disableButton(startButton);
                Utils.disableButton(stopButton);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(this.isCancelled()) return null;
            queue.add(AnkiRequests.setLights(innerCar,15,0,0));
            queue.add(AnkiRequests.setLights(middleCar, 14,9,0));
            queue.add(AnkiRequests.setLights(outerCar, 0,15,0));

            queue.add(AnkiRequests.setSpeed(outerCar, Utils.scale(500, GlobalData.OUTER_MULTIPLIER)));
            queue.add(AnkiRequests.setSpeed(middleCar, Utils.scale(500, GlobalData.MIDDLE_MULTIPLIER)));
            queue.add(AnkiRequests.setSpeed(innerCar, Utils.scale(500, GlobalData.INNER_MULTIPLIER)));

            Utils.sleep(8);
            if(this.isCancelled()) return null;

            queue.add(AnkiRequests.setSpeed(outerCar, Utils.scale(800, GlobalData.OUTER_MULTIPLIER)));
            queue.add(AnkiRequests.setSpeed(middleCar, Utils.scale(800, GlobalData.MIDDLE_MULTIPLIER)));

            Utils.sleep(8);
            if(this.isCancelled()) return null;

            queue.add(AnkiRequests.setSpeed(outerCar, 1250));

            Utils.sleep(15);
            if(this.isCancelled()) return null;
            if(autoStop) {
                endDemo();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if(!this.isCancelled()) {
                Utils.enableButton(stopButton);
            }
        }


        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void stopEverything() {
            if(isFinishing()) {
                this.cancel(true);
                queue.add(AnkiRequests.setSpeed(0));
                Utils.millSleep((short)50);
                queue.add(AnkiRequests.setSpeed(0));
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void queueStop() {
            if(!isChangingConfigurations()) {
                if(this.getStatus() == Status.FINISHED) {
                    Demo1Activity.this.endDemo();
                } else {
                    autoStop = true;
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void cancelStop() {
            autoStop = false;
        }
    }

}
