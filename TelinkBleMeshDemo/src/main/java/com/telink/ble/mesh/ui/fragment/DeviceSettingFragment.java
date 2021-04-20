/********************************************************************************************************
 * @file DeviceSettingFragment.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2010
 *
 * @par Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.core.message.MeshSigModel;
import com.telink.ble.mesh.core.message.config.ConfigStatus;
import com.telink.ble.mesh.core.message.config.ModelPublicationSetMessage;
import com.telink.ble.mesh.core.message.config.ModelPublicationStatusMessage;
import com.telink.ble.mesh.core.message.config.NodeResetMessage;
import com.telink.ble.mesh.core.message.config.NodeResetStatusMessage;
import com.telink.ble.mesh.core.message.lighting.CtlTemperatureSetMessage;
import com.telink.ble.mesh.core.message.lighting.LightnessSetMessage;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.entity.ModelPublication;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.MeshEvent;
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent;
import com.telink.ble.mesh.model.AppSettings;
import com.telink.ble.mesh.model.DeviceInfo;
import com.telink.ble.mesh.model.MeshInfo;
import com.telink.ble.mesh.model.PublishModel;
import com.telink.ble.mesh.model.UnitConvert;
import com.telink.ble.mesh.ui.CompositionDataActivity;
import com.telink.ble.mesh.ui.DeviceOtaActivity;
import com.telink.ble.mesh.ui.ModelSettingActivity;
import com.telink.ble.mesh.ui.NodeNetKeyActivity;
import com.telink.ble.mesh.ui.SchedulerListActivity;
import com.telink.ble.mesh.ui.SetDelayDialogActivity;
import com.telink.ble.mesh.ui.SubnetBridgeActivity;
import com.telink.ble.mesh.ui.TransMeshMessage;
import com.telink.ble.mesh.util.Arrays;
import com.telink.ble.mesh.util.MeshLogger;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

/**
 * device settings
 * Created by kee on 2018/10/10.
 */

public class DeviceSettingFragment extends BaseFragment implements View.OnClickListener, EventListener<String> {

    private AlertDialog confirmDialog;
    private boolean kickDirect;
    private DeviceInfo deviceInfo;
    private Handler delayHandler = new Handler();
    private CheckBox cb_pub, cb_relay;
    private PublishModel pubModel;
    private TextView tv_pub;
    private CardView cv_delay, cv_lum;
    private TextView tv_delay_value;
    private TextView tv_lum_value;
    private ImageView iv_delay, iv_lum;
    private SeekBar sb_lum, sb_delay;
    private static final int PUB_INTERVAL = 20 * 1000;
    private long preTime;
    private static final int DELAY_TIME = 320;
    private static final int PUB_ADDRESS = 0xFFFF;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_setting, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int address = getArguments().getInt("address");
        tv_pub = view.findViewById(R.id.tv_pub);
        deviceInfo = TelinkMeshApplication.getInstance().getMeshInfo().getDeviceByMeshAddress(address);
        initPubModel();
        TextView tv_mac = view.findViewById(R.id.tv_mac);
        tv_mac.setText("UUID: " + Arrays.bytesToHexString(deviceInfo.deviceUUID));
        view.findViewById(R.id.view_scheduler).setOnClickListener(this);
        cv_delay = view.findViewById(R.id.view_delay);
        cv_lum = view.findViewById(R.id.view_lum);
        tv_delay_value = view.findViewById(R.id.tv_delay_value);
        tv_delay_value.setText(String.valueOf(deviceInfo.delay));
        tv_lum_value = view.findViewById(R.id.tv_lum_value);
        tv_lum_value.setText(String.valueOf(deviceInfo.light));
        iv_delay = view.findViewById(R.id.imageView_delay);
        iv_lum = view.findViewById(R.id.imageView_lum);
        iv_delay.setOnClickListener(this);
        iv_lum.setOnClickListener(this);
        sb_delay = view.findViewById(R.id.seekBar_delay);
        sb_delay.setProgress(deviceInfo.delay);
        sb_lum = view.findViewById(R.id.seekBar_lum);
        sb_lum.setProgress(deviceInfo.light);
        sb_delay.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sb_lum.setOnSeekBarChangeListener(onSeekBarChangeListener);
        cb_pub = view.findViewById(R.id.cb_pub);
        cb_relay = view.findViewById(R.id.cb_relay);
        cb_pub.setChecked(deviceInfo.isPubSet());
        cb_relay.setChecked(deviceInfo.isRelayEnable());
        view.findViewById(R.id.view_cps).setOnClickListener(this);
        view.findViewById(R.id.view_pub).setOnClickListener(this);
        view.findViewById(R.id.view_relay).setOnClickListener(this);
        view.findViewById(R.id.view_sub).setOnClickListener(this);
        view.findViewById(R.id.view_ota).setOnClickListener(this);
        view.findViewById(R.id.view_net_key).setOnClickListener(this);
        view.findViewById(R.id.btn_kick).setOnClickListener(this);

