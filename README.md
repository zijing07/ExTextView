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
    implementation 'com.github.zijing07:ExTextView:${LATEST_VERSION}'
}
```

### Code

```java
strikeThroughPainting = StrikeThroughPainting(tv)

// Basic Usage
strikeThroughPainting.strikeThrough()

// All Options
strikeThroughPainting
    .cutTextEdge(cutEdge) // default to true
    .color(strokeColor) // default to Color.BLACK
    .strokeWidth(strokeWidth) // default to 2F px
    .mode(StrikeThroughPainting.MODE_DEFAULT) // default to StrikeThroughPainting.MODE_DEFAULT
    .linePosition(0.65f) // default to 0.65F
    // differently to the following lines
    .firstLinePosition(0.6f) // default to 0.6F, since the first line is calculated
    .totalTime(10000L) // default to 1_000 milliseconds, aka 1s
    .callback{Snackbar.make(findViewById(R.id.container),"Callback after animation", Snackbar.LENGTH_LONG).show()}
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
