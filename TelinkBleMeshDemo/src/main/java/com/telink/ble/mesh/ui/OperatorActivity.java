package com.telink.ble.mesh.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.model.DeviceInfo;

public class OperatorActivity extends AppCompatActivity implements View.OnClickListener {
    DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opertior);

        final Intent intent = getIntent();
        if (intent.hasExtra("deviceAddress")) {
            int address = intent.getIntExtra("deviceAddress", -1);
            deviceInfo = TelinkMeshApplication.getInstance().getMeshInfo().getDeviceByMeshAddress(address);
        } else {
            finish();
            return;
        }

        if (deviceInfo == null) {
            finish();
            return;
        }
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                TransMeshMessage.getInstance().SendDeviceByte(deviceInfo.meshAddress, (byte) 1);
                break;
            case R.id.button2:
                TransMeshMessage.getInstance().SendDeviceByte(deviceInfo.meshAddress, (byte) 2);
                break;
            case R.id.button3:
                TransMeshMessage.getInstance().SendDeviceByte(deviceInfo.meshAddress, (byte) 3);
                break;
            case R.id.button4:
                TransMeshMessage.getInstance().SendDeviceByte(deviceInfo.meshAddress, (byte) 4);
                break;
            case R.id.button5:
                TransMeshMessage.getInstance().SendDeviceByte(deviceInfo.meshAddress, (byte) 5);
                break;
            case R.id.button6:
                TransMeshMessage.getInstance().SendDeviceByte(deviceInfo.meshAddress, (byte) 6);
                break;
            case R.id.button7:
                TransMeshMessage.getInstance().SendDeviceByte(deviceInfo.meshAddress, (byte) 7);
                break;
        }
    }
}