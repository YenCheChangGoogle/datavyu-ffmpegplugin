package org.datavyu.plugins.examples;

import javax.swing.JFrame;
import org.datavyu.plugins.MediaPlayer;

import java.io.File;
import java.net.URI;
import org.datavyu.plugins.ffmpeg.FfmpegSdlAwtMediaPlayer;

public class SimpleSdlAwtMediaPlayer {
  public static void main(String[] args) {
    // Define the media file
    URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();

    // Create the media player using the constructor with File
    MediaPlayer mediaPlayer = new FfmpegSdlAwtMediaPlayer(mediaPath, new JFrame());

    // Initialize the player
    mediaPlayer.init();

    // Open a JFrame to control the media player through key commands
    new JMediaPlayerControlFrame(mediaPlayer);
  }
}
