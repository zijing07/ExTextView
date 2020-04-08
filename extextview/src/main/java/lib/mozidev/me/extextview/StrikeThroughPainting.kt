@file:Suppress("unused")

package lib.mozidev.me.extextview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

/**
 * created by zijing on 17/04/2018
 */
class StrikeThroughPainting constructor(private val targetView: ExTextView) : IPainting {

    private var strikeThroughPaintingCallback: StrikeThroughPaintingCallback? = null
    private val paint = Paint()
    private var drawStrikeThrough = false
    private var textHeight = 0f
    private var strikeThroughProgress = 0f
    private var strikeThroughPosition = STRIKE_THROUGH_POSITION
    private var strikeThroughFirstLinePosition = STRIKE_THROUGH_FIRST_LINE_POSITION
    private val strikeThroughBaseLineTop = STRIKE_THROUGH_BASE_LINE_TOP
    private var strikeThroughColor = STRIKE_THROUGH_COLOR
    private var strikeThroughStrokeWidth = STRIKE_THROUGH_STROKE_WIDTH
    private var strikeThroughTotalTime = STRIKE_THROUGH_TOTAL_TIME
    private var strikeThroughMode = STRIKE_THROUGH_MODE
    private var strikeThroughCutTextEdge = STRIKE_THROUGH_CUT_TEXT_EDGE
    var isStriked = true 
        private set()
    
    /**
     * Set strike through line position
     * @param percentageOfHeight set position of the drawing line, percentage marks the
     * offset from the top of the line
     * @return this
     */
    fun linePosition(percentageOfHeight: Float): StrikeThroughPainting {
        strikeThroughPosition = percentageOfHeight
        return this
    }

    /**
     * Set the strike through line position of the first line. Because the
     * `first line top padding` is smaller, the first line's strike through
     * line needs to be higher than other lines.
     * @param percentageOfHeight set position of the drawing line, percentage marks the
     * offset from the top of the line
     * @return this
     */
    fun firstLinePosition(percentageOfHeight: Float): StrikeThroughPainting {
        strikeThroughFirstLinePosition = percentageOfHeight
        return this
    }

    /**
     * Set the strike through line color
     * @param color A color value in the form 0xAARRGGBB, not a resource ID.
     * @return this
     */
    fun color(color: Int): StrikeThroughPainting {
        strikeThroughColor = color
        paint.color = color
        return this
    }

    /**
     * Set the strike through line width
     * @param width in px
     * @return this
     */
    fun strokeWidth(width: Float): StrikeThroughPainting {
        strikeThroughStrokeWidth = width
        paint.strokeWidth = width
        return this
    }

    /**
     * Total time of the animation
     * @param totalTime in milliseconds
     * @return this
     */
    fun totalTime(totalTime: Long): StrikeThroughPainting {
        strikeThroughTotalTime = totalTime
        return this
    }

    /**
     * Set callback called when the strike through drawing animation ends
     * @param callback StrikeThroughPaintingCallback
     * @return this
     */
    fun callback(callback: (() -> Unit)?): StrikeThroughPainting {
        strikeThroughPaintingCallback = null
        callback?.let {
            strikeThroughPaintingCallback = object : StrikeThroughPaintingCallback {
                override fun onStrikeThroughEnd() {
                    callback.invoke()
                }
            }
        }
        return this
    }

    /**
     * Change strike through drawing mode
     * @param mode one of {@value #MODE_DEFAULT} and {@value #MODE_LINES_TOGETHER}
     */
    fun mode(mode: Int): StrikeThroughPainting {
        require(!(mode != MODE_DEFAULT && mode != MODE_LINES_TOGETHER)) {
            "Mode must be one of MODE_DEFAULT and " +
                    "MODE_LINES_TOGETHER"
        }
        strikeThroughMode = mode
        return this
    }

    /**
     * Set if the strike through line cut the text edge, or fill the full width
     * @param cutEdge whether cut text edge
     * @return this
     */
    fun cutTextEdge(cutEdge: Boolean): StrikeThroughPainting {
        strikeThroughCutTextEdge = cutEdge
        return this
    }

    /**
     * Start strike through animation
     */
    fun strikeThrough() {
        prepareAnimation()
        startAnimation()
    }

    /**
     * Dismiss the strikeThrough line
     */
    fun clearStrikeThrough() {
        isStriked = false
        drawStrikeThrough = false
        strikeThroughProgress = 0f
        targetView.invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (!drawStrikeThrough || textHeight == 0f) {
            return
        }
        canvas.save()
        when (strikeThroughMode) {
            MODE_DEFAULT -> drawDefault(canvas)
            MODE_LINES_TOGETHER -> drawAllTogether(canvas)
        }
        canvas.restore()
    }

    private fun maxDrawingLineNum(animCoveredDistance: Float): Int {
        var tmpDistance = 0f
        for (i in lineRects.indices) {
            val rect = lineRects[i]
            tmpDistance += rect.width().toFloat()
            if (tmpDistance >= animCoveredDistance) {
                return i
            }
        }
        return -1
    }

