package com.eightbitlab.blurview_sample;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    private SeekBar radiusSeekBar;
    
    // Enhanced features components
    private Switch animationsSwitch;
    private Switch dynamicColorsSwitch;
    private Switch performanceOptimizationSwitch;
    private TextView performanceStatsText;
    
    // Feature state
    private boolean isExpanded = false;
    private GestureDetector gestureDetector;

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
        target = findViewById(R.id.target);
        radiusSeekBar = findViewById(R.id.radiusSeekBar);
        
        // Enhanced features UI components
        animationsSwitch = findViewById(R.id.animationsSwitch);
        dynamicColorsSwitch = findViewById(R.id.dynamicColorsSwitch);
        performanceOptimizationSwitch = findViewById(R.id.performanceOptimizationSwitch);
        performanceStatsText = findViewById(R.id.performanceStatsText);
        
        // Rounded corners + casting elevation shadow with transparent background
        topBlurView.setClipToOutline(true);
        topBlurView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                topBlurView.getBackground().getOutline(outline);
                outline.setAlpha(1f);
            }
        });
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
        
        // Setup blur views with enhanced features
        topBlurView.setAnimationsEnabled(true)
                .setPerformanceOptimizationEnabled(true)
                .setDynamicColorsEnabled(false) // Will be enabled via switch
                .setupWith(target)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);

        bottomBlurView.setAnimationsEnabled(true)
                .setPerformanceOptimizationEnabled(true)
                .setDynamicColorsEnabled(false) // Will be enabled via switch
                .setupWith(target)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);

        int initialProgress = (int) (radius * step);
        radiusSeekBar.setProgress(initialProgress);

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBarListenerAdapter() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float blurRadius = progress / step;
                blurRadius = Math.max(blurRadius, minBlurRadius);
                
                if (topBlurView.isAnimationsEnabled() && fromUser) {
                    // Use smooth animations when user changes the value
                    topBlurView.animateBlurRadius(blurRadius);
                    bottomBlurView.animateBlurRadius(blurRadius);
                } else {
                    // Direct set for programmatic changes
                    topBlurView.setBlurRadius(blurRadius);
                    bottomBlurView.setBlurRadius(blurRadius);
                }
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
            topBlurView.setAnimationsEnabled(isChecked);
            bottomBlurView.setAnimationsEnabled(isChecked);
            
            showFeatureToast("iPhone-style Animations", isChecked);
        });
        
        dynamicColorsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            topBlurView.setDynamicColorsEnabled(isChecked);
            bottomBlurView.setDynamicColorsEnabled(isChecked);
            
            if (isChecked) {
                // Trigger dynamic color extraction for both blur views
                topBlurView.applyDynamicColors(null);
                bottomBlurView.applyDynamicColors(null);
            }
            
            showFeatureToast("Windows-style Dynamic Colors", isChecked);
        });
        
        performanceOptimizationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            topBlurView.setPerformanceOptimizationEnabled(isChecked);
            bottomBlurView.setPerformanceOptimizationEnabled(isChecked);
            
            showFeatureToast("Advanced Performance Optimizations", isChecked);
            
            if (isChecked) {
                startPerformanceMonitoring();
            } else {
                performanceStatsText.setText("Performance monitoring disabled");
            }
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
}
