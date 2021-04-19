package com.telink.ble.mesh.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.telink.ble.mesh.demo.R;

public class AddGroupDialogActivity extends Dialog implements View.OnClickListener {
    private TextView mCancel, mConfirm, mTitle;
    public TextInputEditText textInputEditText;
    private IOnCancelListener onCancelListener;
    private IOnConfirmListener onConfirmListener;

    public void setTitleName(String title) {
        mTitle.setText(title);
    }
    public void setInputHint(String hint) {
        textInputEditText.setHint(hint);
    }

    public AddGroupDialogActivity setOnCancelListener(IOnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public AddGroupDialogActivity setOnConfirmListener(IOnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        return this;
    }

    public AddGroupDialogActivity(@NonNull Context context) {
        super(context);
    }

    public AddGroupDialogActivity(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int)(size.x * 0.9);
        getWindow().setAttributes(p);
        mCancel = findViewById(R.id.add_group_cancel);
        mConfirm = findViewById(R.id.add_group_confirm);
        textInputEditText = findViewById(R.id.add_group_input);
        mTitle = findViewById(R.id.add_group_title);
        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_group_cancel:
                if (onCancelListener != null) {
                    onCancelListener.OnCancel(this);
                }
                dismiss();
                break;
            case R.id.add_group_confirm:
                if (onConfirmListener != null) {
                    onConfirmListener.OnConfirm(this);
                }
                dismiss();
                break;
        }
    }

    public interface IOnCancelListener {
        void OnCancel(AddGroupDialogActivity dialog);
    }

    public interface IOnConfirmListener {
        void OnConfirm(AddGroupDialogActivity dialog);
    }
}
