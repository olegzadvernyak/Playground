// DpcRemote.aidl
package com.oz.remotedpcaidl;

import android.os.Bundle;
import com.oz.remotedpcaidl.UpdateStatusCallback;

interface DpcRemote {

    void requestUpdate(String url, in Bundle headers, UpdateStatusCallback updateStatusCallback);

}