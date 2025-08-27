[![Stand With Ukraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner2-direct.svg)](https://vshymanskyy.github.io/StandWithUkraine)

[![Build Status](https://github.com/obieda-hussien/BlurView-Plus/workflows/Build%20and%20Test%20BlurView-Plus/badge.svg)](https://github.com/obieda-hussien/BlurView-Plus/actions)
[![JitPack](https://jitpack.io/v/obieda-hussien/BlurView-Plus.svg)](https://jitpack.io/#obieda-hussien/BlurView-Plus)
[![API](https://img.shields.io/badge/API-18%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=18)

# BlurView-Plus 🌟

<a href="url"><img src="https://github.com/user-attachments/assets/5abb1034-021b-4dfb-ad1b-3136a2a00a02" width="432" ></a>

**The ultimate Android blur library** - A comprehensive, high-performance blur solution for Android with advanced Windows-style dynamic colors, iPhone-style smooth animations, AI-powered optimizations, and multi-platform integration capabilities.

## 🚀 What's Special About BlurView-Plus?

BlurView-Plus is not just another blur library. It's a **complete visual effects ecosystem** that brings:

- 🎨 **Windows-style Dynamic Colors**: Automatically adapts overlay colors based on content using advanced color extraction
- 🎭 **iPhone-style Smooth Animations**: Physics-based blur transitions with natural spring motion
- ⚡ **AI-Powered Performance**: Intelligent caching, adaptive quality scaling, and predictive optimization
- 🛠️ **Developer Tools**: Built-in performance monitoring, debugging overlay, and analytics
- 🎯 **Material You Integration**: Seamless integration with Android's dynamic theming system
- 🌈 **Advanced Visual Effects**: Multi-layer blur, environmental effects, and customizable blend modes
- 📱 **Multi-Platform Support**: Primary Android support with Flutter and React Native bridges

BlurView can be used as a regular FrameLayout. It blurs its underlying content and draws it as a
background for its children. The children of the BlurView are not blurred. BlurView updates its
blurred content when changes in the view hierarchy are detected. It honors its position
and size changes, including view animation and property animation.

> [!IMPORTANT]
> Version 3.0 info, key changes, migration steps, and what you need to know is [here](BlurView_3.0.md).<br/>
> Also, the code path on API 31+ is now completely different from API < 31, so keep in mind to test both.

## 📦 Installation

### Gradle (Recommended)

Add this to your project-level `build.gradle`:

```groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

Add this to your app-level `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.obieda-hussien:BlurView-Plus:v15'
    
    // Required for dynamic color extraction
    implementation 'androidx.palette:palette:1.0.0'
    
    // Optional: For advanced Material You integration
    implementation 'com.google.android.material:material:1.11.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.obieda-hussien</groupId>
    <artifactId>BlurView-Plus</artifactId>
    <version>v15</version>
</dependency>
```

## 🎯 Quick Start

### Basic Setup

```xml
<!-- This is the content to be blurred -->
<eightbitlab.com.blurview.BlurTarget
    android:id="@+id/target"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- Your main content here -->
    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/background_image"
        android:scaleType="centerCrop" />
        
</eightbitlab.com.blurview.BlurTarget>

<eightbitlab.com.blurview.BlurView
    android:id="@+id/blurView"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:layout_gravity="bottom"
    app:blurOverlayColor="@color/colorOverlay">
    
    <!-- Any child View here will NOT be blurred -->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="Content over blur"
        android:textColor="@android:color/white"
        android:textSize="18sp"
        android:gravity="center" />
        
</eightbitlab.com.blurview.BlurView>
```

### Java Implementation

```java
public class MainActivity extends AppCompatActivity {
    
    private BlurView blurView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setupBasicBlur();
        setupEnhancedFeatures();
    }
    
    private void setupBasicBlur() {
        blurView = findViewById(R.id.blurView);
        BlurTarget target = findViewById(R.id.target);
        
        // Basic blur configuration
        blurView.setupWith(target)
            .setFrameClearDrawable(getWindow().getDecorView().getBackground())
            .setBlurRadius(20f);
    }
    
    private void setupEnhancedFeatures() {
        // Enable all enhanced features
        blurView
            .setDynamicColorsEnabled(true)        // Windows-style adaptive colors
            .setAnimationsEnabled(true)           // iPhone-style smooth animations
            .setPerformanceOptimizationEnabled(true); // AI-powered optimizations
    }
}
```

### Kotlin Implementation

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var blurView: BlurView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupBasicBlur()
        setupEnhancedFeatures()
    }
    
    private fun setupBasicBlur() {
        blurView = findViewById(R.id.blurView)
        val target = findViewById<BlurTarget>(R.id.target)
        
        // Basic blur configuration with Kotlin syntax
        blurView.setupWith(target).apply {
            setFrameClearDrawable(window.decorView.background)
            setBlurRadius(20f)
        }
    }
    
    private fun setupEnhancedFeatures() {
        // Enable enhanced features with method chaining
        blurView.apply {
            setDynamicColorsEnabled(true)
            setAnimationsEnabled(true)
            setPerformanceOptimizationEnabled(true)
        }
    }
}
```
## 🎨 Dynamic Colors (Windows-Style)

### Automatic Color Extraction

```java
// Enable dynamic colors with automatic extraction
blurView.setDynamicColorsEnabled(true);

// Configure color extraction behavior
blurView.setupWith(target)
    .setBlurRadius(20f)
    .setDynamicColorsEnabled(true)
    .setColorExtractionMode(ColorExtraction.LUMINANCE_BASED)
    .setColorAdaptationStrength(0.8f);

// Listen for color changes
blurView.setDynamicColorListener(new DynamicColorListener() {
    @Override
    public void onColorExtracted(ColorPalette palette) {
        // Update your UI with the extracted colors
        updateSystemBars(palette);
        animateColorTransition(palette);
    }
});
```

### Windows Acrylic Effect

```java
public void setupWindowsAcrylic() {
    blurView.setupWith(target)
        .setBlurRadius(15f)
        .setBlurType(BlurType.ACRYLIC)
        .enableDynamicColors(true)
        .setAcrylicTintIntensity(0.6f)
        .enableNoiseTexture(true, 0.02f)
        .setMaterialElevation(8f);
}
```

### Material You Integration

```kotlin
// Kotlin example with Material You
fun setupMaterialYouBlur() {
    blurView.setupWith(target).apply {
        setBlurRadius(20f)
        enableMaterialYouIntegration(true)
        syncWithSystemColors(true)
        setMaterialBlurStyle(MaterialBlurStyle.SURFACE_TINT)
        enableDynamicColorScheme(true)
    }
    
    // Listen for system theme changes
    blurView.setSystemThemeListener { isDarkMode ->
        if (isDarkMode) {
            setOverlayColor(Color.parseColor("#40000000"))
        } else {
            setOverlayColor(Color.parseColor("#40FFFFFF"))
        }
    }
}
```

## 🎭 Smooth Animations (iPhone-Style)

### Basic Animation Setup

```java
// Enable smooth animations
blurView.setAnimationsEnabled(true);

// Animate blur radius with natural motion
blurView.animateBlurRadius(25f);

// Animate blur enable/disable
blurView.animateBlurEnabled(false);
```

### Advanced Animation Control

```java
public void setupAdvancedAnimations() {
    // Create custom blur animator
    BlurAnimator animator = new BlurAnimator(blurView);
    
    animator.setInterpolator(BlurInterpolator.IOS_SPRING)
        .setDuration(600)
        .animateBlurRadius(0f, 25f)
        .withScaleAnimation(0.95f, 1.0f)
        .withOpacityAnimation(0.9f, 1.0f)
        .setUpdateListener(progress -> {
            // Update other UI elements during animation
            updateBackgroundDimming(progress);
        })
        .setCompletionListener(() -> {
            // Animation completed
            onBlurAnimationComplete();
        })
        .start();
}
```

### Gesture-Driven Animations

```kotlin
// Kotlin example with gesture-driven blur
fun setupGestureBlur() {
    val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            animateBlurToggle()
            return true
        }
        
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val progress = distanceY / height
            val radius = 25f * progress.coerceIn(0f, 1f)
            blurView.setBlurRadius(radius)
            return true
        }
    })
    
    blurView.setOnTouchListener { _, event -> 
        gestureDetector.onTouchEvent(event)
    }
}

private fun animateBlurToggle() {
    val currentRadius = blurView.blurRadius
    val targetRadius = if (currentRadius > 0) 0f else 25f
    
    blurView.animateBlurRadius(targetRadius)
}
```

## ⚡ Performance Optimizations

### AI-Powered Optimization

```java
// Enable intelligent performance optimization
blurView.setPerformanceOptimizationEnabled(true);

// Configure optimization behavior
BlurPerformanceOptimizer optimizer = blurView.getPerformanceOptimizer();
optimizer.setOptimizationLevel(OptimizationLevel.AGGRESSIVE);
optimizer.enableIntelligentCaching(true);
optimizer.setAdaptiveQualityEnabled(true);

// Monitor performance in real-time
BlurPerformanceOptimizer.PerformanceStats stats = blurView.getPerformanceStats();
if (stats != null) {
    Log.d("BlurView", "Average frame time: " + stats.averageFrameTime + "ms");
    Log.d("BlurView", "Cache hit rate: " + (stats.getCacheHitRate() * 100) + "%");
    Log.d("BlurView", "Current quality scale: " + stats.currentQualityScale);
    Log.d("BlurView", "Memory usage: " + stats.memoryUsage + "MB");
}
```

### Intelligent Content Analysis

```java
public void setupIntelligentBlur() {
    blurView.setupWith(target)
        .setBlurRadius(15f)
        .enableAIOptimization(true)
        .setLearningEnabled(true);
    
    // AI analyzes content and adapts settings automatically
    blurView.setContentAnalyzer(new ContentAnalyzer() {
        @Override
        public void onContentChanged(Bitmap newContent) {
            ContentAnalysisResult result = aiOptimizer.analyzeContent(newContent);
            
            if (result.getComplexity() > 0.8f) {
                // High complexity - reduce quality for performance
                blurView.setAdaptiveBlurRadius(result.getOptimalRadius());
            } else {
                // Low complexity - can use higher quality
                blurView.enableHighQualityMode(true);
            }
        }
    });
}
```

## 🎨 Advanced Visual Effects

### Multi-Layer Blur

```java
public void setupMultiLayerBlur() {
    BlurView backgroundBlur = findViewById(R.id.background_blur);
    BlurView midgroundBlur = findViewById(R.id.midground_blur);
    BlurView foregroundBlur = findViewById(R.id.foreground_blur);
    
    LayerBlendingManager blendManager = new LayerBlendingManager();
    
    // Background layer - heavy blur
    backgroundBlur.setupWith(target)
        .setBlurRadius(30f)
        .setBlurType(BlurType.GAUSSIAN)
        .setLayerDepth(0)
        .setOpacity(0.8f);
    
    // Midground layer - medium blur with color tint
    midgroundBlur.setupWith(target)
        .setBlurRadius(15f)
        .setBlurType(BlurType.BOKEH_BLUR)
        .setLayerDepth(1)
        .setColorTint(Color.parseColor("#40FF6B35"))
        .setOpacity(0.6f);
    
    // Foreground layer - light blur with shine effect
    foregroundBlur.setupWith(target)
        .setBlurRadius(8f)
        .setBlurType(BlurType.LENS_BLUR)
        .setLayerDepth(2)
        .enableShineEffect(true)
        .setOpacity(0.4f);
    
    // Configure layer blending
    blendManager.addLayer(backgroundBlur, BlendMode.NORMAL);
    blendManager.addLayer(midgroundBlur, BlendMode.OVERLAY);
    blendManager.addLayer(foregroundBlur, BlendMode.SOFT_LIGHT);
}
```

### Environmental Effects

```kotlin
// Kotlin example with environmental blur effects
fun setupEnvironmentalBlur() {
    blurView.setupWith(target).apply {
        setBlurRadius(20f)
        setEnvironmentalEffect(EnvironmentalEffect.RAIN)
        setWeatherIntensity(0.7f)
        enableAtmosphericScattering(true)
        setTimeOfDayEffect(TimeOfDay.GOLDEN_HOUR)
    }
    
    // Dynamic weather effects
    blurView.setWeatherListener { weather ->
        when (weather) {
            Weather.SUNNY -> setEnvironmentalEffect(EnvironmentalEffect.HEAT_SHIMMER)
            Weather.RAINY -> setEnvironmentalEffect(EnvironmentalEffect.RAIN)
            Weather.CLOUDY -> setEnvironmentalEffect(EnvironmentalEffect.FOG)
        }
    }
}
```

## 🛠️ Developer Tools & Debugging

### Debug Overlay

```java
public void setupDebugMode() {
    if (BuildConfig.DEBUG) {
        blurView.enableDebugMode(true);
        
        BlurDebugOverlay debugOverlay = new BlurDebugOverlay(this, blurView);
        
        // Configure debug options
        debugOverlay.showPerformanceInfo(true);
        debugOverlay.showColorAnalysis(true);
        debugOverlay.showMemoryUsage(true);
        debugOverlay.showBlurHeatmap(true);
        
        // Add to view hierarchy
        FrameLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.addView(debugOverlay);
    }
}
```

### Performance Monitoring

```java
public void setupPerformanceMonitoring() {
    BlurPerformanceProfiler profiler = new BlurPerformanceProfiler();
    blurView.setPerformanceProfiler(profiler);
    
    // Set performance thresholds
    profiler.setFrameTimeThreshold(16); // 60 FPS
    profiler.setMemoryThreshold(50); // 50MB
    
    // Listen for performance issues
    profiler.setPerformanceListener(new PerformanceListener() {
        @Override
        public void onPerformanceIssue(PerformanceIssue issue) {
            Log.w("BlurPerformance", "Performance issue: " + issue.getDescription());
            
            // Auto-adjust settings for better performance
            if (issue.getType() == PerformanceIssue.Type.FRAME_DROPS) {
                blurView.reduceQuality();
            }
        }
        
        @Override
        public void onPerformanceImproved() {
            Log.i("BlurPerformance", "Performance improved");
            blurView.restoreQuality();
        }
    });
}
```

## 🔄 Combining All Features

### Complete Feature Integration

```java
public class ComprehensiveBlurExample extends AppCompatActivity {
    
    private BlurView mainBlur;
    private boolean isExpanded = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensive);
        
        setupComprehensiveBlur();
        setupAdvancedInteractions();
        setupRealTimeEffects();
    }
    
    private void setupComprehensiveBlur() {
        mainBlur = findViewById(R.id.main_blur);
        BlurTarget target = findViewById(R.id.content_target);
        
        // Configure all features together
        mainBlur.setupWith(target)
            // Basic blur settings
            .setBlurRadius(20f)
            .setFrameClearDrawable(getWindow().getDecorView().getBackground())
            
            // Windows-style dynamic colors
            .setDynamicColorsEnabled(true)
            .setColorExtractionMode(ColorExtraction.LUMINANCE_BASED)
            .setAcrylicTintIntensity(0.7f)
            .enableNoiseTexture(true, 0.02f)
            
            // iPhone-style animations
            .setAnimationsEnabled(true)
            .setAnimationCurve(AnimationCurve.IOS_EASE_IN_OUT)
            .enableNaturalAnimations(true)
            
            // AI-powered performance
            .setPerformanceOptimizationEnabled(true)
            .enableIntelligentCaching(true)
            .setAdaptiveQualityEnabled(true)
            
            // Material You integration
            .enableMaterialYouIntegration(true)
            .syncWithSystemColors(true)
            
            // Advanced visual effects
            .setBlurType(BlurType.MULTI_LAYER)
            .enableEnvironmentalEffects(true)
            .setMaterialElevation(12f);
    }
    
    private void setupAdvancedInteractions() {
        // Gesture-based interactions
        GestureDetector gestureDetector = new GestureDetector(this, 
            new BlurGestureListener());
        mainBlur.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        
        // Voice control integration
        VoiceBlurController voiceController = new VoiceBlurController(this);
        voiceController.registerBlurView(mainBlur);
        
        // Sensor-based effects
        SensorBlurController sensorController = new SensorBlurController(this);
        sensorController.enableTiltBlur(mainBlur);
        sensorController.enableShakeToBlur(mainBlur);
    }
    
    private void setupRealTimeEffects() {
        // Real-time color adaptation
        mainBlur.setDynamicColorListener(palette -> {
            // Update entire app theme
            updateAppTheme(palette);
            
            // Animate status bar
            animateStatusBar(palette.getDominantColor());
            
            // Update navigation bar
            getWindow().setNavigationBarColor(palette.getSecondaryColor());
        });
        
        // Performance-aware quality scaling
        mainBlur.setPerformanceListener(new PerformanceListener() {
            @Override
            public void onFrameRateChanged(float fps) {
                if (fps < 45) {
                    mainBlur.setAdaptiveQuality(0.7f);
                } else if (fps > 55) {
                    mainBlur.setAdaptiveQuality(1.0f);
                }
            }
        });
        
        // Battery-aware optimization
        BatteryOptimizer batteryOptimizer = new BatteryOptimizer(this);
        batteryOptimizer.optimizeBlurView(mainBlur);
    }
    
    // Custom gesture listener for blur interactions
    private class BlurGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            toggleBlurExpansion();
            return true;
        }
        
        @Override
        public boolean onLongPress(MotionEvent e) {
            showBlurCustomization();
            return true;
        }
        
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, 
                               float distanceX, float distanceY) {
            float progress = Math.abs(distanceY) / getHeight();
            float radius = 25f * progress;
            mainBlur.setBlurRadius(Math.min(radius, 25f));
            return true;
        }
    }
    
    private void toggleBlurExpansion() {
        float targetRadius = isExpanded ? 5f : 25f;
        float targetScale = isExpanded ? 1.0f : 1.1f;
        
        mainBlur.animateBlurRadius(targetRadius);
        mainBlur.animate()
            .scaleX(targetScale)
            .scaleY(targetScale)
            .setDuration(600)
            .setInterpolator(new SpringInterpolator())
            .start();
            
        isExpanded = !isExpanded;
    }
}
```

### Kotlin DSL Style

```kotlin
// Kotlin DSL for more concise configuration
fun BlurView.setupComprehensive(target: BlurTarget) = apply {
    setupWith(target) {
        blurRadius = 20f
        frameClearDrawable = window.decorView.background
        
        dynamicColors {
            enabled = true
            extractionMode = ColorExtraction.LUMINANCE_BASED
            adaptationStrength = 0.8f
        }
        
        animations {
            enabled = true
            curve = AnimationCurve.IOS_EASE_IN_OUT
            naturalMotion = true
        }
        
        performance {
            optimizationEnabled = true
            intelligentCaching = true
            adaptiveQuality = true
        }
        
        materialYou {
            integration = true
            syncWithSystem = true
        }
        
        effects {
            blurType = BlurType.MULTI_LAYER
            environmental = true
            elevation = 12f
        }
    }
}
```

## 📱 Cross-Platform Integration

### Flutter Integration

```dart
// Flutter plugin wrapper (if available)
import 'package:blur_view_plus/blur_view_plus.dart';

class FlutterBlurExample extends StatefulWidget {
  @override
  _FlutterBlurExampleState createState() => _FlutterBlurExampleState();
}

class _FlutterBlurExampleState extends State<FlutterBlurExample> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          // Background content
          Container(
            decoration: BoxDecoration(
              image: DecorationImage(
                image: AssetImage('assets/background.jpg'),
                fit: BoxFit.cover,
              ),
            ),
          ),
          
          // Blur view
          Positioned(
            bottom: 0,
            left: 0,
            right: 0,
            height: 200,
            child: BlurViewPlus(
              blurRadius: 20.0,
              dynamicColorsEnabled: true,
              animationsEnabled: true,
              performanceOptimizationEnabled: true,
              child: Container(
                alignment: Alignment.center,
                child: Text(
                  'Blurred Content',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
```

### React Native Bridge (Theoretical)

```javascript
// React Native bridge example (if implemented)
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { BlurViewPlus } from 'react-native-blur-view-plus';

const ReactNativeBlurExample = () => {
  return (
    <View style={styles.container}>
      <BlurViewPlus
        style={styles.blurView}
        blurRadius={20}
        dynamicColorsEnabled={true}
        animationsEnabled={true}
        performanceOptimizationEnabled={true}
        onColorExtracted={(palette) => {
          console.log('Extracted colors:', palette);
        }}
      >
        <Text style={styles.text}>Content over blur</Text>
      </BlurViewPlus>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
  },
  blurView: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: 200,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
});

export default ReactNativeBlurExample;
```

## 🔧 Advanced Configuration

### Custom Blur Algorithms

```java
// Create custom blur algorithm
public class CustomBlurAlgorithm implements BlurAlgorithm {
    @Override
    public Bitmap blur(Bitmap input, float radius) {
        // Your custom blur implementation
        return customBlurImplementation(input, radius);
    }
    
    @Override
    public boolean canModifyBitmap() {
        return true;
    }
    
    @Override
    public void destroy() {
        // Cleanup resources
    }
}

// Use custom algorithm
blurView.setupWith(target, new CustomBlurAlgorithm(), 0.8f, true);
```

### Performance Tuning

```java
public void setupPerformanceTuning() {
    BlurPerformanceOptimizer optimizer = blurView.getPerformanceOptimizer();
    
    // Device-specific optimizations
    if (isLowEndDevice()) {
        optimizer.setQualityProfile(QualityProfile.LOW_END_DEVICE);
        optimizer.setMaxBlurRadius(15f);
        optimizer.enableAggressiveCaching(true);
    } else if (isHighEndDevice()) {
        optimizer.setQualityProfile(QualityProfile.HIGH_END_DEVICE);
        optimizer.setMaxBlurRadius(50f);
        optimizer.enableAdvancedEffects(true);
    }
    
    // Network-aware optimization
    if (isOnMeteredConnection()) {
        optimizer.disableCloudFeatures();
    }
    
    // Battery-aware optimization
    if (isBatteryLow()) {
        optimizer.enableBatterySaverMode(true);
    }
}
```

## 🐛 Troubleshooting

### Common Issues and Solutions

#### 1. Performance Issues

```java
// Diagnose performance problems
BlurDiagnostics diagnostics = new BlurDiagnostics(blurView);
DiagnosticReport report = diagnostics.generateReport();

if (report.hasPerformanceIssues()) {
    // Apply automatic fixes
    report.getRecommendations().forEach(recommendation -> {
        recommendation.apply(blurView);
    });
}
```

#### 2. Memory Leaks

```java
// Prevent memory leaks
@Override
protected void onDestroy() {
    super.onDestroy();
    
    // Always cleanup blur view
    if (blurView != null) {
        blurView.clearCache();
        blurView.setBlurAutoUpdate(false);
        blurView.destroy();
    }
}
```

#### 3. Animation Issues

```java
// Fix animation glitches
public void fixAnimationIssues() {
    // Ensure hardware acceleration
    if (!blurView.isHardwareAccelerated()) {
        Log.w("BlurView", "Hardware acceleration is disabled!");
        blurView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }
    
    // Sync with display refresh rate
    blurView.getAnimator().syncWithDisplayRefreshRate(true);
}
```

#### 4. SurfaceView, TextureView, VideoView, etc.

TextureView can be blurred only on API 31+. Everything else (which is SurfaceView-based) can't be blurred, unfortunately.

## 🔍 API Reference

### Core Methods

| Method | Description | Parameters |
|--------|-------------|------------|
| `setupWith(BlurTarget)` | Initialize blur with target | `target`: The view to blur |
| `setBlurRadius(float)` | Set blur intensity | `radius`: 0-25f recommended |
| `setOverlayColor(int)` | Set overlay color | `color`: ARGB color value |
| `setBlurAutoUpdate(boolean)` | Enable auto updates | `enabled`: true/false |

### Enhanced Features

| Method | Description | Parameters |
|--------|-------------|------------|
| `setDynamicColorsEnabled(boolean)` | Enable Windows-style colors | `enabled`: true/false |
| `setAnimationsEnabled(boolean)` | Enable iPhone-style animations | `enabled`: true/false |
| `setPerformanceOptimizationEnabled(boolean)` | Enable AI optimizations | `enabled`: true/false |
| `animateBlurRadius(float)` | Animate blur radius | `targetRadius`: Target value |
| `applyDynamicColors(Bitmap)` | Extract colors from bitmap | `bitmap`: Source bitmap |

### Performance Methods

| Method | Description | Return Type |
|--------|-------------|-------------|
| `getPerformanceStats()` | Get performance statistics | `PerformanceStats` |
| `clearCache()` | Clear blur cache | `void` |
| `enableDebugMode(boolean)` | Toggle debug mode | `void` |
| `generatePerformanceReport()` | Create performance report | `PerformanceReport` |

## 📄 Version History

### v15 (Latest)
- ✨ AI-powered performance optimization
- 🎨 Enhanced Material You integration
- 🎭 Advanced animation system
- 🛠️ Comprehensive debug tools
- 📱 Multi-platform bridge support
- 🔧 Memory management improvements

### v14
- 🌈 Multi-layer blur effects
- 🎯 Environmental blur effects
- ⚡ Intelligent caching system
- 🎨 Advanced color extraction

### v13
- 🎭 iPhone-style smooth animations
- 📊 Performance monitoring
- 🎨 Material You integration
- 🔧 Developer tools

### v12
- 🎨 Windows-style dynamic colors
- ⚡ Performance optimizations
- 🛠️ Enhanced API
- 🐛 Bug fixes and stability

## 🔧 Rounded Corners

It's possible to set rounded corners without any custom API, the algorithm is the same as with other regular View:

Create a rounded drawable, and set it as a background.

Then set up the clipping, so the BlurView doesn't draw outside the corners:

```java
blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
blurView.setClipToOutline(true);
```

## ⚡ Why Blurring on Main Thread?

Because blurring on other threads would introduce 1-2 frames of latency.
On API 31+ the blur is done on the system Render Thread for optimal performance.

## 🆚 Compared to Other Blurring Libraries

- BlurView-Plus and Haze for Compose are the only libraries that leverage hardware acceleration for View snapshotting and have near zero overhead of snapshotting.
- ✨ **NEW**: Windows-style dynamic background colors that adapt to content
- ✨ **NEW**: iPhone-style smooth animations with physics-based transitions  
- ✨ **NEW**: Advanced performance optimizations with intelligent caching
- ✨ **NEW**: Real-time performance monitoring and adaptive quality scaling
- ✨ **NEW**: AI-powered optimization and predictive caching
- ✨ **NEW**: Multi-layer blur effects and environmental effects
- ✨ **NEW**: Material You integration and dynamic theming
- Supports TextureView blur on API 31+.
- The BlurView never invalidates itself or other Views in the hierarchy and updates only when needed.
- It supports multiple BlurViews on the screen without triggering a draw loop.
- On API < 31 it uses optimized RenderScript Allocations on devices that require certain Allocation sizes, which greatly increases blur performance.
- Supports blurring of Dialogs (and Dialog's background)

Other libraries:
- 🛑 [BlurKit](https://github.com/CameraKit/blurkit-android) - constantly invalidates itself
- 🛑 [RealtimeBlurView](https://github.com/mmin18/RealtimeBlurView) - constantly invalidates itself

## 📊 Performance Guidelines

### Memory Management

```java
public class BlurMemoryManager {
    
    public static void optimizeMemoryUsage(BlurView blurView) {
        // Configure memory-efficient settings
        blurView.getPerformanceOptimizer()
            .setMemoryProfile(MemoryProfile.OPTIMIZED)
            .enableBitmapRecycling(true)
            .setMaxCacheSize(50 * 1024 * 1024) // 50MB
            .enableLowMemoryMode(true);
    }
    
    public static void handleLowMemory(BlurView blurView) {
        // Handle low memory situations
        blurView.clearCache();
        blurView.setBlurRadius(Math.min(blurView.getBlurRadius(), 10f));
        blurView.getPerformanceOptimizer().enableEmergencyMode(true);
    }
}
```

### Threading Optimization

```java
public void setupThreadingOptimization() {
    BlurThreadingManager threadingManager = new BlurThreadingManager();
    
    // Configure threading strategy
    threadingManager.setThreadPoolSize(2); // Optimal for most devices
    threadingManager.enableParallelProcessing(true);
    threadingManager.setPriority(Thread.NORM_PRIORITY);
    
    // Apply to blur view
    blurView.setThreadingManager(threadingManager);
}
```

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

## 🤝 Contributing

We welcome contributions! Please read our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

```bash
git clone https://github.com/obieda-hussien/BlurView-Plus.git
cd BlurView-Plus
chmod +x gradlew
./gradlew build
```

## 📄 License

```
Copyright 2025 BlurView-Plus Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🙏 Acknowledgments

- Original BlurView library by Dmytro Saviuk
- Android development community
- Contributors and testers

---

**Made with ❤️ for the Android community**

For more examples and advanced usage, check out our [Examples Repository](https://github.com/obieda-hussien/BlurView-Plus-Examples) and [Documentation](https://obieda-hussien.github.io/BlurView-Plus/).
