package pt.isec.cub.falldetection._logic.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

public class LocationHandler implements LocationListener {

    // -------------- variables --------------

    private ILocationListener locationListener;

    private LocationManager locationManager;

    private Location lastLocation;

    private boolean listeningGpsProvider;
    private boolean listeningNetworkProvider;

    // -------------- static variables --------------
    private static final int ONE_MINUTES = 1000 * 60 * 2;

    private static LocationHandler instance = null;

    // -------------- static functions --------------
    public static LocationHandler getInstance() {

        if(instance == null){
            instance = new LocationHandler();
        }

        return instance;
    }

    // -------------- class functions --------------
    private LocationHandler(){
        lastLocation = null;
        listeningGpsProvider = false;
        listeningNetworkProvider = false;
    }

    public void startListening(Context context, ILocationListener listener){

        locationListener = listener;

        if(listeningGpsProvider == false){
            enableListeningLocationGpsProvider(context);
        }
        if(listeningNetworkProvider == false){
            enableListeningLocationNetworkProvider(context);
        }

        getUserLocation(context);
    }

    public void stopListening(Context context){

        if(listeningGpsProvider == true || listeningNetworkProvider == true){
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(this);
            listeningGpsProvider = false;
            listeningNetworkProvider = false;
        }

    }

    private void getLastKnowLocationNetworkProvider(Context context){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(isBetterLocation(location, lastLocation)){
                lastLocation = location;
            }
        }
    }

    private void getLastKnowLocationGpsProvider(Context context){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(isBetterLocation(location, lastLocation)){
                lastLocation = location;
            }
        }
    }

    private boolean enableListeningLocationGpsProvider(Context context){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10.0f, this);
            listeningGpsProvider = true;
        }catch (Exception e){
            e.printStackTrace();
            listeningGpsProvider = false;
        }

        return listeningGpsProvider;
    }

    private boolean enableListeningLocationNetworkProvider(Context context){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10.0f, this);
            listeningNetworkProvider = true;
        }catch (Exception e){
            e.printStackTrace();
            listeningNetworkProvider = false;
        }

        return listeningNetworkProvider;

    }

    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    public Location getUserLocation(Context context){
        if(lastLocation == null){
            getLastKnowLocationGpsProvider(context);
            if(lastLocation == null){
                getLastKnowLocationNetworkProvider(context);
            }

        }

        return lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(isBetterLocation(location, lastLocation)) {
            lastLocation = location;
            if (locationListener != null && lastLocation != null) {
                locationListener.onLocationChanged(lastLocation);
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
