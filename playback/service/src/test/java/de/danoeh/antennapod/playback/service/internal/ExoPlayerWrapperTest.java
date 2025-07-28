package de.danoeh.antennapod.playback.service.internal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExoPlayerWrapper functionality.
 * Tests the real implementation with mocked Android framework dependencies.
 */
public class ExoPlayerWrapperTest {

    private ExoPlayerWrapper exoPlayerWrapper;
    @Mock
    private Context context;
    @Mock 
    private Looper mockLooper;
    @Mock
    private Handler mockHandler;
    private MockedStatic<Looper> mockedLooper;
    private MockedStatic<Log> mockedLog;
    private MockedConstruction<Handler> mockedHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock basic context behavior that ExoPlayerWrapper needs
        when(context.getCacheDir()).thenReturn(new java.io.File("/tmp"));
        when(context.getApplicationContext()).thenReturn(context);
        
        // Mock system service calls to return null (safe default)
        when(context.getSystemService(anyString())).thenReturn(null);
        
        // Mock getResources() to return a minimal Resources mock
        android.content.res.Resources mockResources = mock(android.content.res.Resources.class);
        when(context.getResources()).thenReturn(mockResources);
        
        // Mock Android Looper for ExoPlayer
        mockedLooper = mockStatic(Looper.class);
        mockedLooper.when(Looper::myLooper).thenReturn(mockLooper);
        
        // Mock Handler construction for ExoPlayer's DefaultRenderersFactory
        mockedHandler = mockConstruction(Handler.class, (mock, context) -> {
            when(mock.getLooper()).thenReturn(mockLooper);
        });
        
        // Mock Android Log for ExoPlayer internal logging
        mockedLog = mockStatic(Log.class);
        mockedLog.when(() -> Log.i(anyString(), anyString())).thenReturn(0);
        mockedLog.when(() -> Log.d(anyString(), anyString())).thenReturn(0);
        mockedLog.when(() -> Log.w(anyString(), anyString())).thenReturn(0);
        mockedLog.when(() -> Log.w(anyString(), any(Throwable.class))).thenReturn(0);
        mockedLog.when(() -> Log.e(anyString(), anyString())).thenReturn(0);
        mockedLog.when(() -> Log.e(anyString(), anyString(), any(Throwable.class))).thenReturn(0);
        
        exoPlayerWrapper = new ExoPlayerWrapper(context);
    }

    @After
    public void tearDown() {
        if (exoPlayerWrapper != null) {
            exoPlayerWrapper.release();
        }
        if (mockedLooper != null) {
            mockedLooper.close();
        }
        if (mockedLog != null) {
            mockedLog.close();
        }
        if (mockedHandler != null) {
            mockedHandler.close();
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
    public void testResetFunctionality() {
        // Test reset functionality
        exoPlayerWrapper.setPlaybackParams(1.5f, true);
        exoPlayerWrapper.reset();
        
        // After reset, should return to default values
        assertEquals("Speed should reset to 1.0", 1.0f, exoPlayerWrapper.getCurrentSpeedMultiplier(), 0.01f);
        assertFalse("Skip silence should reset to false", exoPlayerWrapper.getCurrentSkipSilence());
    }
}