package com.oz.playground;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.oz.remotedpcaidl.DpcRemote;
import com.oz.remotedpcaidl.UpdateStatusCallback;

public class UpdateWorker extends Worker {

    private Context context;

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Intent intent = new Intent("dpcremoteconnect");
        intent.setComponent(new ComponentName("com.oz.playground", "com.oz.playground.DpcService"));
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        return Result.success();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DpcRemote dpcRemote = DpcRemote.Stub.asInterface(service);
            try {
                Log.d("oleg", "connected to service from worker");
                dpcRemote.requestUpdate("workerurl", null, new UpdateStatusCallback.Stub() {
                    @Override
                    public void onStatusUpdated(int i) throws RemoteException {
                        Log.d("oleg", "server worker: onStatusUpdated " + i);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
