package com.jayz.widget.dashboard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.jayz.widget.dashboard.R;

import androidx.annotation.Nullable;

/**
 * 自定义速度仪表盘
 */
public class SpeedDashboardWidget extends View {
    private Context mContext;
    //仪表盘圆形半径
    private int mDialCircleRadius;
    //控件宽度和高度
    private int mViewWidth, mViewHeight;
    //控件中心点X、Y
    private int mCenterX, mCenterY;
    //背景画笔
    private Paint mDialBgPaint;
    //背景颜色
    private int mDialBgColor;
    //背景渐变
    private Shader mDialBgShader;
    //背景渐变颜色
    private int[] mDialBgShaderColors;
    //刻度背景画笔
    private Paint mDialLineBgPaint;
    //刻度背景颜色
    private int mDialLineBgColor;
    //刻度背景扇形半径
    private float mDialLineBgArcRadius;
    //刻度背景矩形
    private RectF mDialLineBgRectF;
    //刻度画笔
    private Paint mDialLinePaint;
    //刻度正常颜色
    private int mDialLineNormalColor;
    //刻度起始角度
    private float mDialLineStartDegree;
    //刻度最大值
    private float mDialMaxVal;
    //刻度最小值
    private float mDialMinVal;
    //刻度值范围
    private float mDialValRange;
    //刻度梯级
    private int mDialStep;
    //每个梯级度数
    private float mStepDegree;
    //刻度盘内盘半径
    private int mDialInnerRadius;
    //刻度盘正常值上限
    private int mDialNormalValueLimit;
    //短刻度线旋转角度
    private float mShortDialLineRoateDegree;
    //长刻度线长度
    private int mLongDialLineLength = 16;
    //短刻度线长度
    private int mShortDialLineLength = 8;
    //刻度文本画笔
    private Paint mDialTextPaint;
    //刻度文本大小
    private float mDialTextSize;
    //刻度文本正常颜色
    private int mDialTextNormalColor;
    //刻度文本度量
    private Paint.FontMetrics mDialTextFM;
    //速度扇形
    private Paint mSpeedArcPaint;
    //速度扇形矩形
    private RectF mSpeedArcRect;
    //速度扇形半径
    private int mSpeedArcRadius;
    //速度扇形正常/警告渐变
    private Shader mNormalSpeedArcShader, mWarnSpeedArcShader;
    //速度扇形正常/警告颜色
    private int[] mSpeedArcNormalColors, mSpeedArcWarnColors;

    //画速度指针画笔
    private Paint mSpeedPointerPaint;
    //速度指针颜色
    private int mSpeedPointerColor;
    //速度指针形状路径
    private Path mSpeedPointerPath = new Path();

    //画速度值
    private Paint mSpeedBgPaint;
    private Paint mSpeedTextPaint;
    private Paint.FontMetrics mSpeedTextFM;
    private float mSpeedTextBaselineY;
    private int mSpeedTextColor;
    private int mCurrentSpeedBgColor;
    private int mSpeedTextSize;
    private Rect mSpeedTextRect = new Rect();
    private String mSpeedStrValue;
    private int mCurrentSpeed;

    //警告颜色
    private int mWarnColor;


    public SpeedDashboardWidget(Context context) {
        this(context, null);
    }

