package com.rcnbodegas.Global;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ixnet on 17/03/2015.
 */
public class ScannerTC20 extends Activity implements ISubject, EMDKListener, DataListener, StatusListener {

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;

    private boolean bContinuousMode = false;
    private boolean bActive = false;
    private int typeScanner = 0;

    private IObserver _Observer = null;

    public ScannerTC20(Context appContext, int _typeScanner) {
        typeScanner = _typeScanner;
        bContinuousMode = true;

        EMDKResults results = EMDKManager.getEMDKManager(appContext, ScannerTC20.this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            //textViewStatus.setText("Status: " + "EMDKManager object request failed!");
        }
    }
    @Override
    public void ScannerON(Boolean _bActive) {
        if(emdkManager!=null) {
            initScanner(_bActive);
            startScan();
        }
    }
    @Override
    public void ScannerOFF() {
        stopScan();
        deInitScanner();
    }

    @Override
    public void Pass_onKeyDown(int keyCode, KeyEvent event) {

    }

    @Override
    public void Pass_onKeyUp(int keyCode, KeyEvent event) {

    }


    public void AddObserver(IObserver Observer)
    {
        _Observer = Observer;
    }

    public void initScanner(Boolean _bActive) {

        bActive = _bActive;

        if(barcodeManager == null) barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

        if (scanner == null) {

            //if (deviceList.size() != 0) {
            //    scanner = barcodeManager.getDevice(deviceList.get(scannerIndex));
            if (typeScanner != -1) {
                scanner = barcodeManager.getDevice(barcodeManager.getSupportedDevicesInfo().get(typeScanner));
                scanner.triggerType = Scanner.TriggerType.HARD;
                setDecoders();
            }
            else {
                //textViewStatus.setText("Status: " + "Failed to get the specified scanner device! Please close and restart the application.");
            }

            if (scanner != null) {

                scanner.addDataListener(this);
                scanner.addStatusListener(this);

                try {
                    scanner.enable();
                } catch (ScannerException e) {

                    //textViewStatus.setText("Status: " + e.getMessage());
                }
            }
        }
    }
    public void deInitScanner() {

        if (scanner != null) {

            try {

                scanner.cancelRead();

                scanner.removeDataListener(this);
                scanner.removeStatusListener(this);
                scanner.disable();

            } catch (ScannerException e) {

                //textViewStatus.setText("Status: " + e.getMessage());
            }

            scanner = null;

            if (barcodeManager != null)
                barcodeManager = null;

            if (emdkManager != null) {
                emdkManager.release();
                emdkManager = null;
            }
        }
    }

    public void startScan() {

        if(scanner == null) {
            initScanner(bActive);
        }

        if (scanner != null) {
            try {
                // Submit a new read.
                scanner.read();
            } catch (ScannerException e) {

                //textViewStatus.setText("Status: " + e.getMessage());
            }
        }

    }
    public void stopScan() {

        if (scanner != null) {

            try {
                // Cancel the pending read.
                scanner.cancelRead();

            } catch (ScannerException e) {

                //textViewStatus.setText("Status: " + e.getMessage());
            }
        }

    }
    @Override
    public void onOpened(EMDKManager emdkManager) {
        if (this.emdkManager != null) {
            this.emdkManager.release();
            this.emdkManager = null;
        }
        this.emdkManager = emdkManager;
        //if(barcodeManager!=null) barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

        if (_Observer != null)
            _Observer.ScannerReady();

    }
    @Override
    public void onClosed() {

        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
        //textViewStatus.setText("Status: " + "EMDK closed unexpectedly! Please close and restart the application.");
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {

        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
            for(ScanDataCollection.ScanData data : scanData) {

                String dataString =  data.getData();

                if (_Observer != null)
                    _Observer.DataRecived(dataString);
            }
        }
    }

    @Override
    public void onStatus(StatusData statusData) {

        StatusData.ScannerStates state = statusData.getState();

        switch(state) {
            case IDLE:
                //statusString = "Scanner is enabled and idle...";
                //new AsyncStatusUpdate().execute(statusString);
                if (bContinuousMode && bActive) {
                    try {
                        // An attempt to use the scanner continuously and rapidly (with a delay < 100 ms between scans)
                        // may cause the scanner to pause momentarily before resuming the scanning.
                        // Hence add some delay (>= 100ms) before submitting the next read.
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        scanner.read();
                    } catch (ScannerException e) {
                        //statusString = e.getMessage();
                        //new AsyncStatusUpdate().execute(statusString);
                    }
                }
                //new AsyncUiUpdate().execute(true);
                break;
            case WAITING:
                //statusString = "Scanner is waiting for trigger press...";
                //new AsyncStatusUpdate().execute(statusString);
                //new AsyncUiUpdate().execute(false);
                break;
            case SCANNING:
                //statusString = "Scanning...";
                //new AsyncStatusUpdate().execute(statusString);
                //new AsyncUiUpdate().execute(false);
                break;
            case DISABLED:
                //statusString = "Scanner is disabled.";
                //new AsyncStatusUpdate().execute(statusString);
                break;
        }
    }

    private void setDecoders() {
        if (scanner != null) {
            try {

                ScannerConfig config = scanner.getConfig();

                config.decoderParams.ean8.enabled = true;
                config.decoderParams.ean13.enabled = true;
                config.decoderParams.code39.enabled = true;
                config.decoderParams.code128.enabled = true;

                scanner.setConfig(config);

            } catch (ScannerException e) {

                //textViewStatus.setText("Status: " + e.getMessage());
            }
        }
    }

    @Override
    public List<String> GetDeviceListInfo() {
        List<String> list_device = new ArrayList<String>();
        if(barcodeManager == null) barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
        List<ScannerInfo> supportedDevList = barcodeManager.getSupportedDevicesInfo();
        Iterator<ScannerInfo> it = supportedDevList.iterator();
        Integer i = 0;
        while(it.hasNext()) {
            ScannerInfo scnInfo = it.next();
            list_device.add(i.toString() + "-->" + scnInfo.getFriendlyName() + " (" + (scnInfo.getDeviceType() + ")"));
            i = i + 1;
        }
        return list_device;
    }

    public List<ScannerInfo> getListSupportedDevices() {
        if(barcodeManager == null) barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
        List<ScannerInfo> List0 = new ArrayList<ScannerInfo>();
        if(barcodeManager != null) return barcodeManager.getSupportedDevicesInfo();
        else return List0;

/*
        List<ScannerInfo> supportedDevList = barcodeManager.getSupportedDevicesInfo();
        Scanner scanner = null;
        Iterator<ScannerInfo> it = supportedDevList.iterator();
        while(it.hasNext()) {
            ScannerInfo scnInfo = it.next();
            if(scnInfo.getDeviceIdentifier()==DeviceIdentifier.BLUETOOTH_IMAGER_RS6000){
                scanner = barcodeManager.getDevice(scnInfo);
                break;
            }
        }
        */
    }
}
