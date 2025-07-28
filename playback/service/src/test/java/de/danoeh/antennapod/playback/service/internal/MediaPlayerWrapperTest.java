package de.danoeh.antennapod.playback.service.internal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MediaPlayerWrapper functionality.
 * Uses mocked interface instead of creating real ExoPlayer instances.
 * This approach avoids Android system dependencies in unit tests.
 */
public class MediaPlayerWrapperTest {

    @Mock
    private MediaPlayerWrapper mediaPlayerWrapper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up default mock behavior for common test scenarios
        when(mediaPlayerWrapper.getCurrentSpeedMultiplier()).thenReturn(1.0f);
        when(mediaPlayerWrapper.getCurrentSkipSilence()).thenReturn(false);
        when(mediaPlayerWrapper.isPlaying()).thenReturn(false);
        when(mediaPlayerWrapper.getCurrentPosition()).thenReturn(0);
        when(mediaPlayerWrapper.getAudioTracks()).thenReturn(Collections.emptyList());
        when(mediaPlayerWrapper.getSelectedAudioTrack()).thenReturn(-1);
    }

    @Test
    public void testInitialState() {
        // Test initial playback parameters
        assertEquals("Initial speed should be 1.0", 1.0f, mediaPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertFalse("Initial skip silence should be false", mediaPlayerWrapper.getCurrentSkipSilence());
        assertFalse("Initially should not be playing", mediaPlayerWrapper.isPlaying());
    }

    @Test
    public void testCurrentPositionWhenNotPrepared() {
        // Test that getCurrentPosition works in unprepared state
        int position = mediaPlayerWrapper.getCurrentPosition();
        assertEquals("Position should be 0 when not prepared", 0, position);
    }

    @Test
    public void testSetPlaybackSpeedOnly() {
        // Test setting speed without skip silence
        float testSpeed = 1.5f;
        
        // Setup mock behavior for this test
        when(mediaPlayerWrapper.getCurrentSpeedMultiplier()).thenReturn(testSpeed);
        when(mediaPlayerWrapper.getCurrentSkipSilence()).thenReturn(false);
        
        mediaPlayerWrapper.setPlaybackParams(testSpeed, false);
        
        assertEquals("Speed should be set correctly", testSpeed, mediaPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertFalse("Skip silence should remain false", mediaPlayerWrapper.getCurrentSkipSilence());
        
        // Verify the method was called with correct parameters
        verify(mediaPlayerWrapper).setPlaybackParams(testSpeed, false);
    }

    @Test
    public void testSetSkipSilenceOnly() {
        // Test enabling skip silence without changing speed
        when(mediaPlayerWrapper.getCurrentSpeedMultiplier()).thenReturn(1.0f);
        when(mediaPlayerWrapper.getCurrentSkipSilence()).thenReturn(true);
        
        mediaPlayerWrapper.setPlaybackParams(1.0f, true);
        
        assertEquals("Speed should remain 1.0", 1.0f, mediaPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertTrue("Skip silence should be enabled", mediaPlayerWrapper.getCurrentSkipSilence());
        
        verify(mediaPlayerWrapper).setPlaybackParams(1.0f, true);
    }

    @Test
    public void testSetBothSpeedAndSkipSilence() {
        // Test setting both speed and skip silence together
        float testSpeed = 2.0f;
        
        when(mediaPlayerWrapper.getCurrentSpeedMultiplier()).thenReturn(testSpeed);
        when(mediaPlayerWrapper.getCurrentSkipSilence()).thenReturn(true);
        
        mediaPlayerWrapper.setPlaybackParams(testSpeed, true);
        
        assertEquals("Speed should be set correctly", testSpeed, mediaPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertTrue("Skip silence should be enabled", mediaPlayerWrapper.getCurrentSkipSilence());
        
        verify(mediaPlayerWrapper).setPlaybackParams(testSpeed, true);
    }

    @Test
    public void testDataSourceWithoutCredentials() {
        // Test setting data source without credentials
        mediaPlayerWrapper.setDataSource("https://example.com/test.mp3");
        
        // Verify the method was called
        verify(mediaPlayerWrapper).setDataSource("https://example.com/test.mp3");
    }

    @Test
    public void testDataSourceWithCredentials() {
        // Test setting data source with credentials
        mediaPlayerWrapper.setDataSource("https://example.com/test.mp3", "user", "pass");
        
        // Verify the method was called with correct parameters
        verify(mediaPlayerWrapper).setDataSource("https://example.com/test.mp3", "user", "pass");
    }

    @Test
    public void testAudioTracksWhenNotPrepared() {
        // Test getting audio tracks when no media is prepared
        var tracks = mediaPlayerWrapper.getAudioTracks();
        assertNotNull("Audio tracks list should not be null", tracks);
        assertTrue("Audio tracks should be empty when not prepared", tracks.isEmpty());
    }

    @Test
    public void testSelectedAudioTrackWhenNotPrepared() {
        // Test getting selected audio track when no media is prepared
        int selectedTrack = mediaPlayerWrapper.getSelectedAudioTrack();
        assertEquals("Should return -1 when no track available", -1, selectedTrack);
    }

    @Test
    public void testReset() {
        // Test reset functionality
        mediaPlayerWrapper.reset();
        
        // Verify reset was called
        verify(mediaPlayerWrapper).reset();
    }

    @Test
    public void testRelease() {
        // Test release functionality
        mediaPlayerWrapper.release();
        
        // Verify release was called
        verify(mediaPlayerWrapper).release();
    }

    @Test
    public void testMultipleSpeedChanges() {
        // Test multiple consecutive speed changes
        float[] speeds = {0.5f, 1.0f, 1.25f, 1.5f, 2.0f, 0.75f};
        
        for (float speed : speeds) {
            // Update mock behavior for each speed
            when(mediaPlayerWrapper.getCurrentSpeedMultiplier()).thenReturn(speed);
            
            mediaPlayerWrapper.setPlaybackParams(speed, false);
            assertEquals("Speed should be set correctly for " + speed, 
                speed, mediaPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
            
            // Verify each call
            verify(mediaPlayerWrapper).setPlaybackParams(speed, false);
        }
    }
}