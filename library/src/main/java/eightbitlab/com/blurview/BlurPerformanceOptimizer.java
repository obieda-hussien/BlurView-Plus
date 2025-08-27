package eightbitlab.com.blurview;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Advanced performance optimization system for BlurView.
 * Implements intelligent caching, adaptive quality scaling, and performance monitoring.
 */
public class BlurPerformanceOptimizer {
    
    private static final int MAX_CACHE_SIZE = 50; // Maximum cached blur results
    private static final long PERFORMANCE_SAMPLE_INTERVAL = 1000; // 1 second
    private static final float LOW_PERFORMANCE_THRESHOLD = 16.0f; // Target 60fps
    
    // Intelligent cache for blur results
    private final LruCache<String, CachedBlurResult> blurCache;
    
    // Performance monitoring
    private final AtomicInteger frameCount = new AtomicInteger(0);
    private final AtomicInteger performanceSamples = new AtomicInteger(0);
    private volatile float averageFrameTime = 16.0f; // Default to 60fps
    private long lastPerformanceCheck = 0;
    
    // Adaptive quality scaling
    private volatile float currentQualityScale = 1.0f;
    private static final float MIN_QUALITY_SCALE = 0.5f;
    private static final float MAX_QUALITY_SCALE = 1.0f;
    
    // Multi-threading support
    private final ExecutorService backgroundExecutor;
    private final Handler mainHandler;
    
    // Performance statistics
    private volatile long totalBlurTime = 0;
    private volatile int blurOperations = 0;
    
    public BlurPerformanceOptimizer() {
        // Initialize cache with memory-based sizing
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8; // Use 1/8th of available memory
        
        blurCache = new LruCache<String, CachedBlurResult>(cacheSize) {
            @Override
            protected int sizeOf(String key, CachedBlurResult result) {
                return result.getMemorySize();
            }
        };
        
        // Initialize background thread pool for heavy operations
        int threadCount = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        backgroundExecutor = Executors.newFixedThreadPool(threadCount);
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Caches a blur result for future reuse.
     */
    public void cacheBlurResult(@NonNull String key, @NonNull Bitmap blurredBitmap, 
                               float blurRadius, int overlayColor) {
        CachedBlurResult result = new CachedBlurResult(blurredBitmap, blurRadius, overlayColor);
        blurCache.put(key, result);
    }
    
    /**
     * Retrieves a cached blur result if available.
     */
    public CachedBlurResult getCachedBlurResult(@NonNull String key) {
        return blurCache.get(key);
    }
    
    /**
     * Generates a cache key based on blur parameters.
     */
    @NonNull
    public String generateCacheKey(int width, int height, float blurRadius, 
                                  int overlayColor, float scaleFactor) {
        return String.format("%dx%d_r%.1f_c%08x_s%.2f", 
                           width, height, blurRadius, overlayColor, scaleFactor);
    }
    
    /**
     * Records performance data for a blur operation.
     */
    public void recordBlurPerformance(long startTime, long endTime) {
        long blurTime = endTime - startTime;
        frameCount.incrementAndGet();
        
        synchronized (this) {
            totalBlurTime += blurTime;
            blurOperations++;
            
            // Update average frame time
            if (blurOperations > 0) {
                averageFrameTime = (float) totalBlurTime / blurOperations;
            }
        }
        
        // Check if we need to adjust quality based on performance
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPerformanceCheck > PERFORMANCE_SAMPLE_INTERVAL) {
            adjustQualityBasedOnPerformance();
            lastPerformanceCheck = currentTime;
        }
    }
    
    /**
     * Gets the current adaptive quality scale factor.
     */
    public float getCurrentQualityScale() {
        return currentQualityScale;
    }
    
    /**
     * Gets the current average frame time in milliseconds.
     */
    public float getAverageFrameTime() {
        return averageFrameTime;
    }
    
    /**
     * Gets performance statistics.
     */
    @NonNull
    public PerformanceStats getPerformanceStats() {
        synchronized (this) {
            return new PerformanceStats(
                averageFrameTime,
                currentQualityScale,
                blurOperations,
                blurCache.size(),
                blurCache.hitCount(),
                blurCache.missCount()
            );
        }
    }
    