    /**
     * The function that really draws the line
     * @param canvas Canvas to draw
     * @param lineIndex The line to draw strike through on
     * @param distance How long the strike through line should be, -1 indicates a full line draw
     * @return The length of strike through line drawn
     */
    private fun drawStrikeThroughLine(
        canvas: Canvas, lineIndex: Int, distance: Float = -1f
    ): Float {
        var lIndex = lineIndex
        val rect = lineRects[lIndex]
        //        float linePosition = lineIndex == 0 ? strikeThroughFirstLinePosition : strikeThroughPosition;
        val baseLineTop = targetView.paint.textSize * strikeThroughBaseLineTop
        val fontMetrics = targetView.paint.fontMetrics
        val linePosition = -1 * fontMetrics.top - baseLineTop
        var lineTop = 0f
        while (--lIndex >= 0) {
            lineTop += lineRects[lIndex].height().toFloat()
        }
        val lineHeightOffset = lineTop + linePosition
        val howFarToGo = if (distance == -1f) rect.width().toFloat() else distance
        canvas.drawLine(rect.left.toFloat(), lineHeightOffset, rect.left + howFarToGo, lineHeightOffset, paint)
        return howFarToGo
    }

    private fun drawAllTogether(canvas: Canvas) {
        for (i in lineRects.indices) {
            val rect = lineRects[i]
            val distance = rect.width() * strikeThroughProgress
            drawStrikeThroughLine(canvas, i, distance)
        }
    }

    private fun drawDefault(canvas: Canvas) {
        val animCoveredDistance = animTotalDistance * strikeThroughProgress
        val maxDrawingLineNum = maxDrawingLineNum(animCoveredDistance)
        if (maxDrawingLineNum < 0) {
            return
        }
        var drawnDistance = 0f
        // draw full line strike through
        for (i in 0 until maxDrawingLineNum) {
            drawnDistance += drawStrikeThroughLine(canvas, i)
        }

        // last line strike through
        val lastLineDrawDistance = animCoveredDistance - drawnDistance
        drawStrikeThroughLine(canvas, maxDrawingLineNum, lastLineDrawDistance)
    }

    private var animTotalDistance = 0
    private var lineRects: MutableList<Rect> = mutableListOf()

    private fun fillLineRects() {
        val lineCount = targetView.lineCount
        val layout = targetView.layout
        for (i in 0 until lineCount) {
            val rect = Rect()
            if (strikeThroughCutTextEdge) {
                rect[layout.getLineLeft(i).toInt(), layout.getLineTop(i), layout.getLineRight(i).toInt()] = layout.getLineBottom(i)
            } else {
                targetView.getLineBounds(i, rect)
            }
            lineRects.add(rect)
        }
    }

    private fun calcAnimTotalDistance() {
        animTotalDistance = 0
        for (rect in lineRects) {
            animTotalDistance += rect.width()
        }
    }

    private val lineSpacing: Unit
        get() {
            textHeight = 0f
            val fm = targetView.paint.fontMetrics ?: return
            textHeight = fm.bottom - fm.top
        }

    private fun prepareAnimation() {
        lineSpacing
        fillLineRects()
        when (strikeThroughMode) {
            MODE_DEFAULT -> calcAnimTotalDistance()
            MODE_LINES_TOGETHER -> {
            }
        }
    }

    private fun startAnimation() {
        strikeThroughProgress = 0f
        drawStrikeThrough = true
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { animation ->
            strikeThroughProgress = animation.animatedValue as Float
            targetView.invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isStriked = true //since we only animate on strike
                strikeThroughPaintingCallback?.onStrikeThroughEnd()
            }
        })
        animator.duration = strikeThroughTotalTime
        animator.start()
    }

    interface StrikeThroughPaintingCallback {
        fun onStrikeThroughEnd()
    }

    companion object {
        /**
         * If there is only one line, the strike through line is
         * drawn from left to right. If there are multiple lines, strike through line
         * is drawn one line after another.
         */
        const val MODE_DEFAULT = 0

        /**
         * This painting behaves the same when there is only one line.
         * But when there are multiple lines, all lines' strike through lines
         * are drawn simultaneously.
         */
        const val MODE_LINES_TOGETHER = 1
        private const val STRIKE_THROUGH_POSITION = 0.65f

        /**
         * Because the `first line top padding` is smaller, adjust the strike through
         * line position from 0.7 to 0.6, to make the line in the center of texts.
         */
        private const val STRIKE_THROUGH_FIRST_LINE_POSITION = 0.6f
        private const val STRIKE_THROUGH_STROKE_WIDTH = 2f
        private const val STRIKE_THROUGH_COLOR = Color.BLACK
        private const val STRIKE_THROUGH_TOTAL_TIME = 1000L
        private const val STRIKE_THROUGH_MODE = MODE_DEFAULT
        private const val STRIKE_THROUGH_CUT_TEXT_EDGE = true
        private const val STRIKE_THROUGH_BASE_LINE_TOP = 6.0f / 21.0f
    }

    init {
        targetView.addPainting(this)
        paint.color = strikeThroughColor
        paint.strokeWidth = strikeThroughStrokeWidth
    }
}
