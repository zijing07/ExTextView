# ExTextView

Android Extended TextView.

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
    implementation 'com.github.zijing07:ExTextView:v0.1.0'
}
```

### Code

```java
StrikeThroughPainting strikeThroughPainting = new StrikeThroughPainting(tv);

// Basic Usage
strikeThroughPainting.strikeThrough();

// All Options
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
        .linePosition(0.7F)
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

// Clear Strike Through
strikeThroughPainting.clearStrikeThrough();
```

# Done Features
- Animated Strike Through

# TODO
- Random Spotlight (Just like a moving spotlight on a billboard)
- What else to extend? Please feel free to tell via opening an issue.

# Demo
![demo](demo.gif)

# Something Else

This project is written without any Kotlin, just to make it easy to use. But after programming with Kotlin for some time, writing pure Java really pains a little.

The idea to write this library is that, I could not find one library to complete the `Animated Strike Through Textview` task. So I have to build it from scratch.

Well that's not too much though, and the `Spotlight Effect` to be done. Welcome to create PRs.