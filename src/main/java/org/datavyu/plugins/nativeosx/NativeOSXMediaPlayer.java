package org.datavyu.plugins.nativeosx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.DatavyuMediaPlayer;
import org.datavyu.plugins.PlayerStateEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.net.URI;

abstract class NativeOSXMediaPlayer extends DatavyuMediaPlayer {

  private static Logger logger = LogManager.getLogger(NativeOSXMediaPlayer.class);

  // The time seeked to will be within the bound [time-0, time+0]
  protected static final int PRECISE_SEEK_FLAG = 0;
  // The time seeked to will be within the bound [time-0.5, time+0.5]
  protected static final int MODERATE_SEEK_FLAG = 1;
  // The time seeked to will be within the bound [time-INFINITE, time+INFINITE]
  protected static final int NORMAL_SEEK_FLAG = 2;

  protected static final float INITIAL_VOLUME = 1F;

  protected NativeOSXPlayer mediaPlayer;

  protected final int id;
  private static int playerCount = 0;
  protected double fps = -1;
  protected double duration = -1;
  private float volume = INITIAL_VOLUME;
  private boolean seeking = false;
  private float prevRate;

  protected NativeOSXMediaPlayer(URI mediaPath) {
    super(mediaPath);
    this.mediaPlayer = new NativeOSXPlayer(mediaPath);
    this.id = playerCount;
    this.prevRate = 1F;
    sendPlayerStateEvent(eventPlayerUnknown, 0);
  }

  protected static void incPlayerCount() {
    playerCount++;
  }

  protected static void decPlayerCount() {
    playerCount--;
  }

  @Override
  protected void playerPlay() throws MediaException {
    if (getState() == PlayerStateEvent.PlayerState.PAUSED) {
      EventQueue.invokeLater(() -> mediaPlayer.setRate(prevRate, id));
    } else {
      EventQueue.invokeLater(() -> mediaPlayer.setRate(1F, id));
    }

    sendPlayerStateEvent(eventPlayerPlaying, 0);
  }

  @Override
  protected void playerStop() throws MediaException {
      EventQueue.invokeLater(() -> mediaPlayer.stop(id));
      sendPlayerStateEvent(eventPlayerStopped, 0);
  }

  @Override
  protected void playerStepForward() throws MediaException {
    double stepSize = Math.ceil(1000F / mediaPlayer.getFPS(id));
    double time = mediaPlayer.getCurrentTime(id);
    long newTime = (long) Math.min(Math.max(time + stepSize, 0), mediaPlayer.getDuration(id));
    logger.info("Stepping Forward from " + time + " sec, to " + newTime + " sec");
    mediaPlayer.setTimePrecise(newTime, id);
  }

  @Override
  protected void playerStepBackward() throws MediaException {
    double stepSize = Math.ceil(1000F / mediaPlayer.getFPS(id));
    double time = mediaPlayer.getCurrentTime(id);
    long newTime = (long) Math.min(Math.max(time - stepSize, 0), mediaPlayer.getDuration(id));
    logger.info(
        "Stepping Backward from " + (time / 1000.0) + " sec, to " + (newTime / 1000.0) + " sec");
    mediaPlayer.setTimePrecise(newTime, id);
  }

  @Override
  protected void playerPause() throws MediaException {
    // AVFoundation will change the rate to 0 when stopped
    // we need to save the rate before a stop

    if (getState() == PlayerStateEvent.PlayerState.STOPPED) {
      // if the player was stopped, we need to
      // override the stopped state and return
      prevRate = 1F;
      sendPlayerStateEvent(eventPlayerPaused, 0);
      return;
    } else if (getState() == PlayerStateEvent.PlayerState.PLAYING) {
      prevRate = playerGetRate();
      playerStop();

      // Override the stopped state
      sendPlayerStateEvent(eventPlayerPaused, 0);
    }
  }

  @Override
  protected void playerFinish() throws MediaException {}

  @Override
  protected float playerGetRate() throws MediaException {
    return mediaPlayer.getRate(id);
  }

  @Override
  protected void playerSetRate(float rate) throws MediaException {
    logger.info("Setting Rate to : " + rate + "X");
    mediaPlayer.setRate(rate, id);
  }

  @Override
  protected void playerSetStartTime(double startTime) throws MediaException {
    playerSeek((long) (startTime * 1000), PRECISE_SEEK_FLAG);
  }

  @Override
  protected double playerGetPresentationTime() throws MediaException {
    return (mediaPlayer.getCurrentTime(id) / 1000.0);
  }

  @Override
  protected double playerGetFps() throws MediaException {
    return mediaPlayer.getFPS(id);
  }

  @Override
  protected float playerGetVolume() throws MediaException {
    synchronized (this) {
      if (muteEnabled) return mutedVolume;
    }
    return volume;
  }

  @Override
  protected synchronized void playerSetVolume(float volume) throws MediaException {
    logger.info("Setting Volume to " + volume);
    if (!muteEnabled) {
      EventQueue.invokeLater(() -> mediaPlayer.setVolume(volume, id));
      this.volume = mutedVolume = volume;
    } else {
      mutedVolume = volume;
    }
  }

  @Override
  protected double playerGetDuration() throws MediaException {
    return (mediaPlayer.getDuration(id) / 1000.0);
  }

  @Override
  protected void playerSeek(double streamTime) throws MediaException {
    if (!seeking) {
      seeking = true;
      EventQueue.invokeLater(
          () -> {
            boolean wasPlaying = isPlaying();
            prevRate = playerGetRate();
            if (isPlaying()) {
              mediaPlayer.stop(id);
            }
            if (!wasPlaying || prevRate >= 0 && prevRate <= 8) {
              logger.debug("Precise seek to position: " + streamTime);
              playerSeek(streamTime * 1000, PRECISE_SEEK_FLAG);
            } else if (prevRate < 0 && prevRate > -8) {
              logger.debug("Moderate seek to position: " + streamTime);
              playerSeek(streamTime * 1000, MODERATE_SEEK_FLAG);
            } else {
              logger.debug("Seek to position: " + streamTime);
              playerSeek(streamTime * 1000, NORMAL_SEEK_FLAG);
            }
            if (wasPlaying) {
              playerSetRate(prevRate);
            }
            mediaPlayer.repaint();
            seeking = false;
          });
    }
  }

  @Override
  protected void playerDispose() {
    logger.info("Disposing the player");
    decPlayerCount();
  }

  @Override
  protected boolean playerIsSeekPlaybackEnabled() { return false; }

  @Override
  public int getImageWidth() {
    return (int) mediaPlayer.getMovieWidth(id);
  }

  @Override
  public int getImageHeight() {
    return (int) mediaPlayer.getMovieHeight(id);
  }

  protected boolean isPlaying() {
    return !mediaPlayer.isPlaying(id); // the native os plugin return false when is playing
  }

  @Override
  protected float playerGetBalance() throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected void playerSetBalance(float balance) throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected long playerGetAudioSyncDelay() throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected void playerSetAudioSyncDelay(long delay) throws MediaException {
    throw new NotImplementedException();
  }

  protected abstract void playerSeek(double streamTime, int flags) throws MediaException;
}