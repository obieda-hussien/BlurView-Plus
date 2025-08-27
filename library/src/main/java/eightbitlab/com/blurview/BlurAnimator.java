package eightbitlab.com.blurview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.annotation.NonNull;

/**
 * Provides smooth, iPhone-style animations for BlurView properties.
 * Implements physics-based transitions and natural motion curves.
 */
public class BlurAnimator {
    
    // iPhone-style spring animation parameters
    private static final float SPRING_DAMPING = 0.8f;
    private static final float SPRING_RESPONSE = 0.3f;
    private static final int DEFAULT_ANIMATION_DURATION = 300;
    
    private ValueAnimator currentAnimator;
    private final BlurViewFacade blurView;
    
    // Animation interpolators for natural motion
    private final Interpolator springInterpolator = new SpringInterpolator(SPRING_DAMPING, SPRING_RESPONSE);
    private final Interpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
    
    public BlurAnimator(@NonNull BlurViewFacade blurView) {
        this.blurView = blurView;
    }
    
    /**
     * Animates blur radius with iPhone-style spring physics.
     * 
     * @param fromRadius Starting blur radius
     * @param toRadius Target blur radius
     * @param duration Animation duration in milliseconds
     * @param listener Optional animation listener
     */
    public void animateBlurRadius(float fromRadius, float toRadius, long duration, 
                                 AnimatorListenerAdapter listener) {
        cancelCurrentAnimation();
        
        currentAnimator = ValueAnimator.ofFloat(fromRadius, toRadius);
        currentAnimator.setDuration(duration);
        currentAnimator.setInterpolator(springInterpolator);
        
        currentAnimator.addUpdateListener(animation -> {
            float animatedRadius = (float) animation.getAnimatedValue();
            blurView.setBlurRadius(animatedRadius);
        });
        
        if (listener != null) {
            currentAnimator.addListener(listener);
        }
        
        currentAnimator.start();
    }
    
    /**
     * Animates blur radius with default iPhone-style duration.
     */
    public void animateBlurRadius(float fromRadius, float toRadius) {
        animateBlurRadius(fromRadius, toRadius, DEFAULT_ANIMATION_DURATION, null);
    }
    
    /**
     * Smoothly enables or disables blur with fade animation.
     * 
     * @param enabled Target blur state
     * @param duration Animation duration
     */
    public void animateBlurEnabled(boolean enabled, long duration) {
        cancelCurrentAnimation();
        
        float fromAlpha = enabled ? 0f : 1f;
        float toAlpha = enabled ? 1f : 0f;
        
        currentAnimator = ValueAnimator.ofFloat(fromAlpha, toAlpha);
        currentAnimator.setDuration(duration);
        currentAnimator.setInterpolator(decelerateInterpolator);
        
        currentAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            // Apply alpha to the blur by modifying overlay color alpha
            int currentOverlayColor = getCurrentOverlayColor();
            int newAlpha = (int) (255 * alpha);
            int newOverlayColor = (currentOverlayColor & 0x00FFFFFF) | (newAlpha << 24);
            blurView.setOverlayColor(newOverlayColor);
        });
        
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                blurView.setBlurEnabled(enabled);
            }
        });
        
        currentAnimator.start();
    }
    
    /**
     * Animates blur enable/disable with default duration.
     */
    public void animateBlurEnabled(boolean enabled) {
        animateBlurEnabled(enabled, DEFAULT_ANIMATION_DURATION);
    }
    
    /**
     * Creates a gesture-driven blur radius animation based on touch velocity.
     * 
     * @param velocity Touch velocity (pixels per second)
     * @param targetRadius Target blur radius
     */
    public void animateWithVelocity(float velocity, float targetRadius) {
        cancelCurrentAnimation();
        
        // Calculate duration based on velocity (faster gesture = shorter animation)
        long duration = Math.max(150, Math.min(500, (long) (DEFAULT_ANIMATION_DURATION * (1f / Math.max(0.1f, Math.abs(velocity) / 1000f)))));
        
        float currentRadius = getCurrentBlurRadius();
        animateBlurRadius(currentRadius, targetRadius, duration, null);
    }
    
    /**
     * Cancels any currently running animation.
     */
    public void cancelCurrentAnimation() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
            currentAnimator = null;
        }
    }
    
    /**
     * Checks if an animation is currently running.
     */
    public boolean isAnimating() {
        return currentAnimator != null && currentAnimator.isRunning();
    }
    
    /**
     * Gets the current blur radius from the BlurView.
     * This is a simplified implementation - in a real scenario, 
     * BlurViewFacade would need a getter method.
     */
    private float getCurrentBlurRadius() {
        // Default implementation - would need to be enhanced in BlurViewFacade
        return BlurController.DEFAULT_BLUR_RADIUS;
    }
    
    /**
     * Gets the current overlay color from the BlurView.
     * Simplified implementation for animation purposes.
     */
    private int getCurrentOverlayColor() {
        // Default implementation - would need to be enhanced in BlurViewFacade
        return 0x40000000; // Semi-transparent black default
    }
    
    /**
     * Custom spring interpolator that mimics iPhone's spring animations.
     */
    private static class SpringInterpolator implements Interpolator {
        private final float damping;
        private final float response;
        
        SpringInterpolator(float damping, float response) {
            this.damping = damping;
            this.response = response;
        }
        
        @Override
        public float getInterpolation(float input) {
            // Simplified spring physics calculation
            // This creates a natural spring motion similar to iOS animations
            float w = (float) (2 * Math.PI / response);
            float dampingRatio = damping;
            
            if (dampingRatio < 1) {
                // Under-damped spring
                float wd = (float) (w * Math.sqrt(1 - dampingRatio * dampingRatio));
                return (float) (1 - Math.exp(-dampingRatio * w * input) * 
                       (Math.cos(wd * input) + (dampingRatio * w / wd) * Math.sin(wd * input)));
            } else {
                // Critically or over-damped spring
                return (float) (1 - Math.exp(-w * input) * (1 + w * input));
            }
        }
    }
}