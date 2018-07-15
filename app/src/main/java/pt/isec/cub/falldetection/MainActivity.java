package pt.isec.cub.falldetection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pt.isec.cub.falldetection._logic.permissions.PermissionsHandler;
import pt.isec.cub.falldetection._logic.readings.IClassificationListener;
import pt.isec.cub.falldetection._logic.readings.ReadingsManager;
import pt.isec.cub.falldetection._logic.weka.MyClassifier;

public class MainActivity extends AppCompatActivity implements IClassificationListener {


    // ---------------- Sensores ------------------

    //GPS

    //Acelerometro

    //Giroscopio

    //Proximidade

    //Bussola?
    //Magnetometro?


    private ReadingsManager readingsManager;

    @BindView(R.id.btn_play)
    ImageView btnPlay;

    @BindView(R.id.btn_stop)
    ImageView btnStop;

    @BindView(R.id.btn_train)
    Button btnTrain;

    @BindView(R.id.txt_activity)
    TextView txtActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toggleBtnPlay(true);
        toggleBtnStop(false);

        //TODO: download arff file
        //toggleBtnTrain(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        PermissionsHandler.askPermissions(this);
        MyClassifier.getInstance().loadTrainingData(getApplicationContext());
    }

    @Override
    protected void onPause() {

        stopReading();

        super.onPause();
    }

    private void startReading(){
        if(readingsManager == null || !readingsManager.isReading()) {
            readingsManager = new ReadingsManager(getApplicationContext(), null, MainActivity.this, ReadingsManager.EnumReadingMode.CLASSIFYING);
            readingsManager.execute();
            toggleBtnPlay(false);
            toggleBtnStop(true);
        }
    }

    private void stopReading(){
        if(readingsManager != null && readingsManager.isReading()){
            readingsManager.stopReading();
            readingsManager = null;
            toggleBtnPlay(true);
            toggleBtnStop(false);
        }
    }

    @OnClick(R.id.btn_play)
    public void onBtnPlayClick(View v){
//        Toast.makeText(getApplicationContext(), "On Play click!!!", Toast.LENGTH_LONG).show();
        startReading();
    }

    public void toggleBtnPlay(boolean enable){
        if(enable){
//            btnPlay.setEnabled(true);
            btnPlay.setImageResource(R.drawable.ic_icon_play_black);
        }else {
//            btnPlay.setEnabled(false);
            btnPlay.setImageResource(R.drawable.ic_icon_play_grey);
        }
    }

    @OnClick(R.id.btn_stop)
    public void onBtnStopClick(View v){
//        Toast.makeText(getApplicationContext(), "On Stop click!!!", Toast.LENGTH_LONG).show();
        stopReading();
    }

    public void toggleBtnStop(boolean enable){
        if(enable){
//            btnStop.setEnabled(true);
            btnStop.setImageResource(R.drawable.ic_icon_stop_black);
        }else {
//            btnStop.setEnabled(false);
            btnStop.setImageResource(R.drawable.ic_icon_stop_grey);
        }
    }

    @OnClick(R.id.btn_train)
    public void onBtnTrainClick(View v){
//        Toast.makeText(getApplicationContext(), "On Train click!!!", Toast.LENGTH_LONG).show();
        Intent it = new Intent(getApplicationContext(), TrainingActivity.class);
        startActivity(it);
    }

    public void toggleBtnTrain(boolean enable){
        if(enable){
            btnTrain.setEnabled(true);
        }else {
            btnTrain.setEnabled(false);
        }
    }


    @Override
    public void onClassify(final String classification) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtActivity.setText(classification);
            }
        });
    }
}
