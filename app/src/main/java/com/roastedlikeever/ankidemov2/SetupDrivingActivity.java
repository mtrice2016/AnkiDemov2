package com.roastedlikeever.ankidemov2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SetupDrivingActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public static final String INTERACTIVE_DRIVING_CHILD_CAR = "com.att.attankidemo.DemoDriving_Child_Car";
    public static final String INTERACTIVE_DRIVING_AUTO_CAR = "com.att.attankidemo.DemoDriving_Auto_Car";


    RequestQueue queue;
    String[] carNames = {"", ""};
    CarStatus[] carStatuses = {CarStatus.UNSELECTED, CarStatus.UNSELECTED};
    Button activateButton;

    Spinner spinner0;
    Spinner spinner1;

    TextView color0;
    TextView color1;

    private static final Pattern batteryPattern = Pattern.compile("\\{\"battery\":(\\d+)\\}");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_driving);
        queue = Volley.newRequestQueue(this);

        activateButton = findViewById(R.id.activateButton);
        Utils.disableButton(activateButton);


        spinner0 = findViewById(R.id.spinner0);
        spinner1 = findViewById(R.id.spinner1);

        color0 = findViewById(R.id.color0);
        color1 = findViewById(R.id.color1);

        spinner0.setOnItemSelectedListener(this);
        spinner1.setOnItemSelectedListener(this);

        changeStatus(0, CarStatus.UNSELECTED);
        changeStatus(1, CarStatus.UNSELECTED);
    }

    private void selectCar(int pos, String name) {
        if(name.equals("")) {
            carNames[pos] = "";
            changeStatus(pos, CarStatus.UNSELECTED);
        } else {
            if(!carNames[pos].equals(name) || carStatuses[pos] != CarStatus.READY) {
                carNames[pos] = name;
                changeStatus(pos, CarStatus.SELECTED);
                checkCarStatus(name, pos);
            }
        }
    }


    private void changeStatus(int pos, CarStatus status) {
        if(pos < 0 || pos > 2) {
            return;
        }

        carStatuses[pos] = status;

        changeStatusColor(pos, status);

        checkAllReady();
    }

    private void changeStatusColor(int pos, CarStatus status) {
        TextView view = color0;
        switch (pos) {
            case 0:
                view = color0;
                break;
            case 1:
                view = color1;
                break;
        }

        String color = "";
        switch (status) {
            case UNSELECTED:
                color = "Gray";
                break;
            case SELECTED:
                color = "Yellow";
                break;
            case READY:
                color = "Green";
                break;
            case ERROR:
                color = "Red";
                break;
        }

        view.setText(color);
    }

    private void checkCarStatus(String name, int pos) {
        queue.add(getBattery(name, pos));
    }

    public void receiveCarStatus(String response, int pos, String name) {
        if(!carNames[pos].equals(name)){
            // Old request
            return;
        }

        if(!response.equals("error")) {
            Matcher m = batteryPattern.matcher(response);
            if (m.matches()) {
                int level = Integer.parseInt(m.group(1));
                System.out.println(level);
                if(level >= 10) {
                    changeStatus(pos, CarStatus.READY);
                    return;
                }
            }
        }

        //Something did not work correctly, set to red
        changeStatus(pos, CarStatus.ERROR);
    }

    private boolean checkAllReady() {
        if(carStatuses[0] == CarStatus.READY && (carStatuses[1] == CarStatus.READY || carStatuses[1] == CarStatus.UNSELECTED)) {
            if(!carNames[0].equals(carNames[1])) {
                Utils.enableButton(activateButton);
                return true;
            }
        }

        Utils.disableButton(activateButton);
        return false;
    }

    public StringRequest getBattery(final String name, final int pos){//}, StringFunc listener, VolleyErrorFunc error) {
        String query = "/batteryLevel/" + name;
        final SetupDrivingActivity stupidVar = this;
        return AnkiRequests.generateRequest(query, Request.Method.GET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stupidVar.receiveCarStatus(response, pos, name);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stupidVar.receiveCarStatus(error.toString(), pos, name);
                    }
                });
    }




    public void activate(View view) {
        Intent intent = new Intent(this, InteractiveDrivingActivity.class);
        intent.putExtra(INTERACTIVE_DRIVING_AUTO_CAR, carNames[1]);
        intent.putExtra(INTERACTIVE_DRIVING_CHILD_CAR, carNames[0]);
        startActivity(intent);
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String name = adapterView.getItemAtPosition(i).toString();
        name = name.equals("None selected") ? "" : name;
        selectCar(getSpinnerNum(adapterView), name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        selectCar(getSpinnerNum(adapterView), "");
    }

    public int getSpinnerNum(AdapterView spinner) {
        if(spinner == spinner0) { return 0; }
        if(spinner == spinner1) { return 1; }
        return -1;
    }


    public enum CarStatus {
        READY, ERROR, SELECTED, UNSELECTED
    }
}
