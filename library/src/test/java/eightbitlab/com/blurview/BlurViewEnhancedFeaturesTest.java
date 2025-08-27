package eightbitlab.com.blurview;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for BlurView enhanced features including real-time updates and dynamic colors.
 */
class BlurViewEnhancedFeaturesTest {

    @Test
    void dynamicColorExtractor_clearCache_should_not_throw() {
        DynamicColorExtractor extractor = new DynamicColorExtractor();
        assertDoesNotThrow(() -> extractor.clearCache());
    }

    @Test
    void preDrawBlurController_updateBlur_should_be_public() {
        // Test that updateBlur method is accessible (public)
        // This verifies our change to make it public for forceBlurUpdate
        assertDoesNotThrow(() -> {
            PreDrawBlurController.class.getMethod("updateBlur");
        });
    }

    @Test
    void preDrawBlurController_getInternalBitmap_should_be_accessible() {
        // Test that getInternalBitmap method is accessible
        assertDoesNotThrow(() -> {
            PreDrawBlurController.class.getMethod("getInternalBitmap");
        });
    }
}