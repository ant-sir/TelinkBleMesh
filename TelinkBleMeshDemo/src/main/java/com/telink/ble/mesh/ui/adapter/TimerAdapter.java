/********************************************************************************************************
 * @file     GroupAdapter.java 
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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.core.message.generic.OnOffSetMessage;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.model.AppSettings;
import com.telink.ble.mesh.model.GroupInfo;
import com.telink.ble.mesh.model.MeshInfo;
import com.telink.ble.mesh.model.TimerInfo;
import com.telink.ble.mesh.ui.TransMeshMessage;
import com.telink.ble.mesh.util.MeshLogger;

import java.util.List;

/**
 * groups in GroupFragment
 * Created by kee on 2017/8/21.
 */

public class TimerAdapter extends BaseRecyclerViewAdapter<TimerAdapter.ViewHolder> {

    private Context mContext;
    private List<TimerInfo> mTimers;

    public TimerAdapter(Context context, List<TimerInfo> timers) {
        this.mContext = context;
        this.mTimers = timers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_timer, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        holder.tv_timer_id = itemView.findViewById(R.id.tv_timer_id);
        holder.tv_timer_desc = itemView.findViewById(R.id.tv_timer_desc);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mTimers == null ? 0 : mTimers.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        TimerInfo timer = mTimers.get(position);
        holder.tv_timer_id.setText(String.valueOf(timer.id));
        holder.tv_timer_desc.setText(String.valueOf(timer.hour) + ":" + String.valueOf(timer.mim) + ":" + String.valueOf(timer.sec));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_timer_id, tv_timer_desc;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
