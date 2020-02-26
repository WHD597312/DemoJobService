package com.supcon.mes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.supcon.mes.daemonservice.DaemonHolder;


public class TestActivity extends AppCompatActivity {

    Button btnStart;
    Button btnStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnStart=findViewById(R.id.btnStart);
        btnStop=findViewById(R.id.btnStop);
        btnStart.setOnClickListener((v)->{
            DaemonHolder.startService();
        });
        btnStop.setOnClickListener((v)->{
//            DaemonHolder.stopService();
        });
    }

}
