package de.danoeh.antennapod.playback.service.internal;

import java.util.List;

/**
 * Interface for media player implementations to enable easy testing.
 * Abstracts the core functionality needed by the application without
 * tying tests to specific ExoPlayer implementation details.
 */
public interface MediaPlayerWrapper {
    
    // Playback control methods
    boolean isPlaying();
    void reset();
    void release();
    
    // Position and timing
    int getCurrentPosition();
    
    // Playback parameters
    void setPlaybackParams(float speed, boolean skipSilence);
    float getCurrentSpeedMultiplier();
    boolean getCurrentSkipSilence();
    
    // Data source methods
    void setDataSource(String url);
    void setDataSource(String url, String username, String password);
    
    // Audio track methods
    List<String> getAudioTracks();
    int getSelectedAudioTrack();
}