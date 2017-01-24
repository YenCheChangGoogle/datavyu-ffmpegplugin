import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.Hashtable;


public class PlayImageFromVideo extends Canvas {	
	static {
		// Make sure to place the ffmpeg libraries (dll's) in the java library path. I use '.'
		System.loadLibrary("./lib/PlayImageFromVideo");
	}
	
	private static final long serialVersionUID = -6199180436635445511L;
	
	protected ColorSpace cs 						= ColorSpace.getInstance(ColorSpace.CS_sRGB);
	protected ComponentColorModel cm 				= new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	protected Hashtable<String, String> properties 	= new Hashtable<String, String>();
	
	protected int 				nChannel 	= 0;
	protected int 				width 		= 0; // Store width and height to allocate buffers without native calls.
	protected int 				height 		= 0;
	protected BufferedImage 	image 		= null;
	protected ByteBuffer 		buffer 		= null;
	protected byte[] 			data 		= null;
	protected DataBufferByte 	dataBuffer 	= null;
	protected SampleModel 		sm 			= null;
	protected boolean			loaded 		= false;
	
	/**
	 * Get the frame buffer.
	 * If no movie was loaded this method returns null.
	 * @return A frame buffer object.
	 */
	protected native ByteBuffer getFrameBuffer();
	
	/**
	 * Load the next frame.
	 * If no movie was loaded this method returns -1.
	 * ATTENTION: This method blocks if there is no next frame. This avoids active spinning of
	 * a display thread. However, when hooked up to a button you need to make sure that you don't
	 * call this method if there are no frames available otherwise your UI thread is blocked.
	 * The methods atStart or and atEnd can help decide if you can safely make the call.
	 * @return The number of frames loaded. This could be larger than one if frames are skipped.
	 */
	protected native int loadNextFrame(); // loop the video, or stop at the end?
	
	/**
	 * Load the movie with the file name.
	 * If this method is called, multiple times, the under laying implementation
	 * releases resources and re-allocates them.
	 * @param fileName The file name.
	 */
	protected native void loadMovie(String fileName);
	
	/**
	 * Get the number of color channels for the movie.
	 * @return The number of channels.
	 */
	public native int getMovieColorChannels();

	/**
	 * Get the height of the movie frames.
	 * Returns 0 if no movie was loaded.
	 * @return Height.
	 */
	public native int getMovieHeight();
	
	/**
	 * Get the width of the movie frames.
	 * Returns 0 if no movie was loaded.
	 * @return Width.
	 */
	public native int getMovieWidth();
	
	/**
	 * Get the first time stamp of the movie in seconds.
	 * @return First time stamp in the stream in seconds.
	 */
	public native double getMovieStartTimeInSeconds();
	
	/**
	 * Get the last time stamp of the movie in seconds.
	 * ATTENTION: This is an estimate based on duration.
	 * @return ESTIMATED last time stamp in the stream in seconds.
	 */
	public native double getMovieEndTimeInSeconds();
	
	/**
	 * Get the duration of the movie in seconds. 
	 * ATTENTION: This is a best effort estimate by ffmpeg and does not match the actual 
	 * duration when decoding the movie. Usually the actual duration is shorter by less
	 * than one second.
	 * Returns 0 if no movie was loaded.
	 * @return Duration in SECONDS.
	 */
	public native double getMovieDuration();
	
	/**
	 * Get the number of frames if known, otherwise 0.
	 * ATTENTION: This is a best effort estimate by ffmpeg and does not match the actual 
	 * number of frames you can pull from the movie. Usually that number is lower. 
	 * Returns 0 if no movie was loaded.
	 * @return Number of frames.
	 */
	//public native long getMovieNumberOfFrames();
	
	/**
	 * Get the current time of the movie in seconds.
	 * Returns 0 if no movie was loaded.
	 * @return Current time in seconds.
	 */
	public native double getMovieTimeInSeconds();
	
	/**
	 * Get the current time of the movie in frames.
	 * Returns 0 if no movie was loaded.
	 * @return Current time in frames.
	 */
	//public native long getMovieTimeInFrames();
	
	/**
	 * Resets movie either to the front or end of the file depending
	 * on the play back direction.
	 */
	public native void rewindMovie();
	
	/**
	 * Returns true if we are playing the video in forward direction and
	 * false otherwise.
	 * @return True if forward play back otherwise false.
	 */
	protected native boolean forwardPlayback();
	
	/**
	 * Reached the start when reading this file. At this point any further 
	 * loadNextFrame() will return the same frame.
	 * This is intended to be used to stop any active pulling of frames when
	 * the start or end of the file is reached.
	 * @return True if start of file is reached.
	 */
	protected native boolean atStartForRead();
	
