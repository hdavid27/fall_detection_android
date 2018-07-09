package pt.isec.cub.falldetection._logic.sensors;

import android.hardware.SensorEvent;

/**
 * Created by Dav__ on 22/03/2017.
 */

public interface ISensorsListener {
    void onSensorChanged(SensorEvent event);
}
