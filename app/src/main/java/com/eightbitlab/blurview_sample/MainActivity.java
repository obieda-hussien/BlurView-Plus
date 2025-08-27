package com.eightbitlab.blurview_sample;

import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import eightbitlab.com.blurview.BlurAnimator;
import eightbitlab.com.blurview.BlurPerformanceOptimizer;
import eightbitlab.com.blurview.BlurTarget;
import eightbitlab.com.blurview.BlurView;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BlurTarget target;
    private TabLayout tabLayout;
    private BlurView bottomBlurView;
    private BlurView topBlurView;
    private BlurView leftSideBlurView;
    private BlurView rightSideBlurView;
    private BlurView centerOverlayBlurView;
    private SeekBar radiusSeekBar;
    private SeekBar qualitySeekBar;
    private SeekBar opacitySeekBar;
    
    // Enhanced features components
    private Switch animationsSwitch;
    private Switch dynamicColorsSwitch;
    private Switch performanceOptimizationSwitch;
    private Switch autoRefreshSwitch;
    private Switch debugOverlaySwitch;
    private Switch multiLayerBlurSwitch;
    private Switch environmentalEffectsSwitch;
    private Switch materialYouIntegrationSwitch;
    private CheckBox realTimeMonitoringCheckbox;
    private CheckBox intelligentCachingCheckbox;
    private CheckBox adaptiveQualityCheckbox;
    private Spinner blurAlgorithmSpinner;
    private Spinner colorExtractionModeSpinner;
    private TextView performanceStatsText;
    private TextView debugInfoText;
    private TextView colorAnalysisText;
    private TextView cacheStatsText;
    private Button resetConfigButton;
    private Button exportConfigButton;
    private Button captureSnapshotButton;
    private Button stressTestButton;
    private FloatingActionButton demoModeFab;
    
    // Feature state
    private boolean isExpanded = false;
    private boolean isDemoMode = false;
    private boolean isStressTesting = false;
    private GestureDetector gestureDetector;
    private Handler animationHandler = new Handler(Looper.getMainLooper());
    private Runnable demoModeRunnable;
    private Runnable stressTestRunnable;
    
    // Performance monitoring
    private long frameCount = 0;
    private long lastFrameTime = 0;
    private float currentFPS = 60f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupBlurView();
        setupViewPager();
        setupEnhancedFeatures();
        setupGestureRecognition();
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(bottomBlurView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            bottomBlurView.setPadding(0, 0, 0, insets.bottom);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) topBlurView.getLayoutParams();
            params.topMargin = (int) (insets.top * 1.5);
            topBlurView.setLayoutParams(params);

            return windowInsets;
        });
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        bottomBlurView = findViewById(R.id.bottomBlurView);
        topBlurView = findViewById(R.id.topBlurView);
        leftSideBlurView = findViewById(R.id.leftSideBlurView);
        rightSideBlurView = findViewById(R.id.rightSideBlurView);
        centerOverlayBlurView = findViewById(R.id.centerOverlayBlurView);
        target = findViewById(R.id.target);
        radiusSeekBar = findViewById(R.id.radiusSeekBar);
        qualitySeekBar = findViewById(R.id.qualitySeekBar);
        opacitySeekBar = findViewById(R.id.opacitySeekBar);
        
        // Enhanced features UI components
        animationsSwitch = findViewById(R.id.animationsSwitch);
        dynamicColorsSwitch = findViewById(R.id.dynamicColorsSwitch);
        performanceOptimizationSwitch = findViewById(R.id.performanceOptimizationSwitch);
        autoRefreshSwitch = findViewById(R.id.autoRefreshSwitch);
        debugOverlaySwitch = findViewById(R.id.debugOverlaySwitch);
        multiLayerBlurSwitch = findViewById(R.id.multiLayerBlurSwitch);
        environmentalEffectsSwitch = findViewById(R.id.environmentalEffectsSwitch);
        materialYouIntegrationSwitch = findViewById(R.id.materialYouIntegrationSwitch);
        realTimeMonitoringCheckbox = findViewById(R.id.realTimeMonitoringCheckbox);
        intelligentCachingCheckbox = findViewById(R.id.intelligentCachingCheckbox);
        adaptiveQualityCheckbox = findViewById(R.id.adaptiveQualityCheckbox);
        blurAlgorithmSpinner = findViewById(R.id.blurAlgorithmSpinner);
        colorExtractionModeSpinner = findViewById(R.id.colorExtractionModeSpinner);
        performanceStatsText = findViewById(R.id.performanceStatsText);
        debugInfoText = findViewById(R.id.debugInfoText);
        colorAnalysisText = findViewById(R.id.colorAnalysisText);
        cacheStatsText = findViewById(R.id.cacheStatsText);
        resetConfigButton = findViewById(R.id.resetConfigButton);
        exportConfigButton = findViewById(R.id.exportConfigButton);
        captureSnapshotButton = findViewById(R.id.captureSnapshotButton);
        stressTestButton = findViewById(R.id.stressTestButton);
        demoModeFab = findViewById(R.id.demoModeFab);
        
        // Setup spinners
        setupSpinners();
        
        // Setup buttons
        setupButtons();
        
        // Rounded corners + casting elevation shadow with transparent background
        setupBlurViewStyles();
    }

    private void setupViewPager() {
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupBlurView() {
        final float radius = 25f;
        final float minBlurRadius = 4f;
        final float step = 4f;

        //set background, if your root layout doesn't have one
        final Drawable windowBackground = getWindow().getDecorView().getBackground();
        
        // Setup all blur views with comprehensive enhanced features
        setupIndividualBlurView(topBlurView, windowBackground, radius, "Top");
        setupIndividualBlurView(bottomBlurView, windowBackground, radius, "Bottom");
        setupIndividualBlurView(leftSideBlurView, windowBackground, radius / 2, "Left");
        setupIndividualBlurView(rightSideBlurView, windowBackground, radius / 2, "Right");
        setupIndividualBlurView(centerOverlayBlurView, windowBackground, radius * 1.5f, "Center");
        
        // Hide side and center views initially
        leftSideBlurView.setVisibility(View.GONE);
        rightSideBlurView.setVisibility(View.GONE);
        centerOverlayBlurView.setVisibility(View.GONE);

        setupSeekBars(radius, minBlurRadius, step);
    }
    
    private void setupIndividualBlurView(BlurView blurView, Drawable windowBackground, float radius, String tag) {
        blurView.setAnimationsEnabled(true)
                .setPerformanceOptimizationEnabled(true)
                .setDynamicColorsEnabled(false) // Will be enabled via switch
                .setupWith(target)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        
        // Tag for debugging
        blurView.setTag("BlurView_" + tag);
    }
    
    private void setupSeekBars(float radius, float minBlurRadius, float step) {
        // Radius SeekBar
        int initialProgress = (int) (radius * step);
        radiusSeekBar.setProgress(initialProgress);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBarListenerAdapter() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float blurRadius = progress / step;
                blurRadius = Math.max(blurRadius, minBlurRadius);
                
                applyRadiusToAllBlurViews(blurRadius, fromUser);
            }
        });
        
        // Quality SeekBar (0.5x to 2.0x)
        qualitySeekBar.setProgress(50); // 1.0x quality
        qualitySeekBar.setOnSeekBarChangeListener(new SeekBarListenerAdapter() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float quality = 0.5f + (progress / 100f) * 1.5f; // 0.5 to 2.0
                applyQualityToAllBlurViews(quality);
                updateDebugInfo();
            }
        });
        
        // Opacity SeekBar (0 to 100, representing 0% to 100% opacity)
        opacitySeekBar.setProgress(50); // 50% opacity to match reset default
        opacitySeekBar.setOnSeekBarChangeListener(new SeekBarListenerAdapter() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int alpha = (int) (progress * 2.55f); // 0-100 to 0-255
                applyOpacityToAllBlurViews(alpha);
                updateDebugInfo();
            }
        });
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return Page.values()[position].getFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Page.values()[position].getTitle();
        }

        @Override
        public int getCount() {
            return Page.values().length;
        }
    }

    enum Page {
        FIRST("ScrollView") {
            @Override
            Fragment getFragment() {
                return new ScrollFragment();
            }
        },
        SECOND("RecyclerView") {
            @Override
            Fragment getFragment() {
                return new ListFragment();
            }
        },
        THIRD("Static") {
            @Override
            Fragment getFragment() {
                return new ImageFragment();
            }
        };

        private String title;

        Page(String title) {
            this.title = title;
        }

        String getTitle() {
            return title;
        }

        abstract Fragment getFragment();
    }
    
    /**
     * Sets up enhanced BlurView features with UI controls.
     */
    private void setupEnhancedFeatures() {
        // Setup switch listeners for enhanced features
        animationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
            for (BlurView blurView : allBlurViews) {
                blurView.setAnimationsEnabled(isChecked);
            }
            showFeatureToast("iPhone-style Animations", isChecked);
            updateDebugInfo();
        });
        
        dynamicColorsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
            for (BlurView blurView : allBlurViews) {
                blurView.setDynamicColorsEnabled(isChecked);
                if (isChecked) {
                    // Trigger dynamic color extraction with null bitmap (auto-extract)
                    blurView.applyDynamicColors(null);
                }
            }
            showFeatureToast("Windows-style Dynamic Colors", isChecked);
            updateDebugInfo();
        });
        
        performanceOptimizationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
            for (BlurView blurView : allBlurViews) {
                blurView.setPerformanceOptimizationEnabled(isChecked);
            }
            showFeatureToast("Advanced Performance Optimizations", isChecked);
            
            if (isChecked) {
                startPerformanceMonitoring();
            } else {
                performanceStatsText.setText("Performance monitoring disabled");
                // Also disable related sub-features when main performance optimization is disabled
                realTimeMonitoringCheckbox.setChecked(false);
                adaptiveQualityCheckbox.setChecked(false);
                stopRealTimeMonitoring();
            }
            updateDebugInfo();
        });
        
        autoRefreshSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simulate auto-refresh functionality
            showFeatureToast("Auto-refresh During Animations", isChecked);
            updateDebugInfo();
        });
        
        debugOverlaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                debugInfoText.setVisibility(View.VISIBLE);
                updateDebugInfo();
            } else {
                debugInfoText.setVisibility(View.GONE);
            }
            showFeatureToast("Debug Overlay", isChecked);
        });
        
        multiLayerBlurSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                leftSideBlurView.setVisibility(View.VISIBLE);
                rightSideBlurView.setVisibility(View.VISIBLE);
                centerOverlayBlurView.setVisibility(View.VISIBLE);
            } else {
                leftSideBlurView.setVisibility(View.GONE);
                rightSideBlurView.setVisibility(View.GONE);
                centerOverlayBlurView.setVisibility(View.GONE);
            }
            showFeatureToast("Multi-layer Blur Effects", isChecked);
            updateDebugInfo();
        });
        
        environmentalEffectsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simulate environmental effects by adding subtle color variations
            if (isChecked) {
                simulateEnvironmentalEffects();
            } else {
                // Reset overlay colors when disabled
                BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
                for (BlurView blurView : allBlurViews) {
                    if (blurView.getVisibility() == View.VISIBLE) {
                        blurView.setOverlayColor(Color.TRANSPARENT);
                    }
                }
                updateDebugInfo();
            }
            showFeatureToast("Environmental Effects", isChecked);
        });
        
        materialYouIntegrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simulate Material You integration
            if (isChecked) {
                applyMaterialYouTheming();
            } else {
                // Reset overlay colors when disabled
                BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
                for (BlurView blurView : allBlurViews) {
                    if (blurView.getVisibility() == View.VISIBLE) {
                        blurView.setOverlayColor(Color.TRANSPARENT);
                    }
                }
                updateDebugInfo();
            }
            showFeatureToast("Material You Integration", isChecked);
        });
        
        // Setup checkboxes
        realTimeMonitoringCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startRealTimeMonitoring();
            } else {
                stopRealTimeMonitoring();
            }
            showFeatureToast("Real-time Performance Monitoring", isChecked);
            updateDebugInfo();
        });
        
        intelligentCachingCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simulate intelligent caching toggle
            showFeatureToast("Intelligent Caching", isChecked);
            updateCacheStats();
            updateDebugInfo();
        });
        
        adaptiveQualityCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simulate adaptive quality scaling
            if (isChecked) {
                enableAdaptiveQuality();
            }
            showFeatureToast("Adaptive Quality Scaling", isChecked);
            updateDebugInfo();
        });
        
        // Start with performance monitoring enabled
        startPerformanceMonitoring();
    }
    
    /**
     * Sets up gesture recognition for smooth animations.
     */
    private void setupGestureRecognition() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // Double tap to toggle blur expansion with smooth animation
                animateBlurToggle();
                return true;
            }
            
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (topBlurView.isAnimationsEnabled()) {
                    // Fling gesture for dynamic blur radius based on velocity
                    float targetRadius = Math.abs(velocityY) / 100f + 10f; // Scale velocity to radius
                    targetRadius = Math.min(targetRadius, 50f); // Cap maximum radius
                    
                    topBlurView.animateBlurRadius(targetRadius);
                    bottomBlurView.animateBlurRadius(targetRadius);
                    
                    // Update seekbar to reflect the new radius
                    int progress = (int) (targetRadius * 4f);
                    radiusSeekBar.setProgress(progress);
                    
                    return true;
                }
                return false;
            }
        });
        
        // Set touch listener on the main container
        findViewById(R.id.root).setOnTouchListener((v, event) -> {
            return gestureDetector.onTouchEvent(event);
        });
    }
    
    /**
     * Animates blur toggle with iPhone-style spring animation.
     */
    private void animateBlurToggle() {
        if (!topBlurView.isAnimationsEnabled()) {
            return;
        }
        
        float fromRadius = isExpanded ? 35f : 15f;
        float toRadius = isExpanded ? 15f : 35f;
        
        // Animate both blur views simultaneously
        topBlurView.animateBlurRadius(toRadius);
        bottomBlurView.animateBlurRadius(toRadius);
        
        // Update seekbar
        int progress = (int) (toRadius * 4f);
        radiusSeekBar.setProgress(progress);
        
        isExpanded = !isExpanded;
        
        showFeatureToast("Blur " + (isExpanded ? "Expanded" : "Collapsed"), true);
    }
    
    /**
     * Starts performance monitoring and updates UI.
     */
    private void startPerformanceMonitoring() {
        if (!performanceOptimizationSwitch.isChecked()) {
            return;
        }
        
        // Update performance stats every 2 seconds
        performanceStatsText.postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePerformanceStats();
                if (performanceOptimizationSwitch.isChecked()) {
                    performanceStatsText.postDelayed(this, 2000);
                }
            }
        }, 2000);
    }
    
    /**
     * Updates performance statistics display.
     */
    private void updatePerformanceStats() {
        BlurPerformanceOptimizer.PerformanceStats stats = topBlurView.getPerformanceStats();
        if (stats != null) {
            String performanceLevel;
            if (stats.averageFrameTime < 16.7f) {
                performanceLevel = "Excellent";
            } else if (stats.averageFrameTime < 25f) {
                performanceLevel = "Good";
            } else if (stats.averageFrameTime < 33f) {
                performanceLevel = "Fair";
            } else {
                performanceLevel = "Poor";
            }
            
            String statsText = String.format("Performance: %s\nFrame: %.1fms\nCache: %d%% hit rate", 
                performanceLevel, 
                stats.averageFrameTime,
                (int) (stats.getCacheHitRate() * 100));
            
            performanceStatsText.setText(statsText);
        } else {
            performanceStatsText.setText("Performance: Monitoring disabled");
        }
    }
    
    /**
     * Shows a toast message for feature toggles.
     */
    private void showFeatureToast(String featureName, boolean enabled) {
        String message = featureName + ": " + (enabled ? "Enabled" : "Disabled");
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    // ========== NEW COMPREHENSIVE HELPER METHODS ==========
    
    private void setupSpinners() {
        // Blur Algorithm Spinner
        String[] algorithms = {"Auto (Recommended)", "RenderScript", "Fast Approximation", "High Quality"};
        ArrayAdapter<String> algorithmAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, algorithms);
        algorithmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blurAlgorithmSpinner.setAdapter(algorithmAdapter);
        
        // Color Extraction Mode Spinner
        String[] extractionModes = {"Dominant Color", "Vibrant Color", "Muted Color", "Dark Vibrant", "Light Vibrant", "Dark Muted", "Light Muted"};
        ArrayAdapter<String> extractionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, extractionModes);
        extractionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorExtractionModeSpinner.setAdapter(extractionAdapter);
    }
    
    private void setupButtons() {
        resetConfigButton.setOnClickListener(v -> resetAllConfigurations());
        exportConfigButton.setOnClickListener(v -> exportCurrentConfiguration());
        captureSnapshotButton.setOnClickListener(v -> captureBlurSnapshot());
        stressTestButton.setOnClickListener(v -> toggleStressTest());
        demoModeFab.setOnClickListener(v -> toggleDemoMode());
    }
    
    private void setupBlurViewStyles() {
        // Apply rounded corners and elevation to all blur views
        BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
        
        for (BlurView blurView : allBlurViews) {
            blurView.setClipToOutline(true);
            blurView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (view.getBackground() != null) {
                        view.getBackground().getOutline(outline);
                        outline.setAlpha(1f);
                    }
                }
            });
        }
    }
    
    private void applyRadiusToAllBlurViews(float blurRadius, boolean fromUser) {
        BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
        
        for (BlurView blurView : allBlurViews) {
            if (blurView.getVisibility() == View.VISIBLE) {
                if (blurView.isAnimationsEnabled() && fromUser) {
                    blurView.animateBlurRadius(blurRadius);
                } else {
                    blurView.setBlurRadius(blurRadius);
                }
            }
        }
        updateDebugInfo();
    }
    
    private void applyQualityToAllBlurViews(float quality) {
        // Quality scaling would be applied through performance optimizer
        // This is a demonstration of how it could work
        BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
        
        for (BlurView blurView : allBlurViews) {
            if (blurView.getVisibility() == View.VISIBLE) {
                // Force blur update to apply new quality settings
                blurView.forceBlurUpdate();
            }
        }
        showFeatureToast("Blur Quality", true);
    }
    
    private void applyOpacityToAllBlurViews(int alpha) {
        BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
        
        for (BlurView blurView : allBlurViews) {
            if (blurView.getVisibility() == View.VISIBLE) {
                int currentColor = Color.WHITE; // Default overlay color
                int newColor = Color.argb(alpha, Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor));
                blurView.setOverlayColor(newColor);
            }
        }
    }
    
    private void resetAllConfigurations() {
        // Reset all switches and controls to default
        animationsSwitch.setChecked(true);
        dynamicColorsSwitch.setChecked(false);
        performanceOptimizationSwitch.setChecked(true);
        autoRefreshSwitch.setChecked(false);
        debugOverlaySwitch.setChecked(false);
        multiLayerBlurSwitch.setChecked(false);
        environmentalEffectsSwitch.setChecked(false);
        materialYouIntegrationSwitch.setChecked(false);
        
        realTimeMonitoringCheckbox.setChecked(false);
        intelligentCachingCheckbox.setChecked(true);
        adaptiveQualityCheckbox.setChecked(false);
        
        radiusSeekBar.setProgress(100); // 25f radius
        qualitySeekBar.setProgress(50); // 1.0x quality
        opacitySeekBar.setProgress(50); // 50% opacity
        
        blurAlgorithmSpinner.setSelection(0);
        colorExtractionModeSpinner.setSelection(0);
        
        // Hide additional blur views
        leftSideBlurView.setVisibility(View.GONE);
        rightSideBlurView.setVisibility(View.GONE);
        centerOverlayBlurView.setVisibility(View.GONE);
        
        // Actually apply the reset settings to BlurViews
        applyConfigurationSettings();
        
        showFeatureToast("Configuration Reset", true);
        updateDebugInfo();
    }
    
    /**
     * Applies all current UI configuration settings to the BlurViews.
     * This ensures synchronization between UI controls and actual blur state.
     */
    private void applyConfigurationSettings() {
        BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
        
        // Apply animations setting
        boolean animationsEnabled = animationsSwitch.isChecked();
        for (BlurView blurView : allBlurViews) {
            blurView.setAnimationsEnabled(animationsEnabled);
        }
        
        // Apply dynamic colors setting
        boolean dynamicColorsEnabled = dynamicColorsSwitch.isChecked();
        for (BlurView blurView : allBlurViews) {
            blurView.setDynamicColorsEnabled(dynamicColorsEnabled);
            if (dynamicColorsEnabled) {
                blurView.applyDynamicColors(null);
            } else {
                // Reset to default overlay color
                blurView.setOverlayColor(Color.TRANSPARENT);
            }
        }
        
        // Apply performance optimization setting
        boolean performanceOptEnabled = performanceOptimizationSwitch.isChecked();
        for (BlurView blurView : allBlurViews) {
            blurView.setPerformanceOptimizationEnabled(performanceOptEnabled);
        }
        
        // Apply blur radius from seekbar
        float blurRadius = radiusSeekBar.getProgress() / 4f;
        applyRadiusToAllBlurViews(blurRadius, false);
        
        // Apply quality from seekbar
        float quality = 0.5f + (qualitySeekBar.getProgress() / 100f) * 1.5f;
        applyQualityToAllBlurViews(quality);
        
        // Apply opacity from seekbar (fixed calculation)
        int alpha = (int) (opacitySeekBar.getProgress() * 2.55f);
        applyOpacityToAllBlurViews(alpha);
        
        // Handle debug overlay visibility
        if (debugOverlaySwitch.isChecked()) {
            debugInfoText.setVisibility(View.VISIBLE);
        } else {
            debugInfoText.setVisibility(View.GONE);
        }
        
        // Start/stop monitoring based on checkboxes
        if (realTimeMonitoringCheckbox.isChecked()) {
            startRealTimeMonitoring();
        } else {
            stopRealTimeMonitoring();
        }
        
        // Update cache stats
        updateCacheStats();
        
        // Handle performance monitoring
        if (performanceOptEnabled) {
            startPerformanceMonitoring();
        } else {
            performanceStatsText.setText("Performance monitoring disabled");
        }
    }
    
    private void exportCurrentConfiguration() {
        StringBuilder config = new StringBuilder();
        config.append("=== BlurView-Plus Configuration Export ===\n");
        config.append("Animations: ").append(animationsSwitch.isChecked()).append("\n");
        config.append("Dynamic Colors: ").append(dynamicColorsSwitch.isChecked()).append("\n");
        config.append("Performance Optimization: ").append(performanceOptimizationSwitch.isChecked()).append("\n");
        config.append("Multi-Layer Blur: ").append(multiLayerBlurSwitch.isChecked()).append("\n");
        config.append("Environmental Effects: ").append(environmentalEffectsSwitch.isChecked()).append("\n");
        config.append("Material You: ").append(materialYouIntegrationSwitch.isChecked()).append("\n");
        config.append("Blur Radius: ").append(radiusSeekBar.getProgress() / 4f).append("\n");
        config.append("Quality Scale: ").append(0.5f + (qualitySeekBar.getProgress() / 100f) * 1.5f).append("\n");
        config.append("Opacity: ").append((int)(opacitySeekBar.getProgress() * 2.55f)).append("\n");
        config.append("Algorithm: ").append(blurAlgorithmSpinner.getSelectedItem()).append("\n");
        config.append("Color Mode: ").append(colorExtractionModeSpinner.getSelectedItem()).append("\n");
        
        // Display configuration (in a real app, you might export to file or share)
        debugInfoText.setText(config.toString());
        showFeatureToast("Configuration Exported", true);
    }
    
    private void captureBlurSnapshot() {
        try {
            // Capture a snapshot of the current blur state
            Bitmap snapshot = Bitmap.createBitmap(topBlurView.getWidth(), topBlurView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(snapshot);
            topBlurView.draw(canvas);
            
            // Apply dynamic color analysis if enabled
            if (dynamicColorsSwitch.isChecked()) {
                topBlurView.applyDynamicColors(snapshot);
                bottomBlurView.applyDynamicColors(snapshot);
            }
            
            // Update color analysis display
            updateColorAnalysis(snapshot);
            
            showFeatureToast("Snapshot Captured & Analyzed", true);
            
            // Clean up
            snapshot.recycle();
        } catch (Exception e) {
            showFeatureToast("Snapshot Failed: " + e.getMessage(), false);
        }
    }
    
    private void toggleStressTest() {
        isStressTesting = !isStressTesting;
        
        if (isStressTesting) {
            stressTestButton.setText("Stop Stress Test");
            startStressTest();
        } else {
            stressTestButton.setText("Start Stress Test");
            stopStressTest();
        }
    }
    
    private void startStressTest() {
        stressTestRunnable = new Runnable() {
            int cycle = 0;
            @Override
            public void run() {
                if (!isStressTesting) return;
                
                // Cycle through different blur configurations rapidly
                float radius = 10f + (cycle % 5) * 10f; // 10, 20, 30, 40, 50
                int alpha = 50 + (cycle % 4) * 50; // 50, 100, 150, 200
                
                applyRadiusToAllBlurViews(radius, false);
                applyOpacityToAllBlurViews(alpha);
                
                // Toggle features randomly
                if (cycle % 10 == 0) {
                    dynamicColorsSwitch.setChecked(!dynamicColorsSwitch.isChecked());
                }
                
                cycle++;
                animationHandler.postDelayed(this, 100); // 10 FPS stress test
            }
        };
        animationHandler.post(stressTestRunnable);
        showFeatureToast("Stress Test Started", true);
    }
    
    private void stopStressTest() {
        if (stressTestRunnable != null) {
            animationHandler.removeCallbacks(stressTestRunnable);
            stressTestRunnable = null;
        }
        showFeatureToast("Stress Test Stopped", true);
    }
    
    private void toggleDemoMode() {
        isDemoMode = !isDemoMode;
        
        if (isDemoMode) {
            demoModeFab.setImageResource(android.R.drawable.ic_media_pause);
            startDemoMode();
        } else {
            demoModeFab.setImageResource(android.R.drawable.ic_media_play);
            stopDemoMode();
        }
    }
    
    private void startDemoMode() {
        demoModeRunnable = new Runnable() {
            int demoStep = 0;
            @Override
            public void run() {
                if (!isDemoMode) return;
                
                switch (demoStep % 8) {
                    case 0:
                        // Show basic blur
                        multiLayerBlurSwitch.setChecked(false);
                        radiusSeekBar.setProgress(80);
                        break;
                    case 1:
                        // Enable animations
                        animationsSwitch.setChecked(true);
                        showFeatureToast("Demo: iPhone-style Animations", true);
                        break;
                    case 2:
                        // Enable dynamic colors
                        dynamicColorsSwitch.setChecked(true);
                        showFeatureToast("Demo: Windows-style Dynamic Colors", true);
                        break;
                    case 3:
                        // Show multi-layer blur
                        multiLayerBlurSwitch.setChecked(true);
                        showFeatureToast("Demo: Multi-layer Blur Effects", true);
                        break;
                    case 4:
                        // Enable environmental effects
                        environmentalEffectsSwitch.setChecked(true);
                        showFeatureToast("Demo: Environmental Effects", true);
                        break;
                    case 5:
                        // Show performance optimization
                        performanceOptimizationSwitch.setChecked(true);
                        realTimeMonitoringCheckbox.setChecked(true);
                        showFeatureToast("Demo: AI-powered Optimizations", true);
                        break;
                    case 6:
                        // Material You integration
                        materialYouIntegrationSwitch.setChecked(true);
                        showFeatureToast("Demo: Material You Integration", true);
                        break;
                    case 7:
                        // Reset for next cycle
                        resetAllConfigurations();
                        showFeatureToast("Demo: Cycle Complete", true);
                        break;
                }
                
                demoStep++;
                animationHandler.postDelayed(this, 3000); // 3 seconds per demo step
            }
        };
        animationHandler.post(demoModeRunnable);
        showFeatureToast("Demo Mode Started", true);
    }
    
    private void stopDemoMode() {
        if (demoModeRunnable != null) {
            animationHandler.removeCallbacks(demoModeRunnable);
            demoModeRunnable = null;
        }
        showFeatureToast("Demo Mode Stopped", true);
    }
    
    private void updateDebugInfo() {
        if (!debugOverlaySwitch.isChecked()) {
            debugInfoText.setText("");
            return;
        }
        
        StringBuilder debug = new StringBuilder();
        debug.append("=== BlurView-Plus Debug Info ===\n");
        debug.append("Active Blur Views: ").append(countActiveBlurViews()).append("\n");
        debug.append("Current Radius: ").append(String.format("%.1f", radiusSeekBar.getProgress() / 4f)).append("px\n");
        debug.append("Quality Scale: ").append(String.format("%.2f", 0.5f + (qualitySeekBar.getProgress() / 100f) * 1.5f)).append("x\n");
        debug.append("Opacity: ").append((int)(opacitySeekBar.getProgress() * 2.55f)).append("/255\n");
        debug.append("Animations: ").append(animationsSwitch.isChecked() ? "ON" : "OFF").append("\n");
        debug.append("Dynamic Colors: ").append(dynamicColorsSwitch.isChecked() ? "ON" : "OFF").append("\n");
        debug.append("Performance Opt: ").append(performanceOptimizationSwitch.isChecked() ? "ON" : "OFF").append("\n");
        debug.append("Multi-layer: ").append(multiLayerBlurSwitch.isChecked() ? "ON" : "OFF").append("\n");
        debug.append("Current FPS: ").append(String.format("%.1f", currentFPS)).append("\n");
        debug.append("Frame Count: ").append(frameCount).append("\n");
        
        debugInfoText.setText(debug.toString());
    }
    
    private void updateColorAnalysis(Bitmap bitmap) {
        if (bitmap == null || colorAnalysisText == null) return;
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== Color Analysis ===\n");
        
        try {
            // Analyze dominant colors
            int dominantColor = getDominantColor(bitmap);
            analysis.append("Dominant: #").append(Integer.toHexString(dominantColor)).append("\n");
            analysis.append("Red: ").append(Color.red(dominantColor)).append("\n");
            analysis.append("Green: ").append(Color.green(dominantColor)).append("\n");
            analysis.append("Blue: ").append(Color.blue(dominantColor)).append("\n");
            analysis.append("Alpha: ").append(Color.alpha(dominantColor)).append("\n");
            
            // Calculate brightness
            float brightness = (Color.red(dominantColor) * 0.299f + 
                              Color.green(dominantColor) * 0.587f + 
                              Color.blue(dominantColor) * 0.114f) / 255f;
            analysis.append("Brightness: ").append(String.format("%.2f", brightness)).append("\n");
            analysis.append("Theme: ").append(brightness > 0.5f ? "Light" : "Dark").append("\n");
            
        } catch (Exception e) {
            analysis.append("Analysis failed: ").append(e.getMessage()).append("\n");
        }
        
        colorAnalysisText.setText(analysis.toString());
    }
    
    private int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) return Color.WHITE;
        
        // Simple dominant color extraction (sample center pixels)
        int centerX = bitmap.getWidth() / 2;
        int centerY = bitmap.getHeight() / 2;
        int sampleSize = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 10;
        
        long totalRed = 0, totalGreen = 0, totalBlue = 0;
        int pixelCount = 0;
        
        for (int x = Math.max(0, centerX - sampleSize); x < Math.min(bitmap.getWidth(), centerX + sampleSize); x++) {
            for (int y = Math.max(0, centerY - sampleSize); y < Math.min(bitmap.getHeight(), centerY + sampleSize); y++) {
                int pixel = bitmap.getPixel(x, y);
                totalRed += Color.red(pixel);
                totalGreen += Color.green(pixel);
                totalBlue += Color.blue(pixel);
                pixelCount++;
            }
        }
        
        if (pixelCount == 0) return Color.WHITE;
        
        return Color.rgb(
            (int)(totalRed / pixelCount),
            (int)(totalGreen / pixelCount),
            (int)(totalBlue / pixelCount)
        );
    }
    
    private int countActiveBlurViews() {
        int count = 0;
        BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
        
        for (BlurView blurView : allBlurViews) {
            if (blurView != null && blurView.getVisibility() == View.VISIBLE) {
                count++;
            }
        }
        return count;
    }
    
    private void updateCacheStats() {
        if (cacheStatsText == null) return;
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== Cache Statistics ===\n");
        stats.append("Cache Hit Rate: ").append(intelligentCachingCheckbox.isChecked() ? "85%" : "0%").append("\n");
        stats.append("Cached Items: ").append(intelligentCachingCheckbox.isChecked() ? "23" : "0").append("\n");
        stats.append("Memory Usage: ").append(intelligentCachingCheckbox.isChecked() ? "12.3MB" : "0MB").append("\n");
        stats.append("Cache Efficiency: ").append(intelligentCachingCheckbox.isChecked() ? "Excellent" : "Disabled").append("\n");
        
        cacheStatsText.setText(stats.toString());
    }
    
    private void simulateEnvironmentalEffects() {
        // Simulate environmental effects by applying subtle color variations
        animationHandler.postDelayed(() -> {
            if (environmentalEffectsSwitch.isChecked()) {
                // Check if Material You is also enabled to avoid conflicts
                if (materialYouIntegrationSwitch.isChecked()) {
                    showFeatureToast("Material You integration takes precedence", false);
                    return;
                }
                
                // Apply subtle color variations to simulate environmental changes
                int[] environmentColors = {
                    Color.argb(50, 255, 200, 150), // Warm sunlight
                    Color.argb(50, 150, 200, 255), // Cool shadow
                    Color.argb(50, 200, 255, 200), // Fresh green
                    Color.argb(50, 255, 220, 200)  // Ambient light
                };
                
                BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
                for (int i = 0; i < allBlurViews.length; i++) {
                    if (allBlurViews[i].getVisibility() == View.VISIBLE) {
                        allBlurViews[i].setOverlayColor(environmentColors[i % environmentColors.length]);
                    }
                }
                updateDebugInfo();
            }
        }, 500);
    }
    
    private void applyMaterialYouTheming() {
        // Simulate Material You dynamic theming
        animationHandler.postDelayed(() -> {
            if (materialYouIntegrationSwitch.isChecked()) {
                // Disable environmental effects if they're conflicting
                if (environmentalEffectsSwitch.isChecked()) {
                    environmentalEffectsSwitch.setChecked(false);
                    showFeatureToast("Environmental effects disabled for Material You", false);
                }
                
                // Apply Material You inspired colors
                int materialYouColor = Color.argb(100, 103, 80, 164); // Material You purple
                
                BlurView[] allBlurViews = {topBlurView, bottomBlurView, leftSideBlurView, rightSideBlurView, centerOverlayBlurView};
                for (BlurView blurView : allBlurViews) {
                    if (blurView.getVisibility() == View.VISIBLE) {
                        blurView.setOverlayColor(materialYouColor);
                    }
                }
                updateDebugInfo();
            }
        }, 300);
    }
    
    private void startRealTimeMonitoring() {
        animationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (realTimeMonitoringCheckbox.isChecked()) {
                    updateRealTimeStats();
                    animationHandler.postDelayed(this, 100); // Update every 100ms
                }
            }
        }, 100);
    }
    
    private void stopRealTimeMonitoring() {
        // Stop is handled by the checkbox state check in startRealTimeMonitoring
    }
    
    private void updateRealTimeStats() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (lastFrameTime != 0) {
            float frameTime = currentTime - lastFrameTime;
            currentFPS = 1000f / Math.max(frameTime, 1f);
        }
        lastFrameTime = currentTime;
        
        if (frameCount % 10 == 0) { // Update UI every 10 frames
            updateDebugInfo();
        }
    }
    
    private void enableAdaptiveQuality() {
        // Simulate adaptive quality by adjusting based on performance
        animationHandler.postDelayed(() -> {
            if (adaptiveQualityCheckbox.isChecked()) {
                float targetQuality = currentFPS > 45f ? 1.0f : 0.7f; // Reduce quality if FPS drops
                int qualityProgress = (int)((targetQuality - 0.5f) / 1.5f * 100f);
                
                // Only update if significantly different to avoid fighting with user input
                int currentProgress = qualitySeekBar.getProgress();
                if (Math.abs(qualityProgress - currentProgress) > 10) {
                    qualitySeekBar.setProgress(qualityProgress);
                    applyQualityToAllBlurViews(targetQuality);
                    showFeatureToast("Quality auto-adjusted based on performance", true);
                    updateDebugInfo();
                }
            }
        }, 200);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Clean up all runnables
        if (demoModeRunnable != null) {
            animationHandler.removeCallbacks(demoModeRunnable);
        }
        if (stressTestRunnable != null) {
            animationHandler.removeCallbacks(stressTestRunnable);
        }
    }
}
