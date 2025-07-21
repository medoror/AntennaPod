package de.danoeh.antennapod.playback.service.internal;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Unit tests for ExoPlayerWrapper functionality.
 * These tests validate existing behavior without fixing any issues.
 */
@RunWith(AndroidJUnit4.class)
public class ExoPlayerWrapperTest {

    private ExoPlayerWrapper exoPlayerWrapper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        exoPlayerWrapper = new ExoPlayerWrapper(context);
    }

    @After
    public void tearDown() {
        if (exoPlayerWrapper != null) {
            exoPlayerWrapper.release();
        }
    }

    @Test
    public void testInitialState() {
        // Test initial playback parameters
        assertEquals("Initial speed should be 1.0", 1.0f, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertFalse("Initial skip silence should be false", exoPlayerWrapper.getCurrentSkipSilence());
        assertFalse("Initially should not be playing", exoPlayerWrapper.isPlaying());
    }

    @Test
    public void testCurrentPositionWhenNotPrepared() {
        // Test that getCurrentPosition works in unprepared state
        int position = exoPlayerWrapper.getCurrentPosition();
        assertEquals("Position should be 0 when not prepared", 0, position);
    }

    @Test
    public void testSetPlaybackSpeedOnly() {
        // Test setting speed without skip silence
        float testSpeed = 1.5f;
        exoPlayerWrapper.setPlaybackParams(testSpeed, false);
        
        assertEquals("Speed should be set correctly", testSpeed, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertFalse("Skip silence should remain false", exoPlayerWrapper.getCurrentSkipSilence());
    }

    @Test
    public void testSetSkipSilenceOnly() {
        // Test enabling skip silence without changing speed
        exoPlayerWrapper.setPlaybackParams(1.0f, true);
        
        assertEquals("Speed should remain 1.0", 1.0f, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertTrue("Skip silence should be enabled", exoPlayerWrapper.getCurrentSkipSilence());
    }

    @Test
    public void testSetBothSpeedAndSkipSilence() {
        // Test setting both speed and skip silence together
        float testSpeed = 2.0f;
        exoPlayerWrapper.setPlaybackParams(testSpeed, true);
        
        assertEquals("Speed should be set correctly", testSpeed, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertTrue("Skip silence should be enabled", exoPlayerWrapper.getCurrentSkipSilence());
    }

    @Test
    public void testMultipleSpeedChanges() {
        // Test multiple consecutive speed changes (this might reveal the bug)
        float[] speeds = {0.5f, 1.0f, 1.25f, 1.5f, 2.0f, 0.75f};
        
        for (float speed : speeds) {
            exoPlayerWrapper.setPlaybackParams(speed, false);
            assertEquals("Speed should be set correctly for " + speed, 
                speed, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        }
    }

    @Test
    public void testSpeedChangesWithSkipSilence() {
        // Test speed changes while skip silence is enabled (potential bug scenario)
        exoPlayerWrapper.setPlaybackParams(1.0f, true);
        assertTrue("Skip silence should be enabled initially", exoPlayerWrapper.getCurrentSkipSilence());
        
        float[] speeds = {0.8f, 1.5f, 2.0f, 1.0f};
        
        for (float speed : speeds) {
            exoPlayerWrapper.setPlaybackParams(speed, true);
            assertEquals("Speed should be set correctly for " + speed, 
                speed, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
            assertTrue("Skip silence should remain enabled for " + speed, 
                exoPlayerWrapper.getCurrentSkipSilence());
        }
    }

    @Test
    public void testToggleSkipSilenceAtDifferentSpeeds() {
        // Test toggling skip silence at different speeds
        float[] testSpeeds = {0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
        
        for (float speed : testSpeeds) {
            // Enable skip silence at this speed
            exoPlayerWrapper.setPlaybackParams(speed, true);
            assertEquals("Speed should be correct", speed, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
            assertTrue("Skip silence should be enabled", exoPlayerWrapper.getCurrentSkipSilence());
            
            // Disable skip silence at same speed
            exoPlayerWrapper.setPlaybackParams(speed, false);
            assertEquals("Speed should remain the same", speed, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
            assertFalse("Skip silence should be disabled", exoPlayerWrapper.getCurrentSkipSilence());
        }
    }

    @Test
    public void testResetFunctionality() {
        // Test reset functionality
        exoPlayerWrapper.setPlaybackParams(1.5f, true);
        exoPlayerWrapper.reset();
        
        // After reset, should return to default values
        assertEquals("Speed should reset to 1.0", 1.0f, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertFalse("Skip silence should reset to false", exoPlayerWrapper.getCurrentSkipSilence());
    }

    @Test
    public void testBoundarySpeedValues() {
        // Test edge cases for playback speed
        float[] boundaryValues = {0.1f, 0.5f, 1.0f, 3.0f, 5.0f};
        
        for (float speed : boundaryValues) {
            exoPlayerWrapper.setPlaybackParams(speed, false);
            assertEquals("Speed should handle boundary value " + speed, 
                speed, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        }
    }

    @Test
    public void testDataSourceWithoutCredentials() {
        // Test setting data source without credentials
        try {
            exoPlayerWrapper.setDataSource("https://example.com/test.mp3");
            // Should not throw exception
        } catch (Exception e) {
            fail("Setting data source should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testDataSourceWithCredentials() {
        // Test setting data source with credentials
        try {
            exoPlayerWrapper.setDataSource("https://example.com/test.mp3", "user", "pass");
            // Should not throw exception
        } catch (Exception e) {
            fail("Setting data source with credentials should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testAudioTracksWhenNotPrepared() {
        // Test getting audio tracks when no media is prepared
        var tracks = exoPlayerWrapper.getAudioTracks();
        assertNotNull("Audio tracks list should not be null", tracks);
        // List might be empty when no media is prepared, which is expected
    }

    @Test
    public void testSelectedAudioTrackWhenNotPrepared() {
        // Test getting selected audio track when no media is prepared
        int selectedTrack = exoPlayerWrapper.getSelectedAudioTrack();
        // Should return -1 when no track is selected/available
        assertEquals("Should return -1 when no track available", -1, selectedTrack);
    }
}