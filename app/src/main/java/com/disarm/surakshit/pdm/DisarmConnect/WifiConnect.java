package com.disarm.surakshit.pdm.DisarmConnect;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import io.chirp.connect.ChirpConnect;
import io.chirp.connect.models.ChirpError;
import io.chirp.connect.interfaces.ConnectEventListener;


import static com.disarm.surakshit.pdm.DisarmConnect.DCService.networkPass;
import static com.disarm.surakshit.pdm.DisarmConnect.DCService.networkSSID;
import static com.disarm.surakshit.pdm.DisarmConnect.DCService.wifiInfo;

/**
 * Created by hridoy on 21/8/16.
 */
public class WifiConnect implements Runnable {
    private android.os.Handler handler;
    private Context context;
    private FileReader fr = null;
    private BufferedReader br = null;
    public int minDBLevel = 2;
    public int isDBConnected = 0;
    ChirpConnect chirp;
    public int flag = 0;
    int strength;

    public WifiConnect(android.os.Handler handler, Context context) {
        this.handler = handler;
        this.context = context;

        this.handler.post(this);
    }

    @Override
    public void run() {

        DCService.wifi.startScan();
        if (!ApManager.ssid.equalsIgnoreCase("none")) {
            DCService.send();
        }
        List<ScanResult> allScanResults = DCService.wifi.getScanResults();
        //Log.v("WifiConnect allScanResults:",allScanResults.toString());
        Log.v(DCService.TAG2, "Running Autoconnector");
        wifiInfo = DCService.wifi.getConnectionInfo();
        String ssidName = wifiInfo.getSSID();
        Log.v(DCService.TAG2, ssidName);
        if (ssidName.contains("DisarmHotspotDB")) {
            Log.v(DCService.TAG2, "Already Connected DB ");
            Logger.addRecordToLog("Already DB Connected");
        } else if (flag == 1) {
            Log.v(DCService.TAG2, "Already Connected");

            DCService.sendAfterConnection(DCService.networkSSID, DCService.networkPass);
            strength=getSigalStrength();

           // Toast toast = Toast.makeText(context,strength,Toast.LENGTH_SHORT);
         //   toast.show();

            Logger.addRecordToLog("DH Connected:" + DCService.networkSSID);
            try {
                DCService.c = false;
               /* fr = new FileReader("/proc/net/arp");
                br = new BufferedReader(fr);
                String line;
                DCService.IpAddr = new ArrayList<String>();
                DCService.c = false;
                while ((line = br.readLine()) != null) {
                    String[] splitted = line.split(" +");
                    Log.v("Splitted:", Arrays.deepToString(splitted));
                    }
                    */

            } catch (Exception e) {
            }
        } else {
            if (!DCService.networkSSID.equalsIgnoreCase("none")) {
                flag = 1;
                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + DCService.networkSSID + "\"";
                conf.preSharedKey = "\"" + DCService.networkPass + "\"";

                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

                WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                int res = wifi.addNetwork(conf);
                boolean b = wifi.enableNetwork(res, true);
                List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + DCService.networkSSID + "\"")) {
                        wifi.disconnect();
                        wifi.enableNetwork(i.networkId, true);
                        wifi.reconnect();

                        break;
                    }
                }
                Log.v("WifiConnect:", "Res:" + res + ",b:" + b);
                if (res != -1) {
                    Log.v("Test", "Connected");
                    Log.v("Parameters:", conf.SSID + conf.preSharedKey + "," + conf.BSSID + "," + conf.allowedAuthAlgorithms + "," + conf.allowedProtocols + "," + conf.allowedKeyManagement + "," + conf.allowedGroupCiphers + "," + conf.allowedPairwiseCiphers + "," + conf.FQDN + "," + conf.status);
                    Logger.addRecordToLog("DH Connected Successfully," + DCService.networkSSID);
                } else {
                    Logger.addRecordToLog("DH not connected");
                }

                DCService.sendAfterConnection(DCService.networkSSID, DCService.networkPass);
                 strength=getSigalStrength();
              //  Toast toast = Toast.makeText(context,strength,Toast.LENGTH_SHORT);
              //  toast.show();
            } else {
                Log.v(DCService.TAG2, "Disarm Not Available");

                Logger.addRecordToLog("no DH network available");

            }

            /*

            if (allScanResults.toString().contains("DH-")) {
                // Store all DH available in allDHAvailable
                Map allDHAvailable = new HashMap<String, Integer>();

                // Put all found DH to allDHAvailable Map
                for (ScanResult scanResult : allScanResults) {
                    if(scanResult.SSID.toString().contains("DH-")) {
                        allDHAvailable.put(scanResult.SSID,scanResult.level);
                    }
                }

                Log.v("AllDH Available:",Arrays.asList(allDHAvailable).toString());
                Logger.addRecordToLog("All DH available:" + Arrays.asList(allDHAvailable).toString());
                // Find key with the maximum value from allDHAvailable
                String bestFoundSSID="";
                int maxValueInMap = 0;
                try {
                    maxValueInMap = (int) Collections.max(allDHAvailable.values());  // This will return max value in the Hashmap
                    Iterator it = allDHAvailable.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Integer> pair = (Map.Entry) it.next();
                        if (pair.getValue() == maxValueInMap) {
                            Log.v("Best Found SSID:", pair.getKey());     // Print the key with max value
                            Logger.addRecordToLog("Best Found SSID"+ ',' + pair.getKey());
                            bestFoundSSID = pair.getKey().toString();
                        }
                    }
                }
                catch (Exception e)
                {}
                // Connect to the best found network
                String pass = "password123";
                WifiConfiguration wc = new WifiConfiguration();
                wc.SSID = "\"" + bestFoundSSID + "\""; //IMPORTANT! This should be in Quotes!!
                wc.preSharedKey = "\""+ pass +"\"";
                wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if(DCService.wifi.pingSupplicant()){
                    DCService.wifi.disconnect();
                    DCService.wifi.disableNetwork(wifiInfo.getNetworkId());
                }
                int res = DCService.wifi.addNetwork(wc);
                boolean b = DCService.wifi.enableNetwork(res, true);
                Log.v("WifiConnect:","Res:" + res + ",b:" + b);
                if(res != -1 ) {
                    Log.v(DCService.TAG2, "Connected");
                    Log.v("Parameters:", wc.SSID + "," + wc.BSSID + "," + wc.allowedAuthAlgorithms + "," + wc.allowedProtocols + "," + wc.allowedKeyManagement + "," + wc.allowedGroupCiphers + "," + wc.allowedPairwiseCiphers + "," + wc.FQDN + "," + wc.status);
                    Logger.addRecordToLog("DH Connected Successfully," + bestFoundSSID);
                }
                else{
                    Logger.addRecordToLog("DH not connected");
                }
            }
            else{
                Log.v(DCService.TAG2,"Disarm Not Available");

                Logger.addRecordToLog("no DH network available");

            }
*/
        }
        handler.postDelayed(this, 3000);
    }

    public int findDBSignalLevel(List<ScanResult> allScanResults) {
        for (ScanResult scanResult : allScanResults) {
            if (scanResult.SSID.toString().equals(DCService.dbAPName)) {
                Log.v("SSID:", scanResult.SSID.toString());
                int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                return level;
            }
        }
        return 0;
    }

    public int getSigalStrength() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        return level;
    }

}
