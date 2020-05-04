package lib.mozidev.me.demo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import lib.mozidev.me.demo.databinding.ActivityMainBinding
import lib.mozidev.me.extextview.ExTextView
import lib.mozidev.me.extextview.StrikeThroughPainting

class MainActivity : AppCompatActivity() {
    private var strIndex = 0
    private var strokeWidth = 2f
    private var strokeColor = Color.BLACK
    private var cutEdge = true
    private lateinit var strikeThroughPainting: StrikeThroughPainting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reset(binding.extendedTextView)
        binding.button.setOnClickListener {
            reset(binding.extendedTextView)
            strIndex += 1
            if (strIndex == strs.size) {
                strIndex = 0
            }
            binding.extendedTextView.text = strs[strIndex]
        }
        binding.button2.setOnClickListener {
            reset(binding.extendedTextView)
            strikeThroughPainting
                .cutTextEdge(cutEdge)
                .color(strokeColor)
                .strokeWidth(strokeWidth)
                .mode(StrikeThroughPainting.MODE_DEFAULT)
                .strikeThrough()
        }
        binding.button3.setOnClickListener {
            reset(binding.extendedTextView)
            strikeThroughPainting
                .cutTextEdge(cutEdge)
                .color(strokeColor)
                .strokeWidth(strokeWidth)
                .mode(StrikeThroughPainting.MODE_LINES_TOGETHER)
                .strikeThrough()
        }
        binding.switch1.isChecked = cutEdge
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            cutEdge = isChecked
            reset(binding.extendedTextView)
        }
        binding.switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.extendedTextView.setLineSpacing(27f, 2f)
            } else {
                binding.extendedTextView.setLineSpacing(0f, 1f)
            }
            reset(binding.extendedTextView)
        }
        binding.switch3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                strokeColor = Color.BLUE
                strokeWidth = 6f
            } else {
                strokeColor = Color.BLACK
                strokeWidth = 2f
            }
            reset(binding.extendedTextView)
        }
        binding.switch4.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.extendedTextView.gravity = Gravity.CENTER_HORIZONTAL
            } else {
                binding.extendedTextView.gravity = Gravity.NO_GRAVITY
            }
            reset(binding.extendedTextView)
        }
        binding.button5.setOnClickListener {
            reset(binding.extendedTextView)
            strikeThroughPainting
                .cutTextEdge(cutEdge)
                .color(strokeColor)
                .strokeWidth(strokeWidth)
                .linePosition(0.65f)
                .callback { Snackbar.make(
                    findViewById(R.id.container),
                    "Callback after animation", Snackbar.LENGTH_LONG
                ).show()}
                .strikeThrough()
        }
        binding.button4.setOnClickListener { reset(binding.extendedTextView) }
    }

    // if not, we get funcky results when switching configurations
    private fun reset(targetView: ExTextView) {
        if (::strikeThroughPainting.isInitialized) {
            strikeThroughPainting.clearStrikeThrough()
        }
        strikeThroughPainting = StrikeThroughPainting(targetView)
    }

    companion object {
        private const val SINGLE_LINE_TEXT = "I'm making a note here: Huge success!"
        private const val MULTIPLE_LINES_TEXT = "With '\\n' wrap\n" +
                "Aperture Science:\n" +
                "We do what we must\n" +
                "because we can\n" +
                "For the good of all of us.\n" +
                "Except the ones who are dead."
        private const val VERY_LONG_TEXT =
            "Early work on the Aperture Science Handheld Portal Device began; the early version, called the Aperture Science Portable Quantum Tunneling Device, proved to be too bulky for effective use..."
        private val strs = listOf(SINGLE_LINE_TEXT, MULTIPLE_LINES_TEXT, VERY_LONG_TEXT)
    }
}
