package com.rcnbodegas.Global;

import android.content.Context;


/**
 * Created by ixnet on 17/03/2015.
 */

public class ScannerFactory {

    public static TScanner CreateScanner(Context appContext,Context activityContext) {


        ScannerTC20 _scanner = new ScannerTC20(appContext, 0);
        return new TScanner(_scanner);


    }
}
