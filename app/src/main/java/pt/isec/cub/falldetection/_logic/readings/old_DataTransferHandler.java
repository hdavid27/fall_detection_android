package pt.isec.cub.falldetection._logic.readings;

import android.content.Context;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.postgresql.jdbc.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by Dav__ on 01/05/2017.
 */

public class old_DataTransferHandler {

//    private old_DataTransferHandler instance;
//
//    public old_DataTransferHandler getInstance(){
//        if(instance == null){
//            instance = new old_DataTransferHandler();
//        }
//
//        return instance;
//    }
//
//    private old_DataTransferHandler(){
//
//    }

    public static Promise<String, Throwable, Double> tranferData(final Context context){

        final DeferredObject<String, Throwable, Double> deferredObject = new DeferredObject<String, Throwable, Double>();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                String url = "jdbc:postgresql://kenobi.dei.uc.pt/activityRecognition";
                Properties props = new Properties();
                props.setProperty("user", "hugo");
                props.setProperty("password", "21170208");

//                String url = "jdbc:postgresql://192.168.56.175:5432/cub1617";
//                Properties props = new Properties();
//                props.setProperty("user", "postgisuser");
//                props.setProperty("password", "postgisuser");
//                props.setProperty("ssl", "true");

                Connection connection;
                try {
                    DriverManager.setLoginTimeout(5);
                    connection = DriverManager.getConnection(url, props);

                    File filesDir = new File(context.getExternalFilesDir(null).getAbsolutePath());

                    for(File f : filesDir.listFiles()){

                        String[] name = f.getName().split(".");

                        if(name[1] == "csv"){

                            BufferedReader reader = new BufferedReader(new FileReader(f));

                            String line = reader.readLine();

                            while ( (line = reader.readLine()) != null){

                                String[] data = line.split(",");

                                Statement statement = connection.createStatement();

                                String query = "INSERT INTO hugo VALUES (" + data[0] + ", " +   //timestamp
                                                                            data[1] + ", " +    //lat
                                                                            data[2] + ", " +    //lng
                                                                            data[3] + ", " +    //alt
                                                                            data[4] + ", " +    //x_acc
                                                                            data[5] + ", " +    //y_acc
                                                                            data[6] + ", " +    //z_acc
                                                                            data[7] + ", " +    //x_gyro
                                                                            data[8] + ", " +    //y_gyro
                                                                            data[9] + ", " +    //z_gyro
                                                                            data[10] + ", " +   //light
                                                                            "'" + data[11] + "')";  //activity

                                ResultSet rs = statement.executeQuery(query);

                            }

                            reader.close();

                            File to = new File(context.getExternalFilesDir(null).getAbsolutePath() + File.separator + name[0] + "_transfered.csv");
                            f.renameTo(to);
                        }

                    }

                    deferredObject.resolve("finished");

                } catch (Exception e) {
                    e.printStackTrace();
                    deferredObject.reject(e);
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        return deferredObject.promise();
    }

}
