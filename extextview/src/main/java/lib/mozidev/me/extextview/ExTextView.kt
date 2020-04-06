package lib.mozidev.me.extextview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * created by zijing on 17/04/2018
 */
class ExTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var paintings: MutableList<IPainting> = mutableListOf()

    fun addPainting(painting: IPainting) {
        paintings.add(painting)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null || paintings.size == 0) {
            return
        }
        for (painting in paintings) {
            painting.onDraw(canvas)
        }
    }
}