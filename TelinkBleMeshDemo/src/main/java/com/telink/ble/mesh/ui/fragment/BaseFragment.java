/********************************************************************************************************
 * @file     BaseFragment.java 
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
import android.view.View;
import android.widget.TextView;

import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.ui.BaseActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

/**
 * Created by kee on 2017/9/25.
 */

public class BaseFragment extends Fragment {
    private AlertDialog.Builder confirmDialogBuilder;
    public void toastMsg(CharSequence s) {
        ((BaseActivity) getActivity()).toastMsg(s);
    }

    protected void showWaitingDialog(String tip) {
        ((BaseActivity) getActivity()).showWaitingDialog(tip);
    }

    protected void dismissWaitingDialog() {
        ((BaseActivity) getActivity()).dismissWaitingDialog();
    }

    protected void setTitle(View parent, String title) {
        TextView tv_title = parent.findViewById(R.id.tv_title);
        if (tv_title != null) {
            tv_title.setText(title);
        }
    }

    public void showConfirmDialog(String msg, boolean cancel, DialogInterface.OnClickListener confirmClick) {
        if (confirmDialogBuilder == null) {
            confirmDialogBuilder = new AlertDialog.Builder(getActivity());
            confirmDialogBuilder.setCancelable(true);
            confirmDialogBuilder.setTitle("Warning");
//            confirmDialogBuilder.setMessage(msg);
            confirmDialogBuilder.setPositiveButton("Confirm", confirmClick);

            if (cancel) {
                confirmDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
        confirmDialogBuilder.setMessage(msg);
        confirmDialogBuilder.show();
    }
}
