package com.example.petstore.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.petstore.R;

public class PurchaseButton extends View {

    private final static int STATE_NONE = 0;
    private final static int STATE_MOVE = 1;
    private final static int STATE_MOVE_OVER = 2;
    private final static int STATE_ROTATE = 3;
    private final static int STATE_ROTATE_OVER = 4;

    private final static int DEFAULT_DURATION = 250;
    private final static String DEFAULT_SHOPPING_TEXT = "加入购物车";

    private Paint mPaintBg, mPaintText, mPaintNum;
    private Paint mPaintMinus;
    private Path mPath;

    //是否是向前状态
    private boolean mIsForward = true;
    //动画时长
    private int mDuration;
    //购买数量
    private int mNum = 0;
    //展示文案
    private String mShoppingText;
    //当前状态
    private int mState = STATE_NONE;
    //属性值
    private int mWidth = 0;
    private int mAngle = 0;
    private int mTextPosition = 0;
    private int mMinusBtnPosition = 0;
    private int mAlpha = 0;

    private int MAX_WIDTH;
    private int MAX_HEIGHT;

    private ShoppingClickListener mShoppingClickListener;

    public PurchaseButton(Context context) {
        this(context, null);
    }

    public PurchaseButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PurchaseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    public void setIsForward(boolean isForward) {
        this.mIsForward = isForward;
    }


    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PurchaseButton);
        mDuration = typedArray.getInt(R.styleable.PurchaseButton_pb_duration, DEFAULT_DURATION);
        mShoppingText = TextUtils.isEmpty(typedArray.getString(R.styleable.PurchaseButton_pb_text)) ? DEFAULT_SHOPPING_TEXT : typedArray.getString(R.styleable.PurchaseButton_pb_text);
        int textSize = (int) typedArray.getDimension(R.styleable.PurchaseButton_pb_text_size, sp2px(16));
        int bgColor = typedArray.getColor(R.styleable.PurchaseButton_pb_bg_color, ContextCompat.getColor(getContext(), R.color.black));
        typedArray.recycle();

        mPaintBg = new Paint();
        mPaintBg.setColor(bgColor);
        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintBg.setAntiAlias(true);

        mPaintMinus = new Paint();
        mPaintMinus.setColor(bgColor);
        mPaintMinus.setStyle(Paint.Style.STROKE);
        mPaintMinus.setAntiAlias(true);
        mPaintMinus.setStrokeWidth(textSize / 6);
        mPaintText = new Paint();
        mPaintText.setColor(Color.WHITE);
        mPaintText.setStrokeWidth(textSize / 6);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);

        mPaintNum = new Paint();
        mPaintNum.setColor(Color.BLACK);
        mPaintNum.setTextSize(textSize / 3 * 4);
        mPaintNum.setStrokeWidth(textSize / 6);
        mPaintNum.setAntiAlias(true);
        mPath = new Path();
        MAX_WIDTH = getTextWidth(mPaintText, mShoppingText) / 5 * 8;
        MAX_HEIGHT = textSize * 2;

        if (MAX_WIDTH / (float) MAX_HEIGHT < 3.5) {
            MAX_WIDTH = (int) (MAX_HEIGHT * 3.5);
        }

        mTextPosition = MAX_WIDTH / 2;
        mMinusBtnPosition = MAX_HEIGHT / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MAX_WIDTH, MAX_HEIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mState == STATE_NONE) {
            drawBgMove(canvas);
            drawShoppingText(canvas);
        } else if (mState == STATE_MOVE) {
            drawBgMove(canvas);
        } else if (mState == STATE_MOVE_OVER) {
            mState = STATE_ROTATE;
            if (mIsForward) {
                drawAddBtn(canvas);
                startRotateAnim();
            } else {
                drawBgMove(canvas);
                drawShoppingText(canvas);
                mState = STATE_NONE;
                mIsForward = true;
                mNum = 0;
            }
        } else if (mState == STATE_ROTATE) {
            mPaintMinus.setAlpha(mAlpha);
            mPaintNum.setAlpha(mAlpha);

            drawMinusBtn(canvas, mAngle);
            drawNumText(canvas);
            drawAddBtn(canvas);
        } else if (mState == STATE_ROTATE_OVER) {
            drawMinusBtn(canvas, mAngle);
            drawNumText(canvas);
            drawAddBtn(canvas);
            if (!mIsForward) {
                startMoveAnim();
            }
        }
    }

    /*
    绘制移动的背景
     */
    private void drawBgMove(Canvas canvas) {
        canvas.drawArc(new RectF(mWidth, 0, mWidth + MAX_HEIGHT, MAX_HEIGHT), 90, 180, false, mPaintBg);
        canvas.drawRect(new RectF(mWidth + MAX_HEIGHT / 2, 0, MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT), mPaintBg);
        canvas.drawArc(new RectF(MAX_WIDTH - MAX_HEIGHT, 0, MAX_WIDTH, MAX_HEIGHT), 180, 270, false, mPaintBg);
    }

    /*
    绘制按钮文案
     */
    private void drawShoppingText(Canvas canvas) {
        canvas.drawText(mShoppingText, MAX_WIDTH / 2 - getTextWidth(mPaintText, mShoppingText) / 2f, MAX_HEIGHT / 2 + getTextHeight(mShoppingText, mPaintText) / 2f, mPaintText);
    }

    /*
    绘制加号按钮
     */
    private void drawAddBtn(Canvas canvas) {
        canvas.drawCircle(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 2, MAX_HEIGHT / 2, mPaintBg);
        canvas.drawLine(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 4, MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 4 * 3, mPaintText);
        canvas.drawLine(MAX_WIDTH - MAX_HEIGHT / 2 - MAX_HEIGHT / 4, MAX_HEIGHT / 2, MAX_WIDTH - MAX_HEIGHT / 4, MAX_HEIGHT / 2, mPaintText);
    }

    /*
    绘制减号按钮
     */
    private void drawMinusBtn(Canvas canvas, float angle) {
        if (angle != 0) {
            canvas.rotate(angle, mMinusBtnPosition, MAX_HEIGHT / 2);
        }
        canvas.drawCircle(mMinusBtnPosition, MAX_HEIGHT / 2, MAX_HEIGHT / 2 - MAX_HEIGHT / 20, mPaintMinus);
        canvas.drawLine(mMinusBtnPosition - MAX_HEIGHT / 4, MAX_HEIGHT / 2, mMinusBtnPosition + MAX_HEIGHT / 4, MAX_HEIGHT / 2, mPaintMinus);
        if (angle != 0) {
            canvas.rotate(-angle, mMinusBtnPosition, MAX_HEIGHT / 2);
        }
    }

    /*
    绘制购买数量
     */

    private void drawNumText(Canvas canvas) {
        drawText(canvas, String.valueOf(mNum), mTextPosition - getTextWidth(mPaintNum, String.valueOf(mNum)) / 2f, MAX_HEIGHT / 2 + getTextHeight(String.valueOf(mPaintNum), mPaintNum) / 2f, mPaintNum, mAngle);
    }

    /*
    绘制Text带角度
     */
    private void drawText(Canvas canvas, String text, float x, float y, Paint paint, float angle) {
        if (angle != 0) {
            canvas.rotate(angle, x, y);
        }
        canvas.drawText(text, x, y, paint);
        if (angle != 0) {
            canvas.rotate(-angle, x, y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mState == STATE_NONE) {
                    mNum++;
                    if (mShoppingClickListener != null) {
                        mShoppingClickListener.onfirst(mNum);
                    }
                    startMoveAnim();
                } else if (mState == STATE_ROTATE_OVER) {
                    if (isPointInCircle(new PointF(event.getX(), event.getY()), new PointF(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 2), MAX_HEIGHT / 2)) {
                        if (mNum > 0) {
                            mNum++;
                            mIsForward = true;
                            if (mShoppingClickListener != null) {
                                mShoppingClickListener.onAddClick(mNum);
                            }
                        }
                        invalidate();
                    } else if (isPointInCircle(new PointF(event.getX(), event.getY()), new PointF(MAX_HEIGHT / 2, MAX_HEIGHT / 2), MAX_HEIGHT / 2)) {
                        if (mNum > 1) {
                            mNum--;
                            if (mShoppingClickListener != null) {
                                mShoppingClickListener.onMinusClick(mNum);
                            }
                            invalidate();
                        } else {
                            if (mShoppingClickListener != null) {
                                mShoppingClickListener.onMinusClick(0);
                            }
                            mState = STATE_ROTATE;
                            mIsForward = false;
                            startRotateAnim();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /*
    开始移动动画
     */
    public void startMoveAnim() {
        mState = STATE_MOVE;
        final ValueAnimator valueAnimator;
        if (mIsForward) {
            valueAnimator = ValueAnimator.ofInt(0, MAX_WIDTH - MAX_HEIGHT);
        } else {
            valueAnimator = ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT, 0);
        }
        valueAnimator.setDuration(mDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWidth = (Integer) valueAnimator.getAnimatedValue();
                if (mIsForward) {
                    if (mWidth == MAX_WIDTH - MAX_HEIGHT) {
                        mState = STATE_MOVE_OVER;
                    }
                } else {
                    if (mWidth == 0) {
                        mState = STATE_MOVE_OVER;
                    }
                }
                invalidate();
            }
        });
        valueAnimator.start();
    }

    /*
    开始旋转动画
     */
    public void startRotateAnim() {
        final ValueAnimator animatorTextRotate;
        if (mIsForward) {
            animatorTextRotate = ValueAnimator.ofInt(0, 360);
        } else {
            animatorTextRotate = ValueAnimator.ofInt(360, 0);
        }
        animatorTextRotate.setDuration(mDuration);
        animatorTextRotate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngle = (Integer) animation.getAnimatedValue();
                if (mIsForward) {
                    if (mAngle == 360) {
                        mState = STATE_ROTATE_OVER;
                    }
                } else {
                    if (mAngle == 0) {
                        mState = STATE_MOVE_OVER;
                    }
                }
                invalidate();
            }
        });
        ValueAnimator animatorAlpha;
        if (mIsForward) {
            animatorAlpha = ValueAnimator.ofInt(0, 255);
        } else {
            animatorAlpha = ValueAnimator.ofInt(255, 0);
        }
        animatorAlpha.setDuration(mDuration);
        animatorAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAlpha = (Integer) animation.getAnimatedValue();
                if (mIsForward) {
                    if (mAlpha == 255) {
                        mState = STATE_ROTATE_OVER;
                    }
                } else {
                    if (mAlpha == 0) {
                        mState = STATE_ROTATE_OVER;
                    }
                }
                invalidate();
            }
        });

        ValueAnimator animatorTextMove;
        if (mIsForward) {
            animatorTextMove = ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT / 2, MAX_WIDTH / 2);
        } else {
            animatorTextMove = ValueAnimator.ofInt(MAX_WIDTH / 2, MAX_WIDTH - MAX_HEIGHT / 2);
        }
        animatorTextMove.setDuration(mDuration);
        animatorTextMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTextPosition = (Integer) animation.getAnimatedValue();
                if (mIsForward) {
                    if (mTextPosition == MAX_WIDTH / 2) {
                        mState = STATE_ROTATE_OVER;
                    }
                } else {
                    if (mTextPosition == MAX_WIDTH - MAX_HEIGHT / 2) {
                        mState = STATE_ROTATE_OVER;
                    }
                }
                invalidate();
            }
        });

        ValueAnimator animatorBtnMove;
        if (mIsForward) {
            animatorBtnMove = ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 2);
        } else {
            animatorBtnMove = ValueAnimator.ofInt(MAX_HEIGHT / 2, MAX_WIDTH - MAX_HEIGHT / 2);
        }

        animatorBtnMove.setDuration(mDuration);
        animatorBtnMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMinusBtnPosition = (Integer) animation.getAnimatedValue();
                if (mIsForward) {
                    if (mMinusBtnPosition == MAX_HEIGHT / 2) {
                        mState = STATE_ROTATE_OVER;
                    }
                } else {

                    if (mMinusBtnPosition == MAX_WIDTH - MAX_HEIGHT / 2) {
                        mState = STATE_ROTATE_OVER;
                    }
                }
                invalidate();
            }
        });
        animatorAlpha.start();
        animatorTextRotate.start();
        animatorTextMove.start();
        animatorBtnMove.start();
    }

    /*
    设置购买数量
     */
    public void setTextNum(int num) {
        mNum = num;
        mState = STATE_ROTATE_OVER;
        invalidate();
    }

    public void setOnShoppingClickListener(ShoppingClickListener shoppingClickListener) {
        this.mShoppingClickListener = shoppingClickListener;
    }

    public void resetView() {
        mNum = 0;
        mState = STATE_NONE;
        mAngle = 0; // 重置角度
        mWidth = 0; // 重置宽度
        mTextPosition = MAX_WIDTH / 2; // 重置文案位置
        mMinusBtnPosition = MAX_HEIGHT / 2; // 重置减号按钮位置
        mAlpha = 0; // 重置透明度
        mIsForward = true; // 重置方向为向前
        invalidate(); // 通知 View 重新绘制
    }

    public interface ShoppingClickListener {
        void onfirst(int num);
        void onAddClick(int num);

        void onMinusClick(int num);


    }

    /*
    判断是否在圆内
     */
    private boolean isPointInCircle(PointF pointF, PointF circle, float radius) {
        return Math.pow((pointF.x - circle.x), 2) + Math.pow((pointF.y - circle.y), 2) <= Math.pow(radius, 2);
    }

    private int sp2px(float spValue) {
        final float fontSize = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontSize + 0.5f);
    }

    private int getTextHeight(String str, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return (int) (rect.height() / 33f * 29);
    }

    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}

