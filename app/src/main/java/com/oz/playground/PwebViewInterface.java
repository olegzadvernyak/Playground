package com.oz.playground;

import android.util.Log;
import android.webkit.JavascriptInterface;

class PwebViewInterface {

    @JavascriptInterface
    /**Sets the specified button enabled or disabled.**/
    public void setButtonEnabled(int id, boolean enabled) {
        Log.d("oleg", "setButtonEnabled " + id + enabled);
    }


    @JavascriptInterface
    /**Sets the specified button visible (true) or invisible.**/
    public void setButtonVisibility(int id, boolean visible) {
        Log.d("oleg", "setButtonVisibility " + id + visible);
    }


    @JavascriptInterface
    /**Changes the button label.**/
    public void setButtonLabel(int id, String text) {
        Log.d("oleg", "setButtonLabel " + id + text);
    }



    @JavascriptInterface
    /**Add a hostname to the whitelist.**/
    public void addHostname(String hostname) {
        Log.d("oleg", "addHostname " + hostname);
    }

}