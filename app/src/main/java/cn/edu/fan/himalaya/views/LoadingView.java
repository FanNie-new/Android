package cn.edu.fan.himalaya.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import cn.edu.fan.himalaya.R;

@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView {

    //旋转的角度
    private int rotateDegree = 0;
    //是否旋转
    private boolean mNeedRotate = true;

    public LoadingView(Context context) {
        this(context,null); // 保证同一个入口
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0); // 保证同一个入口
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置图标
        setImageResource(R.mipmap.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //绑定到window的时候
        post(new Runnable() {
            @Override
            public void run() {
                rotateDegree += 30;
                rotateDegree = rotateDegree <= 360 ? rotateDegree : 0;
                invalidate(); // 执行
                //是否继续旋转
                if (mNeedRotate) {
                    //延时
                    postDelayed(this,100);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //从window中解绑
        mNeedRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 第一个参数是旋转角度
         * 第二个参数是旋转的x坐标
         * 第二个参数是旋转的y坐标
         */

        canvas.rotate(rotateDegree,getWidth() /2 ,getHeight() /2);
        super.onDraw(canvas);
    }
}
