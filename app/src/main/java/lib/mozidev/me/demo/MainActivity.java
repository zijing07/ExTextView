package lib.mozidev.me.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Arrays;
import java.util.List;

import lib.mozidev.me.extextview.ExTextView;
import lib.mozidev.me.extextview.StrikeThroughPainting;

public class MainActivity extends AppCompatActivity {

    private static final String SINGLE_LINE_TEXT = "I'm making a note here: Huge success!";
    private static final String MULTIPLE_LINES_TEXT = "With '\\n' wrap\n" +
            "Aperture Science:\n" +
            "We do what we must\n" +
            "because we can\n" +
            "For the good of all of us.\n" +
            "Except the ones who are dead.";
    private static final String VERY_LONG_TEXT = "Early work on the Aperture Science Handheld Portal Device began; the early version, called the Aperture Science Portable Quantum Tunneling Device, proved to be too bulky for effective use...";
    private static final List<String> strs = Arrays.asList(SINGLE_LINE_TEXT, MULTIPLE_LINES_TEXT, VERY_LONG_TEXT);
    private int strIndex = 0;

    private float strokeWidth = 2F;
    private int strokeColor = Color.BLACK;
    private boolean cutEdge = true;

    private StrikeThroughPainting strikeThroughPainting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ExTextView tv = findViewById(R.id.extendedTextView);
        strikeThroughPainting = new StrikeThroughPainting(tv);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strIndex += 1;
                if (strIndex == strs.size()) {
                    strIndex = 0;
                }
                tv.setText(strs.get(strIndex));
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strikeThroughPainting
                        .cutTextEdge(cutEdge)
                        .color(strokeColor)
                        .strokeWidth(strokeWidth)
                        .mode(StrikeThroughPainting.MODE_DEFAULT)
                        .strikeThrough();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strikeThroughPainting
                        .cutTextEdge(cutEdge)
                        .color(strokeColor)
                        .strokeWidth(strokeWidth)
                        .mode(StrikeThroughPainting.MODE_LINES_TOGETHER)
                        .strikeThrough();
            }
        });

        Switch switch1 = findViewById(R.id.switch1);
        switch1.setChecked(cutEdge);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cutEdge = isChecked;
            }
        });

        this.<Switch>findViewById(R.id.switch2).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv.setLineSpacing(27F, 2F);
                } else {
                    tv.setLineSpacing(0, 1);
                }
            }
        });

        this.<Switch>findViewById(R.id.switch3).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    strokeColor = Color.BLUE;
                    strokeWidth = 6F;
                } else {
                    strokeColor = Color.BLACK;
                    strokeWidth = 2F;
                }
            }
        });

        this.<Switch>findViewById(R.id.switch4).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    tv.setGravity(Gravity.NO_GRAVITY);
                }
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strikeThroughPainting
                        // default to true
                        .cutTextEdge(cutEdge)
                        // default to Color.BLACK
                        .color(strokeColor)
                        // default to 2F px
                        .strokeWidth(strokeWidth)
                        // default to StrikeThroughPainting.MODE_DEFAULT
                        .mode(StrikeThroughPainting.MODE_DEFAULT)
                        // default to 0.65F
                        .linePosition(0.65F)
                        // default to 0.6F, since the first line is calculated
                        // differently to the following lines
                        .firstLinePosition(0.6F)
                        // default to 1_000 milliseconds, aka 1s
                        .totalTime(10_000L)
                        // default to null
                        .callback(new StrikeThroughPainting.StrikeThroughPaintingCallback() {
                            @Override
                            public void onStrikeThroughEnd() {
                                Snackbar.make(findViewById(R.id.container),
                                        "Callback after animation", Snackbar.LENGTH_LONG).show();
                            }
                        })
                        // do the draw!
                        .strikeThrough();
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strikeThroughPainting.clearStrikeThrough();
            }
        });
    }
}
