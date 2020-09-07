// UpdateStatusCallback.aidl
package com.oz.remotedpcaidl;

interface UpdateStatusCallback {

    const int STATUS_A = 1;

    void onStatusUpdated(int status);

}
