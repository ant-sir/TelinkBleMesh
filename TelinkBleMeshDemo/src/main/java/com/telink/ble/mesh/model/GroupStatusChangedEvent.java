/********************************************************************************************************
 * @file     NodeStatusChangedEvent.java 
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
package com.telink.ble.mesh.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.telink.ble.mesh.foundation.Event;

/**
 * Created by kee on 2019/9/18.
 */

public class GroupStatusChangedEvent extends Event<String> implements Parcelable {
    public static final String EVENT_TYPE_GROUP_STATUS_CHANGED = "com.telink.ble.mesh.EVENT_TYPE_GROUP_STATUS_CHANGED";
    private GroupInfo groupInfo;

    public GroupStatusChangedEvent(Object sender, String type, GroupInfo groupInfo) {
        super(sender, type);
        this.groupInfo = groupInfo;
    }

    protected GroupStatusChangedEvent(Parcel in) {
    }

    public static final Creator<GroupStatusChangedEvent> CREATOR = new Creator<GroupStatusChangedEvent>() {
        @Override
        public GroupStatusChangedEvent createFromParcel(Parcel in) {
            return new GroupStatusChangedEvent(in);
        }

        @Override
        public GroupStatusChangedEvent[] newArray(int size) {
            return new GroupStatusChangedEvent[size];
        }
    };

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
