package cn.edu.fan.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.edu.fan.himalaya.R;

//确认对话框
public class ConfirmCheckBoxDialog extends Dialog {

    private View mCancel;
    private View mConfirmDel;
    private OnDialogActionClickListener mClickListener = null;
    private CheckBox mConfirmDelAll;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        super(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        super(context, true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        //取消
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onCancelClick();
                    dismiss();
                }
            }
        });
        //
        mConfirmDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = mConfirmDelAll.isChecked();
                mClickListener.onConfirmDel(checked);
                dismiss();
            }
        });

    }

    private void initView() {
        mCancel = this.findViewById(R.id.dialog_check_box_cancel);
        mConfirmDel = this.findViewById(R.id.dialog_check_box_confirm);
        mConfirmDelAll = this.findViewById(R.id.dialog_check_box_all);

    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener listener){
        this.mClickListener = listener;
    }
    public interface OnDialogActionClickListener{
        void onCancelClick();

        void onConfirmDel(boolean isCheck);
    }
}
