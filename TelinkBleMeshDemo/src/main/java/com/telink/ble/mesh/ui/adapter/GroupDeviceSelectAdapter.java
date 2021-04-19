/********************************************************************************************************
 * @file     DeviceSelectAdapter.java 
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
package com.telink.ble.mesh.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.core.message.MeshMessage;
import com.telink.ble.mesh.core.message.MeshSigModel;
import com.telink.ble.mesh.core.message.config.ModelSubscriptionSetMessage;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.model.DeviceInfo;
import com.telink.ble.mesh.ui.IconGenerator;
import com.telink.ble.mesh.ui.TransMeshMessage;
import com.telink.ble.mesh.util.MeshLogger;

import java.util.List;

/**
 * select device
 * Created by kee on 2017/8/18.
 */
public class GroupDeviceSelectAdapter extends BaseSelectableListAdapter<GroupDeviceSelectAdapter.ViewHolder> {

    private Context mContext;
    private List<DeviceInfo> mDevices;
    private int mGroupAddress;
    private MeshSigModel[] models = MeshSigModel.getDefaultSubList();
    private int modelIndex = 0;
    private int opGroupAdr;
    private int opType;

    public GroupDeviceSelectAdapter(Context context, List<DeviceInfo> devices, int groupAddress) {
        this.mContext = context;
        this.mDevices = devices;
        this.mGroupAddress = groupAddress;
    }

    public boolean allSelected() {
        for (DeviceInfo deviceInfo : mDevices) {
            if (!deviceInfo.selected) {
                return false;
            }
        }
        return true;
    }

    public void setAll(boolean selected) {
        for (DeviceInfo deviceInfo : mDevices) {
            deviceInfo.selected = selected;
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_device_select, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        holder.iv_device = itemView.findViewById(R.id.iv_device);
        holder.tv_device_info = itemView.findViewById(R.id.tv_device_info);
        holder.cb_device = itemView.findViewById(R.id.cb_device);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mDevices == null ? 0 : mDevices.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        DeviceInfo deviceInfo = mDevices.get(position);
        holder.iv_device.setImageResource(IconGenerator.getIcon(0, deviceInfo.getOnOff()));
        holder.tv_device_info.setText(mContext.getString(R.string.device_state_desc,
                String.format("%04X", deviceInfo.meshAddress),
                deviceInfo.getOnOffDesc()));

        holder.cb_device.setTag(position);

        if (deviceInfo.getOnOff() != -1 /*&& deviceInfo.getTargetEleAdr(MeshSigModel.SIG_MD_SCENE_S.modelId) != -1*/) {
            holder.cb_device.setChecked(deviceInfo.selected);
            holder.cb_device.setEnabled(true);
            holder.cb_device.setOnCheckedChangeListener(this.checkedChangeListener);
        } else {
            holder.cb_device.setChecked(false);
            holder.cb_device.setEnabled(false);
        }
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            DeviceInfo deviceInfo = mDevices.get(position);

            if (deviceInfo.getOnOff() != -1)
                setDeviceGroupInfo(deviceInfo, mGroupAddress, (isChecked == true) ? 0 : 1);
            deviceInfo.selected = isChecked;
            if (isChecked) {
                deviceInfo.subList.add(mGroupAddress);
                TransMeshMessage.getInstance().SetGroup(deviceInfo.meshAddress, mGroupAddress, 1);
            }
            else {
                deviceInfo.subList.remove((Integer)mGroupAddress);
                TransMeshMessage.getInstance().SetGroup(deviceInfo.meshAddress, mGroupAddress, 0);
            }
            if (statusChangedListener != null) {
                statusChangedListener.onStatusChanged(GroupDeviceSelectAdapter.this);
            }
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_device;
        TextView tv_device_info;
        CheckBox cb_device;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * @param groupAddress
     * @param type         0 add, 1 delete
     */
    public void setDeviceGroupInfo(DeviceInfo deviceInfo, int groupAddress, int type) {
        opGroupAdr = groupAddress;
        modelIndex = 0;
        this.opType = type;
        setNextModel(deviceInfo);
    }

    private void setNextModel(DeviceInfo deviceInfo) {
        if (modelIndex > models.length - 1) {
            if (opType == 0) {
                deviceInfo.subList.add(opGroupAdr);
                TransMeshMessage.getInstance().SetGroup(deviceInfo.meshAddress, opGroupAdr, 1);
                MeshLogger.log(String.valueOf(deviceInfo.meshAddress) + " add " + String.valueOf(opGroupAdr), "zyl", MeshLogger.LEVEL_INFO);
            } else {
                deviceInfo.subList.remove((Integer) opGroupAdr);
                TransMeshMessage.getInstance().SetGroup(deviceInfo.meshAddress, opGroupAdr, 0);
                MeshLogger.log(String.valueOf(deviceInfo.meshAddress) + " remove " + String.valueOf(opGroupAdr), "zyl", MeshLogger.LEVEL_INFO);
            }
        } else {
            final int eleAdr = deviceInfo.getTargetEleAdr(models[modelIndex].modelId);
            if (eleAdr == -1) {
                modelIndex++;
                setNextModel(deviceInfo);
                return;
            }
            MeshMessage groupingMessage = ModelSubscriptionSetMessage.getSimple(deviceInfo.meshAddress, opType, eleAdr, opGroupAdr, models[modelIndex].modelId, true);

            if (!MeshService.getInstance().sendMeshMessage(groupingMessage)) {
                MeshLogger.log(String.valueOf(deviceInfo.meshAddress) + " send group message " + String.valueOf(opGroupAdr), "zyl", MeshLogger.LEVEL_INFO);
            }
        }

    }
}
