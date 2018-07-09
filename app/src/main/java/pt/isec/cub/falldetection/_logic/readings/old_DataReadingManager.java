package pt.isec.cub.falldetection._logic.readings;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cub.cubapp.R;

import org.jdeferred.android.DeferredAsyncTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import library.location.ILocationListener;
import library.location.LocationManagerHandler;
import library.sensors.SensorsHandler;
import library.sensors.SensorsListener;

/**
 * Created by Dav__ on 01/05/2017.
 */

public class old_DataReadingManager extends DeferredAsyncTask<Void, Double, String>
                                implements SensorsListener, ILocationListener {

    private boolean isReading;
    private Activity activity;

    private String filename;

    private TextView txt_gyroscope, txt_accelerometer, txt_gps, txt_sensors, txt_light;

    private String activityToRecord;

    private float [] accelerometer_values;
    private float [] gyroscope_values;
    private float light_value;
    private Location lastLocation;

    private long lastFileWrite;

    private FileWriter writer;

    public old_DataReadingManager(Activity activity, String activityToRecord){
        this.activity = activity;
        this.activityToRecord = activityToRecord;
        this.isReading = false;
        this.filename = "HugoDavidCUB" + activityToRecord + Calendar.getInstance().getTimeInMillis() + ".csv";

        this.accelerometer_values = null;
        this.gyroscope_values = null;
        this.light_value = 0;
        this.lastLocation = null;
        this.lastFileWrite = 0;

        this.txt_gps = (TextView) activity.findViewById(R.id.txt_gps);
        this.txt_gyroscope = (TextView) activity.findViewById(R.id.txt_gyroscope);
        this.txt_accelerometer = (TextView) activity.findViewById(R.id.txt_accelerometer);
        this.txt_light = (TextView) activity.findViewById(R.id.txt_light);
        this.txt_sensors = (TextView) activity.findViewById(R.id.txt_sensors);
    }

    public boolean isReading(){
        return isReading;
    }

    public void stopReading(){
        isReading = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            final String str;
            this.accelerometer_values = event.values;

            if(event.values.length >= 3){
                str = "X:" + event.values[0] + "\n" + "Y:" + event.values[1] + "\n" + "Z:" + event.values[2];
            }else {
                str = "";
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_accelerometer.setText(str);
                }
            });
        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){

            final String str;
            this.gyroscope_values = event.values;

            if(event.values.length >= 3){
                str = "X:" + event.values[0] + "\n" + "Y:" + event.values[1] + "\n" + "Z:" + event.values[2];
            }else {
                str = "";
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_gyroscope.setText(str);
                }
            });
        }

        if(event.sensor.getType() == Sensor.TYPE_LIGHT){

            final String str;
            this.light_value = event.values[0];

            if(event.values.length >= 1){
                str = "" + event.values[0];
            }else {
                str = "";
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_light.setText(str);
                }
            });
        }

        writeToCSVFile();

    }

    private void openFileToWrite(){

        File readingsFile = new File(activity.getExternalFilesDir(null).getAbsolutePath() + File.separator + filename);

        try {
            if(readingsFile.exists()){
                writer = new FileWriter(readingsFile, true);
            }else{
                writer = new FileWriter(readingsFile);

                writer.write("timestamp,lat,lng,alt,x_acc,y_acc,z_acc,x_gyro,y_gyro,z_gyro,light,activity\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void writeToCSVFile(){

        if(Calendar.getInstance().getTimeInMillis() >= (lastFileWrite + 15)) {

            if (writer != null && lastLocation != null && accelerometer_values != null && gyroscope_values != null) {
                String str = Calendar.getInstance().getTimeInMillis() + "," +
                        lastLocation.getLatitude() + "," +
                        lastLocation.getLongitude() + "," +
                        lastLocation.getAltitude() + "," +
                        accelerometer_values[0] + "," +
                        accelerometer_values[1] + "," +
                        accelerometer_values[2] + "," +
                        gyroscope_values[0] + "," +
                        gyroscope_values[1] + "," +
                        gyroscope_values[2] + "," +
                        light_value + "," +
                        activityToRecord + "\n";

                try {
                    writer.append(str);
                    lastFileWrite = Calendar.getInstance().getTimeInMillis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void closeFileWriter(){
        if(writer != null){
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        final String str = "lat: " + location.getLatitude() + "\nlng: " + location.getLongitude() + "\nalt: " + location.getAltitude();

        this.lastLocation = location;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_gps.setText(str);
            }
        });
    }

    @Override
    protected String doInBackgroundSafe(Void... params) throws Exception {

        Looper.prepare();

        isReading = true;

        openFileToWrite();

        LocationManagerHandler.getInstance().startListening(activity.getApplicationContext(), old_DataReadingManager.this);
        SensorsHandler.getInstance().startListening(activity.getApplicationContext(), old_DataReadingManager.this);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), "Started reading!", Toast.LENGTH_LONG).show();
            }
        });

        while(isReading){

        }

        LocationManagerHandler.getInstance().stopListening(activity.getApplicationContext());
        SensorsHandler.getInstance().stopListening();

        closeFileWriter();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), "Stopped reading!", Toast.LENGTH_LONG).show();
            }
        });

        return null;
    }
}
