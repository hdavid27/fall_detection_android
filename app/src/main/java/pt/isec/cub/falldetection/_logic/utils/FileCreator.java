package pt.isec.cub.falldetection._logic.utils;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import pt.isec.cub.falldetection._logic.FFT.FFT;

public class FileCreator {

    private static int DATA_SIZE = 64;

    public static String trainingArffFilename = "fall_detection_training.arff";

    public static String arffFilename = "fall_detection_classification.arff";
    public static String arffFilenameTemp = "fall_detection_classification_temp.arff";

    private static String getStoragePath(Context context){
        try {
            return context.getExternalFilesDir(null).getAbsolutePath();
        }catch (NullPointerException e){
            return context.getFilesDir().getAbsolutePath();
        }
    }

    public static String createArffTempFile(Context context){
        File file = new File(getArffFilename(context));

        if(file.exists()){
            File renamedFile = new File(getArffFilenameTemp(context));
            file.renameTo(renamedFile);
            return renamedFile.getAbsolutePath();
        }

        return null;
    }

    public static String getTrainingArffFilename(Context context){
        return getStoragePath(context) + File.separator + trainingArffFilename;
    }

    public static String getArffFilename(Context context){
        return getStoragePath(context) + File.separator + arffFilename;
    }

    public static String getArffFilenameTemp(Context context){
        return getStoragePath(context) + File.separator + arffFilenameTemp;
    }

    public static boolean deleteArffFile(Context context){
        File file = new File(getArffFilename(context));

        if(file.exists()){
            return file.delete();
        }

        return false;
    }

    public static boolean deleteArffFileTemp(Context context){
        File file = new File(getArffFilenameTemp(context));

        if(file.exists()){
            return file.delete();
        }

        return false;
    }

    public static void addDataToTrainingFile(Context context, String activityToRecord, ArrayList<Double> acelerometer_readings, ArrayList<Double> gyroscope_readings){
        addDataToArffFile(getTrainingArffFilename(context), activityToRecord, acelerometer_readings, gyroscope_readings);
    }

    public static void addDataToClassificationFile(Context context, String activityToRecord, ArrayList<Double> acelerometer_readings, ArrayList<Double> gyroscope_readings){
        addDataToArffFile(getArffFilename(context), activityToRecord, acelerometer_readings, gyroscope_readings);
    }

    private static void addDataToArffFile(String filename, String activityToRecord, ArrayList<Double> acelerometer_readings, ArrayList<Double> gyroscope_readings){

        FFT fft;

        double [] acelerometer = new double[acelerometer_readings.size()];
        double [] acelerometer_img = new double[acelerometer_readings.size()];
        for (int i = 0; i < acelerometer_readings.size(); i++){
            acelerometer[i] = acelerometer_readings.get(i);
            acelerometer_img[i] = (double) 0;
        }


        fft = new FFT(DATA_SIZE);
        fft.fft(acelerometer, acelerometer_img);

        double [] gyroscope = new double[gyroscope_readings.size()];
        double [] gyroscope_img = new double[gyroscope_readings.size()];
        for (int i = 0; i < gyroscope_readings.size(); i++){
            gyroscope[i] = gyroscope_readings.get(i);
            gyroscope_img[i] = (double) 0;
        }

        fft = new FFT(DATA_SIZE);
        fft.fft(gyroscope, gyroscope_img);

        double max = 0;

        try {

            File readingsFile = new File(filename);
            FileWriter writer;

            if(!readingsFile.exists()){
                writer = new FileWriter(readingsFile);

                writer.write("@RELATION falldetection\n");
                writer.write("\n");

                for (int i = 1; i <= DATA_SIZE; i++){
                    writer.write("@ATTRIBUTE accelerometer" + i + " real\n");
                }

                writer.write("@ATTRIBUTE accelerometermax real\n");

                for (int i = 1; i <= DATA_SIZE; i++){
                    writer.write("@ATTRIBUTE gyroscope" + i + " real\n");
                }

                writer.write("@ATTRIBUTE gyroscopemax real\n");

                writer.write("@ATTRIBUTE activity {walking, running, sitting, standing, jumping, falling, others}\n");
                writer.write("\n");
                writer.write("@DATA\n");

            }else{
                writer = new FileWriter(readingsFile, true);
            }

            max = 0;
            for (int i = 0; i < DATA_SIZE; i++){
                writer.write(acelerometer[i] + ",");
                if(acelerometer[i] > max){
                    max = acelerometer[i];
                }
            }

            writer.write(max + ",");

            max = 0;
            for (int i = 0; i < DATA_SIZE; i++){
                writer.write(gyroscope[i] + ",");
                if(gyroscope[i] > max){
                    max = gyroscope[i];
                }
            }

            writer.write(max + ",");

            writer.write(activityToRecord + "\n");

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
