# ExTextView

Android Extended TextView (now in Kotlin 🤩)

# Usage

### Add Dependency

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency
``` gradle
dependencies {
    implementation 'com.github.zijing07:ExTextView:v1.0.0'
}
```

### Code

```java
strikeThroughPainting = StrikeThroughPainting(tv)

// Basic Usage
strikeThroughPainting.strikeThrough()

// All Options
strikeThroughPainting // default to true
    .cutTextEdge(cutEdge) // default to Color.BLACK
    .color(strokeColor) // default to 2F px
    .strokeWidth(strokeWidth) // default to StrikeThroughPainting.MODE_DEFAULT
    .mode(StrikeThroughPainting.MODE_DEFAULT) // default to 0.65F
    .linePosition(0.65f) // default to 0.6F, since the first line is calculated
    // differently to the following lines
    .firstLinePosition(0.6f) // default to 1_000 milliseconds, aka 1s
    .totalTime(10000L) // default to null
    .callback(object : StrikeThroughPainting.StrikeThroughPaintingCallback {
        override fun onStrikeThroughEnd() {
            Snackbar.make(
                findViewById(R.id.container),
                "Callback after animation", Snackbar.LENGTH_LONG
            ).show()
        }
    })
    .strikeThrough()

// Clear Strike Through
strikeThroughPainting.clearStrikeThrough()
```

# Done Features
- Animated Strike Through

# TODO
- Random Spotlight (Just like a moving spotlight on a billboard)
- What else to extend? Please feel free to tell via opening an issue.

# Demo
![demo](demo.gif)
