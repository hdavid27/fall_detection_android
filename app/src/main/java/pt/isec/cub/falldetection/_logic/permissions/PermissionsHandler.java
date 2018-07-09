package pt.isec.cub.falldetection._logic.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionsHandler {


    public static String [] LOCATION_PERMISSIONS = { Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public static String [] SENSOR_PERMISSIONS = {Manifest.permission.BODY_SENSORS};

    public static String [] FILE_PERMISSIONS = { Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static int REQUEST_CODE_PERMISSIONS = 999;

    public static boolean checkPermissions (Context context, String [] perms){
        for (String permission : perms){
            if(ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }

        return true;
    }

    public static void askPermissions(Context context){

        ArrayList<String> permissionsDenied = new ArrayList<>();


        for (String permission : LOCATION_PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED){
                permissionsDenied.add(permission);
            }
        }

        for (String permission : SENSOR_PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED){
                permissionsDenied.add(permission);
            }
        }

        for (String permission : FILE_PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED){
                permissionsDenied.add(permission);
            }
        }

        if(permissionsDenied.size() > 0){
            String [] perm = new String[permissionsDenied.size()];
            permissionsDenied.toArray(perm);
            ActivityCompat.requestPermissions((Activity) context, perm, REQUEST_CODE_PERMISSIONS);
        }

    }



}
