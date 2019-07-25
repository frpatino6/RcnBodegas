package com.rcnbodegas.Global;

import android.view.KeyEvent;

import java.util.List;

/**
 * Created by ixnet on 17/03/2015.
 */
public interface ISubject {
    void AddObserver(IObserver Observer);

    void ScannerON(Boolean _bActive);
    void ScannerOFF();

    void Pass_onKeyDown(int keyCode, KeyEvent event);
    void Pass_onKeyUp(int keyCode, KeyEvent event);

    List<String> GetDeviceListInfo();
}