	/**
	 * Reached the end when reading this file. At this point any further
	 * loadNextFrame() will return the same frame.
	 * @return True if the end of the file is reached.
	 */
	protected native boolean atEndForRead();
	
	/**
	 * Release all resources that have been allocated when loading the move.
	 * If this method is called when no movie was loaded no resources are freed.
	 */
	public native void releaseMovie();
	
	/**
	 * Set the play back speed.
	 * @param speed A floating point with the play back speed as factor of the original 
	 * 				play back speed. For instance, for the value of -0.5x the video 
	 * 				is played back at half the rate.
	 * Tested for the range -+0.25x to -+ 4x. 
	 */
	public native void setPlaybackSpeed(float speed);
	
	/**
	 * Set the time within the movie.
	 * @param time The time within the video in SECONDS.
	 * A negative value sets the video to the end. If the duration is set above the length
	 * of the video the video is set to the end.
	 */
	public native void setTimeInSeconds(double time);
	
	/**
	 * Set the time within the movie through a frame number.
	 * @param frameNo Frame number to set to.
	 */
	//public native void setTimeInFrames(long frameNo);
	
	
	public void update(Graphics g){
	    paint(g); // Instead of resetting, paint directly. 
	}
	
	/**
	 * If we are playing in forward mode 
	 * @return
	 */
	public boolean hasNextFrame() {
		return !(forwardPlayback() && atEndForRead() || !forwardPlayback() && atStartForRead());
	}
	
	/**
	 * Get the next frame. With the 
	 * @return The number of frames loaded. This could be larger than one if frames are skipped.
	 */
	public int getNextFrame() {
		int nFrame = loadNextFrame(); // Load the next frame(s). May skip frames.
		buffer = getFrameBuffer(); // Get the buffer.
		data = new byte[width*height*nChannel];	// Allocate the bytes in java.
		buffer.get(data); // Copy from the native buffer into the java buffer.
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height); // Create data buffer.
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0)); // Create writable raster.
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		return nFrame; // Return the number of frames.
	}

	/**
	 * Set a movie with the file name for this player.
	 * @param fileName Name of the movie file.
	 */
	public void setMovie(String fileName) {
		if (loaded) {
			releaseMovie();
		}
		loadMovie(fileName);
		nChannel = getMovieColorChannels();
		width = getMovieWidth();
		height = getMovieHeight();
		loaded = true;
		sm = cm.createCompatibleSampleModel(width, height); // Create sampling model.
		setBounds(0, 0, width, height);
	}
		
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	public PlayImageFromVideo() {
		// Initialize with an empty image.
		nChannel = 3;
		width = 640;
		height = 480;
		data = new byte[width*height*nChannel];	// Allocate the bytes in java.
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		sm = cm.createCompatibleSampleModel(width, height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		setBounds(0, 0, width, height);
	}
	
	/**
	 * An example program.
	 * @param args Arguments for the program.
	 */
	public static void main(String[] args) {
		//String fileName = "C:\\Users\\Florian\\test.mpg";
		//String fileName = "C:\\Users\\Florian\\SleepingBag.MP4"; // put your video file here
		String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		//String fileName = "C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov";
		//String fileName = "C:\\Users\\Florian\\video_1080p.mp4";
		//String fileName = "C:\\Users\\Florian\\video_h264ntscdvw.mp4";
		final PlayImageFromVideo player = new PlayImageFromVideo();
		player.setMovie(fileName);
		int width = player.getMovieWidth();
		int height = player.getMovieHeight();
		double duration = player.getMovieDuration();
		player.setTimeInSeconds(8);
		//player.setPlaybackSpeed(-1f);
		Frame f = new Frame();
        f.setBounds(0, 0, width, height);
        f.add(player);
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	player.releaseMovie();
                System.exit(0);
            }
        } );        
        f.setVisible(true);
        long t0 = System.nanoTime();
        int nFrameReq = 1; // Played number of frames.
        int nFrameDec = 0; // Decoded number of frames.
        int nFrameSkip = 0; // Skipped number of frames.
        for (int iFrame = 0; iFrame < nFrameReq; ++iFrame) {
        	int nFrame = player.getNextFrame();
        	nFrameSkip += nFrame > 1 ? 1 : 0;
        	nFrameDec += nFrame;
        	player.repaint();
        }
        long t1 = System.nanoTime();
		System.out.println("width = " + width + " pixels.");
		System.out.println("height = " + height + " pixels.");
		System.out.println("duration = " + duration + " seconds.");
        System.out.println("Decoded rate = " +  ((double)nFrameDec)/(t1-t0)*1e9f + " frames per second.");
        System.out.println("Skipped " + nFrameSkip + " frames.");
	}	
}
