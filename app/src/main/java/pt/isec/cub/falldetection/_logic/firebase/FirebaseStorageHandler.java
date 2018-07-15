package pt.isec.cub.falldetection._logic.firebase;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import pt.isec.cub.falldetection._logic.utils.FileCreator;

public class FirebaseStorageHandler {
    private static final String STORAGE_BUCKET = "gs://fall-detection-app.appspot.com";


    private FirebaseStorage storage;
    private StorageReference storageRef;

    // ---------- Instance ----------
    private static FirebaseStorageHandler instance;

    private FirebaseStorageHandler(){
        storage = FirebaseStorage.getInstance(STORAGE_BUCKET);
        storageRef = storage.getReference();
    }

    public static FirebaseStorageHandler getInstance(){
        if(instance == null){
            instance = new FirebaseStorageHandler();
        }

        return instance;
    }



    public UploadTask uploadFileTask(String filepath) throws FileNotFoundException {

        UploadTask uploadTask = null;

        File fileToUpload = new File(filepath);

        if(fileToUpload.exists()){

            Uri fileUri = Uri.fromFile(fileToUpload);

            StorageReference fileRef = storageRef.child(FileCreator.trainingArffFilename);

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .build();

            uploadTask = fileRef.putFile(fileUri, metadata);

            return uploadTask;
        }else {
            throw new FileNotFoundException();
        }

    }

    public FileDownloadTask downloadFileTask(Context context) throws IOException {

        StorageReference fileRef = storage.getReferenceFromUrl(STORAGE_BUCKET + "/" +FileCreator.trainingArffFilename);

        File trainingFile = new File(FileCreator.getTrainingArffFilename(context));

        return fileRef.getFile(trainingFile);

    }
}
