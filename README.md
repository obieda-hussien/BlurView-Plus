[![Stand With Ukraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner2-direct.svg)](https://vshymanskyy.github.io/StandWithUkraine)

[![Build Status](https://github.com/obieda-hussien/BlurView-Plus/workflows/Build%20and%20Test%20BlurView-Plus/badge.svg)](https://github.com/obieda-hussien/BlurView-Plus/actions)

# BlurView-Plus

<a href="url"><img src="https://github.com/user-attachments/assets/5abb1034-021b-4dfb-ad1b-3136a2a00a02" width="432" ></a>

Dynamic iOS-like blur for Android Views with advanced Windows-style dynamic colors, smooth animations, and performance optimizations. 

BlurView-Plus includes a comprehensive library and example project with cutting-edge features:
- 🎨 **Windows-style Dynamic Colors**: Automatically adapts overlay colors based on content
- 🎭 **iPhone-style Smooth Animations**: Physics-based blur transitions and natural motion
- ⚡ **Advanced Performance Optimizations**: Intelligent caching and adaptive quality scaling
- 🔧 **Enhanced Developer Tools**: Performance monitoring and debugging capabilities

BlurView can be used as a regular FrameLayout. It blurs its underlying content and draws it as a
background for its children. The children of the BlurView are not blurred. BlurView updates its
blurred content when changes in the view hierarchy are detected. It honors its position
and size changes, including view animation and property animation.

> [!IMPORTANT]
> Version 3.0 info, key changes, migration steps, and what you need to know is [here](BlurView_3.0.md).<br/>
> Also, the code path on API 31+ is now completely different from API < 31, so keep in mind to test both.

## How to use
Now you have to wrap the content you want to blur
into a `BlurTarget`, and pass it into the `setupWith()` method of the `BlurView`.<br/>
The BlurTarget may not contain a BlurView that targets the same BlurTarget.<br/>
The BlurTarget may contain other BlurTargets and BlurViews though.<br/>
```XML
    <!--This is the content to be blurred by the BlurView. 
    It will render normally, and BlurView will use its snapshot for blurring-->
    <eightbitlab.com.blurview.BlurTarget
        android:id="@+id/target"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        
        <!--Your main content here-->

    </eightbitlab.com.blurview.BlurTarget>

    <eightbitlab.com.blurview.BlurView
      android:id="@+id/blurView"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      app:blurOverlayColor="@color/colorOverlay">
    
       <!--Any child View here, TabLayout for example. This View will NOT be blurred -->
    
    </eightbitlab.com.blurview.BlurView>
```

```Java
    float radius = 20f;

    View decorView = getWindow().getDecorView();
    // A view hierarchy you want blur. The BlurTarget can't include the BlurView that targets it.
    BlurTarget target = findViewById(R.id.target);
    
    // Optional:
    // Set the drawable to draw in the beginning of each blurred frame.
    // Can be used in case your layout has a lot of transparent space and your content
    // gets a low alpha value after blur is applied.
    Drawable windowBackground = decorView.getBackground();

    // Basic setup with enhanced features
    blurView.setupWith(target) 
           .setFrameClearDrawable(windowBackground) // Optional
           .setBlurRadius(radius)
           // Enable new features
           .setDynamicColorsEnabled(true)     // Windows-style adaptive colors
           .setAnimationsEnabled(true)        // iPhone-style smooth animations
           .setPerformanceOptimizationEnabled(true); // Advanced optimizations
```

## 🆕 Enhanced Features

### Dynamic Background Colors (Windows-style)
Automatically extracts and adapts overlay colors based on the underlying content, similar to Windows Acrylic effects:

```Java
blurView.setDynamicColorsEnabled(true);

// Manually trigger color extraction from a specific bitmap
blurView.applyDynamicColors(someBitmap);
```

### Smooth Animations (iPhone-style)  
Physics-based blur transitions with natural spring motion:

```Java
blurView.setAnimationsEnabled(true);

// Animate blur radius with smooth transitions
blurView.animateBlurRadius(25f);

// Animate blur enable/disable with fade effects
blurView.animateBlurEnabled(false);
```

### Advanced Performance Optimizations
Intelligent caching, adaptive quality scaling, and performance monitoring:

```Java
blurView.setPerformanceOptimizationEnabled(true);

// Get performance statistics
BlurPerformanceOptimizer.PerformanceStats stats = blurView.getPerformanceStats();
if (stats != null) {
    Log.d("BlurView", "Average frame time: " + stats.averageFrameTime + "ms");
    Log.d("BlurView", "Cache hit rate: " + stats.getCacheHitRate() * 100 + "%");
    Log.d("BlurView", "Current quality scale: " + stats.currentQualityScale);
}
```

### Feature Configuration
All enhanced features can be enabled/disabled individually:

```Java
blurView
    .setDynamicColorsEnabled(true)        // Enable/disable dynamic colors
    .setAnimationsEnabled(true)           // Enable/disable smooth animations  
    .setPerformanceOptimizationEnabled(true); // Enable/disable optimizations
```

## SurfaceView, TextureView, VideoView, MapFragment, GLSurfaceView, etc
TextureView can be blurred only on API 31+. Everything else (which is SurfaceView-based) can't be blurred, unfortunately.

## Gradle

Use Jitpack https://jitpack.io/#Dimezis/BlurView and release tags as the source of stable
artifacts.
```Groovy
implementation 'com.github.Dimezis:BlurView:version-3.1.0'
```

### Dependencies
The enhanced features require the following additional dependency for dynamic color extraction:
```Groovy
implementation 'androidx.palette:palette:1.0.0'
```

## Rounded corners
It's possible to set rounded corners without any custom API, the algorithm is the same as with other regular View:

Create a rounded drawable, and set it as a background.

Then set up the clipping, so the BlurView doesn't draw outside the corners 
```Java
blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
blurView.setClipToOutline(true);
```
Related thread - https://github.com/Dimezis/BlurView/issues/37

## Why blurring on the main thread?
Because blurring on other threads would introduce 1-2 frames of latency.
On API 31+ the blur is done on the system Render Thread.

## Compared to other blurring libs
- BlurView-Plus and Haze for Compose are the only libraries that leverage hardware acceleration for View snapshotting and have near zero overhead of snapshotting.
- ✨ **NEW**: Windows-style dynamic background colors that adapt to content
- ✨ **NEW**: iPhone-style smooth animations with physics-based transitions  
- ✨ **NEW**: Advanced performance optimizations with intelligent caching
- ✨ **NEW**: Real-time performance monitoring and adaptive quality scaling
- Supports TextureView blur on API 31+.
- The BlurView never invalidates itself or other Views in the hierarchy and updates only when needed.
- It supports multiple BlurViews on the screen without triggering a draw loop.
- On API < 31 it uses optimized RenderScript Allocations on devices that require certain Allocation sizes, which greatly increases blur performance.
- Supports blurring of Dialogs (and Dialog's background)

Other libs:
- 🛑 [BlurKit](https://github.com/CameraKit/blurkit-android) - constantly invalidates itself
- 🛑 [RealtimeBlurView](https://github.com/mmin18/RealtimeBlurView) - constantly invalidates itself

## 🚀 Automated Building & CI/CD

This project includes a comprehensive GitHub Actions workflow that automatically builds and tests the library and sample application. The workflow is triggered on:

- **Push to master branch**: Full build, test, and automatic release creation
- **Pull requests**: Build and test validation  
- **Manual workflow dispatch**: On-demand builds

### 📦 Artifacts Generated

The CI automatically generates and uploads the following build artifacts:

1. **Library AAR** (`library-aar`): 
   - Release version of the BlurView-Plus library
   - Ready for integration into other Android projects

2. **Debug APK** (`app-debug-apk`):
   - Debug version of the sample application
   - Includes debugging symbols and is immediately installable

3. **Release APK** (`app-release-apk`):
   - Optimized release version of the sample application
   - Unsigned APK ready for signing and distribution

### 🔧 Workflow Features

- **Code Quality**: Automated linting and static analysis
- **Testing**: Unit tests with comprehensive reporting
- **Multi-step Build**: Library + sample app compilation
- **Performance Testing**: Automated benchmarks (if available)
- **Artifact Management**: Organized upload of all build outputs
- **Auto-Release**: Automatic GitHub releases on master branch pushes

### 📥 Download Build Artifacts

After any successful build, you can download the generated APKs and library files from:
1. Go to the **Actions** tab in the GitHub repository
2. Click on the latest successful workflow run
3. Scroll down to the **Artifacts** section
4. Download the desired artifacts (APKs or AAR files)

### 🛠️ Local Building

To build locally:

```bash
# Clone the repository
git clone https://github.com/obieda-hussien/BlurView-Plus.git
cd BlurView-Plus

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew app:assembleDebug

# Build release APK
./gradlew app:assembleRelease

# Build library AAR
./gradlew library:assembleRelease

# Run tests
./gradlew test

# Run lint checks
./gradlew lint
```

APKs will be generated in `app/build/outputs/apk/` and AAR files in `library/build/outputs/aar/`.

License
-------

    Copyright 2025 Dmytro Saviuk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
