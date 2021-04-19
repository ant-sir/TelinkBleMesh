/********************************************************************************************************
 * @file     GroupFragment.java 
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.telink.ble.mesh.SharedPreferenceHelper;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.event.AutoConnectEvent;
import com.telink.ble.mesh.foundation.event.MeshEvent;
import com.telink.ble.mesh.model.GroupInfo;
import com.telink.ble.mesh.model.GroupStatusChangedEvent;
import com.telink.ble.mesh.model.NodeStatusChangedEvent;
import com.telink.ble.mesh.ui.AddGroupDialogActivity;
import com.telink.ble.mesh.ui.DeviceAutoProvisionActivity;
import com.telink.ble.mesh.ui.DeviceProvisionActivity;
import com.telink.ble.mesh.ui.FastProvisionActivity;
import com.telink.ble.mesh.ui.GroupDeviceSelectActivity;
import com.telink.ble.mesh.ui.GroupSettingActivity;
import com.telink.ble.mesh.ui.RemoteProvisionActivity;
import com.telink.ble.mesh.ui.SceneSettingActivity;
import com.telink.ble.mesh.ui.adapter.BaseRecyclerViewAdapter;
import com.telink.ble.mesh.ui.adapter.GroupAdapter;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Group Control
 * Created by kee on 2017/8/18.
 */

public class GroupFragment extends BaseFragment implements EventListener<String> {
    private List<GroupInfo> groups;
    private GroupAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.title_bar);
        toolbar.setNavigationIcon(null);
        toolbar.inflateMenu(R.menu.device_tab);

        if (TelinkMeshApplication.getInstance().getMeshInfo().companyName == null) {
            setTitle(view, "Group");
        }
        else {
            setTitle(view, TelinkMeshApplication.getInstance().getMeshInfo().companyName);
        }
        final RecyclerView rv_group = view.findViewById(R.id.rv_group);
        groups = TelinkMeshApplication.getInstance().getMeshInfo().groups;

        mAdapter = new GroupAdapter(getActivity(), groups);

        rv_group.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_group.setAdapter(mAdapter);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_add) {
//                    startActivity(new Intent(getActivity(), DeviceProvisionActivity.class));
                    AddGroupDialogActivity addGroupDialogActivity = new AddGroupDialogActivity(getActivity());
                    addGroupDialogActivity.setOnCancelListener(new AddGroupDialogActivity.IOnCancelListener() {
                        @Override
                        public void OnCancel(AddGroupDialogActivity dialog) {
                        }
                    }).setOnConfirmListener(new AddGroupDialogActivity.IOnConfirmListener() {
                        @Override
                        public void OnConfirm(AddGroupDialogActivity dialog) {
                            //Toast.makeText(getActivity(), "你输入的是: " + dialog.textInputEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                            GroupInfo groupInfo = new GroupInfo();
                            groupInfo.name = dialog.textInputEditText.getText().toString();
                            groupInfo.address = TelinkMeshApplication.getInstance().getMeshInfo().getGroupAddress();
                            TelinkMeshApplication.getInstance().getMeshInfo().groups.add(groupInfo);
                            mAdapter.notifyDataSetChanged();
                            TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(getActivity());
                        }
                    }).show();
                    return true;
                }
                return false;
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), GroupDeviceSelectActivity.class);
                intent.putExtra("group", groups.get(position));
                startActivity(intent);
            }
        });

        mAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                Intent intent = new Intent(getActivity(), GroupSettingActivity.class);
                intent.putExtra("group", groups.get(position));
                startActivity(intent);
                return false;
            }
        });

        TelinkMeshApplication.getInstance().addEventListener(GroupStatusChangedEvent.EVENT_TYPE_GROUP_STATUS_CHANGED, this);
    }

    @Override
    public void performed(Event<String> event) {
        String eventType = event.getType();
        if (eventType.equals(GroupStatusChangedEvent.EVENT_TYPE_GROUP_STATUS_CHANGED)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
