package com.xdlysk.multiprocess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by sunkang on 2017/7/29.
 */

public class AidlService extends Service {
    private final static String TAG ="AidlService";
    public static final class Reporter extends IReporter.Stub{

        @Override
        public int report(String values, int type) throws RemoteException {
            Log.d(TAG, "report: "+values+" from " + getProcessName());
            return type;
        }

        public static String getProcessName() {
            try {
                File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
                BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
                String processName = mBufferedReader.readLine().trim();
                mBufferedReader.close();
                return processName;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mReporter;
    }

    private Reporter mReporter;

    public AidlService(){
        mReporter = new Reporter();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        mReporter.report(String.valueOf(SystemClock.currentThreadTimeMillis()),1);
                        Thread.sleep(1000);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }


            }
        });
        //thread.start();
    }
}
