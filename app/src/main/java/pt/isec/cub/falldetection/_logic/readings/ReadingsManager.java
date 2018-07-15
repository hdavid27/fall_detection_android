package pt.isec.cub.falldetection._logic.readings;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import pt.isec.cub.falldetection._logic.location.ILocationListener;
import pt.isec.cub.falldetection._logic.location.LocationHandler;
import pt.isec.cub.falldetection._logic.sensors.ISensorsListener;
import pt.isec.cub.falldetection._logic.sensors.SensorsHandler;
import pt.isec.cub.falldetection._logic.utils.FileCreator;
import pt.isec.cub.falldetection._logic.weka.MyClassifier;
import pt.isec.cub.falldetection._logic.weka.WekaWrapper;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ReadingsManager extends AsyncTask<Void, Double, String>
                            implements ISensorsListener, ILocationListener {

    public enum EnumReadingMode {
        TRAINING,
        CLASSIFYING
    }

    private static String TAG = "Readings Manager";
    private static long TIME_DIFF = 3000;

    private static int accelerometer_resolution = 13;
    private static int accelerometer_range = 16;

    private static int gyroscope_resolution = 16;
    private static int gyroscope_range = 2000;

    private boolean isReading;
    private Context context;

    private ArrayList<Double> accelerometer_values, accelerometer_temp;
    private ArrayList<Double> gyroscope_values, gyroscope_temp;

    private Location lastLocation;

    private IClassificationListener iClassificationListener;

    private long lastClassifiedTime;

    private String activity;

    private EnumReadingMode enumReadingMode;

    public ReadingsManager(Context context, String activity, IClassificationListener iClassificationListener, EnumReadingMode enumReadingMode) {

        this.context = context;
        this.isReading = false;

        this.accelerometer_values = null;
        this.gyroscope_values = null;
        this.lastLocation = null;

        this.accelerometer_values = new ArrayList<>();
        this.gyroscope_values = new ArrayList<>();

        this.accelerometer_temp = new ArrayList<>();
        this.gyroscope_temp = new ArrayList<>();

        if(activity == null){
            this.activity = "others";
        }else {
            this.activity = activity;
        }

        this.iClassificationListener = iClassificationListener;

        this.enumReadingMode = enumReadingMode;
    }

    public boolean isReading(){
        return isReading;
    }

    public void stopReading(){
        LocationHandler.getInstance().stopListening(context);
        SensorsHandler.getInstance().stopListening();
        isReading = false;
        context = null;
        iClassificationListener.onClassify("Stopped reading!!");
    }

    private Double getMeanValue(ArrayList<Double> temArray){

        Double sum = (double) 0;

        for(Double d : temArray){
            sum += d;
        }

        return sum / temArray.size();
    }



    @Override
    protected String doInBackground(Void... voids) {

        if(isReading){
            return "Already Running";
        }

        isReading = true;

        Looper.prepare();

        this.accelerometer_values = new ArrayList<>();
        this.gyroscope_values = new ArrayList<>();

        try{
            FileCreator.deleteArffFileTemp(context);
            FileCreator.deleteArffFile(context);
        }catch (Exception e){
            e.printStackTrace();
        }


        LocationHandler.getInstance().startListening(context, ReadingsManager.this);
        SensorsHandler.getInstance().startListening(context, ReadingsManager.this);

        Log.d(TAG, "Started Reading!!!");

        lastClassifiedTime = Calendar.getInstance().getTimeInMillis();

        while (isReading){

//            if(Calendar.getInstance().getTimeInMillis() - lastClassifiedTime >= TIME_DIFF ){
//                lastClassifiedTime = Calendar.getInstance().getTimeInMillis();
//
//                classifyData();
//            }

        }

        Log.d(TAG, "Stopped Reading!!!");

        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        final String str = "lat: " + location.getLatitude() + "\nlng: " + location.getLongitude() + "\nalt: " + location.getAltitude();

        Log.d(TAG, str);

        this.lastLocation = location;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            final String str;

            double x = (double) event.values[0];
            double y = (double) event.values[1];
            double z = (double) event.values[2];

//            x = x * ( (2*accelerometer_range) / (Math.pow(accelerometer_resolution,2)) );
//            y = y * ( (2*accelerometer_range) / (Math.pow(accelerometer_resolution,2)) );
//            z = z * ( (2*accelerometer_range) / (Math.pow(accelerometer_resolution,2)) );

            this.accelerometer_values.add(Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2)));

//            if(this.accelerometer_temp.size() >= 10){
//                this.accelerometer_values.add(getMeanValue(this.accelerometer_temp));
//                this.accelerometer_temp = new ArrayList<>();
//
//                Log.d(TAG, "Acelerometro novo valor");
//            }

//            if(event.values.length >= 3){
//                str = "X:" + event.values[0] + "\n" + "Y:" + event.values[1] + "\n" + "Z:" + event.values[2];
//            }else {
//                str = "";
//            }
//            Log.d(TAG, "Acelerometro: " + str);

        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            final String str;

            double x = (double) event.values[0];
            double y = (double) event.values[1];
            double z = (double) event.values[2];

//            x = x * ( (2*accelerometer_range) / (Math.pow(accelerometer_resolution,2)) );
//            y = y * ( (2*accelerometer_range) / (Math.pow(accelerometer_resolution,2)) );
//            z = z * ( (2*accelerometer_range) / (Math.pow(accelerometer_resolution,2)) );

            this.gyroscope_values.add(Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2)));

//            if(this.gyroscope_temp.size() >= 10){
//                this.gyroscope_values.add(getMeanValue(this.gyroscope_temp));
//                this.gyroscope_temp = new ArrayList<>();
//
//                Log.d(TAG, "Giroscopio novo valor");
//            }

//            if(event.values.length >= 3){
//                str = "X:" + event.values[0] + "\n" + "Y:" + event.values[1] + "\n" + "Z:" + event.values[2];
//            }else {
//                str = "";
//            }
//            Log.d(TAG, "Giroscopio: " + str);
        }


        if(this.accelerometer_values.size() >= 64 && this.gyroscope_values.size() >= 64){

            if(this.enumReadingMode == EnumReadingMode.CLASSIFYING) {

                FileCreator.addDataToClassificationFile(
                        context,
                        this.activity,
                        new ArrayList<Double>(this.accelerometer_values),
                        new ArrayList<Double>(this.gyroscope_values)
                );
                this.accelerometer_values = new ArrayList<>();
                this.gyroscope_values = new ArrayList<>();

                Log.d(TAG, "Creating Classification File!!!!");
                iClassificationListener.onClassify("Creating Classification File!!!!");

                MyClassifier.getInstance().myClassifier(context, iClassificationListener);
            }

            if(this.enumReadingMode == EnumReadingMode.TRAINING) {


                FileCreator.addDataToTrainingFile(
                        context,
                        this.activity,
                        new ArrayList<Double>(this.accelerometer_values),
                        new ArrayList<Double>(this.gyroscope_values)
                );
                this.accelerometer_values = new ArrayList<>();
                this.gyroscope_values = new ArrayList<>();

                MyClassifier.getInstance().loadTrainingData(context);

                Log.d(TAG, "Training File!!!!");
                iClassificationListener.onClassify("Training File!!!!");

            }

        }

    }
}
