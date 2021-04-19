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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.model.DeviceInfo;
import com.telink.ble.mesh.ui.IconGenerator;
import com.telink.ble.mesh.ui.TransMeshMessage;

import java.util.List;

/**
 * select device
 * Created by kee on 2017/8/18.
 */
public class DeviceAdjoinSelectAdapter extends BaseSelectableListAdapter<DeviceAdjoinSelectAdapter.ViewHolder> {

    private Context mContext;
    private List<DeviceInfo> mDevices;
    private DeviceInfo mCurrentDevice;


    public DeviceAdjoinSelectAdapter(Context context, List<DeviceInfo> devices, DeviceInfo device) {
        this.mContext = context;
        this.mDevices = devices;
        this.mCurrentDevice = device;
    }

    public boolean allSelected() {
        return true;
    }

    public void setAll(boolean selected) {
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
            holder.cb_device.setChecked(mCurrentDevice.adjoins.contains(deviceInfo));
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
            if (isChecked)
            {
                mCurrentDevice.adjoins.add(mDevices.get(position));
                TransMeshMessage.getInstance().SetAdjoin(mCurrentDevice.meshAddress, mDevices.get(position).meshAddress, 1);
            }
            else
            {
                mCurrentDevice.adjoins.remove(mDevices.get(position));
                TransMeshMessage.getInstance().SetAdjoin(mCurrentDevice.meshAddress, mDevices.get(position).meshAddress, 0);
            }
            if (statusChangedListener != null) {
                statusChangedListener.onStatusChanged(DeviceAdjoinSelectAdapter.this);
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
}
