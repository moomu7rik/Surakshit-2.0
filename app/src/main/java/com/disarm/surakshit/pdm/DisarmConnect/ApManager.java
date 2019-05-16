package com.disarm.surakshit.pdm.DisarmConnect;

/**
 * Created by sanna on 2/12/15.
 */

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;


import com.disarm.surakshit.pdm.Util.Params;

import java.lang.reflect.Method;
import java.nio.charset.Charset;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.models.ChirpError;

public class ApManager {

    private static final String TAG = "AP Creation";
    private static WifiManager.LocalOnlyHotspotReservation mReservation;
    public static String password;
    public  static String ssid="none";
    static ChirpConnect chirp;

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            if(isApOn(context)) {
                wifimanager.setWifiEnabled(false);
              //  mReservation.close();
            }
            //Change Name of the Created Hotspot
            try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    Log.i("TAG", "Wifi Hotspot is on now");
                    mReservation = reservation;

                    password = reservation.getWifiConfiguration().preSharedKey;
                    ssid = reservation.getWifiConfiguration().SSID;


                    Log.i("Update", password+" "+ssid);

                }
                public void onStopped() {
                    super.onStopped();

                    Log.d("TAG", "onStopped: ");
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Log.d("TAG", "onFailed: ");
                }
            }, new Handler());

                /* Method getConfigMethod = wifimanager.getClass().getMethod("getWifiApConfiguration");

                WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifimanager);
                //wifiConfig.getClass().getField("apChannel").setInt(wifiConfig, 6);
                Log.v("ApManager", "Best Available Channel:" + DCService.bestAvailableChannel);

                // Channel Allocation

                if (Build.VERSION.SDK_INT > 22) {
                    // Created hotspot in the best available channel
                    wifiConfig.getClass().getField("apChannel").setInt(wifiConfig, DCService.bestAvailableChannel);
                } else {
                    wifiConfig.getClass().getField("channel").setInt(wifiConfig, DCService.bestAvailableChannel);
                }

                wifiConfig.allowedAuthAlgorithms.clear();
                wifiConfig.allowedGroupCiphers.clear();
                wifiConfig.allowedKeyManagement.clear();
                wifiConfig.allowedPairwiseCiphers.clear();
                wifiConfig.allowedProtocols.clear();
                wifiConfig.SSID = "DH-" + Params.SOURCE_PHONE_NO;


                wifiConfig.preSharedKey = "password123";

                wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

                Method setWifiApMethod = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                boolean apstatus = (Boolean) setWifiApMethod.invoke(wifimanager, wifiConfig, true);
                //Log.v("GetAPCOnfig:" + getConfigMethod.toString() + ",setWifiApMethod : " + setWifiApMethod.toString());
                Log.v("WifiConfig: " , wifiConfig.toString());
                */

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}