/********************************************************************************************************
 * @file     SceneSettingActivity.java 
 *
 * @brief    for TLSR chips
 *
 * @author	 telink
 * @date     Sep. 30, 2010
 *
 * @par      Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
 *           All rights reserved.
 *           
 *			 The information contained herein is confidential and proprietary property of Telink 
 * 		     Semiconductor (Shanghai) Co., Ltd. and is available under the terms 
 *			 of Commercial License Agreement between Telink Semiconductor (Shanghai) 
 *			 Co., Ltd. and the licensee in separate contract or the terms described here-in. 
 *           This heading MUST NOT be removed from this file.
 *
 * 			 Licensees are granted free, non-transferable use of the information in this 
 *			 file under Mutual Non-Disclosure Agreement. NO WARRENTY of ANY KIND is provided. 
 *           
 *******************************************************************************************************/
package com.telink.ble.mesh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.core.message.MeshMessage;
import com.telink.ble.mesh.core.message.NotificationMessage;
import com.telink.ble.mesh.core.message.config.ConfigStatus;
import com.telink.ble.mesh.core.message.scene.SceneDeleteMessage;
import com.telink.ble.mesh.core.message.scene.SceneRegisterStatusMessage;
import com.telink.ble.mesh.core.message.scene.SceneStoreMessage;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent;
import com.telink.ble.mesh.model.DeviceInfo;
import com.telink.ble.mesh.model.GroupInfo;
import com.telink.ble.mesh.model.MeshInfo;
import com.telink.ble.mesh.model.Scene;
import com.telink.ble.mesh.ui.adapter.BaseSelectableListAdapter;
import com.telink.ble.mesh.ui.adapter.DeviceSelectAdapter;
import com.telink.ble.mesh.ui.adapter.GroupDeviceSelectAdapter;
import com.telink.ble.mesh.util.MeshLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Scene Setting
 * Created by kee on 2018/9/18.
 */
public class GroupDeviceSelectActivity extends BaseActivity implements View.OnClickListener, BaseSelectableListAdapter.SelectStatusChangedListener {

    //    private GroupSelectAdapter mGroupAdapter;
    private GroupDeviceSelectAdapter mDeviceAdapter;
    private MeshInfo mesh;
    private List<DeviceInfo> devices;
    //    private List<Group> groups;
    private CheckBox cb_device, cb_group;
    private GroupInfo group;

    private Handler delayHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!validateNormalStart(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_scene_setting);
        mesh = TelinkMeshApplication.getInstance().getMeshInfo();
        final Intent intent = getIntent();
        if (intent.hasExtra("group")) {
            group = (GroupInfo) intent.getSerializableExtra("group");
        } else {
            toastMsg("group null");
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.title_bar);

        setTitle("Group Device Setting");
        enableBackNav(true);
        cb_device = findViewById(R.id.cb_device);
        cb_group = findViewById(R.id.cb_group);
        RecyclerView rv_device = findViewById(R.id.rv_device);

        rv_device.setLayoutManager(new LinearLayoutManager(this));
        if (mesh.nodes != null) {
            for (DeviceInfo deviceInfo : mesh.nodes) {
                deviceInfo.selected = deviceInfo.subList.contains(group.address);
            }
        }
        devices = mesh.nodes;
        mDeviceAdapter = new GroupDeviceSelectAdapter(this, devices, group.address);
        mDeviceAdapter.setStatusChangedListener(this);
        rv_device.setAdapter(mDeviceAdapter);
        cb_device.setChecked(mDeviceAdapter.allSelected());
        findViewById(R.id.btn_save_scene).setOnClickListener(this);
        findViewById(R.id.cb_device).setOnClickListener(this);
        findViewById(R.id.cb_group).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delayHandler.removeCallbacksAndMessages(null);
        //TelinkMeshApplication.getInstance().removeEventListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_device:
                for (DeviceInfo deviceInfo : devices) {

                }
                mDeviceAdapter.setAll(!mDeviceAdapter.allSelected());
                break;

            case R.id.cb_group:
//                mGroupAdapter.setAll(!mGroupAdapter.allSelected());
                break;
        }
    }

    @Override
    public void onStatusChanged(BaseSelectableListAdapter adapter) {
        if (adapter == mDeviceAdapter) {
            showWaitingDialog("setting");
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // on grouping timeout
                    dismissWaitingDialog();
                }
            }, 500);
            cb_device.setChecked(mDeviceAdapter.allSelected());
            TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(this);
        }
    }

}