        if (AppSettings.DRAFT_FEATURES_ENABLE) {
            view.findViewById(R.id.view_subnet).setOnClickListener(this);
        } else {
            view.findViewById(R.id.view_subnet).setVisibility(View.GONE);
        }


        TelinkMeshApplication.getInstance().addEventListener(ModelPublicationStatusMessage.class.getName(), this);
        TelinkMeshApplication.getInstance().addEventListener(NodeResetStatusMessage.class.getName(), this);
        TelinkMeshApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
    }

    /**
     * check available publish model
     * check device has ctl model, if not -> check hsl, if not -> check onoff
     */
    public void initPubModel() {

        final int pubInterval = PUB_INTERVAL;
        final int address = PUB_ADDRESS;
//        int address = TelinkMeshApplication.getInstance().getMeshInfo().localAddress;
        int modelId = MeshSigModel.SIG_MD_LIGHT_CTL_S.modelId;
        int pubEleAdr = deviceInfo.getTargetEleAdr(modelId);
        String desc = null;

        if (pubEleAdr != -1) {
            pubModel = new PublishModel(pubEleAdr, modelId, address, pubInterval);
            desc = "CTL";
            tv_pub.setText(getString(R.string.publication_setting, String.format("%04X", pubEleAdr), desc));
            return;
        }

        modelId = MeshSigModel.SIG_MD_LIGHT_HSL_S.modelId;
        pubEleAdr = deviceInfo.getTargetEleAdr(modelId);
        if (pubEleAdr != -1) {
            pubModel = new PublishModel(pubEleAdr, modelId, address, pubInterval);
            desc = "HSL";
            tv_pub.setText(getString(R.string.publication_setting, String.format("%04X", pubEleAdr), desc));
            return;
        }

        modelId = MeshSigModel.SIG_MD_LIGHTNESS_S.modelId;
        pubEleAdr = deviceInfo.getTargetEleAdr(modelId);
        if (pubEleAdr != -1) {
            pubModel = new PublishModel(pubEleAdr, modelId, address, pubInterval);
            desc = "LIGHTNESS";
            tv_pub.setText(getString(R.string.publication_setting, String.format("%04X", pubEleAdr), desc));
            return;
        }

        modelId = MeshSigModel.SIG_MD_G_ONOFF_S.modelId;
        pubEleAdr = deviceInfo.getTargetEleAdr(modelId);
        if (pubEleAdr != -1) {
            pubModel = new PublishModel(pubEleAdr, modelId, address, pubInterval);
            desc = "ONOFF";
            tv_pub.setText(getString(R.string.publication_setting, String.format("%04X", pubEleAdr), desc));
            return;
        }

        tv_pub.setText("Publication (no available model)");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TelinkMeshApplication.getInstance().removeEventListener(this);
        delayHandler.removeCallbacksAndMessages(null);
    }

    private void showKickConfirmDialog() {
        if (confirmDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle("Warn");
            builder.setMessage("Confirm to remove device?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    kickOut();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmDialog = builder.create();
        }
        confirmDialog.show();
    }

    private void kickOut() {
        // send reset message
        MeshService.getInstance().sendMeshMessage(new NodeResetMessage(deviceInfo.meshAddress));
        TransMeshMessage.getInstance().DeviceReSet(deviceInfo.meshAddress);
        kickDirect = deviceInfo.meshAddress == MeshService.getInstance().getDirectConnectedNodeAddress();
        showWaitingDialog("kick out processing");
        if (!kickDirect) {
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onKickOutFinish();
                }
            }, 3 * 1000);
        } else {
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onKickOutFinish();
                }
            }, 10 * 1000);
        }
    }

    private void onKickOutFinish() {
        delayHandler.removeCallbacksAndMessages(null);
        MeshService.getInstance().removeDevice(deviceInfo.meshAddress);
        TelinkMeshApplication.getInstance().getMeshInfo().removeDeviceByMeshAddress(deviceInfo.meshAddress);
        TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(getActivity().getApplicationContext());
        dismissWaitingDialog();
        getActivity().finish();
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(MeshEvent.EVENT_TYPE_DISCONNECTED)) {
            if (kickDirect) {
                onKickOutFinish();
            } else {
//                refreshUI();
            }
        } else if (event.getType().equals(ModelPublicationStatusMessage.class.getName())) {
            final ModelPublicationStatusMessage statusMessage = (ModelPublicationStatusMessage) ((StatusNotificationEvent) event).getNotificationMessage().getStatusMessage();
            if (statusMessage.getStatus() == ConfigStatus.SUCCESS.code) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        delayHandler.removeCallbacks(cmdTimeoutTask);
                        dismissWaitingDialog();
                        boolean settle = statusMessage.getPublication().publishAddress != 0;
                        cb_pub.setChecked(settle);
                        deviceInfo.setPublishModel(settle ? pubModel : null);
                        TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(getActivity());
                    }
                });

            } else {
                MeshLogger.log("publication err: " + statusMessage.getStatus());
            }


        } else if (event.getType().equals(NodeResetStatusMessage.class.getName())) {
            if (!kickDirect) {
                onKickOutFinish();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_ota:
                Intent otaIntent = new Intent(getActivity(), DeviceOtaActivity.class);
                otaIntent.putExtra("meshAddress", deviceInfo.meshAddress);
                startActivity(otaIntent);
                getActivity().finish();
                break;

            case R.id.btn_kick:
                showKickConfirmDialog();
                break;

            case R.id.view_cps:
                Intent cpsIntent = new Intent(getActivity(), CompositionDataActivity.class);
                cpsIntent.putExtra("meshAddress", deviceInfo.meshAddress);
                startActivity(cpsIntent);
                break;

            case R.id.view_scheduler:
                if (deviceInfo.getTargetEleAdr(MeshSigModel.SIG_MD_SCHED_S.modelId) == -1) {
                    toastMsg("scheduler model not found");
                    return;
                }
                Intent schedulerIntent = new Intent(getActivity(), SchedulerListActivity.class);
                schedulerIntent.putExtra("address", deviceInfo.meshAddress);
                startActivity(schedulerIntent);
                break;

            case R.id.imageView_delay:
                if (deviceInfo.getOnOff() == -1) return;
                SetDelayDialogActivity setDelayDialogActivity = new SetDelayDialogActivity(getActivity());
                setDelayDialogActivity.setOnCancelListener(new SetDelayDialogActivity.IOnCancelListener() {
                    @Override
                    public void OnCancel(SetDelayDialogActivity dialog) {
                    }
                }).setOnConfirmListener(new SetDelayDialogActivity.IOnConfirmListener() {
                    @Override
                    public void OnConfirm(SetDelayDialogActivity dialog) {
                        //Toast.makeText(getActivity(), "你输入的是: " + dialog.textInputEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                        String textInput = dialog.editText.getText().toString();
                        if (textInput.length() > 0) {
                            int delay = Integer.parseInt(textInput);
                            if (delay >= 0 && delay <= 256) {
                                showWaitingDialog("setting");
                                delayHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // on grouping timeout
                                        dismissWaitingDialog();
                                    }
                                }, 500);
                                deviceInfo.delay = delay;
                                tv_delay_value.setText(textInput);
                                sb_delay.setProgress(delay);
                                TransMeshMessage.getInstance().SetDeviceDelay(deviceInfo.meshAddress, delay);
                                TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(getActivity());
                            }
                        }
                    }
                }).show();
                break;
            case R.id.imageView_lum:
                if (deviceInfo.getOnOff() == -1) return;
                SetDelayDialogActivity setLumDialogActivity = new SetDelayDialogActivity(getActivity());
                setLumDialogActivity.setOnCancelListener(new SetDelayDialogActivity.IOnCancelListener() {
                    @Override
                    public void OnCancel(SetDelayDialogActivity dialog) {
                    }
                }).setOnConfirmListener(new SetDelayDialogActivity.IOnConfirmListener() {
                    @Override
                    public void OnConfirm(SetDelayDialogActivity dialog) {
                        //Toast.makeText(getActivity(), "你输入的是: " + dialog.textInputEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                        String textInput = dialog.editText.getText().toString();
                        if (textInput.length() > 0) {
                            int lum = Integer.parseInt(textInput);
                            if (lum >= 0 && lum <= 100) {
                                showWaitingDialog("setting");
                                delayHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // on grouping timeout
                                        dismissWaitingDialog();
                                    }
                                }, 500);
                                deviceInfo.light = lum;
                                tv_lum_value.setText(textInput);
                                sb_lum.setProgress(lum);
                                TransMeshMessage.getInstance().SetDeviceLum(deviceInfo.meshAddress, lum);
                                TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(getActivity());
                            }
                        }
                    }
                }).show();
                break;

            case R.id.view_sub:
                Intent intent = new Intent(getActivity(), ModelSettingActivity.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
                break;

            case R.id.view_pub:
                if (deviceInfo.getOnOff() == -1 || pubModel == null) return;

                int pubAdr = 0;
                if (cb_pub.isChecked()) {
                    MeshLogger.log("cancel publication");
                } else {
                    MeshLogger.log("set publication");
                    pubAdr = pubModel.address;
                }
                int appKeyIndex = TelinkMeshApplication.getInstance().getMeshInfo().getDefaultAppKeyIndex();
                int modelId = pubModel.modelId;
                int eleAdr = deviceInfo.getTargetEleAdr(modelId);
                ModelPublication modelPublication = ModelPublication.createDefault(eleAdr, pubAdr, appKeyIndex, pubModel.period, pubModel.modelId, true);
                ModelPublicationSetMessage publicationSetMessage = new ModelPublicationSetMessage(deviceInfo.meshAddress, modelPublication);
                boolean result = MeshService.getInstance().sendMeshMessage(publicationSetMessage);
                if (result) {
                    showWaitingDialog("processing...");
                    delayHandler.removeCallbacks(cmdTimeoutTask);
                    delayHandler.postDelayed(cmdTimeoutTask, 5 * 1000);
                }
                break;

            case R.id.view_relay:
//                boolean relayResult = MeshService.getInstance().cfgCmdRelaySet(deviceInfo.meshAddress, deviceInfo.isRelayEnable() ? 0 : 1);
                // todo mesh interface
                /*boolean relayResult = MeshService.getInstance().setRelay(deviceInfo.meshAddress, deviceInfo.isRelayEnable() ? 0 : 1, null);
                if (relayResult) {
                    showWaitingDialog("processing...");
                    delayHandler.removeCallbacks(cmdTimeoutTask);
                    delayHandler.postDelayed(cmdTimeoutTask, 5 * 1000);
                }*/
                break;


            case R.id.view_net_key:
                startActivity(new Intent(getActivity(), NodeNetKeyActivity.class)
                        .putExtra("meshAddress", deviceInfo.meshAddress));
                break;

            case R.id.view_subnet:
                startActivity(new Intent(getActivity(), SubnetBridgeActivity.class)
                        .putExtra("meshAddress", deviceInfo.meshAddress));
                break;
        }
    }

    private Runnable cmdTimeoutTask = new Runnable() {
        @Override
        public void run() {
            toastMsg("pub timeout");
            dismissWaitingDialog();
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == sb_delay) {
                tv_delay_value.setText(String.valueOf(progress));
            }
            else if (seekBar == sb_lum) {
                tv_lum_value.setText(String.valueOf(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            onProgressUpdate(seekBar, seekBar.getProgress(), true);
        }

        void onProgressUpdate(SeekBar seekBar, int progress, boolean immediate) {

            if (seekBar == sb_lum || seekBar == sb_delay) {

                long currentTime = System.currentTimeMillis();
                if (seekBar == sb_lum) {
                    deviceInfo.light = progress;
                    tv_lum_value.setText(String.valueOf(progress));
                    progress = Math.max(1, progress);
                    if ((currentTime - preTime) >= DELAY_TIME || immediate) {
                        preTime = currentTime;
                        TransMeshMessage.getInstance().SetDeviceLum(deviceInfo.meshAddress, progress);
                    }
                } else if (seekBar == sb_delay) {
                    deviceInfo.delay = progress;
                    tv_delay_value.setText(String.valueOf(progress));
                    if ((currentTime - preTime) >= DELAY_TIME || immediate) {
                        preTime = currentTime;
                        TransMeshMessage.getInstance().SetDeviceDelay(deviceInfo.meshAddress, progress);
                    }
                }
            }
        }
    };
}
