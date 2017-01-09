package shayan.progressview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import java.util.ArrayList;

/**
 * Created by shayan on 1/7/17.
 */

public class CircularProgress extends View {
    private Paint firstPartFinishedPaint;
    private Paint unfinishedPaint;
    private Paint innerCirclePaint;

    ArrayList<RectF> finishedOuterRect = new ArrayList<>();
    ArrayList<RectF> unfinishedOuterRect = new ArrayList<>();
    private int progressNum = 2;

    private boolean indeterminate;
    private float progress = 0;
    private int max;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private int startingDegree;
    private int indeterminateSize;
    private float finishedStrokeWidth;
    private float unfinishedStrokeWidth;
    private int innerBackgroundColor;
    private int animationDuration;

    private RotateAnimation rotate;
    private boolean mAggregatedIsVisible;


    private final float default_stroke_width;
    private final int default_finished_color = Color.rgb(0, 51, 102);
    private final int default_unfinished_color = Color.argb(255, 192, 192, 192);
    private final int default_inner_background_color = Color.TRANSPARENT;
    private final int default_max = 360;
    private final int default_startingDegree = 0;
    private final int default_animation_duration = 3000;
    private final int min_size;


    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width";
    private static final String INSTANCE_UNFINISHED_STROKE_WIDTH = "unfinished_stroke_width";
    private static final String INSTANCE_BACKGROUND_COLOR = "inner_background_color";
    private static final String INSTANCE_STARTING_DEGREE = "starting_degree";
    private static final String INSTANCE_INDETERMINATE = "indeterminate";
    private static final String INSTANCE_INDETERMINATE_SIZE = "indeterminate_size";
    private static final String INSTANCE_PROGRESS_ANGEL_NUM = "progress_angel";
    private static final String INSTANCE_ANIMATION_DURATION = "animation_duration";

    /**
     * if you have this method in your project you can remove it
     */
    //region util part
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    //endregion

    //region constructors
    public CircularProgress(Context context) {
        this(context, null);
    }

