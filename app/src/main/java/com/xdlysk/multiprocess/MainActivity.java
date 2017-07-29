package com.xdlysk.multiprocess;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {
    private IReporter mReporterAidl;

    public class AidlConnection implements ServiceConnection {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mReporterAidl = IReporter.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mReporterAidl = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getProcessName().equals("com.xdlysk.multiprocess")){
            Intent intent = new Intent(this,AidlService.class);
            bindService(intent,new AidlConnection(),BIND_AUTO_CREATE);
        }
        Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mReporterAidl.report(getProcessName(),1);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });
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
