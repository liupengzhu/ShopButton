package com.example.shopbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/9/21/021.
 */

public class ShopButtonView extends View {



    protected int mCount;
    private int maxCount;

    private Paint delPaint;
    private Path delPath;
    private Region delRegion;
    private int leftColor;

    private Paint textPaint;
    private int countColor;
    private float countSize;

    private Paint addPaint;
    private Path addPath;
    private Region addRegion;
    private int rightCircleColor;
    private int rightAddColor;
    private int rightCircleUnEnableColor;
    private float mGapBetweenCircle;



    private float mRadius;//圆的半径
    private int mPandingLeft;
    private int mPandingRight;
    private int mPandingTop;
    private int mPandingBottom;
    private float mCircleWidth;
    private float mLineWidth;

    //View的宽和高
    private float mHeight;
    private float mWidth;
    //是否绘制Hint区域
    private boolean isHintMode;
    private Paint hintPaint;
    private int hintColor;
    private int hintTextColor;
    private float hintTextSize;
    private float hintRoundValues;
    private String hintText;

    //提示语收缩动画

    private ValueAnimator mAnimReduce;//收缩动画
    private ValueAnimator mAnimExpand;//展开动画
    protected float mAmimExpandHintFraction;

    private ValueAnimator mAnimDel;//减动画
    private ValueAnimator mAnimAdd;//加动画
    protected float mAmimFraction;

    private OnCountChangeListener listener;



    public ShopButtonView(Context context) {
        this(context,null);
    }

