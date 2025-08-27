package eightbitlab.com.blurview;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Extracts dynamic colors from blurred content to create Windows-style adaptive backgrounds.
 * Uses color analysis to determine the most suitable overlay colors based on the underlying content.
 */
public class DynamicColorExtractor {
    
    private static final int DEFAULT_TIMEOUT_MS = 50; // Fast extraction for real-time updates
    private final Map<String, Integer> colorCache = Collections.synchronizedMap(new HashMap<String, Integer>());
    private final Object cacheLock = new Object();
    
    /**
     * Extracts the dominant color from a bitmap and returns an adaptive overlay color.
     * 
     * @param bitmap The bitmap to analyze
     * @param defaultColor The default color to return if extraction fails
     * @return The adaptive overlay color
     */
    @ColorInt
    public int extractAdaptiveColor(@NonNull Bitmap bitmap, @ColorInt int defaultColor) {
        if (bitmap.isRecycled() || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return defaultColor;
        }
        
        // Create a cache key based on bitmap properties
        String cacheKey = bitmap.getWidth() + "x" + bitmap.getHeight() + "_" + bitmap.hashCode();
        
        synchronized (cacheLock) {
            Integer cachedColor = colorCache.get(cacheKey);
            if (cachedColor != null) {
                return cachedColor;
            }
        }
        
        // Use CountDownLatch for synchronous color extraction with timeout
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] extractedColor = {defaultColor};
        
        try {
            Palette.from(bitmap)
                .maximumColorCount(16) // Limit colors for performance
                .generate(palette -> {
                    if (palette != null) {
                        extractedColor[0] = selectBestOverlayColor(palette, defaultColor);
                    }
                    latch.countDown();
                });
            
            // Wait for extraction with timeout
            latch.await(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            
        } catch (Exception e) {
            // Fallback to manual color extraction if Palette fails
            extractedColor[0] = extractColorManually(bitmap, defaultColor);
        }
        
        // Cache the result
        synchronized (cacheLock) {
            colorCache.put(cacheKey, extractedColor[0]);
            // Keep cache size manageable
            if (colorCache.size() > 50) {
                colorCache.clear();
            }
        }
        
        return extractedColor[0];
    }
    
    /**
     * Selects the best overlay color from the palette based on Windows Acrylic design principles.
     */
    @ColorInt
    private int selectBestOverlayColor(@NonNull Palette palette, @ColorInt int defaultColor) {
        // Try to get a muted color first (Windows Acrylic style)
        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
        if (mutedSwatch != null) {
            return createAdaptiveOverlay(mutedSwatch.getRgb());
        }
        
        // Fallback to dominant color
        Palette.Swatch dominantSwatch = palette.getDominantSwatch();
        if (dominantSwatch != null) {
            return createAdaptiveOverlay(dominantSwatch.getRgb());
        }
        
        // Try light muted for better contrast
        Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
        if (lightMutedSwatch != null) {
            return createAdaptiveOverlay(lightMutedSwatch.getRgb());
        }
        
        return defaultColor;
    }
    
    /**
     * Creates an adaptive overlay color with appropriate alpha and saturation adjustments.
     */
    @ColorInt
    private int createAdaptiveOverlay(@ColorInt int baseColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        
        // Adjust saturation for better overlay effect (Windows Acrylic style)
        hsv[1] = Math.min(hsv[1] * 0.7f, 1.0f); // Reduce saturation
        hsv[2] = Math.max(hsv[2] * 0.9f, 0.1f); // Slightly darken
        
        int adjustedColor = Color.HSVToColor(hsv);
        
        // Apply alpha for overlay effect (semi-transparent)
        return Color.argb(120, Color.red(adjustedColor), Color.green(adjustedColor), Color.blue(adjustedColor));
    }
    
    /**
     * Manual color extraction fallback method.
     */
    @ColorInt
    private int extractColorManually(@NonNull Bitmap bitmap, @ColorInt int defaultColor) {
        try {
            // Sample pixels from the center region for better color representation
            int centerX = bitmap.getWidth() / 2;
            int centerY = bitmap.getHeight() / 2;
            int sampleSize = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 4;
            
            long red = 0, green = 0, blue = 0;
            int sampleCount = 0;
            
            for (int x = centerX - sampleSize/2; x < centerX + sampleSize/2; x += 2) {
                for (int y = centerY - sampleSize/2; y < centerY + sampleSize/2; y += 2) {
                    if (x >= 0 && x < bitmap.getWidth() && y >= 0 && y < bitmap.getHeight()) {
                        int pixel = bitmap.getPixel(x, y);
                        red += Color.red(pixel);
                        green += Color.green(pixel);
                        blue += Color.blue(pixel);
                        sampleCount++;
                    }
                }
            }
            
            if (sampleCount > 0) {
                int avgRed = (int) (red / sampleCount);
                int avgGreen = (int) (green / sampleCount);
                int avgBlue = (int) (blue / sampleCount);
                
                return createAdaptiveOverlay(Color.rgb(avgRed, avgGreen, avgBlue));
            }
            
        } catch (Exception e) {
            // Return default color if manual extraction fails
        }
        
        return defaultColor;
    }
    
    /**
     * Clears the color cache to free memory.
     */
    public void clearCache() {
        synchronized (cacheLock) {
            colorCache.clear();
        }
    }
}