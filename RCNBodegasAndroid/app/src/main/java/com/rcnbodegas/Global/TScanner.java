package com.rcnbodegas.Global;

import android.view.KeyEvent;

import com.rcnbodegas.Interfaces.IObserver;
import com.rcnbodegas.Interfaces.IScanner;
import com.rcnbodegas.Interfaces.ISubject;

import java.util.List;

/**
 * Created by ixnet on 17/03/2015.
 */
public class TScanner implements IObserver, IScanner, ISubject {
    private ISubject newScanner = null;
    private IObserver _Observer = null;

    public TScanner(ScannerTC20 scannerPsion)
    {
        this.newScanner = scannerPsion;
        this.newScanner.AddObserver(this);
    }



    @Override
    public void DataRecived(String BarcodeData) {
        if(this._Observer!= null) this._Observer.DataRecived(BarcodeData);
    }

    @Override
    public void ScannerReady() {
        if(this._Observer!= null) this._Observer.ScannerReady();
    }

    @Override
    public void ScannerON(Boolean _bActive) {
        //((ScannerPsion)this.newScanner).ScannerON(true);
        this.newScanner.ScannerON(true);
    }

    @Override
    public void ScannerOFF() {
        //((ScannerPsion)this.newScanner).ScannerOFF();
        this.newScanner.ScannerOFF();
    }

    @Override
    public void Pass_onKeyDown(int keyCode, KeyEvent event) {
        this.newScanner.Pass_onKeyDown(keyCode, event);
    }

    @Override
    public void Pass_onKeyUp(int keyCode, KeyEvent event) {
        this.newScanner.Pass_onKeyUp(keyCode, event);
    }

    @Override
    public void AddObserver(IObserver Observer) {
        _Observer = Observer;
    }

    @Override
    public List<String> GetDeviceListInfo() {
        return this.newScanner.GetDeviceListInfo();
    }
}
