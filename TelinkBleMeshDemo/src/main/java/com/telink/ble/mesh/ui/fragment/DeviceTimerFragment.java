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

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.telink.ble.mesh.model.TimerInfo;
import com.telink.ble.mesh.ui.AddTimerDialogActivity;
import com.telink.ble.mesh.ui.TimerActivity;
import com.telink.ble.mesh.ui.TransMeshMessage;
import com.telink.ble.mesh.ui.adapter.BaseRecyclerViewAdapter;
import com.telink.ble.mesh.ui.adapter.GroupInfoAdapter;
import com.telink.ble.mesh.ui.adapter.TimerAdapter;
import com.telink.ble.mesh.util.MeshLogger;

import java.util.List;

/**
 * Created by kee on 2018/10/10.
 */

public class DeviceTimerFragment extends BaseFragment{

    private TimerAdapter mAdapter;
    private List<TimerInfo> allTimers;
    private DeviceInfo deviceInfo;
    private Handler delayHandler = new Handler();
    int address;
    int m_position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_timer, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        address = getArguments().getInt("address");
        deviceInfo = TelinkMeshApplication.getInstance().getMeshInfo().getDeviceByMeshAddress(address);
        allTimers = deviceInfo.timers;

        RecyclerView rv_timers = view.findViewById(R.id.rv_timer);
        FloatingActionButton fa_button = view.findViewById(R.id.fa_button);
        mAdapter = new TimerAdapter(getActivity(), allTimers);

        rv_timers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_timers.setAdapter(mAdapter);

        mAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                if (deviceInfo.getOnOff() == -1) {
                    showConfirmDialog("please confirm device online.", false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    return false;
                }

                m_position = position;
                showConfirmDialog("delete this timer ?", true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deviceInfo.timers.remove(m_position);
                        showWaitingDialog("setting");
                        delayHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // on grouping timeout
                                dismissWaitingDialog();
                            }
                        }, 500);
                        TransMeshMessage.getInstance().DelDeviceTimer(deviceInfo.meshAddress, m_position);
                        mAdapter.notifyDataSetChanged();
                        TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(getActivity());
                    }
                });
                return false;
            }
        });
        fa_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceInfo.getOnOff() == -1) {
                    showConfirmDialog("please confirm device online.", false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    return;
                }
                AddTimerDialogActivity addTimerDialogActivity = new AddTimerDialogActivity(getActivity());
                addTimerDialogActivity.setOnCancelListener(new AddTimerDialogActivity.IOnCancelListener() {
                    @Override
                    public void OnCancel(AddTimerDialogActivity dialog) {
                    }
                }).setOnConfirmListener(new AddTimerDialogActivity.IOnConfirmListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void OnConfirm(AddTimerDialogActivity dialog) {
                        TimerInfo timerInfo = new TimerInfo();
                        int id = deviceInfo.getTimerId();
                        if (id == -1)
                        {
                            return;
                        }
                        showWaitingDialog("setting");
                        delayHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // on grouping timeout
                                dismissWaitingDialog();
                            }
                        }, 500);
                        timerInfo.id = id;
                        timerInfo.hour = dialog.timePicker.getHour();
                        timerInfo.mim = dialog.timePicker.getMinute();
                        deviceInfo.timers.add(timerInfo);
                        deviceInfo.sortTimers();
                        TransMeshMessage.getInstance().SetDeviceTimer(deviceInfo.meshAddress, timerInfo.id, timerInfo.hour, timerInfo.mim);
                        mAdapter.notifyDataSetChanged();
                        TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(getActivity());

                    }
                }).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
