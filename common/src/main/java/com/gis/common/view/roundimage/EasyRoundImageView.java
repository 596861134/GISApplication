package com.gis.common.view.roundimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatImageView;

import com.gis.common.R;


/**
 * 可设置圆角或圆形图片控件，如需分别设置4个圆角大小可用 <p>RoundImageView<p/>
 * Created by chengzf on 18/6/29.
 *   圆角 在布局内加入 app:type="round"
 *   圆形 在布局内加入 app:type="circle"
 */
public class EasyRoundImageView extends AppCompatImageView {
    /**
     * 圆形模式
     */
    public static final int MODE_CIRCLE = 1;
    /**
     * 普通模式
     */
    public static final int MODE_NONE = 0;
    /**
     * 圆角模式
     */
    public static final int MODE_ROUND = 2;
    private Paint mPaint;
    private int currMode = 0;
    /**
     * 圆角半径
     */
    private int currRound = dp2px(10);

    public EasyRoundImageView(Context context) {
        super(context);
        initViews();
    }

    public EasyRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttrs(context, attrs, defStyleAttr);
        initViews();
    }

    private void obtainStyledAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EasyRoundImageView, defStyleAttr, 0);
        currMode = a.hasValue(R.styleable.EasyRoundImageView_type) ? a.getInt(R.styleable.EasyRoundImageView_type, MODE_NONE) : MODE_NONE;
        currRound = a.hasValue(R.styleable.EasyRoundImageView_radiu) ? a.getDimensionPixelSize(R.styleable.EasyRoundImageView_radiu, currRound) : currRound;
        a.recycle();
    }

    private void initViews() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 当模式为圆形模式的时候，我们强制让宽高一致
         */
        if (currMode == MODE_CIRCLE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int result = Math.min(getMeasuredHeight(), getMeasuredWidth());
            setMeasuredDimension(result, result);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable mDrawable = getDrawable();
        Matrix mDrawMatrix = getImageMatrix();
        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            mDrawable.draw(canvas);
        } else {
            final int saveCount = canvas.getSaveCount();
            canvas.save();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (getCropToPadding()) {
                    final int scrollX = getScrollX();
                    final int scrollY = getScrollY();
                    canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                            scrollX + getRight() - getLeft() - getPaddingRight(),
                            scrollY + getBottom() - getTop() - getPaddingBottom());
                }
            }
            canvas.translate(getPaddingLeft(), getPaddingTop());
            if (currMode == MODE_CIRCLE) {//当为圆形模式的时候
                Bitmap bitmap = drawable2Bitmap(mDrawable);
                mPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
            } else if (currMode == MODE_ROUND) {//当为圆角模式的时候
                Bitmap bitmap = drawable2Bitmap(mDrawable);
                mPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawRoundRect(new RectF(0, 0, getWidth() - getPaddingRight()-getPaddingLeft(), getHeight() - getPaddingBottom()-getPaddingTop()),
                        currRound, currRound, mPaint);
            } else {
                if (mDrawMatrix != null) {
                    canvas.concat(mDrawMatrix);
                }
                mDrawable.draw(canvas);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    public void setCurrRound(int currRound) {
        this.currRound = currRound;
    }

    public void setCurrMode(int currMode) {
        this.currMode = currMode;
    }

    /**
     * drawable转换成bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //根据传递的scaletype获取matrix对象，设置给bitmap
        Matrix matrix = getImageMatrix();
        if (matrix != null) {
            canvas.concat(matrix);
        }
        drawable.draw(canvas);
        return bitmap;
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
