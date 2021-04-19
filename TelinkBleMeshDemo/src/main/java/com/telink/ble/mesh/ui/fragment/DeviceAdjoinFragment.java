/********************************************************************************************************
 * @file     DeviceGroupFragment.java 
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
package com.telink.ble.mesh.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.core.message.MeshMessage;
import com.telink.ble.mesh.core.message.MeshSigModel;
import com.telink.ble.mesh.core.message.NotificationMessage;
import com.telink.ble.mesh.core.message.config.ConfigStatus;
import com.telink.ble.mesh.core.message.config.ModelSubscriptionSetMessage;
import com.telink.ble.mesh.core.message.config.ModelSubscriptionStatusMessage;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.MeshEvent;
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent;
import com.telink.ble.mesh.model.DeviceInfo;
import com.telink.ble.mesh.model.GroupInfo;
import com.telink.ble.mesh.model.NodeStatusChangedEvent;
import com.telink.ble.mesh.ui.TransMeshMessage;
import com.telink.ble.mesh.ui.adapter.BaseRecyclerViewAdapter;
import com.telink.ble.mesh.ui.adapter.BaseSelectableListAdapter;
import com.telink.ble.mesh.ui.adapter.DeviceAdjoinSelectAdapter;
import com.telink.ble.mesh.ui.adapter.DeviceSelectAdapter;
import com.telink.ble.mesh.ui.adapter.GroupInfoAdapter;
import com.telink.ble.mesh.util.MeshLogger;

import java.util.List;

/**
 * Created by kee on 2018/10/10.
 */

public class DeviceAdjoinFragment extends BaseFragment {

    private DeviceAdjoinSelectAdapter mAdapter;
    private List<DeviceInfo> mDevices;
    private Handler delayHandler = new Handler();
    private DeviceInfo deviceInfo;
    int address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_select, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        address = getArguments().getInt("address");
        deviceInfo = TelinkMeshApplication.getInstance().getMeshInfo().getDeviceByMeshAddress(address);
        mDevices = TelinkMeshApplication.getInstance().getMeshInfo().nodes;

        RecyclerView rv_device_select = view.findViewById(R.id.rv_device_select);
        mAdapter = new DeviceAdjoinSelectAdapter(getActivity(), mDevices, deviceInfo);

        rv_device_select.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_device_select.setAdapter(mAdapter);

        mAdapter.setStatusChangedListener(new BaseSelectableListAdapter.SelectStatusChangedListener() {
            @Override
            public void onStatusChanged(BaseSelectableListAdapter adapter) {
                showWaitingDialog("Setting");
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // on grouping timeout
                        dismissWaitingDialog();
                    }
                }, 500);
                TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(getActivity());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
