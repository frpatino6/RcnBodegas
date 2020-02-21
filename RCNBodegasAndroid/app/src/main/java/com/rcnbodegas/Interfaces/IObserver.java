package com.rcnbodegas.Interfaces;

/**
 * Created by ixnet on 17/03/2015.
 */
public interface IObserver {
    void DataRecived(String BarcodeData);
    void ScannerReady();
}
