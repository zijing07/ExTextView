package lib.mozidev.me.extextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import lib.mozidev.me.extendedtextview.ExtendedTextView;
import lib.mozidev.me.extendedtextview.StrikeThroughPainting;

public class MainActivity extends AppCompatActivity {

    private static final String SINGLE_LINE_TEXT = "I'm making a note here: Huge success!";
    private static final String MULTIPLE_LINES_TEXT = "Aperture Science:\n" +
            "We do what we must\n" +
            "because we can\n" +
            "For the good of all of us.\n" +
            "Except the ones who are dead.";

    private StrikeThroughPainting strikeThroughPainting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ExtendedTextView tv = findViewById(R.id.extendedTextView);
        strikeThroughPainting = new StrikeThroughPainting(tv);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(SINGLE_LINE_TEXT);
                strikeThroughPainting.strikeThrough();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(MULTIPLE_LINES_TEXT);
                strikeThroughPainting
                        .mode(StrikeThroughPainting.MODE_DEFAULT)
                        .strikeThrough();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(MULTIPLE_LINES_TEXT);
                strikeThroughPainting
                        .mode(StrikeThroughPainting.MODE_ALL_LINE_TOGETHER)
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
