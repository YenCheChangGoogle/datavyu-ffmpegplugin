// Currently, these are the same error codes as from the gstreamer javafx solution
// TODO(fraudies): Auto-generate this header file from the java source code
// org.datavyu.plugins.ffmpeg.MediaError (see javafx source code for details)

#ifndef _FFMPEG_MEDIA_ERRORS_H_
#define _FFMPEG_MEDIA_ERRORS_H_

#define    ERROR_BASE_MEDIA    256
#define    ERROR_BASE_MANAGER    512
#define    ERROR_BASE_PIPELINE    768
#define    ERROR_BASE_FACTORY    1024
#define    ERROR_BASE_LOCATOR    1280
#define    ERROR_BASE_REGISTRY    1536
#define    ERROR_BASE_GSTREAMER    2048
#define    ERROR_BASE_SYSTEM    2560
#define    ERROR_BASE_FUNCTION    2816
#define    ERROR_BASE_JNI    3072
#define    WARNING_BASE_JFXMEDIA    1048576
#define    WARNING_BASE_GSTREAMER    8388608
#define    WARNING_BASE_GLIB    9437184
#define    ERROR_MASK_BASE    3840
#define    WARNING_MASK_BASE    15728640
#define    ERROR_NONE    0
#define    ERROR_MANAGER_NULL    513
#define    ERROR_MANAGER_CREATION    514
#define    ERROR_MANAGER_ENGINEINIT_FAIL    515
#define    ERROR_MANAGER_RUNLOOP_FAIL    516
#define    ERROR_MANAGER_LOGGER_INIT    517
#define    ERROR_MEDIA_NULL    257
#define    ERROR_MEDIA_CREATION    258
#define    ERROR_MEDIA_UNKNOWN_PIXEL_FORMAT    259
#define    ERROR_MEDIA_INVALID    260
#define    ERROR_MEDIA_MARKER_NAME_NULL    261
#define    ERROR_MEDIA_MARKER_TIME_NEGATIVE    262
#define    ERROR_MEDIA_MARKER_MAP_NULL    263
#define    ERROR_MEDIA_VIDEO_FORMAT_UNSUPPORTED    264
#define    ERROR_MEDIA_AUDIO_FORMAT_UNSUPPORTED    265
#define    ERROR_MEDIA_MP3_FORMAT_UNSUPPORTED    266
#define    ERROR_MEDIA_AAC_FORMAT_UNSUPPORTED    267
#define    ERROR_MEDIA_H264_FORMAT_UNSUPPORTED    268
#define    ERROR_MEDIA_HLS_FORMAT_UNSUPPORTED    269
#define    ERROR_MEDIA_CORRUPTED    270
#define    ERROR_PIPELINE_NULL    769
#define    ERROR_PIPELINE_CREATION    770
#define    ERROR_PIPELINE_NO_FRAME_QUEUE    771
#define    ERROR_FACTORY_NULL    1025
#define    ERROR_FACTORY_CONTAINER_CREATION    1026
#define    ERROR_FACTORY_INVALID_URI    1027
#define    ERROR_LOCATOR_NULL    1281
#define    ERROR_LOCATOR_UNSUPPORTED_TYPE    1282
#define    ERROR_LOCATOR_UNSUPPORTED_MEDIA_FORMAT    1283
#define    ERROR_LOCATOR_CONNECTION_LOST    1284
#define    ERROR_LOCATOR_CONTENT_TYPE_NULL    1285
#define    ERROR_REGISTRY_NULL    1537
#define    ERROR_REGISTRY_PLUGIN_ALREADY_EXIST    1538
#define    ERROR_REGISTRY_PLUGIN_PATH    1539
#define    ERROR_REGISTRY_NO_MATCHING_RECIPE    1540
#define    ERROR_GSTREAMER_ERROR    2049
#define    ERROR_GSTREAMER_PIPELINE_CREATION    2050
#define    ERROR_GSTREAMER_AUDIO_DECODER_SINK_PAD    2051
#define    ERROR_GSTREAMER_AUDIO_DECODER_SRC_PAD    2052
#define    ERROR_GSTREAMER_AUDIO_SINK_SINK_PAD    2053
#define    ERROR_GSTREAMER_VIDEO_DECODER_SINK_PAD    2054
#define    ERROR_GSTREAMER_PIPELINE_STATE_CHANGE    2055
#define    ERROR_GSTREAMER_PIPELINE_SEEK    2056
#define    ERROR_GSTREAMER_PIPELINE_QUERY_LENGTH    2057
#define    ERROR_GSTREAMER_PIPELINE_QUERY_POS    2058
#define    ERROR_GSTREAMER_PIPELINE_METADATA_TYPE    2059
#define    ERROR_GSTREAMER_AUDIO_SINK_CREATE    2060
#define    ERROR_GSTREAMER_GET_BUFFER_SRC_PAD    2061
#define    ERROR_GSTREAMER_CREATE_GHOST_PAD    2062
#define    ERROR_GSTREAMER_ELEMENT_ADD_PAD    2063
#define    ERROR_GSTREAMER_UNSUPPORTED_PROTOCOL    2064
#define    ERROR_GSTREAMER_SOURCEFILE_NONEXISTENT    2080
#define    ERROR_GSTREAMER_SOURCEFILE_NONREGULAR    2096
#define    ERROR_GSTREAMER_ELEMENT_LINK    2112
#define    ERROR_GSTREAMER_ELEMENT_LINK_AUDIO_BIN    2128
#define    ERROR_GSTREAMER_ELEMENT_LINK_VIDEO_BIN    2144
#define    ERROR_GSTREAMER_ELEMENT_CREATE    2160
#define    ERROR_GSTREAMER_VIDEO_SINK_CREATE    2176
#define    ERROR_GSTREAMER_BIN_CREATE    2192
#define    ERROR_GSTREAMER_BIN_ADD_ELEMENT    2208
#define    ERROR_GSTREAMER_ELEMENT_GET_PAD    2224
#define    ERROR_GSTREAMER_MAIN_LOOP_CREATE    2240
#define    ERROR_GSTREAMER_BUS_SOURCE_ATTACH    2241
#define    ERROR_GSTREAMER_PIPELINE_SET_RATE_ZERO    2256
#define    ERROR_GSTREAMER_VIDEO_SINK_SINK_PAD    2272
#define    ERROR_NOT_IMPLEMENTED    2561
#define    ERROR_MEMORY_ALLOCATION    2562
#define    ERROR_OS_UNSUPPORTED    2563
#define    ERROR_PLATFORM_UNSUPPORTED    2564
#define    ERROR_FUNCTION_PARAM    2817
#define    ERROR_FUNCTION_PARAM_NULL    2818
#define    ERROR_JNI_SEND_PLAYER_MEDIA_ERROR_EVENT    3073
#define    ERROR_JNI_SEND_PLAYER_HALT_EVENT    3074
#define    ERROR_JNI_SEND_PLAYER_STATE_EVENT    3075
#define    ERROR_JNI_SEND_NEW_FRAME_EVENT    3076
#define    ERROR_JNI_SEND_FRAME_SIZE_CHANGED_EVENT    3077
#define    ERROR_JNI_SEND_END_OF_MEDIA_EVENT    3078
#define    ERROR_JNI_SEND_AUDIO_TRACK_EVENT    3079
#define    ERROR_JNI_SEND_VIDEO_TRACK_EVENT    3080
#define    ERROR_JNI_SEND_METADATA_EVENT    3081
#define    ERROR_JNI_SEND_MARKER_EVENT    3082
#define    ERROR_JNI_SEND_BUFFER_PROGRESS_EVENT    3083
#define    ERROR_JNI_SEND_STOP_REACHED_EVENT    3084
#define    ERROR_JNI_SEND_DURATION_UPDATE_EVENT    3085
#define    ERROR_JNI_SEND_AUDIO_SPECTRUM_EVENT    3086
#define    WARNING_GSTREAMER_WARNING    8388609
#define    WARNING_GSTREAMER_PIPELINE_ERROR    8388610
#define    WARNING_GSTREAMER_PIPELINE_WARNING    8388611
#define    WARNING_GSTREAMER_PIPELINE_STATE_EVENT    8388612
#define    WARNING_GSTREAMER_PIPELINE_FRAME_SIZE    8388613
#define    WARNING_GSTREAMER_INVALID_FRAME    8388614
#define    WARNING_GSTREAMER_PIPELINE_INFO_ERROR    8388615
#define    WARNING_GSTREAMER_AUDIO_BUFFER_FIELD    8388616

#endif // _FFMPEG_MEDIA_ERRORS_H_
