package pt.isec.cub.falldetection;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pt.isec.cub.falldetection._logic.firebase.FirebaseStorageHandler;
import pt.isec.cub.falldetection._logic.permissions.PermissionsHandler;
import pt.isec.cub.falldetection._logic.readings.IClassificationListener;
import pt.isec.cub.falldetection._logic.readings.ReadingsManager;
import pt.isec.cub.falldetection._logic.utils.FileCreator;

public class TrainingActivity extends AppCompatActivity implements IClassificationListener{

    // ---------------- Cenarios de treino ------------------
    //Andar
    //Correr
    //Saltar
    //Sentar
    //Deitar
    //Queda
    //Queda telemóvel (Possível queda, emitir timer de alerta?)
    //Queda andar / correr
    //Queda sentado

    private ReadingsManager readingsManager;

    @BindView(R.id.btn_play)
    ImageView btnPlay;

    @BindView(R.id.btn_stop)
    ImageView btnStop;

    @BindView(R.id.btn_upload)
    ImageView btnUpload;

    @BindView(R.id.radioGroupActivities)
    RadioGroup radioGroup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.bind(this);


        // TODO: check files to upload
        //toggleBtnUpload(false);
    }

    @Override
    protected void onPause() {

        stopReading();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PermissionsHandler.askPermissions(this);
    }

    private void startReading(String activity){
        if(readingsManager == null) {
            readingsManager = new ReadingsManager(getApplicationContext(), activity, TrainingActivity.this, ReadingsManager.EnumReadingMode.TRAINING);
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

        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.radioButtonWalking:
                startReading("walking");
                break;

            case R.id.radioButtonRunning:
                startReading("running");
                break;

            case R.id.radioButtonSitting:
                startReading("sitting");
                break;

            case R.id.radioButtonStanding:
                startReading("standing");
                break;

            case R.id.radioButtonJumping:
                startReading("jumping");
                break;

            case R.id.radioButtonFalling:
                startReading("falling");
                break;

            default:
                Toast.makeText(getApplicationContext(), "Select an activity!!!", Toast.LENGTH_LONG).show();
                break;
        }

    }

    public void toggleBtnPlay(boolean enable){
        if(enable){
            //btnPlay.setEnabled(true);
            btnPlay.setImageResource(R.drawable.ic_icon_play_black);
        }else {
            //btnPlay.setEnabled(false);
            btnPlay.setImageResource(R.drawable.ic_icon_play_grey);
        }
    }

    @OnClick(R.id.btn_stop)
    public void onBtnStopClick(View v){
        stopReading();
    }

    public void toggleBtnStop(boolean enable){
        if(enable){
            //btnStop.setEnabled(true);
            btnStop.setImageResource(R.drawable.ic_icon_stop_black);
        }else {
            //btnStop.setEnabled(false);
            btnStop.setImageResource(R.drawable.ic_icon_stop_grey);
        }
    }


    @OnClick(R.id.btn_upload)
    public void onBtnUploadClick(View v){
        //Toast.makeText(getApplicationContext(), "On Upload click!!!", Toast.LENGTH_LONG).show();

        try {
            FirebaseStorageHandler.getInstance().uploadFileTask(FileCreator.getTrainingArffFilename(getApplicationContext()))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Upload successful!!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Fail to upload file!!", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void toggleBtnUpload(boolean enable){
        if(enable){
            //btnUpload.setEnabled(true);
            btnUpload.setImageResource(R.drawable.ic_icon_backup_black);
        }else {
            //btnUpload.setEnabled(false);
            btnUpload.setImageResource(R.drawable.ic_icon_backup_grey);
        }
    }

    @Override
    public void onClassify(String classification) {
        Toast.makeText(getApplicationContext(), classification, Toast.LENGTH_LONG).show();
    }
}
