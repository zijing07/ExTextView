package lib.mozidev.me.extendedtextview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;

import java.util.ArrayList;
import java.util.List;

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
    public static final int MODE_LINES_TOGETHER = 1;

    private static final float STRIKE_THROUGH_POSITION = 0.65F;
    /**
     * Because the `first line top padding` is smaller, adjust the strike through
     * line position from 0.7 to 0.6, to make the line in the center of texts.
     */
    private static final float STRIKE_THROUGH_FIRST_LINE_POSITION = 0.6F;
    private static final float STRIKE_THROUGH_STROKE_WIDTH = 2F;
    private static final int STRIKE_THROUGH_COLOR = Color.BLACK;
    private static final long STRIKE_THROUGH_TOTAL_TIME = 1_000L;
    private static final int STRIKE_THROUGH_MODE = MODE_DEFAULT;
    private static final boolean STRIKE_THROUGH_CUT_TEXT_EDGE = true;

    @NonNull
    private ExtendedTextView targetView;
    @Nullable
    private StrikeThroughPaintingCallback strikeThroughPaintingCallback = null;
    private Paint paint = new Paint();
    private boolean drawStrikeThrough = false;
    private float textHeight = 0F;

    private float strikeThroughProgress = 0F;
    private float strikeThroughPosition = STRIKE_THROUGH_POSITION;
    private float strikeThroughFirstLinePosition = STRIKE_THROUGH_FIRST_LINE_POSITION;
    private int strikeThroughColor = STRIKE_THROUGH_COLOR;
    private float strikeThroughStrokeWidth = STRIKE_THROUGH_STROKE_WIDTH;
    private long strikeThroughTotalTime = STRIKE_THROUGH_TOTAL_TIME;
    private int strikeThroughMode = STRIKE_THROUGH_MODE;
    private boolean strikeThroughCutTextEdge = STRIKE_THROUGH_CUT_TEXT_EDGE;

    public StrikeThroughPainting(@NonNull ExtendedTextView targetView) {
        this.targetView =targetView;
        this.targetView.addPainting(this);
        paint.setColor(strikeThroughColor);
        paint.setStrokeWidth(strikeThroughStrokeWidth);
    }

    /**
     * Set strike through line position
     * @param percentageOfHeight set position of the drawing line, percentage marks the
     *                           offset from the top of the line
     * @return this
     */
    public StrikeThroughPainting linePosition(float percentageOfHeight) {
        this.strikeThroughPosition = percentageOfHeight;
        return this;
    }

    /**
     * Set the strike through line position of the first line. Because the
     * `first line top padding` is smaller, the first line's strike through
     * line needs to be higher than other lines.
     * @param percentageOfHeight set position of the drawing line, percentage marks the
     *                           offset from the top of the line
     * @return this
     */
    public StrikeThroughPainting firstLinePosition(float percentageOfHeight) {
        this.strikeThroughFirstLinePosition = percentageOfHeight;
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
     * @return this
     */
    public StrikeThroughPainting callback(@NonNull StrikeThroughPaintingCallback callback) {
        this.strikeThroughPaintingCallback = callback;
        return this;
    }

    /**
     * Change strike through drawing mode
     * @param mode one of {@value #MODE_DEFAULT} and {@value #MODE_LINES_TOGETHER}
     */
    public StrikeThroughPainting mode(int mode) {
        if (mode != MODE_DEFAULT && mode != MODE_LINES_TOGETHER) {
            throw new IllegalArgumentException("Mode must be one of MODE_DEFAULT and " +
                    "MODE_LINES_TOGETHER");
        }
        this.strikeThroughMode = mode;
        return this;
    }

    /**
     * Set if the strike through line cut the text edge, or fill the full width
     * @param cutEdge whether cut text edge
     * @return this
     */
    public StrikeThroughPainting cutTextEdge(boolean cutEdge) {
        this.strikeThroughCutTextEdge = cutEdge;
        return this;
    }

    /**
     * Start strike through animation
     */
    public void strikeThrough() {
        targetView.post(new Runnable() {
            @Override
            public void run() {
                prepareAnimation();
                startAnimation();
            }
        });
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
        if (!drawStrikeThrough || textHeight == 0F || lineRects == null) {
            return ;
        }
        canvas.save();

        switch (strikeThroughMode) {
            case MODE_DEFAULT:
                drawDefault(canvas);
                break;
            case MODE_LINES_TOGETHER:
                drawAllTogether(canvas);
                break;
        }

        canvas.restore();
    }

    private int maxDrawingLineNum(float animCoveredDistance) {
        float tmpDistance = 0F;
        if (lineRects == null) {
            return -1;
        }
        for (int i = 0; i < lineRects.size(); ++i) {
            Rect rect = lineRects.get(i);
            tmpDistance += (float) rect.width();
            if (tmpDistance >= animCoveredDistance) {
                return i;
            }
        }
        return -1;
    }

    private float drawStrikeThroughLine(@NonNull Canvas canvas, int lineIndex) {
        return drawStrikeThroughLine(canvas, lineIndex, -1);
    }

    /**
     * The function that really draws the line
     * @param canvas Canvas to draw
     * @param lineIndex The line to draw strike through on
     * @param distance How long the strike through line should be, -1 indicates a full line draw
     * @return The length of strike through line drawn
     */
    private float drawStrikeThroughLine(@NonNull Canvas canvas, int lineIndex, float distance) {
        Rect rect = lineRects.get(lineIndex);
        float linePosition = lineIndex == 0 ?
                strikeThroughFirstLinePosition : strikeThroughPosition;
        float lineHeightOffset = targetView.getLineHeight() * lineIndex
                + textHeight * linePosition;
        float howFarToGo = distance == -1 ? rect.width() : distance;
        canvas.drawLine(rect.left, lineHeightOffset, rect.left + howFarToGo, lineHeightOffset, paint);
        return howFarToGo;
    }

    private void drawAllTogether(@NonNull Canvas canvas) {
        for (int i = 0; i < lineRects.size(); ++i) {
            Rect rect = lineRects.get(i);
            float distance = rect.width() * strikeThroughProgress;
            drawStrikeThroughLine(canvas, i, distance);
        }
    }

    private void drawDefault(@NonNull Canvas canvas) {
        float animCoveredDistance = animTotalDistance * strikeThroughProgress;
        int maxDrawingLineNum = maxDrawingLineNum(animCoveredDistance);

        if (maxDrawingLineNum < 0) {
            return ;
        }

        float drawnDistance = 0F;
        // draw full line strike through
        for (int i = 0; i < maxDrawingLineNum; ++i) {
            drawnDistance += drawStrikeThroughLine(canvas, i);
        }

        // last line strike through
        float lastLineDrawDistance = animCoveredDistance - drawnDistance;
        drawStrikeThroughLine(canvas, maxDrawingLineNum, lastLineDrawDistance);
    }

    private int animTotalDistance = 0;
    private List<Rect> lineRects;

    private void fillLineRects() {
        int lineCount = targetView.getLineCount();
        lineRects = new ArrayList<>();
        Layout layout = targetView.getLayout();
        for (int i = 0; i < lineCount; ++i) {
            Rect rect = new Rect();
            if (strikeThroughCutTextEdge) {
                rect.set(
                        (int) layout.getLineLeft(i),
                        layout.getLineTop(i),
                        (int) layout.getLineRight(i),
                        layout.getLineBottom(i)
                );
            } else {
                targetView.getLineBounds(i, rect);
            }
            lineRects.add(rect);
        }
    }

    private void calcAnimTotalDistance() {
        animTotalDistance = 0;
        if (lineRects == null) {
            return ;
        }
        for (Rect rect: lineRects) {
            animTotalDistance += rect.width();
        }
    }

    private void getLineSpacing() {
        textHeight = 0F;
        Paint.FontMetrics fm = targetView.getPaint().getFontMetrics();
        if (fm == null) {
            return ;
        }
        textHeight = fm.bottom - fm.top;
    }

    private void prepareAnimation() {
        getLineSpacing();
        fillLineRects();
        switch (strikeThroughMode) {
            case MODE_DEFAULT:
                calcAnimTotalDistance();
                break;
            case MODE_LINES_TOGETHER:
                // ignore
                break;
        }
    }

    private void startAnimation() {
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

    public interface StrikeThroughPaintingCallback {
        void onStrikeThroughEnd();
    }
}
