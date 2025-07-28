package de.danoeh.antennapod.playback.service.internal;

import android.content.Context;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertNotNull;

/**
 * Test to verify ExoPlayerWrapper creation works properly in test environment with Robolectric.
 * This test validates that ExoPlayerWrapper can be created without display context issues.
 */
@RunWith(RobolectricTestRunner.class)
public class ExoPlayerWrapperDisplayTest {

    private Context context;

    @Test
    public void testExoPlayerWrapperCreationInTestEnvironment() {
        // Get the Robolectric application context which provides a working Android environment
        context = RuntimeEnvironment.getApplication();
        
        // Create ExoPlayerWrapper with Robolectric context - no mocking needed
        ExoPlayerWrapper wrapper = null;
        try {
            wrapper = new ExoPlayerWrapper(context);
            assertNotNull("ExoPlayerWrapper should be created successfully", wrapper);
        } finally {
            if (wrapper != null) {
                wrapper.release();
            }
        }
    }
    
    @After
    public void tearDown() {
        // No cleanup needed with Robolectric approach
    }
}