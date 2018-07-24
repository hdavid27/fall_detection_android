package pt.isec.cub.falldetection._logic.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorsHandler implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor light;

    private ISensorsListener listener;

    private static SensorsHandler instance;

    public static SensorsHandler getInstance(){
        if(instance == null){
            instance = new SensorsHandler();
        }

        return instance;
    }

    private SensorsHandler(){

    }

    public void startListening(Context context, ISensorsListener listener){

        this.listener = listener;

        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        this.gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.sensorManager.registerListener(this, this.gyroscope, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

//        this.light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        this.sensorManager.registerListener(this, this.light, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopListening(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        listener.onSensorChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
