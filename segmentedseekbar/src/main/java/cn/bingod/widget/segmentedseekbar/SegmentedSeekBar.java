package cn.bingod.widget.segmentedseekbar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.max;
import static android.R.attr.padding;
import static android.R.attr.start;
import static android.R.attr.text;
import static android.R.attr.x;

/**
 * <p>a segmented view like o——o——o</p>
 * <p>segmentCount is always >= 1</p>
 *
 * @author bingod
 * @since 2016/4/8
 */
public class SegmentedSeekBar extends View {

    private String TAG = getClass().getSimpleName();
    /**
     * 分段数
     */
    private int segmentCount;
    /**
     * 分段线底色
     */
    private int lineColor;
    /**
     * 文字颜色
     */
    private int textColor;
    /**
     * 当前刻度高亮色
     */
    private int textAccentColor;
    /**
     * 分段线粗细
     */
    private int lineWeight;
    /**
     * 文字大小
     */
    private int textSize;
    /**
     * 文字与线段的间距
     */
    private int textPadding;
    /**
     * 方向
     */
    private int orientation;
    /**
     * 文字方向,在线段上面或者下面或者左边或者右边
     */
    private int textAlign;
    /**
     * 分段的图片
     */
    private Drawable segmentDrawable;
    /**
     * 分段的文字信息
     */
    private CharSequence[] segmentTexts;
    /**
     * 点击移动的图片
     */
    private Drawable touchDrawable;
    private Paint paint;
    private TextPaint textPaint;
    private TextPaint textAccentPaint;
    private Rect segmentDrawableBounds;
    private Rect touchDrawableBounds;
    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;
    private final int TOP = 10;
    private final int BOTTOM = 11;
    private final int LEFT = 12;
    private final int RIGHT = 13;
    private int x_center;
    private int y_center;
    private int startX;
    private int startY;
    private int stopX;
    private int stopY;
    private int index = 0;
    private int segmentIndex = 0;
    private int[] index_x;
    private int[] index_y;
    private int boundsGap;
    private int touchX;
    private int touchY;
    private int segmentDistance;
    private OnSegmentSeekListener onSegmentSeekListener;
    private float lineX;
    private float lineY;

    public SegmentedSeekBar(Context context) {
        super(context);
    }

    public SegmentedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /*代码设置属性*/
    public void setSegmentCount(int segmentCount) {
        this.segmentCount = segmentCount;
        initIndexArray();
        setIndex(0);
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }

    public void setLineWeight(int lineWeight) {
        this.lineWeight = lineWeight;
        invalidate();
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        invalidate();
    }

    public void setSegmentDrawable(Drawable segmentDrawable) {
        this.segmentDrawable = segmentDrawable;
        invalidate();
    }

    public void setSegmentTexts(CharSequence[] segmentTexts) {
        this.segmentTexts = segmentTexts;
        invalidate();
    }

    public void setTouchDrawable(Drawable touchDrawable) {
        this.touchDrawable = touchDrawable;
        setDrawableBounds();
        invalidate();
    }

    public void setIndex(int index) {
        this.segmentIndex = this.index = index;
        if(index_x != null)touchX = index_x[index];
        if(index_y != null)touchY = index_y[index];
        invalidate();
    }

    public void setOnSegmentSeekListener(OnSegmentSeekListener onSegmentSeekListener) {
        this.onSegmentSeekListener = onSegmentSeekListener;
    }

    public int getTouchY() {
        return touchY;
    }

    public void setTouchY(int touchY) {
        this.touchY = touchY;
    }

    public int getTouchX() {
        return touchX;
    }

    public void setTouchX(int touchX) {
        this.touchX = touchX;
    }

