package com.github.lzyzsd.circleprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bruce on 11/4/14.
 */
public class CircleProgress extends View {
    private Paint textPaint;
    private RectF rectF = new RectF();

    private float textSize;
    private int textColor;
    private int progress = 0;
    private int max;
    private int finishedColor;
    private int unfinishedColor;
    private int finishedDrawableId;
    private int unfinishedDrawableId;
    private Bitmap finishedBitmap;
    private Bitmap unfinishedBitmap;
    private String prefixText = "";
    private String suffixText = "%";

    private final int default_finished_color = Color.rgb(66, 145, 241);
    private final int default_unfinished_color = Color.rgb(204, 204, 204);
    private final int default_text_color = Color.WHITE;
    private final int default_max = 100;
    private final float default_text_size;
    private final int min_size;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_FINISHED_DRAWABLE = "finished_drawable";
    private static final String INSTANCE_UNFINISHED_DRAWABLE = "unfinished_drawable";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";

    private Paint paint = new Paint();

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_text_size = Utils.sp2px(getResources(), 18);
        min_size = (int) Utils.dp2px(getResources(), 100);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }


    protected void initByAttributes(TypedArray attributes) {
        finishedColor = attributes.getColor(R.styleable.CircleProgress_circle_finished_color, default_finished_color);
        unfinishedColor = attributes.getColor(R.styleable.CircleProgress_circle_unfinished_color, default_unfinished_color);
        finishedDrawableId = attributes.getResourceId(R.styleable.CircleProgress_circle_finished_drawable, -1);
        unfinishedDrawableId = attributes.getResourceId(R.styleable.CircleProgress_circle_unfinished_drawable, -1);
        if(finishedDrawableId != -1) {
            finishedBitmap = BitmapFactory.decodeResource(getContext().getResources(), finishedDrawableId);
        }
        if(unfinishedDrawableId != -1)
            unfinishedBitmap = BitmapFactory.decodeResource(getContext().getResources(), unfinishedDrawableId);
        textColor = attributes.getColor(R.styleable.CircleProgress_circle_text_color, default_text_color);
        textSize = attributes.getDimension(R.styleable.CircleProgress_circle_text_size, default_text_size);

        setMax(attributes.getInt(R.styleable.CircleProgress_circle_max, default_max));
        setProgress(attributes.getInt(R.styleable.CircleProgress_circle_progress, 0));

        if (attributes.getString(R.styleable.CircleProgress_circle_prefix_text) != null) {
            setPrefixText(attributes.getString(R.styleable.CircleProgress_circle_prefix_text));
        }
        if (attributes.getString(R.styleable.CircleProgress_circle_suffix_text) != null) {
            setSuffixText(attributes.getString(R.styleable.CircleProgress_circle_suffix_text));
        }
    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        paint.setAntiAlias(true);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int getFinishedColor() {
        return finishedColor;
    }

    public void setFinishedColor(int finishedColor) {
        this.finishedColor = finishedColor;
        this.invalidate();
    }

    public int getUnfinishedColor() {
        return unfinishedColor;
    }

    public void setUnfinishedColor(int unfinishedColor) {
        this.unfinishedColor = unfinishedColor;
        this.invalidate();
    }

    public int getFinishedDrawableId() {
        return finishedDrawableId;
    }

    public void setFinishedDrawableId(int finishedDrawable) {
        this.finishedDrawableId = finishedDrawable;
        invalidate();
    }

    public int getUnfinishedDrawableId() {
        return unfinishedDrawableId;
    }

    public void setUnfinishedDrawableId(int unfinishedDrawable) {
        this.unfinishedDrawableId = unfinishedDrawable;
        this.invalidate();
    }

    public String getPrefixText() {
        return prefixText;
    }

    public void setPrefixText(String prefixText) {
        this.prefixText = prefixText;
        this.invalidate();
    }

    public String getSuffixText() {
        return suffixText;
    }

    public void setSuffixText(String suffixText) {
        this.suffixText = suffixText;
        this.invalidate();
    }

    public String getDrawText() {
        return getPrefixText() + getProgress() + getSuffixText();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }

    public float getProgressPercentage() {
        return getProgress() / (float) getMax();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        rectF.set(0, 0, MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }


    @Override protected void onDraw(Canvas canvas) {
        float yHeight = getProgress() / (float) getMax() * getHeight();
        float radius = getWidth() / 2f;
        float angle = (float) (Math.acos((radius - yHeight) / radius) * 180 / Math.PI);
        float startAngle = 90 + angle;
        float sweepAngle = 360 - angle * 2;
        Rect unfinishedRect;
        Rect finishedSrcRect;
        Rect finishedDestRect;
        paint.setColor(getUnfinishedColor());
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);

        canvas.save();

         if(unfinishedBitmap != null) {
            unfinishedRect= new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(unfinishedBitmap, null, unfinishedRect, null);
        }

        canvas.rotate(180, getWidth() / 2, getHeight() / 2);
        paint.setColor(getFinishedColor());
        canvas.drawArc(rectF, 270 - angle, angle * 2, false, paint);

        canvas.restore();

        if(finishedBitmap != null) {
            finishedSrcRect = new Rect(0, getHeight() - (int)yHeight, getWidth(), getHeight());
            finishedDestRect= new Rect(0, getHeight() - (int)yHeight, getWidth(), getHeight() );
            canvas.drawBitmap(finishedBitmap, finishedSrcRect, finishedDestRect, null);
        }


        // Also works.
//        paint.setColor(getFinishedColor());
//        canvas.drawArc(rectF, 90 - angle, angle * 2, false, paint);

        String text = getDrawText();
        if (!TextUtils.isEmpty(text)) {
            float textHeight = textPaint.descent() + textPaint.ascent();
            canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, (getWidth() - textHeight) / 2.0f, textPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedColor());
        bundle.putInt(INSTANCE_UNFINISHED_DRAWABLE, getUnfinishedColor());
        bundle.putInt(INSTANCE_FINISHED_DRAWABLE, getFinishedDrawableId());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        bundle.putString(INSTANCE_PREFIX, getPrefixText());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            finishedColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            finishedDrawableId = bundle.getInt(INSTANCE_FINISHED_DRAWABLE);
            unfinishedDrawableId = bundle.getInt(INSTANCE_UNFINISHED_DRAWABLE);
            initPainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            prefixText = bundle.getString(INSTANCE_PREFIX);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(finishedDrawableId != -1) {
            finishedBitmap = getCircularclip(
                    Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(
                                    getContext().getResources(), finishedDrawableId
                            ), getWidth(), getHeight(), false)
            );
        }
        if(unfinishedDrawableId != -1) {
            unfinishedBitmap = getCircularclip(
                    Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(
                                    getContext().getResources(), unfinishedDrawableId
                            ), getWidth(), getHeight(), false)
            );
        }
    }

    protected static Bitmap getCircularclip(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
