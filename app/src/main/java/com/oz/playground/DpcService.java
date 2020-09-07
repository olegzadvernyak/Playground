package com.oz.playground;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oz.remotedpcaidl.DpcRemote;
import com.oz.remotedpcaidl.UpdateStatusCallback;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DpcService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("oleg", "server service create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("oleg", "server service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final DpcRemote.Stub binder = new DpcRemote.Stub() {

        @Override
        public void requestUpdate(String url, Bundle headers, UpdateStatusCallback updateStatusCallback) throws RemoteException {
            Log.d("oleg", "server update requested");
            if (isCallerValid(new CallerInfo(Binder.getCallingUid(), Binder.getCallingPid()))) {
                Log.d("oleg", "server changing status");
                updateStatusCallback.onStatusUpdated(UpdateStatusCallback.STATUS_A);
            }
        }

    };

    private List<String> signaturesWhitelist = Arrays.asList(
            "fdbycCch8l910Py+MyQwXj2C25XtU3cEu8o3aibc2H8="
    );

    private List<String> packageNamesWhitelist = Arrays.asList(
            "com.oz.aidlconsumerpoc"
    );

    private Map<CallerInfo, Boolean> knownCallers = new HashMap<>();

    private boolean isCallerValid(CallerInfo callerInfo) {
        if (knownCallers.containsKey(callerInfo)) {
            Boolean knownValidation = knownCallers.get(callerInfo);
            if (knownValidation == null) knownValidation = false;
            return knownValidation;
        }

        PackageManager packageManager = getPackageManager();
        String callerPackage = packageManager.getNameForUid(callerInfo.callerUid);
        if (callerPackage == null) callerPackage = "";
        if (!packageNamesWhitelist.contains(callerPackage)) {
            knownCallers.put(callerInfo, false);
            return false;
        }
        try {
            Signature[] signatures;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo callerPackageInfo = packageManager.getPackageInfo(callerPackage, PackageManager.GET_SIGNING_CERTIFICATES);
                SigningInfo signingInfo = callerPackageInfo.signingInfo;
                if (signingInfo.hasMultipleSigners()) {
                    signatures = signingInfo.getApkContentsSigners();
                } else {
                    signatures = signingInfo.getSigningCertificateHistory();
                }
            } else {
                @SuppressLint("PackageManagerGetSignatures")
                PackageInfo callerPackageInfo = packageManager.getPackageInfo(callerPackage, PackageManager.GET_SIGNATURES);
                signatures = callerPackageInfo.signatures;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            for (Signature signature: signatures) {
                messageDigest.update(signature.toByteArray());
                String base64Signature = new String(Base64.encode(messageDigest.digest(), Base64.DEFAULT)).trim();
                if (signaturesWhitelist.contains(base64Signature)) {
                    knownCallers.put(callerInfo, true);
                    return true;
                }
            }
        } catch (Exception e) {
            // todo
        }
        knownCallers.put(callerInfo, false);
        return false;
    }

    private static final class CallerInfo {

        int callerUid;
        int callerPid;

        CallerInfo(int callerUid, int callerPid) {
            this.callerUid = callerUid;
            this.callerPid = callerPid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CallerInfo that = (CallerInfo) o;
            return callerUid == that.callerUid &&
                    callerPid == that.callerPid;
        }

        @Override
        public int hashCode() {
            return Objects.hash(callerUid, callerPid);
        }

    }

}
