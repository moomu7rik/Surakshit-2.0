package com.disarm.surakshit.pdm.DisarmConnect;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.List;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.interfaces.ConnectEventListener;
import io.chirp.connect.models.ChirpError;

/**
 * Created by Sanna on 16-02-2016.
 */
public class DCService extends Service {

    public static WifiManager wifi;
    public static String checkWifiState="0x";
    public static int level;
    public static BatteryLevel bl;
    public static WifiScanReceiver wifiReciever;
    public static boolean isHotspotOn,c;
    public static WifiInfo wifiInfo;
    public static List<String> IpAddr;
    public static String mobileAPName = "DH";
    public static String dbAPName = "DisarmHotspotDB";
    public static String dbPass = "DisarmDB";
    public FileReader fr = null;
    public static int count=0,startwififirst = 1;
    public static Handler handler;
    public static double wifiState;
    public static int macCount = 0;
    public static String TAG1 = "Timer_Toggle";
    public static String TAG2 = "WifiConnect";
    public static String TAG3 = "Toggler";
    public static String TAG4 = "Searching DB";
    public Timer_Toggler tt;
    public SearchingDisarmDB sDDB;
    public WifiConnect wifiC;
    public ToggleWRTSpeed toggleWRTSpeed;
    private final IBinder myServiceBinder = new MyServiceBinder();
    public BufferedReader br = null;
    private Logger logger;
    public static String phoneVal;
    public static String presentState="wifi";
    public static List<ScanResult> wifiScanList;
    public static int bestAvailableChannel;

    String CHIRP_APP_KEY = "3FF76aA924ee6DE8aBEB06aeb";
    String CHIRP_APP_SECRET = "da30663a050148D08Ac01AF11557FE938E8a02c11Ac2e1f2D5";
    String CHIRP_APP_CONFIG = "W8OsyCTBRCl5eLqiSqAqu1Oxx+zUp8Hbw7BN2xtbl+axSmINFrEpuI0TETsBIG4XtFZvLDgIxdYCLbC36Sms1IXB+LMnOVOb9OtgR29l7CoYMtwMDh+m7NK799z+ywd/DCOxXpPf92c/5vnnZOta3y7tSEKcQH0+hFwgPBOquZ7YLdjrhHUOy6zi1BsXQi7iAYHLfCfLYZM4yFKCdlp8OFhrNsjiQJ+bO3myZt2E3vQUa9UnbUrmX1IvHdOdmEH1iMdOT3dnSNTWwO0F6ZiKZqvuFmgjNoKgp9CZFXY1WvztnzBWh0ryTY4CfAkX6LBBRfTb7CDvDblHIdAdmk8R7nzCVPgb6OeNdlouYzOfyZryhhzcNd3By/v1JG14Sot1aZWd1A9FFYXJO8qyzyWkCyfzuh1jt4Ga0ZngUjVE3yrZwD9ZyIU65bSjfbnt3JWACx0DIb2uWowjfThoQ2lZtLAmKM3ALt3kvk8oCLleYhmzxypza41Q78/FbCX55jLHQ3mnL9WO9n6m8XIyXFCUHGtQzCdMOTNM6Ghg/NqIVtDI/+IdF9ilEcnJjqLPVE43vY988Nwev7+EnudSuHaWoLCkzoeCgWy3qC820dRh+omtgBUslHFB52Mdh+SvMaP8/qEukef4FLUbnQt6w5NlT2IO92p8fBUWM7Tpe500gajpKEh8uIPxaSq+OtfePpdl06sEVO+Rc8iW+82e0u4mpJf/okpHJdZVb3KxcI8aYGDwds4rJvlJu8OyQuC/kepK/ClwPMOSgy+yB0hpxdF/lyc4GRdY6x7crzUAF85/c9q6kczBlRIskEpQIVZmHw6rvc/kgUC6qDP7JeX2VjMLdILuN/Ai4NV4XaO0Yl9GgVmNri1kxMoE45FsvmMHwe+c3aUgGAJKjDuiTyHsQRPX67LWHEPsxaZPn8n/3ttWny/89iKsZly7IL2zMSs4qH1z6h4nyhawf/N2urUWHiOa54QOeVLm+wMkjFC0vYe9LKQO+M603Hn5p1lcxWYXa8pZJbRaiGSRDCYBc2HS+7gRHm9SKTHONkjJlXnwc8NraH34DaGr8ANypVVmA++3Xz821WTyia0SOiDcEvBRNTdB1fgPEbO5Vcb4zAlWBMHffxs=";
    public static String networkPass="none";
   public static String networkSSID="none";
    static ChirpConnect chirp;

