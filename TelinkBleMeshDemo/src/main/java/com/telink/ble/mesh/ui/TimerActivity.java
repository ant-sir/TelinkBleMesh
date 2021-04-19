package com.telink.ble.mesh.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telink.ble.mesh.TelinkMeshApplication;
import com.telink.ble.mesh.core.message.MeshMessage;
import com.telink.ble.mesh.demo.R;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.model.GroupInfo;
import com.telink.ble.mesh.model.TimerInfo;
import com.telink.ble.mesh.ui.BaseActivity;
import com.telink.ble.mesh.ui.adapter.BaseRecyclerViewAdapter;
import com.telink.ble.mesh.ui.adapter.TimerAdapter;
import com.telink.ble.mesh.util.Arrays;

import java.util.Collections;
import java.util.List;

public class TimerActivity extends BaseActivity {
    private List<TimerInfo> timers;
    private TimerAdapter mAdapter;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        enableBackNav(true);
        setTitle("Timer List");
        Toolbar toolbar = findViewById(R.id.title_bar);
        toolbar.inflateMenu(R.menu.device_tab);
        RecyclerView rv_timer = findViewById(R.id.rv_timer);
        timers = TelinkMeshApplication.getInstance().getMeshInfo().globalTimers;
        mAdapter = new TimerAdapter(this, timers);
        rv_timer.setLayoutManager(new LinearLayoutManager(this));
        rv_timer.setAdapter(mAdapter);

        mAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(final int position) {
                mPosition = position;
                showConfirmDialog("Will delete this timer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TimerInfo timer = TelinkMeshApplication.getInstance().getMeshInfo().globalTimers.get(mPosition);
                        TransMeshMessage.getInstance().DelTimer(0xFFFF, timer.id);
                        TelinkMeshApplication.getInstance().getMeshInfo().removeTimerByPosition(mPosition);
                        mAdapter.notifyDataSetChanged();
                        TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(TimerActivity.this);
                    }
                });
                return false;
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_add) {
                    AddTimerDialogActivity addTimerDialogActivity = new AddTimerDialogActivity(TimerActivity.this);
                    addTimerDialogActivity.setOnCancelListener(new AddTimerDialogActivity.IOnCancelListener() {
                        @Override
                        public void OnCancel(AddTimerDialogActivity dialog) {
                        }
                    }).setOnConfirmListener(new AddTimerDialogActivity.IOnConfirmListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void OnConfirm(AddTimerDialogActivity dialog) {
                            TimerInfo timerInfo = new TimerInfo();
                            int id = TelinkMeshApplication.getInstance().getMeshInfo().getTimerId();
                            if (id == -1)
                            {
                                return;
                            }
                            timerInfo.id = id;
                            timerInfo.hour = dialog.timePicker.getHour();
                            timerInfo.mim = dialog.timePicker.getMinute();
                            TelinkMeshApplication.getInstance().getMeshInfo().globalTimers.add(timerInfo);
                            TelinkMeshApplication.getInstance().getMeshInfo().sortTimers();
                            TransMeshMessage.getInstance().SetTimer(0xFFFF, timerInfo.id, timerInfo.hour, timerInfo.mim);
                            mAdapter.notifyDataSetChanged();
                            TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(TimerActivity.this);
                        }
                    }).show();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
