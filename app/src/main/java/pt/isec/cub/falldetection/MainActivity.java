package pt.isec.cub.falldetection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pt.isec.cub.falldetection._logic.permissions.PermissionsHandler;

public class MainActivity extends AppCompatActivity {


    // ---------------- Sensores ------------------

    //GPS

    //Acelerometro

    //Giroscopio

    //Proximidade

    //Bussola?
    //Magnetometro?


    // ---------------- Cenarios de treino ------------------
    //Sentar
    //Deitar
    //Queda
    //Queda telemóvel (Possível queda, emitir timer de alerta?)

    @BindView(R.id.btn_play)
    ImageView btnPlay;

    @BindView(R.id.btn_stop)
    ImageView btnStop;

    @BindView(R.id.btn_upload)
    ImageView btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toggleBtnPlay(true);
        toggleBtnStop(false);
        toggleBtnUpload(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PermissionsHandler.askPermissions(this);
    }

    @OnClick(R.id.btn_play)
    public void onBtnPlayClick(View v){
        Toast.makeText(getApplicationContext(), "On Play click!!!", Toast.LENGTH_LONG).show();
    }

    public void toggleBtnPlay(boolean enable){
        if(enable){
            btnPlay.setEnabled(true);
            btnPlay.setImageResource(R.drawable.ic_icon_play_black);
        }else {
            btnPlay.setEnabled(false);
            btnPlay.setImageResource(R.drawable.ic_icon_play_grey);
        }
    }

    @OnClick(R.id.btn_stop)
    public void onBtnStopClick(View v){
        Toast.makeText(getApplicationContext(), "On Stop click!!!", Toast.LENGTH_LONG).show();
    }

    public void toggleBtnStop(boolean enable){
        if(enable){
            btnStop.setEnabled(true);
            btnStop.setImageResource(R.drawable.ic_icon_stop_black);
        }else {
            btnStop.setEnabled(false);
            btnStop.setImageResource(R.drawable.ic_icon_stop_grey);
        }
    }

    @OnClick(R.id.btn_upload)
    public void onBtnUploadClick(View v){
        Toast.makeText(getApplicationContext(), "On Upload click!!!", Toast.LENGTH_LONG).show();
    }

    public void toggleBtnUpload(boolean enable){
        if(enable){
            btnUpload.setEnabled(true);
            btnUpload.setImageResource(R.drawable.ic_icon_backup_black);
        }else {
            btnUpload.setEnabled(false);
            btnUpload.setImageResource(R.drawable.ic_icon_backup_grey);
        }
    }
}