    @Override
    public IBinder onBind(Intent intent) {

        return myServiceBinder;
    }
    public class MyServiceBinder extends Binder {
        public DCService getService() {
            // Return this instance of SyncService so activity can call public methods
            return DCService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();

        chirp = new ChirpConnect(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);
        ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
      ChirpError error1= chirp.start();
        if (error1.getCode() > 0) {
            Log.e("ChirpError: ", error.getMessage());
        } else {
            Log.v("ChirpSDK: ", "Started ChirpSDK");
        }

        if (error.getCode() == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK");
        } else {
            Log.e("ChirpError1: ", error.getMessage());
        }
        // DisarmConnect Started
        Log.v("DCService:", "DisarmConnect Started");

        chirp.setListener(chirpEventListener);


        // WifiScanReceiver registered
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiReciever = new WifiScanReceiver();
        registerReceiver(wifiReciever, filter);

        // Start scan for Wifi List
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.startScan();

        // Logger Initiated
        logger = new Logger();

        // Battery Level Indicator registered
        bl = new BatteryLevel();
        IntentFilter batfilter = new IntentFilter();
        batfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(bl, batfilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // DisarmConnect Service started
        logger.addRecordToLog("DisarmConnect Started");


        // Acquired WakeLock
        WakeLockHelper.keepCpuAwake(getApplicationContext(), true);
        WakeLockHelper.keepWiFiOn(getApplicationContext(), true);

        // Handler started
        handler = new Handler();
        tt = new Timer_Toggler(handler,getApplicationContext());
        wifiC = new WifiConnect(handler,getApplicationContext());
        sDDB = new SearchingDisarmDB(handler,getApplicationContext());

        return START_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    public void onDestroy() {
        super.onDestroy();
        chirp.stop();
        try {
            chirp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Unregistering receivers
        unregisterReceiver(wifiReciever);
        unregisterReceiver(bl);

        // Disabling hotspot and enabling WiFi Mode on app destroy
        isHotspotOn = ApManager.isApOn(DCService.this);
        if(isHotspotOn){
            ApManager.configApState(DCService.this);
            wifi.setWifiEnabled(true);
            Logger.addRecordToLog("Stopping DisarmConnect Hotspot Disabled");
        }

        // Stopping all services
        handler.removeCallbacksAndMessages(null);

        // Release lock
        WakeLockHelper.keepCpuAwake(getApplicationContext(), false);
        WakeLockHelper.keepWiFiOn(getApplicationContext(), false);

        // Adding stop record to log
        logger.addRecordToLog("DisarmConnect Stopped");
        Log.v("DCService:", "DisarmConnect Stopped");
    }

    ConnectEventListener chirpEventListener = new ConnectEventListener() {

        @Override
        public void onStateChanged(int i, int i1) {

        }

        @Override
        public void onSent(byte[] bytes, int i) {

        }

        @Override
        public void onSending(byte[] bytes, int i) {


        }

        @Override
        public void onReceiving(int i) {


        }

        @Override
        public void onReceived(byte[] data, int channel) {

            if (data != null) {
                String identifier = new String(data);
                // Toast.makeText(MainActivity.this, identifier, Toast.LENGTH_SHORT).show();
                Log.v("ChirpSDK1: ", "Received " + identifier);
                String[] parts = identifier.split(":");
                networkPass =parts[1].trim();
                networkSSID=parts[0].trim();

                //  Toast.makeText(MainActivity.this,networkPass+","+networkSSID, Toast.LENGTH_SHORT).show();




            } else {
                //Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e("ChirpError: ", "Decode failed");
            }


        }


        @Override
        public void onSystemVolumeChanged(int old, int current) {
        }
    };
    public static void send() {

        String identifier =ApManager.ssid+":" +ApManager.password;
        byte[] payload = identifier.getBytes(Charset.forName("UTF-8"));

        ChirpError error = chirp.send(payload);
        if (error.getCode() > 0) {
            Log.e("ChirpError22: ", error.getMessage());
        } else {
            Log.v("ChirpSDK2: ", "Sent " + identifier);
        }
    }
    public static void sendAfterConnection(String dssid,String dpassword) {

        String identifier =dssid+":" +dpassword;
        byte[] payload = identifier.getBytes(Charset.forName("UTF-8"));

        ChirpError error = chirp.send(payload);
        if (error.getCode() > 0) {
            Log.e("ChirpError22: ", error.getMessage());
        } else {
            Log.v("ChirpSDK2: ", "Sent " + identifier);
        }
    }

}