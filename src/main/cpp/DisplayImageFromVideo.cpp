#include <jni.h>
#include <stdio.h>
#include <math.h>
extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libswscale/swscale.h>
}
#include "DisplayImageFromVideo.h"

// Florian Raudies, 10/01/2017, Mountain View, CA.
// vcvarsall.bat x64
/*
cl DisplayImageFromVideo.cpp /Fe"..\..\lib\DisplayImageFromVideo"^
 /I"C:\Users\Florian\FFmpeg-release-3.3"^
 /I"C:\Program Files\Java\jdk1.8.0_144\include"^
 /I"C:\Program Files\Java\jdk1.8.0_144\include\win32"^
 /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_144\lib\jawt.lib"^
 "C:\Users\Florian\FFmpeg-release-3.3\libavcodec\avcodec.lib"^
 "C:\Users\Florian\FFmpeg-release-3.3\libavformat\avformat.lib"^
 "C:\Users\Florian\FFmpeg-release-3.3\libavutil\avutil.lib"^
 "C:\Users\Florian\FFmpeg-release-3.3\libswscale\swscale.lib"
*/

int					width		= 0;
int					height		= 0;
int					nChannel	= 3;
int					iFrame		= 0;
AVFormatContext		*pFormatCtx = NULL;
int					iVideoStream;
AVCodecContext		*pCodecCtx	= NULL;
AVCodec				*pCodec		= NULL;
AVFrame				*pFrame		= NULL; 
AVFrame				*pFrameShow = NULL;
AVPacket			packet;
int					frameFinished;
int					numBytes;
uint8_t				*bufferShow = NULL;
AVDictionary		*optionsDict= NULL;
struct SwsContext   *sws_ctx	= NULL;

void loadNextFrame() {
	while(av_read_frame(pFormatCtx, &packet)>=0) {
		// Is this a packet from the video stream?
		if(packet.stream_index == iVideoStream) {
			// Decode video frame
			avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);

			// Did we get a video frame?
			if(frameFinished) {
				// Convert the image from its native format to RGB
				sws_scale
				(
					sws_ctx,
					(uint8_t const * const *)pFrame->data,
					pFrame->linesize,
					0,
					pCodecCtx->height,
					pFrameShow->data,
					pFrameShow->linesize
				);

				width = pCodecCtx->width;
				height = pCodecCtx->height;
				av_free_packet(&packet);

				fprintf(stdout,"Read frame %d.\n",iFrame++);

				return;
			}
			// Free the packet that was allocated by av_read_frame
			av_free_packet(&packet);
		}
	}
}


JNIEXPORT jobject JNICALL Java_DisplayImageFromVideo_getFrameBuffer
(JNIEnv *env, jobject thisObject) {
	return env->NewDirectByteBuffer((void*) pFrameShow->data[0], width*height*nChannel*sizeof(uint8_t));
}

JNIEXPORT void JNICALL Java_DisplayImageFromVideo_loadNextFrame
(JNIEnv *, jobject) {
	loadNextFrame();
}

// Opens movie file and loads first frame.
JNIEXPORT void JNICALL Java_DisplayImageFromVideo_loadMovie
(JNIEnv *env, jobject thisObject, jstring jFileName) {
	const char *fileName = env->GetStringUTFChars(jFileName, 0);

	// Register all formats and codecs
	av_register_all();

	// Open video file
	if(avformat_open_input(&pFormatCtx, fileName, NULL, NULL)!=0) {
		fprintf(stderr, "Couldn't open file.\n");
		exit(1);
	}

	// Retrieve stream information
	if(avformat_find_stream_info(pFormatCtx, NULL)<0) {
		fprintf(stderr, "Couldn't find stream information.\n");
		exit(1);
	}
  
	// Dump information about file onto standard error
	av_dump_format(pFormatCtx, 0, fileName, 0);

	// Find the first video stream
	iVideoStream = -1;
	for(int i = 0; i < pFormatCtx->nb_streams; i++)
		if(pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO) {
		iVideoStream=i;
		break;
	}

	if(iVideoStream == -1) {
		fprintf(stderr, "Didn't find a video stream.\n");
		exit(1);
	}

	// Get a pointer to the codec context for the video stream
	pCodecCtx = pFormatCtx->streams[iVideoStream]->codec;

	// Find the decoder for the video stream
	pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
	if(pCodec == NULL) {
		fprintf(stderr, "Unsupported codec!\n");
		exit(1);
	}
	// Open codec
	if(avcodec_open2(pCodecCtx, pCodec, &optionsDict)<0) {
		fprintf(stderr, "Could not open codec.\n");
		exit(1);
	}

	// Allocate video frame
	pFrame = av_frame_alloc();

	pFrameShow = av_frame_alloc();
	if(pFrameShow == NULL) {
		fprintf(stderr, "Could not allocate RGB frame for show.\n");
		exit(1);
	}


	// Determine required buffer size and allocate buffer
	numBytes = avpicture_get_size(AV_PIX_FMT_RGB24, pCodecCtx->width,
				  pCodecCtx->height);
	bufferShow = (uint8_t *)av_malloc(numBytes*sizeof(uint8_t));

	sws_ctx = sws_getContext
		(
			pCodecCtx->width,
			pCodecCtx->height,
			pCodecCtx->pix_fmt,
			pCodecCtx->width,
			pCodecCtx->height,
			AV_PIX_FMT_RGB24,
			SWS_BILINEAR,
			NULL,
			NULL,
			NULL
		);
  
	// Assign appropriate parts of buffer to image planes in pFrameRGB
	// Note that pFrameRGB is an AVFrame, but AVFrame is a superset
	// of AVPicture
	avpicture_fill((AVPicture *)pFrameShow, bufferShow, AV_PIX_FMT_RGB24,
		 pCodecCtx->width, pCodecCtx->height);

	loadNextFrame();

	env->ReleaseStringUTFChars(jFileName, fileName);
}

JNIEXPORT jint JNICALL Java_DisplayImageFromVideo_getMovieHeight
(JNIEnv *env, jobject thisObject) {
	return (jint) height;
}

JNIEXPORT jint JNICALL Java_DisplayImageFromVideo_getMovieWidth
(JNIEnv *env, jobject thisObject) {
	return (jint) width;
}


JNIEXPORT void JNICALL Java_DisplayImageFromVideo_release
(JNIEnv *env, jobject thisObject) {
	// Free the RGB image
	av_free(bufferShow);
	av_free(pFrameShow);

	// Free the YUV frame
	av_free(pFrame);

	// Close the codec
	avcodec_close(pCodecCtx);

	// Close the video file
	avformat_close_input(&pFormatCtx);  
}