    /**
     * Adjusts quality scale based on current performance.
     */
    private void adjustQualityBasedOnPerformance() {
        if (averageFrameTime > LOW_PERFORMANCE_THRESHOLD * 1.5f) {
            // Performance is poor, decrease quality
            currentQualityScale = Math.max(MIN_QUALITY_SCALE, currentQualityScale - 0.1f);
        } else if (averageFrameTime < LOW_PERFORMANCE_THRESHOLD * 0.8f) {
            // Performance is good, try to increase quality
            currentQualityScale = Math.min(MAX_QUALITY_SCALE, currentQualityScale + 0.05f);
        }
    }
    
    /**
     * Executes a task on background thread for heavy operations.
     */
    public void executeOnBackground(@NonNull Runnable task) {
        backgroundExecutor.execute(task);
    }
    
    /**
     * Executes a task on main thread.
     */
    public void executeOnMain(@NonNull Runnable task) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            task.run();
        } else {
            mainHandler.post(task);
        }
    }
    
    /**
     * Determines optimal blur algorithm based on device capabilities.
     */
    @NonNull
    public BlurAlgorithmRecommendation getOptimalBlurAlgorithm() {
        // Use hardware acceleration when available (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return BlurAlgorithmRecommendation.HARDWARE_ACCELERATED;
        }
        
        // For older devices, choose based on performance characteristics
        if (averageFrameTime > LOW_PERFORMANCE_THRESHOLD * 2) {
            return BlurAlgorithmRecommendation.FAST_APPROXIMATION;
        } else {
            return BlurAlgorithmRecommendation.RENDERSCRIPT;
        }
    }
    
    /**
     * Clears the blur cache to free memory.
     */
    public void clearCache() {
        blurCache.evictAll();
    }
    
    /**
     * Releases resources and shuts down background threads.
     */
    public void destroy() {
        backgroundExecutor.shutdown();
        clearCache();
    }
    
    /**
     * Cached blur result container.
     */
    public static class CachedBlurResult {
        private final Bitmap blurredBitmap;
        private final float blurRadius;
        private final int overlayColor;
        private final long creationTime;
        
        CachedBlurResult(@NonNull Bitmap blurredBitmap, float blurRadius, int overlayColor) {
            this.blurredBitmap = blurredBitmap;
            this.blurRadius = blurRadius;
            this.overlayColor = overlayColor;
            this.creationTime = System.currentTimeMillis();
        }
        
        @NonNull
        public Bitmap getBlurredBitmap() {
            return blurredBitmap;
        }
        
        public float getBlurRadius() {
            return blurRadius;
        }
        
        public int getOverlayColor() {
            return overlayColor;
        }
        
        public long getAge() {
            return System.currentTimeMillis() - creationTime;
        }
        
        int getMemorySize() {
            return blurredBitmap.getByteCount();
        }
    }
    
    /**
     * Performance statistics container.
     */
    public static class PerformanceStats {
        public final float averageFrameTime;
        public final float currentQualityScale;
        public final int totalBlurOperations;
        public final int cacheSize;
        public final long cacheHits;
        public final long cacheMisses;
        
        PerformanceStats(float averageFrameTime, float currentQualityScale, 
                        int totalBlurOperations, int cacheSize, long cacheHits, long cacheMisses) {
            this.averageFrameTime = averageFrameTime;
            this.currentQualityScale = currentQualityScale;
            this.totalBlurOperations = totalBlurOperations;
            this.cacheSize = cacheSize;
            this.cacheHits = cacheHits;
            this.cacheMisses = cacheMisses;
        }
        
        public float getCacheHitRate() {
            long totalRequests = cacheHits + cacheMisses;
            return totalRequests > 0 ? (float) cacheHits / totalRequests : 0f;
        }
    }
    
    /**
     * Blur algorithm recommendations.
     */
    public enum BlurAlgorithmRecommendation {
        HARDWARE_ACCELERATED,
        RENDERSCRIPT,
        FAST_APPROXIMATION
    }
}