    public CircularProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        min_size = (int) dp2px(getResources(), 100);
        default_stroke_width = dp2px(getResources(), 10);
        indeterminateSize = (int) dp2px(getResources(), 24);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Circular, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }
    //endregion

    //region override method

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    @Override
    public void postInvalidate() {
        initPainters();
        super.postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float delta = Math.max(finishedStrokeWidth, unfinishedStrokeWidth);

        for (int i = 0; i < progressNum; i++) {
            RectF finishPartRect = finishedOuterRect.get(i);
            finishPartRect.set(delta,
                    delta,
                    getCircularWidth() - delta,
                    getCircularHeight() - delta);
        }
        for (int i = 0; i < progressNum; i++) {
            RectF finishPartRect = unfinishedOuterRect.get(i);
            finishPartRect.set(delta,
                    delta,
                    getCircularWidth() - delta,
                    getCircularHeight() - delta);
        }


        float innerCircleRadius = (getCircularWidth() - Math.min(finishedStrokeWidth, unfinishedStrokeWidth) + Math.abs(finishedStrokeWidth - unfinishedStrokeWidth)) / 2f;
        canvas.drawCircle(getCircularWidth() / 2.0f, getCircularHeight() / 2.0f, innerCircleRadius, innerCirclePaint);


        for (int i = 1; i <= progressNum; i++) {
            canvas.drawArc(finishedOuterRect.get(i - 1),
                    ((i - 1) * (360 / progressNum)) + (getStartingDegree() + (360 / progressNum)) % 360,
                    getProgressAngle() / progressNum,
                    false,
                    firstPartFinishedPaint);
        }

        if (unfinishedStrokeWidth != 0) {

            for (int i = 1; i <= progressNum; i++) {
                canvas.drawArc(unfinishedOuterRect.get(i - 1),
                        (((i - 1) * (360 / progressNum)) + getStartingDegree() + (getProgressAngle() / progressNum)) % 360,
                        (360 / progressNum) - (getProgressAngle() / progressNum),
                        false,
                        unfinishedPaint);

            }
        }

        if (rotate == null) {
            createRotateAnimation(canvas);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_STARTING_DEGREE, getStartingDegree());
        bundle.putFloat(INSTANCE_PROGRESS, getProgress());
        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth());
        bundle.putFloat(INSTANCE_UNFINISHED_STROKE_WIDTH, getUnfinishedStrokeWidth());
        bundle.putInt(INSTANCE_BACKGROUND_COLOR, getInnerBackgroundColor());
        bundle.putBoolean(INSTANCE_INDETERMINATE, indeterminate);
        bundle.putInt(INSTANCE_INDETERMINATE_SIZE, indeterminateSize);
        bundle.putInt(INSTANCE_PROGRESS_ANGEL_NUM, progressNum);
        bundle.putInt(INSTANCE_ANIMATION_DURATION, animationDuration);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            finishedStrokeWidth = bundle.getFloat(INSTANCE_FINISHED_STROKE_WIDTH);
            unfinishedStrokeWidth = bundle.getFloat(INSTANCE_UNFINISHED_STROKE_WIDTH);
            innerBackgroundColor = bundle.getInt(INSTANCE_BACKGROUND_COLOR);
            animationDuration = bundle.getInt(INSTANCE_ANIMATION_DURATION);
            initPainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setStartingDegree(bundle.getInt(INSTANCE_STARTING_DEGREE));
            setProgress(bundle.getFloat(INSTANCE_PROGRESS));
            setIndeterminate(bundle.getBoolean(INSTANCE_INDETERMINATE));
            setIndeterminateSize(bundle.getInt(INSTANCE_INDETERMINATE_SIZE));
            setProgressNum(bundle.getInt(INSTANCE_PROGRESS_ANGEL_NUM));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (progress == 0 && progress == getMax()) {
            // don't need animation
            return;
        }

        if (getVisibility() != VISIBLE) {
            // view is not visible so not need to animation
            return;
        }

        postInvalidate();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stopAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != VISIBLE) {
            stopAnimation();
        }
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible != mAggregatedIsVisible) {
            mAggregatedIsVisible = isVisible;

            // let's be nice with the UI thread
            if (isVisible) {
                createRotateAnimation(null);
            } else {
                stopAnimation();
            }
        }
    }

    //endregion

    //region initializer

    protected void initPainters() {

        firstPartFinishedPaint = new Paint();
        firstPartFinishedPaint.setColor(finishedStrokeColor);
        firstPartFinishedPaint.setStyle(Paint.Style.STROKE);
        firstPartFinishedPaint.setAntiAlias(true);
        firstPartFinishedPaint.setStrokeWidth(finishedStrokeWidth);


        unfinishedPaint = new Paint();
        unfinishedPaint.setColor(unfinishedStrokeColor);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setAntiAlias(true);
        unfinishedPaint.setStrokeWidth(unfinishedStrokeWidth);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(innerBackgroundColor);
        innerCirclePaint.setAntiAlias(true);
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.Circular_Circular_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.Circular_Circular_unfinished_color, default_unfinished_color);

        setMax(attributes.getInt(R.styleable.Circular_Circular_max, default_max));
        setProgress(attributes.getFloat(R.styleable.Circular_Circular_progress, 0));
        finishedStrokeWidth = attributes.getDimension(R.styleable.Circular_Circular_finished_stroke_width, default_stroke_width);
        unfinishedStrokeWidth = attributes.getDimension(R.styleable.Circular_Circular_unfinished_stroke_width, default_stroke_width);

        startingDegree = attributes.getInt(R.styleable.Circular_Circular_starting_degree, default_startingDegree);
        innerBackgroundColor = attributes.getColor(R.styleable.Circular_Circular_background_color, default_inner_background_color);
        animationDuration = attributes.getInt(R.styleable.Circular_Circular_animation_duration, default_animation_duration);

        progressNum = attributes.getInt(R.styleable.Circular_Circular_progress_num, 2);
        indeterminate = attributes.getBoolean(R.styleable.Circular_Circular_indeterminate, false);

        for (int i = 0; i < progressNum; i++) {
            finishedOuterRect.add(new RectF());
            unfinishedOuterRect.add(new RectF());
        }
    }

    //endregion

    //region getter setter

    public float getFinishedStrokeWidth() {
        return finishedStrokeWidth;
    }

    public void setFinishedStrokeWidth(float finishedStrokeWidth) {
        this.finishedStrokeWidth = finishedStrokeWidth;
        postInvalidate();
    }

    public float getUnfinishedStrokeWidth() {
        return unfinishedStrokeWidth;
    }

    public void setUnfinishedStrokeWidth(float unfinishedStrokeWidth) {
        this.unfinishedStrokeWidth = unfinishedStrokeWidth;
        postInvalidate();
    }

    public float getProgress() {
        if (indeterminate) {
            return dp2px(getResources(), 5);
        }
        return progress;
    }

    public void setProgress(float progress) {

        // invalidate view is redundant
        if (getProgressAngle() == getProgressAngle(progress)) {
            return;
        }

        if (this.progress == progress) {
            return;
        }

        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }

        postInvalidate();

    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            postInvalidate();
        }
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
        postInvalidate();
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        postInvalidate();
    }

    public int getInnerBackgroundColor() {
        return innerBackgroundColor;
    }

    public void setInnerBackgroundColor(int innerBackgroundColor) {
        this.innerBackgroundColor = innerBackgroundColor;
        postInvalidate();
    }

    public int getStartingDegree() {
        return startingDegree;
    }

    public void setStartingDegree(int startingDegree) {
        this.startingDegree = startingDegree;
        postInvalidate();
    }

    public void setIndeterminate(boolean indeterminate) {
        if (this.indeterminate == indeterminate) {
            return;
        }
        this.indeterminate = indeterminate;
        stopAnimation();
        requestLayout();
    }

    public int getIndeterminateSize() {
        return indeterminateSize;
    }

    public void setIndeterminateSize(int indeterminateSize) {
        this.indeterminateSize = indeterminateSize;
    }

    public int getProgressNum() {
        return progressNum;
    }

    public void setProgressNum(int progressNum) {

        if (progressNum > this.progressNum) {
            for (int i = 0; i < progressNum - this.progressNum; i++) {
                finishedOuterRect.add(new RectF());
                unfinishedOuterRect.add(new RectF());
            }
        } else {
            for (int i = 0; i < this.progressNum - progressNum; i++) {
                finishedOuterRect.remove(0);
                unfinishedOuterRect.remove(0);
            }
        }

        this.progressNum = progressNum;

        postInvalidate();
    }

    public void setAnimationDuration(int animationDuration) {
        if (animationDuration == this.animationDuration) {
            return;
        }

        this.animationDuration = animationDuration;
        stopAnimation();
        postInvalidate();
    }

    //endregion

    //region private method

    private void stopAnimation() {
        clearAnimation();
        rotate = null;
    }


    private float getProgressAngle(float progress) {
        return progress / (float) max * 360f;
    }

    private float getProgressAngle() {
        return getProgress() / (float) max * 360f;
    }

    private int measure(int measureSpec) {

        if (indeterminate) {
            return (int) getCircularWidth();
        }

        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = min_size;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private float getCircularWidth() {
        if (indeterminate) {
            return dp2px(getResources(), getIndeterminateSize());
        }
        return getWidth();
    }

    private float getCircularHeight() {
        if (indeterminate) {
            return dp2px(getResources(), getIndeterminateSize());
        }
        return getHeight();
    }

    private void createRotateAnimation(Canvas canvas) {


        if (getVisibility() != VISIBLE || getWindowVisibility() != VISIBLE) {
            return;
        }

        if (progress == 0 || progress == getMax()) {
            return;
        }

        int width = canvas == null ? (int) (getCircularWidth() / 2) : canvas.getWidth() / 2;
        int height = canvas == null ? (int) (getCircularHeight() / 2) : canvas.getHeight() / 2;

        stopAnimation();

        rotate = new RotateAnimation(0, 360, width, height);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setDuration(animationDuration);
        startAnimation(rotate);

    }

    //endregion

}