    public ShopButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShopButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        initAnim();
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.ShopButtonView);
        if(array!=null){
            mRadius = array.getDimension(R.styleable.ShopButtonView_circleRadius,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,getContext().getResources().getDisplayMetrics()));
            mCircleWidth = array.getDimension(R.styleable.ShopButtonView_circleWidth,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1,getContext().getResources().getDisplayMetrics()));
            mGapBetweenCircle = array.getDimension(R.styleable.ShopButtonView_mGapBetweenCircle,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getContext().getResources().getDisplayMetrics()));
            leftColor = array.getColor(R.styleable.ShopButtonView_leftColor,Color.BLUE);
            rightCircleColor = array.getColor(R.styleable.ShopButtonView_rightCircleColor,Color.GREEN);
            rightAddColor = array.getColor(R.styleable.ShopButtonView_rightAddColor,Color.WHITE);
            rightCircleUnEnableColor = array.getColor(R.styleable.ShopButtonView_rightCircleUnEnableColor,Color.rgb(155,155,155));
            countColor = array.getColor(R.styleable.ShopButtonView_countColor,Color.BLUE);
            countSize = array.getDimension(R.styleable.ShopButtonView_countSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,18,getContext().getResources().getDisplayMetrics()));
            maxCount = array.getInt(R.styleable.ShopButtonView_maxCount,99);
            hintTextSize = array.getDimension(R.styleable.ShopButtonView_textSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,getContext().getResources().getDisplayMetrics()));
            hintTextColor = array.getColor(R.styleable.ShopButtonView_textColor,Color.WHITE);
            hintText = array.getString(R.styleable.ShopButtonView_text);
            if(hintText==null){
                hintText = "添加到购物车";
            }
            array.recycle();
        }
    }
    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
        cancelAllAnim();
        initSetting();
    }

    private void initSetting() {
        if(mCount == 0){
            //0 不显示数字
            mAmimFraction = 1;
            isHintMode = true;
            mAmimExpandHintFraction = 0;
        }else {
            mAmimFraction = 0;
            isHintMode = false;
            mAmimExpandHintFraction = 1;
        }
    }

    //取消动画
    private void cancelAllAnim() {

        if(null!=mAnimReduce){
            mAnimReduce.cancel();
        }
        if(null!=mAnimExpand){
            mAnimExpand.cancel();
        }
        if(null!=mAnimAdd){
            mAnimAdd.cancel();
        }
        if(null!=mAnimDel){
            mAnimDel.cancel();
        }
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    private void initAnim() {
        //收缩动画由0-1
        mAnimReduce = ValueAnimator.ofFloat(0,1);
        mAnimReduce.setDuration(500);
        mAnimReduce.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAmimExpandHintFraction = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mAnimReduce.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //hint层收缩动画完成调用圆圈层展开动画
                isHintMode = false;
                mAnimAdd.start();
            }
        });
        mAnimExpand= ValueAnimator.ofFloat(1,0);
        mAnimExpand.setDuration(500);
        mAnimExpand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAmimExpandHintFraction = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mAnimExpand.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isHintMode = true;
            }
        });

        mAnimDel = ValueAnimator.ofFloat(0,1);
        mAnimDel.setDuration(500);
        mAnimDel.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAmimFraction = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mAnimDel.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //圆圈收缩动画结束调用hint层展开动画
                mAnimExpand.start();
            }
        });
        mAnimAdd = ValueAnimator.ofFloat(1,0);
        mAnimAdd.setDuration(500);
        mAnimAdd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAmimFraction = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });



    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {


        mCount = 0;
        isHintMode = true;
        //maxCount = 99;
        //初始化 抗抖动
        delPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        delPaint.setStyle(Paint.Style.STROKE);
        delPath = new Path();
        delRegion = new Region();

        //countColor = Color.BLUE;
        //countSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,18,getContext().getResources().getDisplayMetrics());
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //mGapBetweenCircle = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getContext().getResources().getDisplayMetrics());

        addPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        addPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        addPath = new Path();
        addRegion = new Region();

        //初始化圆的半径
       // mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,getContext().getResources().getDisplayMetrics());

        mCircleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1,getContext().getResources().getDisplayMetrics());
        mLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getContext().getResources().getDisplayMetrics());


        hintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //hintTextColor = Color.WHITE;
        //hintTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,getContext().getResources().getDisplayMetrics());


        //hintText = "添加到购物车";
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (wMode) {
            case MeasureSpec.AT_MOST:
                wSize = (int) (getPaddingLeft()+mRadius*2+mGapBetweenCircle+mRadius*2+getPaddingRight()+mCircleWidth);
                break;
        }
        switch (hMode) {
            case MeasureSpec.AT_MOST:
                hSize = (int) (getPaddingTop()+mRadius*2+getPaddingBottom()+mCircleWidth*2);
                break;
        }
        setMeasuredDimension(wSize,hSize);
        cancelAllAnim();
        initSetting();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight();
        mWidth = getWidth();
        mPandingLeft = getPaddingLeft();
        mPandingRight = getPaddingRight();
        mPandingTop = getPaddingTop();
        mPandingBottom = getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isHintMode){
            hintColor = rightCircleColor;
            hintRoundValues = mRadius*2;

            hintPaint.setColor(hintColor);
            RectF rectF = new RectF(mPandingLeft  +(mWidth-mRadius*2)*mAmimExpandHintFraction,mPandingTop,mWidth,mHeight);
            canvas.drawRoundRect(rectF,hintRoundValues,hintRoundValues,hintPaint);


            hintPaint.setColor(hintTextColor);
            hintPaint.setAlpha((int) (255*(1-mAmimExpandHintFraction)));
            hintPaint.setTextSize(hintTextSize);

            float startX = (mWidth-mRadius*2)*mAmimExpandHintFraction+mWidth/2-hintPaint.measureText(hintText)/2;
            float startY = mHeight/2-(hintPaint.descent()+hintPaint.ascent())/2;
            canvas.drawText(hintText,startX,startY,hintPaint);



        }else {
            //判断减按钮是否可用 来初始化不同颜色
            if(mCount>0){
                delPaint.setColor(leftColor);

            }else {

                delPaint.setColor(leftColor);
            }
            //动画位移的max
            float animOffsetMax = mRadius*2+mGapBetweenCircle;
            int animAlphaMax = 255;//透明度
            int animRotateMax = 360;//旋转

            delPaint.setStrokeWidth(mCircleWidth);
            //设置透明度
            delPaint.setAlpha((int) (animAlphaMax*(1-mAmimFraction)));
            //考虑动画
            delPath.reset();
            //设置位移
            delPath.addCircle(animOffsetMax*mAmimFraction+mPandingLeft+mRadius+mCircleWidth,mPandingTop+mRadius+mCircleWidth,mRadius, Path.Direction.CW);
            delRegion.setPath(delPath,new Region(mPandingLeft,mPandingTop,getWidth()-mPandingRight,getHeight()-mPandingBottom));

            canvas.drawPath(delPath,delPaint);

            canvas.save();
            //位移
            canvas.translate(animOffsetMax * mAmimFraction + mPandingLeft + mRadius + mCircleWidth,mPandingTop+mRadius+mCircleWidth);
            //旋转
            canvas.rotate(animRotateMax*(1-mAmimFraction));
            delPaint.setStrokeWidth(mLineWidth);
            canvas.drawLine(-mRadius/2, 0,
                    +mRadius/2,0,delPaint);

            canvas.restore();

            //绘制数量
            canvas.save();
            //平移动画
            canvas.translate(mAmimFraction*(mGapBetweenCircle/2-textPaint.measureText(mCount+"")/2+mRadius),0);
            //旋转
            canvas.rotate(mAmimFraction*animRotateMax,mPandingLeft+mRadius*2+mGapBetweenCircle/2,mPandingTop+mRadius);

            textPaint.setColor(countColor);
            textPaint.setTextSize(countSize);
            textPaint.setAlpha((int) (animAlphaMax*(1-mAmimFraction)));
            canvas.drawText(mCount+"",mPandingLeft+mRadius*2+mGapBetweenCircle/2+mCircleWidth*2-textPaint.measureText(mCount+"")/2,mPandingTop+mRadius-(textPaint.descent()+textPaint.ascent())/2,textPaint);

            canvas.restore();
            //绘制右边的园
            if(mCount<maxCount){
                addPaint.setColor(rightCircleColor);

            }else {
                addPaint.setColor(rightCircleUnEnableColor);
            }
            addPaint.setStrokeWidth(mCircleWidth);
            //右边园的左边界
            float left = mPandingLeft+mRadius*2+mGapBetweenCircle;
            addPath.reset();
            addPath.addCircle(left+mRadius,mPandingTop+mRadius+mCircleWidth,mRadius, Path.Direction.CW);
            addRegion.setPath(addPath,new Region(mPandingLeft,mPandingTop,getWidth()-mPandingRight,getHeight()-mPandingBottom));
            canvas.drawPath(addPath,addPaint);

            if(mCount<maxCount){
                addPaint.setColor(rightAddColor);

            }else {
                addPaint.setColor(rightAddColor);
            }
            addPaint.setStrokeWidth(mLineWidth);

            canvas.drawLine(left+mRadius/2,mPandingTop+mRadius,
                    left+mRadius/2+mRadius,mPandingTop+mRadius,addPaint
            );
            canvas.drawLine(left+mRadius,mPandingTop+mRadius/2, left+mRadius,
                    mPandingTop+mRadius/2+mRadius,addPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(isHintMode){
                    onAddClick();
                }else {
                    if(addRegion.contains((int)event.getX(), (int)event.getY())){
                        onAddClick();
                    }else if(delRegion.contains((int)event.getX(), (int)event.getY())){
                        onDelClick();
                    }

                }

                break;
            default:
                break;

        }
        return true;
    }

    public void onDelClick() {

        if(mCount>0){
            mCount--;
            if(listener!=null){
                listener.onCountChanged(mCount);
            }
            onDelSuccessListener();
            invalidate();
        }else {
            Toast.makeText(getContext(),"购物车商品数量小于零",Toast.LENGTH_SHORT).show();
        }
    }

    public void onAddClick() {
        if(mCount<maxCount){
            mCount++;
            if(listener!=null){
                listener.onCountChanged(mCount);
            }
            onAddSuccessListener();
            invalidate();

        }else {
            Toast.makeText(getContext(),"购物车商品数量满了",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 当加事件成功触发调用
     */
    public void onAddSuccessListener(){

        if(mCount==1){
            mAnimReduce.start();

        }
    }
    /**
     * 当减事件成功触发调用
     */
    public void onDelSuccessListener(){
        if(mCount == 0){
            //数量为零时调用圆圈收缩动画
            mAnimDel.start();

        }

    }
    //设置Count数量改变时的监听事件
    public void setOnCountChangeListener(OnCountChangeListener listener){

        this.listener = listener;
    }
    interface OnCountChangeListener{
        void onCountChanged(int mCount);
    }

    public int getLeftColor() {
        return leftColor;
    }

    public void setLeftColor(int leftColor) {
        this.leftColor = leftColor;
    }

    public int getCountColor() {
        return countColor;
    }

    public void setCountColor(int countColor) {
        this.countColor = countColor;
    }

    public float getCountSize() {
        return countSize;
    }

    public void setCountSize(float countSize) {
        this.countSize = countSize;
    }

    public int getRightCircleColor() {
        return rightCircleColor;
    }

    public void setRightCircleColor(int rightCircleColor) {
        this.rightCircleColor = rightCircleColor;
    }

    public int getRightAddColor() {
        return rightAddColor;
    }

    public void setRightAddColor(int rightAddColor) {
        this.rightAddColor = rightAddColor;
    }

    public int getRightCircleUnEnableColor() {
        return rightCircleUnEnableColor;
    }

    public void setRightCircleUnEnableColor(int rightCircleUnEnableColor) {
        this.rightCircleUnEnableColor = rightCircleUnEnableColor;
    }

    public int getHintTextColor() {
        return hintTextColor;
    }

    public void setHintTextColor(int hintTextColor) {
        this.hintTextColor = hintTextColor;
    }

    public float getHintTextSize() {
        return hintTextSize;
    }

    public void setHintTextSize(float hintTextSize) {
        this.hintTextSize = hintTextSize;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }
}
