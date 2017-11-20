package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") // TODO: Performance measurement; good for integration into test cases
public class MeasureFrameRate {

    private MovieStreamProvider movieStreamProvider;
    private Frame frame;
    private static final int TIME_OUT_SEC = 10;

    private MeasureFrameRate(String movieFileName, float speed) throws IOException {
        movieStreamProvider = new MovieStreamProvider();
        final ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        AudioFormat reqAudioFormat = AudioSoundStreamListener.getNewMonoFormat();
        frame = new Frame();
        // Add the audio sound listener
        movieStreamProvider.addAudioStreamListener(new AudioSoundStreamListener(movieStreamProvider));
        // Add video display
        movieStreamProvider.addVideoStreamListener(new VideoDisplayStreamListener(movieStreamProvider, frame,
                reqColorSpace));
        // Open the movie stream provider
        movieStreamProvider.open(movieFileName, "0.0.0.1", reqColorSpace, reqAudioFormat);
        movieStreamProvider.setSpeed(speed);
        int width = movieStreamProvider.getWidthOfView();
        int height = movieStreamProvider.getHeightOfView();
        frame.setBounds(0, 0, width, height);
        frame.setVisible(true);
    }

    private void start() {
        movieStreamProvider.start();
    }

    private void stop() {
        movieStreamProvider.stop();
    }

    private void close() throws IOException {
        frame.setVisible(false);
        movieStreamProvider.close();
    }

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.OFF);
        String movieFileName = "C:\\Users\\Florian\\DatavyuSampleVideo.mp4";
        List<Float> speeds = new ArrayList<Float>() {{
            add(1f); add(2f); add(4f); add(8f); add(16f); add(32f);
        }};
        try {
            for (float speed : speeds) {
                MeasureFrameRate measureFrameRate = new MeasureFrameRate(movieFileName, speed);
                measureFrameRate.start();
                try {
                    Thread.sleep(TIME_OUT_SEC * 1000);
                } catch (InterruptedException ie) {
                    System.out.println("Timeout failed with " + ie);
                }
                measureFrameRate.stop();
                double averageFrameRate = measureFrameRate.movieStreamProvider.getAverageFrameRate();
                double frameRate = ((double)measureFrameRate.movieStreamProvider.getNumberOfFrames())/TIME_OUT_SEC;
                double isSpeed = frameRate/averageFrameRate;
                System.out.println("The expected speed: " + speed);
                System.out.println("The detected speed: " + isSpeed);
                //assert Math.abs(speed - isSpeed) < Math.ulp(1f);
                measureFrameRate.close();
            }
        } catch (IOException io) {
            System.err.println("Could not open file " + movieFileName + ". Exception: " + io);
        }
    }
}