    /*xml设置属性*/
    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SegmentedSeekBar);
        try {
            segmentCount = ta.getInteger(R.styleable.SegmentedSeekBar_segmentCount, 1);
            orientation = ta.getInteger(R.styleable.SegmentedSeekBar_orientation, 0);
            textAlign = ta.getInteger(R.styleable.SegmentedSeekBar_textAlign, TOP);
            segmentDrawable = ta.getDrawable(R.styleable.SegmentedSeekBar_segmentDrawable);
            segmentTexts = ta.getTextArray(R.styleable.SegmentedSeekBar_segmentStrings);
            textColor = ta.getColor(R.styleable.SegmentedSeekBar_text_color, Color.GRAY);
            textAccentColor = ta.getColor(R.styleable.SegmentedSeekBar_text_AccentColor, Color.MAGENTA);
            touchDrawable = ta.getDrawable(R.styleable.SegmentedSeekBar_touchDrawable);
            lineColor = ta.getColor(R.styleable.SegmentedSeekBar_line_color, Color.GRAY);
            lineWeight = ta.getDimensionPixelSize(R.styleable.SegmentedSeekBar_line_weight, 1);
            textSize = ta.getDimensionPixelSize(R.styleable.SegmentedSeekBar_text_size, 11);
            textPadding = ta.getDimensionPixelSize(R.styleable.SegmentedSeekBar_textPadding, 1);
            if (segmentTexts != null && segmentTexts.length > 1) {
                segmentCount = segmentTexts.length - 1;
            }
        } finally {
            ta.recycle();
        }

        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(lineColor);
        }
        if (textPaint == null) {
            textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
        }
        if (textAccentPaint == null) {
            textAccentPaint = new TextPaint();
            textAccentPaint.setAntiAlias(true);
            textAccentPaint.setColor(textAccentColor);
            textAccentPaint.setTextSize(textSize);
        }
        setDrawableBounds();
        initIndexArray();
    }

    private void initIndexArray() {
        switch (orientation) {
            case HORIZONTAL:
                index_x = new int[segmentCount + 1];
                break;
            case VERTICAL:
                index_y = new int[segmentCount + 1];
                break;
            default:
                break;
        }
    }

    private void setDrawableBounds() {
        if (segmentDrawable != null){
            segmentDrawable.setBounds(0, 0, segmentDrawable.getIntrinsicWidth(), segmentDrawable.getIntrinsicHeight());
            segmentDrawableBounds = segmentDrawable.getBounds();
        }
        if (touchDrawable != null) {
            touchDrawable.setBounds(0, 0, touchDrawable.getIntrinsicWidth(), touchDrawable.getIntrinsicHeight());
            touchDrawableBounds = touchDrawable.getBounds();
        }
        switch (orientation) {
            case VERTICAL:
                boundsGap = Math.abs(touchDrawableBounds.height() - segmentDrawableBounds.height());
                break;
            case HORIZONTAL:
            default:
                boundsGap = Math.abs(touchDrawableBounds.width() - segmentDrawableBounds.width());
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        touchX = getPaddingLeft();
        if (touchX == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            touchX = getPaddingStart();
        }
        touchY = getPaddingTop();
        if (hasSegmentTexts()) {
            switch (orientation) {
                case VERTICAL:
                    getLineX(w);
                    break;
                case HORIZONTAL:
                default:
                    getLineY(h);
                    break;
            }
        }
    }

    private void getLineY(int h) {
        int contentHeight = getVerticalBigBounds().height();
        int halfBounds = getVerticalBigBounds().height() >> 1;
        int textHeight = getTextHeight(textPaint, segmentTexts[0].toString()) + textPadding;
        if(textHeight >= halfBounds) {
            contentHeight = textHeight + halfBounds;
        }
        int padding = (h - getPaddingTop() - getPaddingBottom() - contentHeight) >> 1;
        switch (textAlign) {
            case BOTTOM:
                lineY = getPaddingTop() + padding + halfBounds + (lineWeight>>1);
                break;
            case TOP:
            default:
                lineY = getPaddingTop() + padding + contentHeight - halfBounds - (lineWeight>>1);
                break;
        }
    }

    private void getLineX(int w) {
        int contentWidth = getHorizontalBigBounds().width();
        int halfBounds = getHorizontalBigBounds().width() >> 1;
        int textWidth = (int) (getTextWidth() + textPadding);
        if(textWidth >= halfBounds) {
            contentWidth = textWidth + halfBounds;
        }
        int padding = (w - getPaddingLeft() - getPaddingRight() - contentWidth) >> 1;
        switch (textAlign) {
            case LEFT:
                lineX = getPaddingLeft() + padding + contentWidth - halfBounds - (lineWeight>>1);
                break;
            case RIGHT:
            default:
                lineX = getPaddingLeft() + padding + halfBounds + (lineWeight>>1);
                break;
        }
    }

    private float getTextWidth() {
        float textWidth = textPaint.measureText(segmentTexts[0].toString());
        for (CharSequence segmentText : segmentTexts) {
            float width = textPaint.measureText(segmentText.toString());
            if(width > textWidth){
                textWidth = width;
            }
        }
        return textWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int minWidth = segmentDrawableBounds.width();
            width = Math.max(touchDrawableBounds.width(), minWidth);
            if (segmentTexts != null && segmentTexts.length > 1) {
                switch (orientation) {
                    case VERTICAL:
                        width += getTextWidth() + textPadding;
                        break;
                    case HORIZONTAL:
                    default:
                        for (CharSequence segmentText : segmentTexts) {
                            if (segmentText != null) {
                                width += textPaint.measureText(segmentText.toString());
                            }
                        }
                        break;
                }
            }
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            if (paddingLeft == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paddingLeft = getPaddingStart();
            }
            if (paddingRight == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paddingRight = getPaddingEnd();
            }
            width += paddingLeft;
            width += paddingRight;
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(widthSize, width);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int minHeight = segmentDrawableBounds.height();
            height = Math.max(touchDrawableBounds.height(), minHeight);
            if (hasSegmentTexts()) {
                int textHeight = getTextHeight(textPaint, segmentTexts[0].toString()) + textPadding;
                if(textHeight >= (height >> 1)) {
                    height = textHeight + (height >> 1);
                }
            }
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            height += paddingTop;
            height += paddingBottom;
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(heightSize, height);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //默认居中
        if (y_center == 0)
            y_center = ((getHeight() - getPaddingTop() - getPaddingBottom()) >> 1) + getPaddingTop();
        if (x_center == 0)
            x_center = ((getWidth() - getPaddingLeft() - getPaddingRight()) >> 1) + getPaddingLeft();
        //画线
        drawLine(canvas, orientation);
        //画第一个点和最后一个点
        drawStartAndEnd(canvas, orientation);
        //画中间其他点
        if (segmentCount > 1) drawOther(canvas, orientation);
        //画触摸移动图片
        drawTouchableDrawble2Index(canvas, orientation);
    }

    private void drawLine(Canvas canvas, int orientation) {
        if (lineWeight <= 0) return;
        float strokeWidth = paint.getStrokeWidth();
        int color = paint.getColor();
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWeight);
        switch (orientation) {
            case HORIZONTAL:
                drawHorizontalLine(canvas);
                break;
            case VERTICAL:
                drawVerticalLine(canvas);
                break;
            default:
                break;
        }
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
    }

    private void drawVerticalLine(Canvas canvas) {
        float startX = x_center;
        float startY = getPaddingTop() + (getVerticalBigBounds().height()>>1);
        if (hasSegmentTexts()) {
            startX = lineX;
        }
        float stopX = startX;
        float stopY = (getHeight() - getPaddingBottom()) - (getVerticalBigBounds().width() >> 1);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private void drawHorizontalLine(Canvas canvas) {
        float startX = getPaddingLeft() + (getHorizontalBigBounds().width() >> 1);
        float startY = y_center;
        float stopX = (getWidth() - getPaddingRight()) - (getHorizontalBigBounds().width() >> 1);
        if (hasSegmentTexts()) {
            startY = lineY;
        }
        float stopY = startY;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private void drawStartAndEnd(Canvas canvas, int orientation) {
        switch (orientation) {
            case HORIZONTAL:
                drawStartAndEndHorizontal(canvas);
                break;
            case VERTICAL:
                drawStartAndEndVertical(canvas);
                break;
            default:
                break;
        }
    }

    private void drawStartAndEndVertical(Canvas canvas) {
        int x_start = x_center - (segmentDrawableBounds.height() >> 1);
        int y_start = getPaddingTop() + (boundsGap >> 1);
        if (hasSegmentTexts()) {
            x_start = (int) (lineX - (segmentDrawableBounds.height() >> 1));
        }
        drawSegmentDrawable(canvas, x_start, y_start);
        int x_end = x_start;
        int y_end = getHeight() - getPaddingTop() - getVerticalBigBounds().height() + (boundsGap >> 1);
        drawSegmentDrawable(canvas, x_end, y_end);
        startX = x_start;
        startY = y_start;
        stopX = x_end;
        stopY = y_end;
        index_y[0] = startY - (boundsGap >> 1);
        index_y[index_y.length - 1] = stopY - (boundsGap >> 1);
        if (hasSegmentTexts()) {
            int x_text = (int)lineX;
            switch (textAlign) {
                case LEFT:
                    x_text -= textPadding + (int)textPaint.measureText(segmentTexts[0].toString()) + (lineWeight>>1);
                    break;
                case RIGHT:
                default:
                    x_text += textPadding + (lineWeight>>1);
                    break;
            }
            if (segmentTexts[0] != null) {
                int y_text = y_start + (segmentDrawableBounds.height()>>1) + (int)textPaint.descent();
                if (y_text < 0) y_text = 0;
                canvas.drawText(segmentTexts[0].toString(), x_text, y_text, index == 0 ? textAccentPaint : textPaint);
            }
            if (segmentTexts[segmentTexts.length - 1] != null) {
                int y_text = y_end + (segmentDrawableBounds.height()>>1) + (int)textPaint.descent();
                int max_y_text = getHeight() - getPaddingBottom() - (int)textPaint.descent();
                if (y_text > max_y_text) y_text = max_y_text;
                canvas.drawText(segmentTexts[segmentTexts.length - 1].toString(), x_text, y_text,
                        index == (segmentTexts.length - 1) ? textAccentPaint : textPaint);
            }
        }
    }

    private void drawStartAndEndHorizontal(Canvas canvas) {
        int x_start = getPaddingLeft() + (boundsGap >> 1);
        int y_start = y_center - (segmentDrawableBounds.height() >> 1);
        if (hasSegmentTexts()) {
            y_start = (int) (lineY - (segmentDrawableBounds.height() >> 1));
        }
        drawSegmentDrawable(canvas, x_start, y_start);
        int x_end = (getWidth() - getPaddingRight() - getHorizontalBigBounds().width()) + (boundsGap >> 1);
        int y_end = y_start;
        drawSegmentDrawable(canvas, x_end, y_end);
        startX = x_start;
        startY = y_start;
        stopX = x_end;
        stopY = y_end;
        index_x[0] = startX - (boundsGap >> 1);
        index_x[index_x.length - 1] = stopX - (boundsGap >> 1);
        if (hasSegmentTexts()) {
            int y_text = y_center;
            switch (textAlign) {
                case BOTTOM:
                    y_text += (textPadding + getTextHeight(textPaint, segmentTexts[0].toString())) >> 1;
                    break;
                case TOP:
                default:
                    y_text -= (textPadding >> 1);
                    break;
            }
            if (segmentTexts[0] != null) {
                int x_text = x_start - ((int) textPaint.measureText(segmentTexts[0].toString()) >> 1) + (segmentDrawableBounds.width() >> 1);
                if (x_text < 0) x_text = 0;
                canvas.drawText(segmentTexts[0].toString(), x_text, y_text, index == 0 ? textAccentPaint : textPaint);
            }
            if (segmentTexts[segmentTexts.length - 1] != null) {
                int x_text = x_end - ((int) textPaint.measureText(segmentTexts[segmentTexts.length - 1].toString()) >> 1) + (segmentDrawableBounds.width() >> 1);
                int max_x_text = getWidth() - getPaddingRight() - (int) textPaint.measureText(segmentTexts[segmentTexts.length - 1].toString());
                if (x_text > max_x_text) x_text = max_x_text;
                canvas.drawText(segmentTexts[segmentTexts.length - 1].toString(), x_text, y_text, index == (segmentTexts.length - 1) ? textAccentPaint : textPaint);
            }
        }
    }

    private void drawOther(Canvas canvas, int orientation) {
        switch (orientation) {
            case VERTICAL:
                drawOtherVertical(canvas);
                break;
            case HORIZONTAL:
            default:
                drawOtherHorizontal(canvas);
                break;
        }
    }

    private void drawOtherVertical(Canvas canvas) {
        int count = segmentCount - 1;
        int y;
        segmentDistance = (stopY - startY) / segmentCount;
        for (int i = 0; i < count; i++) {
            y = startY + segmentDistance * (1 + i);
            index_y[i + 1] = y - (Math.abs(boundsGap) >> 1);
            drawSegmentDrawable(canvas, startX, y);
            if (hasSegmentTexts()) {
                String text = segmentTexts[i + 1].toString();
                int x_text = (int)lineX;
                int y_text = y + (segmentDrawableBounds.height()>>1) + (int)textPaint.descent();
                switch (textAlign) {
                    case LEFT:
                        x_text -= textPadding + (int)textPaint.measureText(text) + (lineWeight>>1);
                        break;
                    case RIGHT:
                    default:
                        x_text += textPadding + (lineWeight >> 1);
                        break;
                }
                canvas.drawText(text, x_text, y_text, index == (i + 1) ? textAccentPaint : textPaint);
            }
        }
    }

    private void drawOtherHorizontal(Canvas canvas) {
        int count = segmentCount - 1;
        int x;
        segmentDistance = (stopX - startX) / segmentCount;
        for (int i = 0; i < count; i++) {
            x = startX + segmentDistance * (1 + i);
            index_x[i + 1] = x - (Math.abs(boundsGap) >> 1);
            drawSegmentDrawable(canvas, x, startY);
            if (hasSegmentTexts()) {
                String text = segmentTexts[i + 1].toString();
                int x_text = x - ((int) textPaint.measureText(text) >> 1) + (segmentDrawableBounds.width() >> 1);
                int y_text = y_center;
                switch (textAlign) {
                    case BOTTOM:
                        y_text += (textPadding + getTextHeight(textPaint, segmentTexts[0].toString())) >> 1;
                        break;
                    case TOP:
                    default:
                        y_text -= (textPadding >> 1);
                        break;
                }
                canvas.drawText(text, x_text, y_text, index == (i + 1) ? textAccentPaint : textPaint);
            }
        }
    }

    private void drawTouchableDrawble2Index(Canvas canvas, int orientation) {
        switch (orientation) {
            case VERTICAL:
                if (segmentIndex > 0) {
                    if(index_x != null)touchX = index_x[index];
                    if(index_y != null)touchY = index_y[index];
                    segmentIndex = 0;
                }
                touchX = x_center - (touchDrawableBounds.bottom >> 1);
                if (hasSegmentTexts()) {
                    touchX = (int) (lineX - (getVerticalBigBounds().height()>>1));
                }
                drawTouchDrawble(canvas, touchX, touchY);
                break;
            case HORIZONTAL:
            default:
                if (segmentIndex > 0) {
                    if(index_x != null)touchX = index_x[index];
                    if(index_y != null)touchY = index_y[index];
                    segmentIndex = 0;
                }
                touchY = y_center - (touchDrawableBounds.bottom >> 1);
                if (hasSegmentTexts()) {
                    touchY = (int) (lineY - (getVerticalBigBounds().height()>>1));
                }
                drawTouchDrawble(canvas, touchX, touchY);
                break;
        }
    }

    private void drawTouchDrawble(Canvas canvas, int x, int y) {
        canvas.save();
        canvas.translate(x, y);
        touchDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawSegmentDrawable(Canvas canvas, int x, int y) {
        canvas.save();
        canvas.translate(x, y);
        segmentDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp();
                break;
            default:
                break;
        }
        performClick();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void actionMove(MotionEvent event) {
        switch (orientation) {
            case HORIZONTAL:
                moveX((int) event.getX());
                break;
            case VERTICAL:
                moveY((int) event.getY());
                break;
            default:
                break;
        }
    }

    private void actionDown(MotionEvent event) {
        actionMove(event);
    }

    private void actionUp() {
        //判断离哪个最近
        int min = segmentDistance;
        switch (orientation) {
            case VERTICAL:
                for (int i = 0; i < index_y.length; i++) {
                    //获取每一个距离
                    int m = Math.abs(touchY - index_y[i]);
                    if (m < min) {
                        min = m;
                        index = i;
                    }
                }
                //移动至对应点
                if (onSegmentSeekListener != null)
                    onSegmentSeekListener.onSegmentSeek(index);
                anim2Point(touchY, index_y[index]);
                break;
            case HORIZONTAL:
            default:
                for (int i = 0; i < index_x.length; i++) {
                    //获取每一个距离
                    int m = Math.abs(touchX - index_x[i]);
                    if (m < min) {
                        min = m;
                        index = i;
                    }
                }
                //移动至对应点
                if (onSegmentSeekListener != null)
                    onSegmentSeekListener.onSegmentSeek(index);
                anim2Point(touchX, index_x[index]);
                break;
        }

    }

    private void moveX(int x) {
        //计算x
        touchX = x - (touchDrawableBounds.bottom >> 1);
        //边界判断
        processTouchCoord(touchX);
        invalidate();
    }

    private void moveY(int y) {
        //计算y
        touchY = y - (touchDrawableBounds.height() >> 1);
        //边界判断
        processTouchCoord(touchY);
        invalidate();
    }

    private boolean processTouchCoord(int coord) {
        boolean r = false;
        switch (orientation) {
            case HORIZONTAL:
                int minX = getPaddingLeft();
                int maxX = getWidth() - getPaddingRight() - getHorizontalBigBounds().width();
                if (coord < minX) {
                    touchX = minX;
                    r = true;
                }
                if (coord > maxX) {
                    touchX = maxX;
                    r = true;
                }
                break;
            case VERTICAL:
                int minY = getPaddingTop();
                int maxY = getHeight() - getPaddingBottom() - getVerticalBigBounds().width();
                if (coord < minY) {
                    touchY = minY;
                    r = true;
                }
                if (coord > maxY) {
                    touchY = maxY;
                    r = true;
                }
                break;
            default:
                break;
        }
        return r;
    }

    private void anim2Point(int from, int to) {
        int animTime = 180;
        switch (orientation) {
            case VERTICAL:
                ObjectAnimator vAnimator = ObjectAnimator.ofInt(this, "touchY", from, to);
                vAnimator.setDuration(animTime).setInterpolator(new DecelerateInterpolator());
                vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        touchX = (int) animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
                vAnimator.start();
                break;
            case HORIZONTAL:
            default:
                ObjectAnimator hAnimator = ObjectAnimator.ofInt(this, "touchX", from, to);
                hAnimator.setDuration(animTime).setInterpolator(new DecelerateInterpolator());
                hAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        touchX = (int) animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
                hAnimator.start();
                break;
        }
    }

    private boolean hasSegmentTexts() {
        if (segmentTexts != null && segmentTexts.length > 1) return true;
        return false;
    }

    private int getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    private Rect getHorizontalBigBounds(){
        Rect big = segmentDrawableBounds;
        int boundsGap = touchDrawableBounds.width() - segmentDrawableBounds.width();
        if (boundsGap > 0) {
            big = touchDrawableBounds;
        }
        return big;
    }

    private Rect getVerticalBigBounds(){
        Rect big = segmentDrawableBounds;
        int boundsGap = touchDrawableBounds.height() - segmentDrawableBounds.height();
        if (boundsGap > 0) {
            big = touchDrawableBounds;
        }
        return big;
    }

    public interface OnSegmentSeekListener {
        void onSegmentSeek(int index);
    }
}
