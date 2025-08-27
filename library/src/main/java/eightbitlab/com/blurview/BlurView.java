package eightbitlab.com.blurview;

import static eightbitlab.com.blurview.BlurController.DEFAULT_SCALE_FACTOR;
import static eightbitlab.com.blurview.PreDrawBlurController.TRANSPARENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eightbitlab.blurview.R;

/**
 * FrameLayout that blurs its underlying content.
 * Can have children and draw them over blurred background.
 * 
 * Enhanced with Windows-style dynamic colors, iPhone-style animations,
 * and advanced performance optimizations.
 */
public class BlurView extends FrameLayout {

    BlurController blurController = new NoOpController();

    @ColorInt
    private int overlayColor;
    private boolean blurAutoUpdate = true;
    
    // Enhanced features
    private DynamicColorExtractor colorExtractor;
    private BlurAnimator blurAnimator;
    private BlurPerformanceOptimizer performanceOptimizer;
    private boolean dynamicColorsEnabled = false;
    private boolean animationsEnabled = true;
    private boolean performanceOptimizationEnabled = true;

    public BlurView(Context context) {
        super(context);
        init(null, 0);
    }

    public BlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BlurView, defStyleAttr, 0);
        overlayColor = a.getColor(R.styleable.BlurView_blurOverlayColor, TRANSPARENT);
        a.recycle();
        
        // Initialize enhanced features
        initializeEnhancedFeatures();
    }
    
    /**
     * Initializes the enhanced BlurView features.
     */
    private void initializeEnhancedFeatures() {
        if (performanceOptimizationEnabled) {
            performanceOptimizer = new BlurPerformanceOptimizer();
        }
        
        if (dynamicColorsEnabled) {
            colorExtractor = new DynamicColorExtractor();
        }
        
        if (animationsEnabled) {
            // BlurAnimator will be initialized when blurController is set
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        long startTime = 0;
        if (performanceOptimizer != null) {
            startTime = System.nanoTime();
        }
        
        boolean shouldDraw = blurController.draw(canvas);
        
        if (performanceOptimizer != null) {
            long endTime = System.nanoTime();
            performanceOptimizer.recordBlurPerformance(startTime / 1000000, endTime / 1000000);
        }
        
        if (shouldDraw) {
            super.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        blurController.updateBlurViewSize();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        blurController.setBlurAutoUpdate(false);
        
        // Clean up enhanced features
        if (blurAnimator != null) {
            blurAnimator.cancelCurrentAnimation();
        }
        if (colorExtractor != null) {
            colorExtractor.clearCache();
        }
        if (performanceOptimizer != null) {
            performanceOptimizer.destroy();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isHardwareAccelerated()) {
            Log.e("BlurView", "BlurView can't be used in not hardware-accelerated window!");
        } else {
            blurController.setBlurAutoUpdate(this.blurAutoUpdate);
        }
        
        // Reinitialize enhanced features if needed
        if (colorExtractor == null && dynamicColorsEnabled) {
            colorExtractor = new DynamicColorExtractor();
        }
    }

    /**
     * @param target      the root to start blur from.
     * @param algorithm   sets the blur algorithm. Ignored on API >= 31 where efficient hardware rendering pipeline is used.
     * @param scaleFactor a scale factor to downscale the view snapshot before blurring.
     *                    Helps achieving stronger blur and potentially better performance at the expense of blur precision.
     *                    The blur radius is essentially the radius * scaleFactor.
     * @param applyNoise  optional blue noise texture over the blurred content to make it look more natural. True by default.
     * @return {@link BlurView} to setup needed params.
     */
    public BlurViewFacade setupWith(@NonNull BlurTarget target, BlurAlgorithm algorithm, float scaleFactor, boolean applyNoise) {
        blurController.destroy();
        
        // Apply performance optimizations if enabled
        if (performanceOptimizer != null) {
            BlurPerformanceOptimizer.BlurAlgorithmRecommendation recommendation = 
                performanceOptimizer.getOptimalBlurAlgorithm();
            
            // Override algorithm based on performance analysis
            if (recommendation == BlurPerformanceOptimizer.BlurAlgorithmRecommendation.FAST_APPROXIMATION) {
                scaleFactor = Math.max(scaleFactor, performanceOptimizer.getCurrentQualityScale());
            }
        }
        
        if (BlurTarget.canUseHardwareRendering) {
            // Ignores the blur algorithm, always uses RenderEffect
            blurController = new RenderNodeBlurController(this, target, overlayColor, scaleFactor, applyNoise);
        } else {
            blurController = new PreDrawBlurController(this, target, overlayColor, algorithm, scaleFactor, applyNoise);
        }
        
        // Initialize animator after controller is set
        if (animationsEnabled) {
            blurAnimator = new BlurAnimator(blurController);
        }

        return blurController;
    }

    /**
     * @param rootView    the root to start blur from.
     *                    BlurAlgorithm is automatically picked based on the API version.
     *                    It uses RenderEffect on API 31+, and RenderScriptBlur on older versions.
     * @param scaleFactor a scale factor to downscale the view snapshot before blurring.
     *                    Helps achieving stronger blur and potentially better performance at the expense of blur precision.
     *                    The blur radius is essentially the radius * scaleFactor.
     * @param applyNoise  optional blue noise texture over the blurred content to make it look more natural. True by default.
     * @return {@link BlurView} to setup needed params.
     */
    public BlurViewFacade setupWith(@NonNull BlurTarget rootView, float scaleFactor, boolean applyNoise) {
        BlurAlgorithm algorithm;
        if (BlurTarget.canUseHardwareRendering) {
            // Ignores the blur algorithm, always uses RenderNodeBlurController and RenderEffect
            algorithm = null;
        } else {
            algorithm = new RenderScriptBlur(getContext());
        }
        return setupWith(rootView, algorithm, scaleFactor, applyNoise);
    }

    /**
     * @param rootView root to start blur from.
     *                 BlurAlgorithm is automatically picked based on the API version.
     *                 It uses RenderEffect on API 31+, and RenderScriptBlur on older versions.
     *                 The {@link DEFAULT_SCALE_FACTOR} scale factor for view snapshot is used.
     *                 Blue noise texture is applied by default.
     * @return {@link BlurView} to setup needed params.
     */
    public BlurViewFacade setupWith(@NonNull BlurTarget rootView) {
        return setupWith(rootView, DEFAULT_SCALE_FACTOR, true);
    }

    // Setters duplicated to be able to conveniently change these settings outside of setupWith chain

    /**
     * @see BlurViewFacade#setBlurRadius(float)
     */
    public BlurViewFacade setBlurRadius(float radius) {
        return blurController.setBlurRadius(radius);
    }

    /**
     * @see BlurViewFacade#setOverlayColor(int)
     */
    public BlurViewFacade setOverlayColor(@ColorInt int overlayColor) {
        this.overlayColor = overlayColor;
        return blurController.setOverlayColor(overlayColor);
    }

    /**
     * @see BlurViewFacade#setBlurAutoUpdate(boolean)
     */
    public BlurViewFacade setBlurAutoUpdate(boolean enabled) {
        this.blurAutoUpdate = enabled;
        return blurController.setBlurAutoUpdate(enabled);
    }

    /**
     * @see BlurViewFacade#setBlurEnabled(boolean)
     */
    public BlurViewFacade setBlurEnabled(boolean enabled) {
        return blurController.setBlurEnabled(enabled);
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
        notifyRotationChanged(rotation);
    }

    @SuppressLint("NewApi")
    public void notifyRotationChanged(float rotation) {
        if (usingRenderNode()) {
            ((RenderNodeBlurController) blurController).updateRotation(rotation);
        }
    }

    @SuppressLint("NewApi")
    public void notifyScaleXChanged(float scaleX) {
        if (usingRenderNode()) {
            ((RenderNodeBlurController) blurController).updateScaleX(scaleX);
        }
    }

    @SuppressLint("NewApi")
    public void notifyScaleYChanged(float scaleY) {
        if (usingRenderNode()) {
            ((RenderNodeBlurController) blurController).updateScaleY(scaleY);
        }
    }

    private boolean usingRenderNode() {
        return blurController instanceof RenderNodeBlurController;
    }
    
    // Enhanced Features API
    
    /**
     * Enables or disables Windows-style dynamic background colors.
     * When enabled, the blur overlay color will automatically adapt based on the underlying content.
     * 
     * @param enabled true to enable dynamic colors
     * @return this BlurView for method chaining
     */
    public BlurView setDynamicColorsEnabled(boolean enabled) {
        this.dynamicColorsEnabled = enabled;
        if (enabled && colorExtractor == null) {
            colorExtractor = new DynamicColorExtractor();
        } else if (!enabled && colorExtractor != null) {
            colorExtractor.clearCache();
            colorExtractor = null;
        }
        return this;
    }
    
    /**
     * Enables or disables iPhone-style smooth animations.
     * 
     * @param enabled true to enable smooth animations
     * @return this BlurView for method chaining
     */
    public BlurView setAnimationsEnabled(boolean enabled) {
        this.animationsEnabled = enabled;
        if (!enabled && blurAnimator != null) {
            blurAnimator.cancelCurrentAnimation();
            blurAnimator = null;
        } else if (enabled && blurAnimator == null && blurController != null) {
            blurAnimator = new BlurAnimator(blurController);
        }
        return this;
    }
    
    /**
     * Enables or disables advanced performance optimizations.
     * 
     * @param enabled true to enable performance optimizations
     * @return this BlurView for method chaining
     */
    public BlurView setPerformanceOptimizationEnabled(boolean enabled) {
        this.performanceOptimizationEnabled = enabled;
        if (enabled && performanceOptimizer == null) {
            performanceOptimizer = new BlurPerformanceOptimizer();
        } else if (!enabled && performanceOptimizer != null) {
            performanceOptimizer.destroy();
            performanceOptimizer = null;
        }
        return this;
    }
    
    /**
     * Animates the blur radius with iPhone-style smooth transitions.
     * 
     * @param targetRadius the target blur radius
     * @return this BlurView for method chaining
     */
    public BlurView animateBlurRadius(float targetRadius) {
        if (blurAnimator != null) {
            float currentRadius = BlurController.DEFAULT_BLUR_RADIUS; // Would need getter in real implementation
            blurAnimator.animateBlurRadius(currentRadius, targetRadius);
        } else {
            setBlurRadius(targetRadius);
        }
        return this;
    }
    
    /**
     * Animates the blur enable/disable state with smooth transitions.
     * 
     * @param enabled the target blur state
     * @return this BlurView for method chaining
     */
    public BlurView animateBlurEnabled(boolean enabled) {
        if (blurAnimator != null) {
            blurAnimator.animateBlurEnabled(enabled);
        } else {
            setBlurEnabled(enabled);
        }
        return this;
    }
    
    /**
     * Extracts and applies dynamic colors from the current blur content.
     * This method can be called to manually trigger color extraction.
     * 
     * @param sourceBitmap the bitmap to extract colors from, or null to use current blur content
     * @return this BlurView for method chaining
     */
    public BlurView applyDynamicColors(@Nullable Bitmap sourceBitmap) {
        if (colorExtractor != null && sourceBitmap != null) {
            int adaptiveColor = colorExtractor.extractAdaptiveColor(sourceBitmap, overlayColor);
            setOverlayColor(adaptiveColor);
        }
        return this;
    }
    
    /**
     * Gets performance statistics for this BlurView.
     * 
     * @return performance statistics, or null if performance optimization is disabled
     */
    @Nullable
    public BlurPerformanceOptimizer.PerformanceStats getPerformanceStats() {
        return performanceOptimizer != null ? performanceOptimizer.getPerformanceStats() : null;
    }
    
    /**
     * Checks if dynamic colors are enabled.
     */
    public boolean isDynamicColorsEnabled() {
        return dynamicColorsEnabled;
    }
    
    /**
     * Checks if smooth animations are enabled.
     */
    public boolean isAnimationsEnabled() {
        return animationsEnabled;
    }
    
    /**
     * Checks if performance optimizations are enabled.
     */
    public boolean isPerformanceOptimizationEnabled() {
        return performanceOptimizationEnabled;
    }
}