    public SpeedDashboardWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedDashboardWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        initPaint();
    }

    /**
     * 更新速度
     * @param speed 速度
     */
    public void updateSpeed(int speed) {
        if (speed >= 0 || speed <= mDialMaxVal) {
            mCurrentSpeed = speed;
            mSpeedStrValue = String.valueOf(speed);
            invalidate();
        }
    }

    /**
     * 初始化
     * @param context 上下文
     * @param attrs 参数
     */
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray ta =  mContext.obtainStyledAttributes(attrs, R.styleable.SpeedDashboardWidget);
        try {
            mDialCircleRadius = (int) ta.getDimension(R.styleable.SpeedDashboardWidget_circleRadius, dip2px(200));
            mDialTextSize = (int) ta.getDimension(R.styleable.SpeedDashboardWidget_dialTextSize, sp2px(14));
            mSpeedTextSize = (int) ta.getDimension(R.styleable.SpeedDashboardWidget_speedTextSize, sp2px(20));
            mSpeedTextColor = ta.getColor(R.styleable.SpeedDashboardWidget_speedTextColor, Color.parseColor("#FFFFFF"));
            mDialBgColor = ta.getColor(R.styleable.SpeedDashboardWidget_dialBgColor, Color.parseColor("#2e13b7"));
            mDialTextNormalColor = ta.getColor(R.styleable.SpeedDashboardWidget_dialTextNormalColor, Color.parseColor("#000000"));
            mDialLineNormalColor = ta.getColor(R.styleable.SpeedDashboardWidget_dialLineNormalColor, Color.parseColor("#2e13b7"));
            mWarnColor = ta.getColor(R.styleable.SpeedDashboardWidget_warnColor, Color.parseColor("#AAFF0000"));
            mSpeedPointerColor = ta.getColor(R.styleable.SpeedDashboardWidget_speedPointerColor, Color.parseColor("#a02e13b7"));
            mDialLineBgColor = ta.getColor(R.styleable.SpeedDashboardWidget_dialLineBgColor, Color.parseColor("#a02e13b7"));
            mCurrentSpeedBgColor = ta.getColor(R.styleable.SpeedDashboardWidget_currentSpeedBgColor, Color.parseColor("#2e13b7"));
            mSpeedArcNormalColors = new int[]{Color.TRANSPARENT, Color.TRANSPARENT, mDialBgColor};
            int tmpColor1 = (mWarnColor & 0x00FFFFFF) | (mWarnColor & 0x30000000);
            int tmpColor2 = (mWarnColor & 0x00FFFFFF) | (mWarnColor & 0xB0000000);
            mSpeedArcWarnColors = new int[]{Color.TRANSPARENT, Color.TRANSPARENT, tmpColor1, tmpColor2};
            mDialBgShaderColors = new int[]{(mDialBgColor & 0x00FFFFFF) | (mDialBgColor & 0x55000000), mDialBgColor};

            mDialMaxVal = ta.getFloat(R.styleable.SpeedDashboardWidget_dialMaxValue, 260f);
            mDialMinVal = ta.getFloat(R.styleable.SpeedDashboardWidget_dialMinValue, 0f);
            mDialStep = ta.getInteger(R.styleable.SpeedDashboardWidget_dialStepValue, 20);
            mDialLineStartDegree = ta.getFloat(R.styleable.SpeedDashboardWidget_dialLineStartDegree, 135f);

            mDialValRange = mDialMaxVal - mDialMinVal;
            float drawDegree = (360 - (180 - mDialLineStartDegree) * 2);
            mStepDegree = drawDegree / (int)Math.ceil(mDialValRange / mDialStep);
            mShortDialLineRoateDegree = mStepDegree / mDialStep;
            mDialNormalValueLimit = 200;
            mDialInnerRadius = mDialCircleRadius - dip2px(10);
            mLongDialLineLength = dip2px(16);
            mShortDialLineLength = dip2px(8);
            mSpeedArcRadius = mDialCircleRadius - dip2px(10f);
            mDialLineBgArcRadius = mDialCircleRadius - dip2px(15);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mDialBgPaint = new Paint();
        mDialBgPaint.setAntiAlias(true);

        mDialLinePaint = new Paint();
        mDialLinePaint.setAntiAlias(true);

        mDialTextPaint = new Paint();
        mDialTextPaint.setAntiAlias(true);
        mDialTextPaint.setTextSize(sp2px(mDialTextSize));
        mDialTextPaint.setTextAlign(Paint.Align.CENTER);
        mDialTextFM = mDialTextPaint.getFontMetrics();

        mSpeedArcPaint = new Paint();
        mSpeedArcPaint.setAntiAlias(true);
        mSpeedArcPaint.setStyle(Paint.Style.FILL);

        mSpeedPointerPaint = new Paint();
        mSpeedPointerPaint.setAntiAlias(true);
        mSpeedPointerPaint.setStyle(Paint.Style.FILL);
        mSpeedPointerPaint.setStrokeCap(Paint.Cap.ROUND);
        mSpeedPointerPaint.setColor(mSpeedPointerColor);
        mSpeedPointerPaint.setStrokeWidth(dip2px(2f));

        mDialLineBgPaint = new Paint();
        mDialLineBgPaint.setAntiAlias(true);
        mDialLineBgPaint.setStyle(Paint.Style.STROKE);
        mDialLineBgPaint.setStrokeWidth(dip2px(10.0f));

        mSpeedBgPaint = new Paint();
        mSpeedBgPaint.setAntiAlias(true);

        mSpeedTextPaint = new Paint();
        mSpeedTextPaint.setAntiAlias(true);
        mSpeedTextPaint.setStyle(Paint.Style.FILL);
        mSpeedTextPaint.setColor(mSpeedTextColor);
        mSpeedTextPaint.setTextSize(mSpeedTextSize);
        mSpeedTextPaint.setFakeBoldText(true);
        mSpeedTextPaint.setTextAlign(Paint.Align.CENTER);
        mSpeedTextFM = mSpeedTextPaint.getFontMetrics();
        //得到基线的位置（（0，0）中心点已经移至屏幕中心，所以y = 0）
        mSpeedTextBaselineY = 0 + (mSpeedTextFM.bottom - mSpeedTextFM.top) / 2 - mSpeedTextFM.bottom - 10;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取测量的宽高的size和mode
        final int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        //根据宽高模式测量控件宽高
        if (measureWidthMode == MeasureSpec.AT_MOST && measureHeightMode == MeasureSpec.AT_MOST) {
            //width: wrap_content 宽度需自行计算
            //height:wrap_content 高度需自行计算
            mViewWidth = mDialCircleRadius * 2;
            mViewHeight = mViewWidth;
        } else if (measureWidthMode == MeasureSpec.EXACTLY && measureHeightMode == MeasureSpec.EXACTLY) {
            //width: match_parent 宽度为父控件测量建议值
            //height:match_parent 高度为父控件测量建议值
            mViewWidth = measureWidth;
            mViewHeight = measureHeight;
        } else if (measureWidthMode == MeasureSpec.AT_MOST && measureHeightMode == MeasureSpec.EXACTLY) {
            //width：wrap_content 宽度需自行计算
            //height：match_parent 高度为父控件测量建议值
            mViewWidth = mDialCircleRadius * 2;
            mViewHeight = measureHeight;
        } else {
            //width：match_parent 宽度为父控件测量建议值
            //height：wrap_content 高度需自行计算
            mViewWidth = measureWidth;
            mViewHeight = mDialCircleRadius * 2;
        }
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = mViewWidth/2;
        mCenterY = mViewHeight/2;
        mSpeedArcRect = new RectF(0 - mSpeedArcRadius, 0 - mSpeedArcRadius, 0 + mSpeedArcRadius, 0 + mSpeedArcRadius);
        mNormalSpeedArcShader = new RadialGradient(0, 0, mSpeedArcRadius, mSpeedArcNormalColors, null, Shader.TileMode.REPEAT);
        mWarnSpeedArcShader = new RadialGradient(0, 0, mSpeedArcRadius, mSpeedArcWarnColors, null, Shader.TileMode.REPEAT);
        mDialBgShader = new RadialGradient(0, 0, mDialCircleRadius, mDialBgShaderColors, null, Shader.TileMode.REPEAT);
        mSpeedPointerPath.moveTo(mDialInnerRadius *0.3f + dip2px(8f), -dip2px(4));
        mSpeedPointerPath.lineTo(mDialInnerRadius *0.96f, -dip2px(1));
        mSpeedPointerPath.lineTo(mDialInnerRadius *0.96f, dip2px(1));
        mSpeedPointerPath.lineTo(mDialInnerRadius *0.3f + dip2px(8f), dip2px(4));
        mSpeedPointerPath.lineTo(mDialInnerRadius *0.3f + dip2px(8f), -dip2px(4));
        mDialLineBgRectF = new RectF(0 - mDialLineBgArcRadius,
                0 - mDialLineBgArcRadius,
                0 + mDialLineBgArcRadius,
                0 + mDialLineBgArcRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将坐标位置（0,0）设置到圆心位置
        canvas.translate(mCenterX, mCenterY);
        //画背景
        drawDialBg(canvas);
        //画刻度线
        drawDialLine(canvas);
        //画扇形
        drawSpeedArc(canvas);
        //画外圈
        drawDialLineBg(canvas);
        //画速度指针
        drawSpeedPointer(canvas);
        //画速度背景
        drawSpeedBg(canvas);
        //画速度值
        drawSpeedText(canvas);
    }

    /**
     * 画表盘背景
     * @param canvas
     */
    private void drawDialBg(Canvas canvas) {
        canvas.save();
        //画笔属性是实心圆
        mDialBgPaint.setStyle(Paint.Style.FILL);
        mDialBgPaint.setColor(mDialBgColor);
        mDialBgPaint.setShader(mDialBgShader);
        canvas.drawCircle(0, 0, mDialCircleRadius, mDialBgPaint);
        canvas.restore();
    }

    /**
     * 画刻度线
     * @param canvas
     */
    private void drawDialLine(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDialLineStartDegree);
        for (int i = 0; i <= mDialValRange; i++) {
            if (i < mDialNormalValueLimit) {
                mDialLinePaint.setColor(mDialLineNormalColor);
            } else {
                mDialLinePaint.setColor(mWarnColor);
            }

            if (i % mDialStep == 0) {
                mDialLinePaint.setStrokeWidth(dip2px(3f));
                canvas.drawLine(mDialInnerRadius, 0, mDialInnerRadius - mLongDialLineLength, 0, mDialLinePaint);
                drawDialText(canvas, String.format("%.0f", mDialMinVal + i), i);
            } else if (i % 5 == 0){
                mDialLinePaint.setStrokeWidth(dip2px(1f));
                canvas.drawLine(mDialInnerRadius, 0, mDialInnerRadius - mShortDialLineLength, 0, mDialLinePaint);
            }
            canvas.rotate(mShortDialLineRoateDegree);
        }
        canvas.restore();
    }

    /**
     * 画刻度背景
     * @param canvas
     */
    private void drawDialLineBg(Canvas canvas) {
        canvas.save();
        //将起始角度0°旋转至刻度盘其实角度mDialDegree
        canvas.rotate(mDialLineStartDegree);
        //画外边框
        mDialLineBgPaint.setColor(mDialLineBgColor);
        canvas.drawCircle(0, 0, mDialCircleRadius - dip2px(5.0f), mDialLineBgPaint);
        //画外圈扇形正常速度背景
        mDialLineBgPaint.setColor(mDialLineBgColor);
        mDialLineBgPaint.setAlpha(0x55);
        canvas.drawArc(mDialLineBgRectF,
                0f,
                mShortDialLineRoateDegree * mDialNormalValueLimit,
                false,
                mDialLineBgPaint);
        //画外圈扇形警告速度背景
        mDialLineBgPaint.setColor(mWarnColor);
        mDialLineBgPaint.setAlpha(0x90);
        canvas.drawArc(mDialLineBgRectF,
                mShortDialLineRoateDegree * mDialNormalValueLimit,
                mShortDialLineRoateDegree * (mDialMaxVal - mDialMinVal - mDialNormalValueLimit),
                false,
                mDialLineBgPaint);
        canvas.restore();
    }

    /**
     * 画刻度文本
     * @param canvas
     * @param text 文本
     * @param i 某个刻度索引
     */
    private void drawDialText(Canvas canvas, String text, int i) {
        canvas.save();
        if (i >= mDialNormalValueLimit) {
            mDialTextPaint.setColor(mWarnColor);
        } else {
            mDialTextPaint.setColor(mDialTextNormalColor);
        }
        int currentCenterX = (int) (mDialInnerRadius - mLongDialLineLength - dip2px(3f) - mDialTextPaint.measureText(String.valueOf(text)) / 2);
        canvas.translate(currentCenterX, 0);
        canvas.rotate(360 - mDialLineStartDegree -  i * mShortDialLineRoateDegree);
        int textBaseLine = (int) (0 + (mDialTextFM.bottom - mDialTextFM.top) /2 - mDialTextFM.bottom);
        canvas.drawText(text, 0, textBaseLine, mDialTextPaint);
        canvas.restore();
    }

    /**
     * 画速度扇形
     * @param canvas
     */
    private void drawSpeedArc(Canvas canvas) {
        canvas.save();
        mSpeedArcPaint.setStyle(Paint.Style.FILL);
        canvas.rotate(mDialLineStartDegree);
        if (mCurrentSpeed < mDialNormalValueLimit) {
            mSpeedArcPaint.setShader(mNormalSpeedArcShader);
        } else {
            mSpeedArcPaint.setShader(mWarnSpeedArcShader);
        }
        canvas.drawArc(mSpeedArcRect, 0, mCurrentSpeed * mShortDialLineRoateDegree, true, mSpeedArcPaint);
        canvas.restore();
    }

    /**
     * 画速度指针
     * @param canvas
     */
    private void drawSpeedPointer(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDialLineStartDegree + mCurrentSpeed * mShortDialLineRoateDegree);
        if (mCurrentSpeed >= mDialNormalValueLimit) {
            mSpeedPointerPaint.setColor(Color.parseColor("#FF0000"));
        } else {
            mSpeedPointerPaint.setColor(mSpeedPointerColor);
        }
        canvas.drawPath(mSpeedPointerPath, mSpeedPointerPaint);
        canvas.restore();
    }

    /**
     * 画速度值背景
     * @param canvas
     */
    private void drawSpeedBg(Canvas canvas) {
        //画中间实时速度文本背景
        if (mCurrentSpeed >= mDialNormalValueLimit) {
            mSpeedBgPaint.setColor(mWarnColor);
        } else {
            mSpeedBgPaint.setColor(mCurrentSpeedBgColor);
        }
        mSpeedBgPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, mDialCircleRadius * 0.3f, mSpeedBgPaint);

        mSpeedBgPaint.setStyle(Paint.Style.STROKE);
        mSpeedBgPaint.setStrokeWidth(dip2px(10f));
        mSpeedBgPaint.setColor(mCurrentSpeedBgColor);
        mSpeedBgPaint.setAlpha(0xA0);
        canvas.drawCircle(0, 0, mDialCircleRadius * 0.3f, mSpeedBgPaint);
    }

    /**
     * 画速度文本
     * @param canvas
     */
    private void drawSpeedText(Canvas canvas) {
        mSpeedTextPaint.getTextBounds(mSpeedStrValue, 0, mSpeedStrValue.length(), mSpeedTextRect);
        mSpeedTextPaint.setTextSize(mSpeedTextSize);
        canvas.drawText(mSpeedStrValue, 0, mSpeedTextBaselineY, mSpeedTextPaint);
        mSpeedTextPaint.setTextSize(mSpeedTextSize * 0.5f);
        canvas.drawText("km/h", 0, mSpeedTextBaselineY + (mSpeedTextFM.bottom - mSpeedTextFM.top)/2, mSpeedTextPaint);
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
//        final float scale = mContext.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, getResources().getDisplayMetrics());
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue sp
     * @return 返回px
     */
    private int sp2px(float spValue) {
//        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (spValue * fontScale + 0.5f);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, getResources().getDisplayMetrics());
    }

}
