package de.danoeh.antennapod.playback.service.internal;

import android.content.Context;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Test to reproduce the display context issue with ExoPlayerWrapper in test environment.
 * This test should fail initially due to NullPointerException when DefaultTrackSelector
 * tries to access display information in a headless test environment.
 */
public class ExoPlayerWrapperDisplayTest {

    @Mock
    private Context mockContext;

    @Test
    public void testExoPlayerWrapperCreationInTestEnvironment() {
        MockitoAnnotations.openMocks(this);
        
        // Mock basic context behavior that ExoPlayerWrapper might need
        when(mockContext.getCacheDir()).thenReturn(new java.io.File("/tmp"));
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        // Mock system service calls that DefaultTrackSelector might need
        when(mockContext.getSystemService("uimode")).thenReturn(null);
        
        // This should fail with NullPointerException due to display access
        // when DefaultTrackSelector tries to get current display mode size
        ExoPlayerWrapper wrapper = null;
        try {
            wrapper = new ExoPlayerWrapper(mockContext);
            assertNotNull("ExoPlayerWrapper should be created successfully", wrapper);
        } finally {
            if (wrapper != null) {
                wrapper.release();
            }
        }
    }
}