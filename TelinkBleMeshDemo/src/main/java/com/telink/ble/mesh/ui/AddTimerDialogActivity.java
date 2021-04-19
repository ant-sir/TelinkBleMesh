package com.telink.ble.mesh.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.telink.ble.mesh.demo.R;

public class AddTimerDialogActivity extends Dialog implements View.OnClickListener {
    private TextView mCancel, mConfirm;
    public TimePicker timePicker;
    private IOnCancelListener onCancelListener;
    private IOnConfirmListener onConfirmListener;

    public AddTimerDialogActivity setOnCancelListener(IOnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public AddTimerDialogActivity setOnConfirmListener(IOnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        return this;
    }

    public AddTimerDialogActivity(@NonNull Context context) {
        super(context);
    }

    public AddTimerDialogActivity(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_dialog);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int)(size.x * 0.9);
        getWindow().setAttributes(p);
        mCancel = findViewById(R.id.add_timer_cancel);
        mConfirm = findViewById(R.id.add_timer_confirm);
        timePicker = findViewById(R.id.add_timer_input);
        timePicker.setIs24HourView(true);
        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_timer_cancel:
                if (onCancelListener != null) {
                    onCancelListener.OnCancel(this);
                }
                dismiss();
                break;
            case R.id.add_timer_confirm:
                if (onConfirmListener != null) {
                    onConfirmListener.OnConfirm(this);
                }
                dismiss();
                break;
        }
    }

    public interface IOnCancelListener {
        void OnCancel(AddTimerDialogActivity dialog);
    }

    public interface IOnConfirmListener {
        void OnConfirm(AddTimerDialogActivity dialog);
    }
}
