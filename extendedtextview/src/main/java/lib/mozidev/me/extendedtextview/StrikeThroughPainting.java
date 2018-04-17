package lib.mozidev.me.extendedtextview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * created by zijing on 17/04/2018
 */
public class StrikeThroughPainting implements IPainting {

    /**
     * If there is only one line, the strike through line is
     * drawn from left to right. If there are multiple lines, strike through line
     * is drawn one line after another.
     */
    public static final int MODE_DEFAULT = 0;

    /**
     * This painting behaves the same when there is only one line.
     * But when there are multiple lines, all lines' strike through lines
     * are drawn simultaneously.
     */
    public static final int MODE_ALL_LINE_TOGETHER = 1;

    /**
     * Because the existence of `font padding`, adjust the strike through line position
     * from 0.5 to 0.6, to make the line in the center of texts.
     */
    private static final float STRIKE_THROUGH_POSITION = 0.6F;
    private static final float STRIKE_THROUGH_STROKE_WIDTH = 2F;
    private static final int STRIKE_THROUGH_COLOR = Color.BLACK;
    private static final long STRIKE_THROUGH_TOTAL_TIME = 1_000L;
    private static final int STRIKE_THROUGH_MODE = MODE_DEFAULT;

    @NonNull
    private ExtendedTextView targetView;
    @Nullable
    private StrikeThroughPaintingCallback strikeThroughPaintingCallback = null;
    private Paint paint = new Paint();
    private boolean drawStrikeThrough = false;

    private float strikeThroughProgress = 0F;
    private float strikeThroughPosition = STRIKE_THROUGH_POSITION;
    private int strikeThroughColor = STRIKE_THROUGH_COLOR;
    private float strikeThroughStrokeWidth = STRIKE_THROUGH_STROKE_WIDTH;
    private long strikeThroughTotalTime = STRIKE_THROUGH_TOTAL_TIME;
    private int strikeThroughMode = STRIKE_THROUGH_MODE;

    public StrikeThroughPainting(@NonNull ExtendedTextView targetView) {
        this.targetView =targetView;
        this.targetView.addPainting(this);
        paint.setColor(strikeThroughColor);
        paint.setStrokeWidth(strikeThroughStrokeWidth);
    }

    /**
     * Set strike through line position
     * @param percentageOfHeight set position of the drawing line, percentage marks the
     *                           offset from the top of the view
     * @return this
     */
    public StrikeThroughPainting linePosition(float percentageOfHeight) {
        this.strikeThroughPosition = percentageOfHeight;
        return this;
    }

    /**
     * Set the strike through line color
     * @param color A color value in the form 0xAARRGGBB, not a resource ID.
     * @return this
     */
    public StrikeThroughPainting color(int color) {
        this.strikeThroughColor = color;
        paint.setColor(color);
        return this;
    }

    /**
     * Set the strike through line width
     * @param width in px
     * @return this
     */
    public StrikeThroughPainting strokeWidth(float width) {
        this.strikeThroughStrokeWidth = width;
        paint.setStrokeWidth(width);
        return this;
    }

    /**
     * Total time of the animation
     * @param totalTime in milliseconds
     * @return this
     */
    public StrikeThroughPainting totalTime(long totalTime) {
        this.strikeThroughTotalTime = totalTime;
        return this;
    }

    /**
     * Set callback called when the strike through drawing animation ends
     * @param callback StrikeThroughPaintingCallback
     */
    public void callback(@NonNull StrikeThroughPaintingCallback callback) {
        strikeThroughPaintingCallback = callback;
    }

    /**
     * Change strike through drawing mode
     * @param mode one of {@value #MODE_DEFAULT} and {@value #MODE_ALL_LINE_TOGETHER}
     */
    public StrikeThroughPainting mode(int mode) {
        if (mode != MODE_DEFAULT && mode != MODE_ALL_LINE_TOGETHER) {
            throw new IllegalArgumentException("Mode must be one of MODE_DEFAULT and " +
                    "MODE_ALL_LINE_TOGETHER");
        }
        this.strikeThroughMode = mode;
        return this;
    }

    /**
     * Start strike through animation
     */
    public void strikeThrough() {
        strikeThroughProgress = 0F;
        drawStrikeThrough = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0F, 1F);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                strikeThroughProgress = (float) animation.getAnimatedValue();
                targetView.invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (strikeThroughPaintingCallback != null) {
                    strikeThroughPaintingCallback.onStrikeThroughEnd();
                }
            }
        });
        animator.setDuration(strikeThroughTotalTime);
        animator.start();
    }

    /**
     * Dismiss the strikeThrough line
     */
    public void clearStrikeThrough() {
        drawStrikeThrough = false;
        strikeThroughProgress = 0F;
        targetView.invalidate();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (!drawStrikeThrough) {
            return ;
        }
        canvas.save();

        float y = targetView.getLineHeight() * strikeThroughPosition;
        float endX = targetView.getMeasuredWidth() * strikeThroughProgress;
        canvas.drawLine(0, y, endX, y, paint);

        canvas.restore();
    }

    public interface StrikeThroughPaintingCallback {
        void onStrikeThroughEnd();
    }
}
