package lib.mozidev.me.extextview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * created by zijing on 17/04/2018
 */
public class ExTextView extends AppCompatTextView {

    @Nullable
    private List<IPainting> paintings = null;

    public ExTextView(Context context) {
        this(context, null);
    }

    public ExTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ExTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void addPainting(IPainting painting) {
        if (paintings == null) {
            paintings = new ArrayList<>();
        }
        paintings.add(painting);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas == null) {
            return ;
        }

        if (paintings == null || paintings.size() == 0) {
            return ;
        }

        for (IPainting painting: paintings) {
            painting.onDraw(canvas);
        }
    }
}
