package com.gis.common.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;

/**
 * https://github.com/pinguo-zhouwei/CustomPopwindow
 * 自定义PopWindow类，封装了PopWindow的一些常用属性，用Builder模式支持链式调用
 * Created by zhouwei on 16/11/28.
 *
 * 用法：
 *
 * // 声明 + 初始view
 * private var mPopWindow: CustomPopWindow? = null
 * private val mPopAdapter: OpinionPopAdapter by lazy { OpinionPopAdapter() }
 * private val mPopWindowView: View by lazy { LayoutInflater.from(mActivity).inflate(R.layout.opinion_popup_view,null) }
 *
 * // 初始化
 * private fun initPopWindow() {
 *         // 避免第一次加载获取不到宽高
 *         mPopWindowView.measure(0,0)
 *         val close = mPopWindowView.findViewById<ImageView>(R.id.iv_close)
 *         val popRecycle = mPopWindowView.findViewById<RecyclerView>(R.id.recycle_pop)
 *         close.setOnClickListener { mPopWindow?.dissmiss() }
 *         popRecycle.apply {
 *             layoutManager = GridLayoutManager(mActivity, 2)
 *             adapter = mPopAdapter
 *             addItemDecoration(GridSpacingItemDecoration(20F.dip2px(), 20F.dip2px(), true))
 *         }
 *    }
 *
 * // 调用显示
 * private fun createPop(view: View, info: List<FavorInfoBean>){
 *         mPopAdapter.setNewData(info)
 *         mPopWindow = CustomPopWindow.PopupWindowBuilder(mActivity)
 *             .setView(mPopWindowView)
 *             .setOutsideTouchable(true) //是否PopupWindow 以外触摸dissmiss
 *             .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
 *             .setBgDarkAlpha(0.7f) // 控制亮度
 *             .create()
 *             .showAsDropDown(view, -(mPopWindowView.measuredWidth - 40F.dip2px()), 0, Gravity.BOTTOM)
 *     }
 *
 *
 */
public class BasePopWindow implements PopupWindow.OnDismissListener{
    private static final String TAG = "BasePopWindow";
    private static final float DEFAULT_ALPHA = 0.7f;
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private boolean mIsFocusable = true;
    private boolean mIsOutside = true;
    private int mResLayoutId = -1;
    private View mContentView;
    private PopupWindow mPopupWindow;
    private int mAnimationStyle = -1;

    private boolean mClippEnable = true;//default is true
    private boolean mIgnoreCheekPress = false;
    private int mInputMode = -1;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private int mSoftInputMode = -1;
    private boolean mTouchable = true;//default is ture
    private View.OnTouchListener mOnTouchListener;

    private Window mWindow;//当前Activity 的窗口
    /**
     * 弹出PopWindow 背景是否变暗，默认不会变暗。
     */
    private boolean mIsBackgroundDark = false;

    private float mBackgroundDrakValue = 0;// 背景变暗的值，0 - 1
    /**
     * 设置是否允许点击 PopupWindow之外的地方，关闭PopupWindow
     */
    private boolean enableOutsideTouchDisMiss = true;// 默认点击pop之外的地方可以关闭

