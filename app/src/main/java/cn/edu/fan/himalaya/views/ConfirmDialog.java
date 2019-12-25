package cn.edu.fan.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.edu.fan.himalaya.R;

//确认对话框
public class ConfirmDialog extends Dialog {

    private View mCancelSub;
    private View mGiveUp;
    private OnDialogActionClickListener mClickListener = null;

    public ConfirmDialog(@NonNull Context context) {
        super(context,0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, true,null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        //取消
        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onCancelClick();
                    dismiss();
                }
            }
        });
        //
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.OnGiveUpClick();
                dismiss();
            }
        });

    }

    private void initView() {
        mCancelSub = this.findViewById(R.id.dialog_check_box_cancel);
        mGiveUp = this.findViewById(R.id.dialog_check_box_confirm);
    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener listener){
        this.mClickListener = listener;
    }
    public interface OnDialogActionClickListener{
        void onCancelClick();

        void OnGiveUpClick();
    }
}