    private BasePopWindow(Context context){
        mContext = context;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     *
     * @param anchor
     * @param xOff
     * @param yOff
     * @return
     */
    public BasePopWindow showAsDropDown(View anchor, int xOff, int yOff){
        if(mPopupWindow!=null){
            mPopupWindow.showAsDropDown(anchor,xOff,yOff);
        }
        return this;
    }

    public BasePopWindow showAsDropDown(View anchor){
        if(mPopupWindow!=null){
            mPopupWindow.showAsDropDown(anchor);
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public BasePopWindow showAsDropDown(View anchor, int xOff, int yOff, int gravity){
        if(mPopupWindow!=null){
            mPopupWindow.showAsDropDown(anchor,xOff,yOff,gravity);
        }
        return this;
    }


    /**
     * 相对于父控件的位置（通过设置Gravity.CENTER，下方Gravity.BOTTOM等 ），可以设置具体位置坐标
     * @param parent 父控件
     * @param gravity
     * @param x the popup's x location offset
     * @param y the popup's y location offset
     * @return
     */
    public BasePopWindow showAtLocation(View parent, int gravity, int x, int y){
        if(mPopupWindow!=null){
            mPopupWindow.showAtLocation(parent,gravity,x,y);
        }
        return this;
    }

    /**
     * 添加一些属性设置
     * @param popupWindow
     */
    private void apply(PopupWindow popupWindow){
        popupWindow.setClippingEnabled(mClippEnable);
        if(mIgnoreCheekPress){
            popupWindow.setIgnoreCheekPress();
        }
        if(mInputMode!=-1){
            popupWindow.setInputMethodMode(mInputMode);
        }
        if(mSoftInputMode!=-1){
            popupWindow.setSoftInputMode(mSoftInputMode);
        }
        if(mOnDismissListener!=null){
            popupWindow.setOnDismissListener(mOnDismissListener);
        }
        if(mOnTouchListener!=null){
            popupWindow.setTouchInterceptor(mOnTouchListener);
        }
        popupWindow.setTouchable(mTouchable);



    }

    private PopupWindow build(){

        if(mContentView == null){
            mContentView = LayoutInflater.from(mContext).inflate(mResLayoutId,null);
        }

        // 2017.3.17 add
        // 获取当前Activity的window
        Activity activity = (Activity) mContentView.getContext();
        if(activity!=null && mIsBackgroundDark){
            //如果设置的值在0 - 1的范围内，则用设置的值，否则用默认值
            final  float alpha = (mBackgroundDrakValue > 0 && mBackgroundDrakValue < 1) ? mBackgroundDrakValue : DEFAULT_ALPHA;
            mWindow = activity.getWindow();
            WindowManager.LayoutParams params = mWindow.getAttributes();
            params.alpha = alpha;
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mWindow.setAttributes(params);
        }


        if(mWidth != 0 && mHeight!=0 ){
            mPopupWindow = new PopupWindow(mContentView,mWidth,mHeight);
        }else{
            mPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if(mAnimationStyle!=-1){
            mPopupWindow.setAnimationStyle(mAnimationStyle);
        }

        apply(mPopupWindow);//设置一些属性

        if(mWidth == 0 || mHeight == 0){
            mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            //如果外面没有设置宽高的情况下，计算宽高并赋值
            mWidth = mPopupWindow.getContentView().getMeasuredWidth();
            mHeight = mPopupWindow.getContentView().getMeasuredHeight();
        }

        // 添加dissmiss 监听
        mPopupWindow.setOnDismissListener(this);

        //2017.6.27 add:fix 设置  setOutsideTouchable（false）点击外部取消的bug.
        // 判断是否点击PopupWindow之外的地方关闭 popWindow
        if(!enableOutsideTouchDisMiss){
            //注意这三个属性必须同时设置，不然不能disMiss，以下三行代码在Android 4.4 上是可以，然后在Android 6.0以上，下面的三行代码就不起作用了，就得用下面的方法
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setBackgroundDrawable(null);
            //注意下面这三个是contentView 不是PopupWindow
            mPopupWindow.getContentView().setFocusable(true);
            mPopupWindow.getContentView().setFocusableInTouchMode(true);
            mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mPopupWindow.dismiss();

                        return true;
                    }
                    return false;
                }
            });
            //在Android 6.0以上 ，只能通过拦截事件来解决
            mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if ((event.getAction() == MotionEvent.ACTION_DOWN)
                            && ((x < 0) || (x >= mWidth) || (y < 0) || (y >= mHeight))) {
                        Log.e(TAG,"out side ");
                        Log.e(TAG,"width:"+mPopupWindow.getWidth()+"height:"+mPopupWindow.getHeight()+" x:"+x+" y  :"+y);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        Log.e(TAG,"out side ...");
                        return true;
                    }
                    return false;
                }
            });
        }else{
            mPopupWindow.setFocusable(mIsFocusable);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setOutsideTouchable(mIsOutside);
        }
        // update
        mPopupWindow.update();

        return mPopupWindow;
    }

    @Override
    public void onDismiss() {
        dissmiss();
    }

    /**
     * 关闭popWindow
     */
    public void dissmiss(){

        if(mOnDismissListener!=null){
            mOnDismissListener.onDismiss();
        }

        //如果设置了背景变暗，那么在dissmiss的时候需要还原
        if(mWindow!=null){
            WindowManager.LayoutParams params = mWindow.getAttributes();
            params.alpha = 1.0f;
            mWindow.setAttributes(params);
        }
        if(mPopupWindow!=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public static class PopupWindowBuilder{
        private BasePopWindow mCustomPopWindow;

        public PopupWindowBuilder(Context context){
            mCustomPopWindow = new BasePopWindow(context);
        }
        public PopupWindowBuilder size(int width,int height){
            mCustomPopWindow.mWidth = width;
            mCustomPopWindow.mHeight = height;
            return this;
        }


        public PopupWindowBuilder setFocusable(boolean focusable){
            mCustomPopWindow.mIsFocusable = focusable;
            return this;
        }



        public PopupWindowBuilder setView(int resLayoutId){
            mCustomPopWindow.mResLayoutId = resLayoutId;
            mCustomPopWindow.mContentView = null;
            return this;
        }

        public PopupWindowBuilder setView(View view){
            mCustomPopWindow.mContentView = view;
            mCustomPopWindow.mResLayoutId = -1;
            return this;
        }

        public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable){
            mCustomPopWindow.mIsOutside = outsideTouchable;
            return this;
        }

        /**
         * 设置弹窗动画
         * @param animationStyle
         * @return
         */
        public PopupWindowBuilder setAnimationStyle(int animationStyle){
            mCustomPopWindow.mAnimationStyle = animationStyle;
            return this;
        }


        public PopupWindowBuilder setClippingEnable(boolean enable){
            mCustomPopWindow.mClippEnable =enable;
            return this;
        }


        public PopupWindowBuilder setIgnoreCheekPress(boolean ignoreCheekPress){
            mCustomPopWindow.mIgnoreCheekPress = ignoreCheekPress;
            return this;
        }

        public PopupWindowBuilder setInputMethodMode(int mode){
            mCustomPopWindow.mInputMode = mode;
            return this;
        }

        public PopupWindowBuilder setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener){
            mCustomPopWindow.mOnDismissListener = onDissmissListener;
            return this;
        }


        public PopupWindowBuilder setSoftInputMode(int softInputMode){
            mCustomPopWindow.mSoftInputMode = softInputMode;
            return this;
        }


        public PopupWindowBuilder setTouchable(boolean touchable){
            mCustomPopWindow.mTouchable = touchable;
            return this;
        }

        public PopupWindowBuilder setTouchIntercepter(View.OnTouchListener touchIntercepter){
            mCustomPopWindow.mOnTouchListener = touchIntercepter;
            return this;
        }

        /**
         * 设置背景变暗是否可用
         * @param isDark
         * @return
         */
        public PopupWindowBuilder enableBackgroundDark(boolean isDark){
            mCustomPopWindow.mIsBackgroundDark = isDark;
            return this;
        }

        /**
         * 设置背景变暗的值
         * @param darkValue
         * @return
         */
        public PopupWindowBuilder setBgDarkAlpha(float darkValue){
            mCustomPopWindow.mBackgroundDrakValue = darkValue;
            return this;
        }

        /**
         * 设置是否允许点击 PopupWindow之外的地方，关闭PopupWindow
         * @param disMiss
         * @return
         */
        public PopupWindowBuilder enableOutsideTouchableDissmiss(boolean disMiss){
            mCustomPopWindow.enableOutsideTouchDisMiss = disMiss;
            return this;
        }


        public BasePopWindow create(){
            //构建PopWindow
            mCustomPopWindow.build();
            return mCustomPopWindow;
        }

    }

}